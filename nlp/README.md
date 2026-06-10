# SanRaksha NLP Engine (Offline)

This feature uses OpenAI Whisper for extracting structured health data from ASHA worker audio in English, Hindi and Hinglish.

## Setup

1. Clone the repo
2. Create a virtual environment 
3. Install dependencies:
4. Add audio files 

```bash
pip install -r requirements.txt

python run_pipeline.py --audio path/to/file.mp3 --model small
```

## Evaluation

Run the reproducible transcript-parser benchmark from the repository root:

```bash
python nlp/evaluation/evaluate_parser.py
```

The benchmark and metric definitions are in `nlp/evaluation/`. Results and important
scope limitations are documented in [`docs/EVALUATION.md`](../docs/EVALUATION.md).
This benchmark evaluates structured extraction from labeled transcripts; it is not an
end-to-end Whisper/audio evaluation.
