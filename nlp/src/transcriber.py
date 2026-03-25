"""
transcriber.py  ·  SanRaksha NLP Module
========================================
Changes from original:
  • Returns a structured TranscriptionResult (text + confidence + language)
    instead of a bare string — callers can gate on .needs_review before
    sending data to the risk model.
  • Confidence derived from Whisper's per-segment avg_logprob, mapped to
    0-1.  Threshold = 0.55 (tune after field testing).
  • Language auto-detection enabled by default — ASHA workers may speak
    Hindi, Bhojpuri, Awadhi etc.  Pass language="hi" to force Hindi.
  • Configurable model size — use "tiny" for on-device Android export,
    "small" / "medium" for server-side retraining pipeline.
  • Graceful error handling: all exceptions return a valid result with
    needs_review=True and a populated .error field.
"""

import whisper
import numpy as np
from dataclasses import dataclass, field
from typing import Optional, List


# Transcripts below this confidence score are flagged for manual review
# before entering the risk-scoring pipeline.
CONFIDENCE_THRESHOLD = 0.55


@dataclass
class TranscriptionResult:
    text: str
    confidence: float           # 0.0 (very uncertain) → 1.0 (very confident)
    language: str               # ISO-639-1 code detected by Whisper e.g. "hi"
    language_probability: float # Whisper's own language-detection confidence
    needs_review: bool          # True when confidence < CONFIDENCE_THRESHOLD
    segments: List[dict] = field(default_factory=list)
    error: Optional[str] = None


def transcribe_audio(
    audio_path: str,
    model_size: str = "small",
    language: Optional[str] = None,
) -> TranscriptionResult:
    """
    Transcribe an audio file and return a structured result with confidence.

    Args:
        audio_path : Path to .mp3 / .wav / .m4a audio file.
        model_size : Whisper model variant.
                     "tiny"   — fastest, lowest accuracy (on-device Android)
                     "small"  — balanced (default, server pipeline)
                     "medium" — higher accuracy, higher RAM requirement
        language   : ISO-639-1 code to force transcription language, e.g.
                     "hi" for Hindi.  Leave None to auto-detect — recommended
                     for field use where language may vary by region.

    Returns:
        TranscriptionResult.  Always check .needs_review before using .text
        downstream.  If .error is set, transcription failed entirely.

    Confidence mapping:
        Whisper avg_logprob is typically in [-2, 0].
        We map linearly: confidence = clamp((logprob + 2) / 2, 0, 1)
        Good transcription  avg_logprob ≈ -0.3  →  confidence ≈ 0.85
        Poor transcription  avg_logprob ≈ -1.5  →  confidence ≈ 0.25
    """
    try:
        model = whisper.load_model(model_size)

        result = model.transcribe(
            audio_path,
            language=language,        # None triggers Whisper's auto-detection
            verbose=False,
            word_timestamps=False,    # save memory on low-end devices
        )

        # ── Confidence ──────────────────────────────────────────────────────
        segments = result.get("segments", [])
        if segments:
            avg_logprob = float(np.mean([s["avg_logprob"] for s in segments]))
            confidence = float(np.clip((avg_logprob + 2.0) / 2.0, 0.0, 1.0))
        else:
            confidence = 0.0   # empty / silent audio

        # ── Language ────────────────────────────────────────────────────────
        detected_lang = result.get("language", "unknown")
        lang_prob = float(result.get("language_probability", 1.0 if language else 0.0))

        text = result["text"].strip()
        needs_review = confidence < CONFIDENCE_THRESHOLD or not text

        return TranscriptionResult(
            text=text,
            confidence=round(confidence, 3),
            language=detected_lang,
            language_probability=round(lang_prob, 3),
            needs_review=needs_review,
            segments=segments,
        )

    except FileNotFoundError:
        return TranscriptionResult(
            text="", confidence=0.0, language="unknown",
            language_probability=0.0, needs_review=True,
            error=f"Audio file not found: {audio_path}",
        )
    except Exception as exc:
        return TranscriptionResult(
            text="", confidence=0.0, language="unknown",
            language_probability=0.0, needs_review=True,
            error=str(exc),
        )
