import re
from word2number import w2n

unit_map = {
    "shoonya": 0, "ek": 1, "do": 2, "teen": 3, "chaar": 4, "paanch": 5,
    "cheh": 6, "saat": 7, "aath": 8, "nau": 9, "das": 10
}

teen_map = {
    "gyaarah": 11, "baarah": 12, "terah": 13, "chaudah": 14, "pandrah": 15,
    "solah": 16, "satrah": 17, "atharah": 18, "unnis": 19
}

tens_map = {
    "bees": 20, "tees": 30, "chaalis": 40, "pachaas": 50, "saath": 60,
    "sattar": 70, "assi": 80, "nabbe": 90
}

hundred_map = {"sau": 100}

def hindi_phrase_to_number(phrase):
    words = phrase.lower().strip().split()
    total = 0
    current = 0

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
        else:
            continue

    total += current
    return total if total else None

def words_to_number(text):
    try:
        return w2n.word_to_num(text)
    except:
        return hindi_phrase_to_number(text)

def extract_health_data(text: str) -> dict:
    text = text.lower()
    data = {}

    replacements = {
        "upar wala": "systolic",
        "upar ka": "systolic",
        "first reading": "systolic",
        "upar": "systolic",
        
        "neeche wala": "diastolic",
        "neeche ka": "diastolic",
        "second reading": "diastolic",
        "neeche": "diastolic",

        "rakt chaap": "blood pressure",
        "bp": "blood pressure",
        "blood pressure": "blood pressure",

        "dil ki dhadkan": "heart rate",
        "dhadkan": "heart rate",
        "nabz": "heart rate",
        "pulse": "heart rate",
        "heartbeat": "heart rate",

        "bukhar": "body temperature",
        "tapmaan": "body temperature",
        "taapmaan": "body temperature",
        "tapman": "body temperature",
        "temperature": "body temperature",

        "cheeni": "blood sugar",
        "sugar": "blood sugar",
        "sugar level": "blood sugar",
        "blood sugar": "blood sugar",
        "sugar test": "blood sugar",

        "weight": "bmi",
        "weight ratio": "bmi",
        "height weight": "bmi",
        "bmi": "bmi"
    }


    for hin, eng in replacements.items():
        text = text.replace(hin, eng)

    text = text.replace("over", "/").replace("by", "/")

    systolic, diastolic = None, None
    bp_match = re.search(r"(blood pressure)[^\d]*(\d{2,3})\s*/\s*(\d{2,3})", text)
    if not bp_match:
        word_bp_match = re.search(r"(blood pressure)[^\d]*([a-z\s\-]+)\s*/\s*([a-z\s\-]+)", text)
        if word_bp_match:
            systolic = words_to_number(word_bp_match.group(2).strip())
            diastolic = words_to_number(word_bp_match.group(3).strip())
    else:
        systolic, diastolic = bp_match.group(2), bp_match.group(3)

    if not systolic:
        match = re.search(r"(systolic|upper)[^\d]*(\d{2,3}|[a-z\s\-]+)", text)
        if match:
            systolic = match.group(2)
            if systolic.isalpha():
                systolic = words_to_number(systolic)

    if not diastolic:
        match = re.search(r"(diastolic|lower)[^\d]*(\d{2,3}|[a-z\s\-]+)", text)
        if match:
            diastolic = match.group(2)
            if diastolic.isalpha():
                diastolic = words_to_number(diastolic)

    def extract_pattern(label, pattern):
        match = re.search(pattern, text)
        if match:
            value = match.group(1)
            if not re.match(r"^\d", value):
                value = words_to_number(value.strip())
            try:
                return float(value) if '.' in str(value) else int(value)
            except:
                return value
        return None

    data["systolic_bp"] = int(systolic) if systolic else None
    data["diastolic_bp"] = int(diastolic) if diastolic else None
    data["blood_sugar"] = extract_pattern("blood_sugar", r"blood sugar[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)")
    data["body_temp"] = extract_pattern("body_temp", r"(?:temp|temperature)[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)")
    data["bmi"] = extract_pattern("bmi", r"bmi[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)")
    data["heart_rate"] = extract_pattern("heart_rate", r"(?:heart rate|pulse)[^\d]*(\d+(?:\.\d+)?|[a-z\s\-]+)")

    return data