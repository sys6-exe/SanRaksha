"""Evaluate transcript-to-vitals extraction against a labeled fixture set."""

from __future__ import annotations

import argparse
import json
import sys
from collections import defaultdict
from pathlib import Path
from typing import Any

NLP_ROOT = Path(__file__).resolve().parents[1]
if str(NLP_ROOT) not in sys.path:
    sys.path.insert(0, str(NLP_ROOT))

from src.parser import CLINICAL_RANGES, extract_health_data  # noqa: E402

FIELDS = tuple(CLINICAL_RANGES)


def values_equal(actual: Any, expected: Any) -> bool:
    if actual is None or expected is None:
        return actual is expected
    return abs(float(actual) - float(expected)) < 1e-9


def expected_confidence(field: str, value: Any) -> str:
    if value is None:
        return "missing"
    low, high = CLINICAL_RANGES[field]
    return "ok" if low <= float(value) <= high else "uncertain"


def safe_div(numerator: int, denominator: int) -> float:
    return numerator / denominator if denominator else 0.0


def evaluate(cases: list[dict[str, Any]]) -> dict[str, Any]:
    tp = fp = fn = tn = 0
    exact_records = confidence_correct = review_correct = 0
    populated_expected = hallucination_opportunities = hallucinations = 0
    language_stats: dict[str, dict[str, int]] = defaultdict(
        lambda: {"correct": 0, "slots": 0, "exact_records": 0, "cases": 0}
    )
    failures: list[dict[str, Any]] = []

    for case in cases:
        result = extract_health_data(case["transcript"])
        expected = case["expected"]
        record_exact = True
        case_failures = []

        expected_uncertain = []
        expected_missing = []
        for field in FIELDS:
            actual_value = result.fields[field]
            expected_value = expected[field]
            correct = values_equal(actual_value, expected_value)
            record_exact &= correct

            bucket = language_stats[case["language"]]
            bucket["correct"] += int(correct)
            bucket["slots"] += 1

            if expected_value is None:
                hallucination_opportunities += 1
                if actual_value is None:
                    tn += 1
                else:
                    fp += 1
                    hallucinations += 1
            else:
                populated_expected += 1
                if correct:
                    tp += 1
                else:
                    fn += 1
                    if actual_value is not None:
                        fp += 1

            wanted_confidence = expected_confidence(field, expected_value)
            confidence_correct += int(result.confidence[field] == wanted_confidence)
            if wanted_confidence == "uncertain":
                expected_uncertain.append(field)
            elif wanted_confidence == "missing":
                expected_missing.append(field)

            if not correct:
                case_failures.append(
                    {"field": field, "expected": expected_value, "actual": actual_value}
                )

        expected_review = bool(expected_uncertain) or len(expected_missing) > 2
        review_correct += int(result.needs_review == expected_review)
        exact_records += int(record_exact)
        language_stats[case["language"]]["exact_records"] += int(record_exact)
        language_stats[case["language"]]["cases"] += 1
        if case_failures:
            failures.append({"id": case["id"], "differences": case_failures})

    total_slots = len(cases) * len(FIELDS)
    precision = safe_div(tp, tp + fp)
    recall = safe_div(tp, tp + fn)
    f1 = safe_div(2 * precision * recall, precision + recall)

    by_language = {}
    for language, counts in sorted(language_stats.items()):
        by_language[language] = {
            "cases": counts["cases"],
            "field_value_accuracy": safe_div(counts["correct"], counts["slots"]),
            "exact_record_accuracy": safe_div(counts["exact_records"], counts["cases"]),
        }

    return {
        "cases": len(cases),
        "fields_per_case": len(FIELDS),
        "field_slots": total_slots,
        "populated_ground_truth_fields": populated_expected,
        "field_value_accuracy": safe_div(tp + tn, total_slots),
        "exact_record_accuracy": safe_div(exact_records, len(cases)),
        "extraction_precision": precision,
        "extraction_recall": recall,
        "extraction_f1": f1,
        "hallucination_rate": safe_div(hallucinations, hallucination_opportunities),
        "confidence_label_accuracy": safe_div(confidence_correct, total_slots),
        "review_flag_accuracy": safe_div(review_correct, len(cases)),
        "slot_counts": {"tp": tp, "fp": fp, "fn": fn, "tn": tn},
        "by_language": by_language,
        "failures": failures,
    }


def print_report(metrics: dict[str, Any]) -> None:
    percent_keys = (
        "field_value_accuracy",
        "exact_record_accuracy",
        "extraction_precision",
        "extraction_recall",
        "extraction_f1",
        "hallucination_rate",
        "confidence_label_accuracy",
        "review_flag_accuracy",
    )
    print(f"Cases: {metrics['cases']}")
    print(f"Field slots: {metrics['field_slots']}")
    print(f"Populated ground-truth fields: {metrics['populated_ground_truth_fields']}")
    for key in percent_keys:
        print(f"{key}: {metrics[key] * 100:.2f}%")
    print(f"Slot counts: {metrics['slot_counts']}")
    for language, values in metrics["by_language"].items():
        print(
            f"  {language}: n={values['cases']}, "
            f"field accuracy={values['field_value_accuracy'] * 100:.2f}%, "
            f"exact records={values['exact_record_accuracy'] * 100:.2f}%"
        )
    print(f"Failed records: {len(metrics['failures'])}")
    for failure in metrics["failures"]:
        differences = ", ".join(
            f"{item['field']} expected={item['expected']} actual={item['actual']}"
            for item in failure["differences"]
        )
        print(f"  {failure['id']}: {differences}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--benchmark",
        type=Path,
        default=Path(__file__).with_name("benchmark.json"),
    )
    parser.add_argument("--json-output", type=Path)
    args = parser.parse_args()

    cases = json.loads(args.benchmark.read_text(encoding="utf-8"))
    metrics = evaluate(cases)
    print_report(metrics)
    if args.json_output:
        args.json_output.parent.mkdir(parents=True, exist_ok=True)
        args.json_output.write_text(json.dumps(metrics, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
