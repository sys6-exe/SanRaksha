import whisper

def transcribe_audio(mp3_path: str) -> str:
    model = whisper.load_model("small")
    result = model.transcribe(mp3_path,language="en")
    return result["text"]

