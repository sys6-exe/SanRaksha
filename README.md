# 🩺 **SanRaksha: A Maternal Health Risk Assessment Ecosystem**

SanRaksha is an open‑source, AI‑driven platform that helps **ASHA workers**, **ANMs**, and **PHC/CHC staff** detect and track high‑risk pregnancies in rural India **Safeguarding Moms** .  
It works fully **offline** on low‑cost Android phones and **syncs** when connectivity returns.

---

## 🚀 Key Features

| Category | Feature | Why It Matters |
|----------|---------|----------------|
| **Offline‑First** | All core risk‑score calculations run on device (no internet needed). | ASHA workers often have patchy or no connectivity. |
| **Hybrid Models** | *Offline* → compact neural net<br>*Online* → XGBoost on the server. | Balances speed + accuracy + explainability. |
| **Layered Risk Logic** | Two logistic‑regression (LR) layers aggregate clinically linked factors before the neural net. | Makes individual risk factors transparent to health workers. |
| **Modular & Extensible** | Flower templates ready for future federated learning. | Keeps patient data private while enabling model updates. |

---

## 🧬 **Input Features & Model Interpretation**

| Feature Abbr. | Full Name & Unit | Typical Range |
|---------------|------------------|---------------|
| **BMI** | Body Mass Index (kg/m²) | 16 – 40 |
| **BS** | Random Blood Sugar (mmol/L) | 4-15 |
| **HR** | Heart Rate (beats/min) | 60 – 140 |
| **BT** | Body Temperature (°F) | 96-100 |
| **PrevComp** | Previous Pregnancy Complications (binary) | 0/1 |
| **PreDM** | Pre‑existing Diabetes (binary) | 0/1 |
| **GDM** | Gestational Diabetes in current pregnancy (binary) | 0/1 |
| **MentHlth** | Documented Mental‑Health Concerns (binary) | 0/1 |

### 🔢 **Risk‑Scoring Pipeline**

1. **LR‑A (Obstetric History Layer)**  
   - Inputs: `PrevComp`, `PreDM`, `GDM`, `MentHlth`  
   - Output: *Score A* = probability of complications due to history.

2. **LR‑B (Vitals & Anthropometry Layer)**  
   - Inputs: `HR`, `BT`, `BS`, `BMI` (high/low flags)  
   - Output: *Score B* = probability of complications due to current vitals.

3. **LR‑C (Meta Layer)**  
   - Inputs: *Score A*, *Score B*  
   - Output: *Final Risk Score* (0–1).

4. **Offline Neural Net**  
   - Architecture: `Input(2) → Dense(16, ReLU) → Dense(8, ReLU) → Dense(1, Sigmoid)`  
   - Purpose: Refines *Final Risk Score* using non‑linear interactions learned from field data.  
   - Output: `risk_flag` (High / Low) shown to ASHA.

5. **Online Model (optional)**  
   - Same inputs but aggregated on the server.  
   - Algorithm: **XGBoost** .  
   - Provides higher‑granularity risk probabilities for researchers & PHC doctors when data syncs.

---

## 🎯 Impact & Use‑Cases

* Pregnant women in remote villages receive **earlier referrals**.  
* ASHA / ANM workers get **actionable alerts during home visits**.  
* PHC/CHC staff monitor **block‑level trends** via the dashboard.  
* NGOs & researchers can evaluate **maternal‑mortality interventions** at scale.

---

## 👥 Core Contributors

* **Arindol Sarkar** – Machine Learning & Risk‑Scoring Pipeline  
* **Atul Gadkoti** – Android App, Offline Storage & Sync  
* **Ishita Singh** – Web Dashboard, Geolocation‑Driven Analytics  

We welcome collaborators in **obstetrics, public health, dataset curation, and clinical validation**.

---

## 📦 Tech Stack

| Layer | Tech |
|-------|------|
| App | Kotlin + TFLite |
| Server | FastAPI  |
| ML | TensorFlow, scikit‑learn, XGBoost |
| FL Ready | Flower (client & server templates)[To be implemented] |

---

## 📄 License
**Apache License 2.0** – see [`LICENSE`](./LICENSE).  
Earlier releases remain under MIT.

Attribution details are in [`NOTICE`](./NOTICE).

---

## ⚙️ Quick Start

### Clone the repository
```bash
git clone https://github.com/<your‑org>/sanraksha.git
cd sanraksha
```
### Run local API server
```bash
cd server && uvicorn main:app --reload
```
### Train / test ML (Jupyter)
```bash
cd ml_models && jupyter notebook
```

## 🤝 Want to Collaborate?
Open an issue, start a discussion, or email 24cd3007@rgipt.ac.in
We’re especially keen on:

- Clinical validation partnerships

- Rural deployment pilots (PHC/CHC, NGOs)

- Dataset sharing under open‑data agreements

Let’s make maternal healthcare safer and more accessible. 🚑

