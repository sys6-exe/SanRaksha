# SanRaksha NLP Engine (Offline)

This project uses OpenAI Whisper for extracting structured health data from ASHA worker audio in English, Hindi and Hinglish

## Setup

1. Clone the repo
2. Create a virtual environment 
3. Install dependencies:

```bash
pip install -r requirements.txt

python run_pipeline.py --audio path/to/file.mp3 --model small

