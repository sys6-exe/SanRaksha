"""
run_pipeline.py  ·  SanRaksha NLP Module
==========================================
Changes from original:
  • Uses TranscriptionResult + ParseResult instead of raw strings/dicts.
  • Confidence gate: if transcription confidence < threshold, prints a
    WARNING block showing the raw transcript and asks the user to confirm
    or correct before proceeding.  In a production Android app, this maps
    to the manual-correction screen shown to the ASHA worker.
  • Per-field uncertainty warnings: any field with CONF_UNCERTAIN is
    printed in yellow with the extracted value and valid range, so the
    ASHA worker can spot-check before the data enters the risk model.
  • Missing-field summary: lists fields not found in the audio.
  • output/parsed.json now includes confidence map and review flags.
  • Error handling for transcription failures.
"""

import os
import json

from transcriber import transcribe_audio, CONFIDENCE_THRESHOLD
from parser import extract_health_data, CONF_UNCERTAIN, CONF_MISSING, CLINICAL_RANGES

# ── Colour helpers (ANSI — works in most terminals) ──────────────────────────
RED    = "\033[91m"
YELLOW = "\033[93m"
GREEN  = "\033[92m"
CYAN   = "\033[96m"
RESET  = "\033[0m"
BOLD   = "\033[1m"

AUDIO_PATH = ""   # ← set your audio file path here


def print_section(title: str) -> None:
    print(f"\n{BOLD}{CYAN}{'─'*50}{RESET}")
    print(f"{BOLD}{CYAN} {title}{RESET}")
    print(f"{BOLD}{CYAN}{'─'*50}{RESET}")


def prompt_manual_correction(transcript: str) -> str:
    """
    In CLI mode, prompt the user to accept or correct the transcript.
    In the Android app, this maps to the editable transcript screen
    shown when confidence < CONFIDENCE_THRESHOLD.
    """
    print(f"\n{YELLOW}{BOLD}⚠  LOW CONFIDENCE TRANSCRIPT{RESET}")
    print(f"{YELLOW}The ASR model is uncertain about this transcription.{RESET}")
    print(f"\nRaw transcript:\n  {BOLD}{transcript}{RESET}\n")
    print("Options:")
    print("  [1] Use as-is and continue")
    print("  [2] Enter corrected transcript manually")
    choice = input("\nEnter 1 or 2: ").strip()
    if choice == "2":
        corrected = input("Enter corrected transcript: ").strip()
        return corrected if corrected else transcript
    return transcript


if __name__ == "__main__":

    # ── Step 1: Transcription ────────────────────────────────────────────────
    print_section("Step 1 · Transcription")

    if not AUDIO_PATH:
        print(f"{RED}ERROR: AUDIO_PATH is not set.  "
              f"Edit run_pipeline.py and set AUDIO_PATH.{RESET}")
        exit(1)

    print(f"[INFO] Transcribing: {AUDIO_PATH}")
    result = transcribe_audio(AUDIO_PATH)

    if result.error:
        print(f"{RED}[ERROR] Transcription failed: {result.error}{RESET}")
        exit(1)

    print(f"[INFO] Language detected  : {result.language} "
          f"(p={result.language_probability:.2f})")
    print(f"[INFO] Confidence score   : {result.confidence:.3f}  "
          f"(threshold = {CONFIDENCE_THRESHOLD})")

    # ── Confidence gate ──────────────────────────────────────────────────────
    transcript = result.text
    if result.needs_review:
        transcript = prompt_manual_correction(transcript)
    else:
        print(f"{GREEN}[OK] Confidence above threshold — proceeding automatically.{RESET}")
        print(f"\nTranscribed text:\n  {transcript}")

    # ── Step 2: Parsing ──────────────────────────────────────────────────────
    print_section("Step 2 · Structured Extraction")

    parse = extract_health_data(transcript)
    fields     = parse.fields
    confidence = parse.confidence

    print(f"\n{'Field':<20} {'Value':<15} {'Status'}")
    print("─" * 50)
    for key, value in fields.items():
        conf = confidence[key]
        status_str = (
            f"{GREEN}ok{RESET}"        if conf == "ok"        else
            f"{YELLOW}uncertain{RESET}" if conf == "uncertain" else
            f"{RED}missing{RESET}"
        )
        lo, hi = CLINICAL_RANGES.get(key, (None, None))
        range_str = f"  (valid: {lo}–{hi})" if lo and conf == CONF_UNCERTAIN else ""
        print(f"  {key:<20} {str(value):<15} {status_str}{range_str}")

    # ── Step 3: Review summary ───────────────────────────────────────────────
    if parse.needs_review:
        print_section("Review Required")
        if parse.uncertain_fields:
            print(f"{YELLOW}Fields outside clinical range — verify with ASHA worker:{RESET}")
            for f in parse.uncertain_fields:
                lo, hi = CLINICAL_RANGES.get(f, ("?", "?"))
                print(f"  • {f}: {fields[f]}  (expected {lo}–{hi})")
        if len(parse.missing_fields) > 2:
            print(f"\n{RED}Many fields missing — consider re-recording:{RESET}")
            for f in parse.missing_fields:
                print(f"  • {f}")
    else:
        print(f"\n{GREEN}[OK] All fields extracted within clinical ranges.{RESET}")

    # ── Step 4: Save outputs ─────────────────────────────────────────────────
    print_section("Step 3 · Saving Output")

    os.makedirs("output", exist_ok=True)

    with open("output/transcript.txt", "w", encoding="utf-8") as f:
        f.write(transcript)

    output_data = {
        "transcript": transcript,
        "transcription_confidence": result.confidence,
        "transcription_language": result.language,
        "transcription_needs_review": result.needs_review,
        "fields": fields,
        "field_confidence": confidence,
        "uncertain_fields": parse.uncertain_fields,
        "missing_fields": parse.missing_fields,
        "parse_needs_review": parse.needs_review,
    }

    with open("output/parsed.json", "w", encoding="utf-8") as f:
        json.dump(output_data, f, indent=2, ensure_ascii=False)

    print(f"[INFO] Transcript saved  → output/transcript.txt")
    print(f"[INFO] Parsed data saved → output/parsed.json")
    print(f"\n{'─'*50}")
    print(f"{'REVIEW REQUIRED' if (result.needs_review or parse.needs_review) else 'PIPELINE COMPLETE — READY FOR RISK MODEL'}")
    print(f"{'─'*50}\n")
