
import re
from dataclasses import dataclass, field
from typing import Any, Dict, List, Optional

from word2number import w2n


# ── Clinical validation ranges (NHM / FOGSI guidelines) ─────────────────────
# Values outside these ranges are "uncertain" — not rejected, but flagged.
# Ranges are for adult pregnant women.
CLINICAL_RANGES: Dict[str, tuple] = {
    "systolic_bp":   (70,  180),   # mmHg
    "diastolic_bp":  (40,  120),   # mmHg
    "blood_sugar":   (40,  400),   # mg/dL (random)
    "body_temp":     (95.0, 104.0),# °F
    "bmi":           (13.0, 55.0), # kg/m²
    "heart_rate":    (40,  180),   # bpm
    "hemoglobin":    (4.0, 18.0),  # g/dL
    "spo2":          (70,  100),   # %
}

# Confidence levels for each field
CONF_OK        = "ok"
CONF_UNCERTAIN = "uncertain"   # parsed but outside clinical range
CONF_MISSING   = "missing"     # could not extract


# ── Hindi / regional number maps ──────────────────
unit_map = {
    "shoonya": 0, "ek": 1, "do": 2, "teen": 3, "chaar": 4, "paanch": 5,
    "cheh": 6, "saat": 7, "aath": 8, "nau": 9, "das": 10,
}
teen_map = {
    "gyaarah": 11, "baarah": 12, "terah": 13, "chaudah": 14, "pandrah": 15,
    "solah": 16, "satrah": 17, "atharah": 18, "unnis": 19,
}
tens_map = {
    "bees": 20, "tees": 30, "chaalis": 40, "pachaas": 50, "saath": 60,
    "sattar": 70, "assi": 80, "nabbe": 90,
}
hundred_map = {"sau": 100}


def hindi_phrase_to_number(phrase: str) -> Optional[int]:
    words = phrase.lower().strip().split()
    total, current = 0, 0
    for word in words:
        if word in teen_map:
            current += teen_map[word]
        elif word in unit_map:
            current += unit_map[word]
        elif word in tens_map:
            current += tens_map[word]
        elif word in hundred_map:
            if current == 0:
                current = 1
            current *= 100
            total += current
            current = 0
    total += current
    return total if total else None


def words_to_number(text: str) -> Optional[float]:
    try:
        return w2n.word_to_num(text)
    except Exception:
        return hindi_phrase_to_number(text)


# ── Result dataclass ─────────────────────────────────────────────────────────
@dataclass
class ParseResult:
    fields: Dict[str, Any]              # extracted values (None if missing)
    confidence: Dict[str, str]          # per-field: "ok" | "uncertain" | "missing"
    uncertain_fields: List[str] = field(default_factory=list)
    missing_fields: List[str] = field(default_factory=list)

    @property
    def needs_review(self) -> bool:
        """True when any field is uncertain or more than 2 fields are missing."""
        return bool(self.uncertain_fields) or len(self.missing_fields) > 2

    def flat(self) -> Dict[str, Any]:
        """Return just the values dict — drop-in replacement for old dict return."""
        return self.fields


# ── Main extraction function ─────────────────────────────────────────────────
def extract_health_data(text: str) -> ParseResult:
    """
    Extract structured health vitals from a transcribed string.

    Supports: BP (systolic/diastolic), blood sugar, body temperature, BMI,
    heart rate, hemoglobin, SpO2.

    Returns ParseResult.  Use .flat() for backwards-compatible dict access.
    """
    text = text.lower()

    # ── Synonym normalisation (original mappings preserved + new additions) ──
    replacements = {
        # BP direction helpers
        "upar wala": "systolic",  "upar ka": "systolic",
        "first reading": "systolic", "upar": "systolic",
        "neeche wala": "diastolic", "neeche ka": "diastolic",
        "second reading": "diastolic", "neeche": "diastolic",

        # Blood pressure
        "rakt chaap": "blood pressure", "bp": "blood pressure",
        "blood pressure": "blood pressure",

        # Heart rate
        "dil ki dhadkan": "heart rate", "dhadkan": "heart rate",
        "nabz": "heart rate", "pulse": "heart rate", "heartbeat": "heart rate",

        # Body temperature
        "bukhar": "body temperature", "tapmaan": "body temperature",
        "taapmaan": "body temperature", "tapman": "body temperature",
        "temperature": "body temperature",

        # Blood sugar
        "cheeni": "blood sugar", "sugar": "blood sugar",
        "sugar level": "blood sugar", "blood sugar": "blood sugar",
        "sugar test": "blood sugar",

        # BMI
        "weight": "bmi", "weight ratio": "bmi",
        "height weight": "bmi", "bmi": "bmi",

        # ── NEW: Hemoglobin ──────────────────────────────────────────────
        "khoon ki kami": "hemoglobin", "hemoglobin": "hemoglobin",
        "haemoglobin": "hemoglobin",  "hb": "hemoglobin",
        "hgb": "hemoglobin", "blood count": "hemoglobin",
        "khoon": "hemoglobin",

        # ── NEW: SpO2 / Oxygen saturation ────────────────────────────────
        "oxygen level": "spo2", "oxygen saturation": "spo2",
        "spo2": "spo2", "o2 level": "spo2", "o2": "spo2",
        "pulse oximeter": "spo2", "pran vayu": "spo2",
        "oxygen": "spo2",
    }

    for hin, eng in replacements.items():
        text = text.replace(hin, eng)

    text = text.replace("over", "/").replace("by", "/")

    # ── Helper: validate against clinical range ──────────────────────────────
    def validate(field_name: str, value) -> str:
        if value is None:
            return CONF_MISSING
        lo, hi = CLINICAL_RANGES.get(field_name, (None, None))
        if lo is None:
            return CONF_OK
        try:
            v = float(value)
            return CONF_OK if lo <= v <= hi else CONF_UNCERTAIN
        except (TypeError, ValueError):
            return CONF_UNCERTAIN

    # ── Helper: generic regex extractor (unchanged logic) ───────────────────
    def extract_pattern(pattern: str):
        match = re.search(pattern, text)
        if match:
            value = match.group(1)
            if not re.match(r"^\d", value):
                value = words_to_number(value.strip())
            try:
                return float(value) if "." in str(value) else int(value)
            except (TypeError, ValueError):
                return None
        return None

    # ── Blood pressure (unchanged logic) ────────────────────────────────────
    systolic, diastolic = None, None
    bp_match = re.search(
        r"blood pressure[^\d]*(\d{2,3})\s*/\s*(\d{2,3})", text
    )
    if not bp_match:
        word_bp = re.search(
            r"blood pressure[^\d]*([a-z\s\-]+)\s*/\s*([a-z\s\-]+)", text
        )
        if word_bp:
            systolic  = words_to_number(word_bp.group(1).strip())
            diastolic = words_to_number(word_bp.group(2).strip())
    else:
        systolic, diastolic = bp_match.group(1), bp_match.group(2)

    if not systolic:
        m = re.search(r"(systolic|upper)[^\d]*(\d{2,3}|[a-z\s\-]+)", text)
        if m:
            v = m.group(2)
            systolic = v if v[0].isdigit() else words_to_number(v)

    if not diastolic:
        m = re.search(r"(diastolic|lower)[^\d]*(\d{2,3}|[a-z\s\-]+)", text)
        if m:
            v = m.group(2)
            diastolic = v if v[0].isdigit() else words_to_number(v)

    # ── Extract all fields ───────────────────────────────────────────────────
    extracted = {
        "systolic_bp":  int(systolic)  if systolic  else None,
        "diastolic_bp": int(diastolic) if diastolic else None,
        "blood_sugar":  extract_pattern(
            r"blood sugar[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)"
        ),
        "body_temp":    extract_pattern(
            r"(?:body temperature)[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)"
        ),
        "bmi":          extract_pattern(
            r"bmi[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)"
        ),
        "heart_rate":   extract_pattern(
            r"(?:heart rate)[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)"
        ),
        # ── NEW fields ───────────────────────────────────────────────────
        "hemoglobin":   extract_pattern(
            r"hemoglobin[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)"
        ),
        "spo2":         extract_pattern(
            r"spo2[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)"
        ),
    }

    # ── Build confidence map ─────────────────────────────────────────────────
    confidence = {k: validate(k, v) for k, v in extracted.items()}
    uncertain  = [k for k, c in confidence.items() if c == CONF_UNCERTAIN]
    missing    = [k for k, c in confidence.items() if c == CONF_MISSING]

    return ParseResult(
        fields=extracted,
        confidence=confidence,
        uncertain_fields=uncertain,
        missing_fields=missing,
    )
