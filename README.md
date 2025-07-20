<p align="center">
  <img src="https://github.com/sys6-exe/SanRaksha/blob/main/assets/Banner.png?raw=true" alt="SanRaksha Banner" width="100%" height = "300px" />
</p>

# 🩺 SanRaksha: A Maternal Health Risk Assessment Ecosystem

**SanRaksha** is an open‑source Mission for Optimal Motherhood, AI‑driven platform that helps **ASHA workers, ANMs, and PHC/CHC staff** detect and track **high‑risk pregnancies** in rural India.  
It works fully offline on **low‑cost Android phones** and syncs when connectivity returns — ensuring healthcare continuity even in no-network zones. Loading with a dashboard allowing PHC staff to track block-wise risk and complaints

---

## 🚀 Key Features

| Category         | Feature                                              | Why It Matters |
|------------------|------------------------------------------------------|----------------|
| **Offline‑First** | Risk-scoring runs on device, no internet needed      | ASHA workers often work in low-connectivity regions |
| **Hybrid Models**| Offline: Compact neural net<br>Online: XGBoost      | Combines speed + accuracy + explainability |
| **Layered Risk Logic** | Two logistic-regression layers before the neural net | Transparent factor-wise scoring for ASHA workers |
| **Modular & Extensible** | FL-ready via Flower templates | Future-proof, privacy-preserving updates |
| **NLP Data Entry** | Extracts vitals like BP, HR, Sugar from ASHA voice notes | Speeds up data entry for low-literacy field workers |
| **PHC Dashboard** | Streamlit-based geolocation dashboard with map, pie charts, reports, and analytics | Allows PHC staff to track block-wise risk and complaints |

---

## 🧬 Input Features & Model Interpretation

| Feature Abbr. | Full Name & Unit                         | Typical Range |
|---------------|-------------------------------------------|----------------|
| BMI           | Body Mass Index (kg/m²)                   | 16 – 40        |
| BS            | Random Blood Sugar (mmol/L)               | 4–15           |
| HR            | Heart Rate (beats/min)                    | 60–140         |
| BT            | Body Temperature (°F)                     | 96–100         |
| PrevComp      | Previous Pregnancy Complications (binary) | 0 / 1          |
| PreDM         | Pre‑existing Diabetes (binary)            | 0 / 1          |
| GDM           | Gestational Diabetes (binary)             | 0 / 1          |
| MentHlth      | Mental‑Health Concerns (binary)           | 0 / 1          |

---

## 🔢 Risk‑Scoring Pipeline

**LR‑A (Obstetric History Layer)**  
Inputs: `PrevComp`, `PreDM`, `GDM`, `MentHlth` → Output: **Score A**

**LR‑B (Vitals Layer)**  
Inputs: `HR`, `BT`, `BS`, `BMI` → Output: **Score B**

**LR‑C (Meta Layer)**  
Inputs: `Score A`, `Score B` → Output: **Final Risk Score (0–1)**

**Offline Neural Net**  
- `Input(2)` → Dense(16, ReLU) → Dense(8, ReLU) → Dense(1, Sigmoid)
- Refines Score using non-linear field data patterns

**Online Model (XGBoost)**  
- Same inputs, synced when internet returns  
- Used for dashboard analytics & deeper predictions

---

## 🧠 NLP + Voice-Based Data Entry (Offline Whisper)

- ASHA workers **speak vitals** ("BP 130/90, sugar 120")
- A **lightweight Whisper model** (converted via OpenAI Whisper) runs locally
- A regex-based parser extracts and autofills:
- BP, Sugar, HR, Temp, BMI
- Designed for **fully offline Android** execution with Whisper inference

Powered by OpenAI Whisper – an automatic speech recognition system for multilingual voice-to-text transcription.

---

## 🖥️ PHC Dashboard

Built with **Streamlit**, the dashboard provides:

- **Geolocation map** of risk cases (Folium + Plotly)
- **Risk pie charts**, bar graphs, daily checkup stats
- **Complaint panel** (via form + auto CSV save)
- Downloadable reports
- India-focused view with filtering by state

> Perfect for PHC/CHC staff to monitor local maternal risk trends.

---

## 🎯 Impact & Use Cases

- Early referrals for rural pregnant women
- ASHA/ANM workers receive **real-time alerts**
- PHC/CHC staff track **block-wise and daily trends**
- NGOs and researchers access open-data reports

---

## 👥 Core Contributors

| Name | Role |
|------|------|
| **Arindol Sarkar** | ML Pipeline + Risk Scoring Models |
| **Atul Gadkoti** | Android App + Offline Sync |
| **Ishita Singh** | Web Dashboard + Geolocation Visualization + NLP Integration |

We welcome collaborations in **clinical validation, federated learning, and dataset curation**.

---

## 📦 Tech Stack

| Layer      | Technology |
|------------|------------|
| **App**    | Kotlin + TFLite |
| **Server** | FastAPI |
| **ML**     | TensorFlow, scikit-learn, XGBoost |
| **Dashboard** | Streamlit, Plotly, Folium |
| **FL-ready** | Flower (client + server templates, in progress) |

---

## 📄 License

Apache License 2.0 — see `LICENSE` file  
Attribution details available in `NOTICE`

---

## ⚙️ Quick Start

```bash
# Clone the repository
git clone https://github.com/sys6-exe/SanRaksha
cd sanraksha

# Run backend API server
cd server && uvicorn main:app --reload

# Run NLP + ML models (Jupyter)
cd ml_models && jupyter notebook

# Launch Dashboard
streamlit run dashboard/app.py
```
---


## 🤝 Want to Collaborate?
Open an issue, start a discussion, or email 24cd3007@rgipt.ac.in
We’re especially keen on:

- Clinical validation partnerships

- Rural deployment pilots (PHC/CHC, NGOs)

- Dataset sharing under open‑data agreements

Let’s make maternal healthcare safer and more accessible. 🚑

