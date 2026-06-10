# SanRaksha Evaluation and Resume Metrics

This document separates **reproducible measurements**, **saved notebook results**, and
**code-verifiable product scope**. Do not combine them into a single "pipeline
accuracy" number: the repository does not contain a labeled audio corpus that would
allow Whisper transcription and structured extraction to be evaluated end to end.

## 1. NLP structured-extraction benchmark (reproducible)

Run:

```bash
python nlp/evaluation/evaluate_parser.py
```

The fixture contains 30 manually labeled transcripts, 240 field slots, and 104
populated ground-truth values. It covers English, Hinglish, transliterated Hindi,
partial records, out-of-range values, negative/no-vital statements, numeric values,
and spoken-number values.

| Metric | Result |
|---|---:|
| Field-value accuracy, including correctly missing fields | **97.08%** (233/240) |
| Exact-record accuracy, all 8 fields correct | **80.00%** (24/30) |
| Extraction precision | **100.00%** (97/97) |
| Extraction recall | **93.27%** (97/104) |
| Extraction F1 | **96.52%** |
| Hallucination rate on absent fields | **0.00%** (0/136) |
| Confidence-label accuracy | **97.08%** (233/240) |
| Review-flag accuracy | **100.00%** (30/30) |

### Slice results

| Transcript slice | Cases | Field-value accuracy | Exact-record accuracy |
|---|---:|---:|---:|
| English (numeric values) | 14 | 100.00% | 100.00% |
| Hinglish (numeric values) | 10 | 100.00% | 100.00% |
| English spoken numbers | 4 | 84.38% | 0.00% |
| Transliterated Hindi numbers | 2 | 87.50% | 0.00% |

The seven false negatives all occur in spoken-number extraction. This is an honest
limitation to fix before claiming robust voice-number support. The benchmark tests
**transcript parsing only**; it does not measure word error rate, audio robustness, or
end-to-end ASR-plus-parser accuracy.

## 2. Maternal-risk model results (saved notebook evidence)

These values are recorded in executed notebook outputs. The repository does not
include the source datasets/splits, so they can be audited in the notebooks but cannot
currently be independently reproduced from a clean checkout.

### Offline/on-device binary model

The saved test output contains 296 examples and confusion matrix `[[173, 8], [3,
112]]`. Recalculation from that matrix gives:

| Metric | Result |
|---|---:|
| Accuracy | **96.28%** (285/296) |
| High-risk precision | **93.33%** (112/120) |
| High-risk recall/sensitivity | **97.39%** (112/115) |
| High-risk F1 | **95.32%** |
| Specificity | **95.58%** (173/181) |
| Balanced accuracy | **96.49%** |
| Test errors | **11** (8 false positives, 3 false negatives) |

The notebook prints an AUC of 0.9649, but it passes thresholded class predictions
rather than probability scores to `roc_auc_score`. That number is equivalent to
balanced accuracy for this binary result and should **not** be presented as a
probability-based ROC-AUC.

### Online/dashboard risk model

The saved validation classification report contains 75 examples:

- **97.33% accuracy** (73/75 correct, inferred from the per-class supports/rounded
  report), with **0.97 macro F1**.
- Low-risk class: 0.98 precision, 0.98 recall, and 0.98 F1 (47 examples).
- High-risk class: 0.96 precision, 0.96 recall, and 0.96 F1 (28 examples).

Because only rounded report values and no confusion matrix are saved for this model,
use the 97% figures rather than adding more decimal precision.

## 3. Dashboard numeric scope (not clinical-impact metrics)

The checked-in dashboard supports:

- **2 roles**: ASHA worker and doctor.
- Up to **4 views**: Overview, State Analysis, Trends, and doctor-only Pending Alerts.
- **3 live summary KPIs**: high-risk cases, low-risk cases, and total checkups.
- **60-second data-cache TTL** for dashboard refreshes.
- A **3-second backend timeout** before offline fallback.
- Analysis across **6 displayed state-level measures**: high-risk count, low-risk count,
  total cases, average BP, sugar range, and BMI range.

No `risk_cases.csv`, usage telemetry, latency benchmark, user study, or deployment log
is committed, so the repository cannot substantiate claims such as number of patients
served, percentage reduction in referral time, dashboard speedup, or geographic
coverage. Add those measurements before using impact language.

## 4. Resume-ready wording

Use wording that preserves the scope of each measurement:

> Built an offline maternal-health NLP parser and benchmarked transcript-to-vitals
> extraction on 30 labeled English/Hinglish test utterances (240 field slots), achieving
> 97.1% field accuracy, 100% precision, 93.3% recall, 96.5% F1, and 0% hallucination on
> absent fields; identified spoken-number parsing as the primary error slice.

> Developed an on-device maternal-risk classifier that recorded 96.3% accuracy, 97.4%
> high-risk recall, and 95.3% high-risk F1 on a saved 296-example test split.

> Engineered a Streamlit PHC monitoring dashboard with 2 role-based access levels, 4
> analytical/alert views, 3 live risk KPIs, geospatial case mapping, weekly trends, CSV
> reporting, and offline API fallbacks.

If space permits, add “prototype benchmark” or “held-out notebook test split” to avoid
implying external clinical validation.
