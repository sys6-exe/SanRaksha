import os
import json

from src.transcriber import transcribe_audio
from src.parser import extract_health_data

AUDIO_PATH = ""

if __name__ == "__main__":
    print("[INFO] Transcribing...")
    transcript = transcribe_audio(AUDIO_PATH)
    print("\nTranscribed Text:\n", transcript)

    print("\n[INFO] Extracting structured health data...")
    structured_data = extract_health_data(transcript)

    print("\nExtracted Parameters:")
    for key, value in structured_data.items():
        print(f"{key.replace('_', ' ').title()}: {value}")

    os.makedirs("output", exist_ok=True)

    with open("output/transcript.txt", "w", encoding="utf-8") as f:
        f.write(transcript)

    with open("output/parsed.json", "w", encoding="utf-8") as f:
        json.dump(structured_data, f, indent=2, ensure_ascii=False)
