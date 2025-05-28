# SanRaksha

## Issue
How might we leverage AI to revolutionise healthcare delivery, bridging critical gaps in accessibility, affordability, and quality while ensuring inclusive care across diverse geographic and socioeconomic barriers?

<h2>Main Challenges in Maternal Health</h2> <br>

- 33% of children under 5 are underweight, and 60% suffer from anaemia <br>
- Delayed detection of high-risk pregnancies leads to complications<br>
- Dietary recommendations fail to consider locally available, culturally accepted foods, limiting adoption<br>
- Doctor-patient ratio of ~1:1456 and ~1:25,000 in rural areas<br>
- Delayed diagnosis and treatment leading to preventable complications<br>
- Rural doctors lack instant access to best practices and on-time government guidelines<br>


<h2>What we aim to bring to the table</h2><br>
-Early detection of high-risk pregnancies based on the health of the mother<br>
-Support in data collection and data management in terms of patient history<br>
-Shorter timelines in preliminary diagnostic and administrative tasks<br>
-Increased doctor bandwidth to serve more patients<br>


<h2>Our Solution</h2>

A machine learning–powered API to predict **maternal health risk** (Low / High) based on physiological and medical input data. Built using **FastAPI** and trained on a clinical dataset with missing value indicators and robust preprocessing.

## 🧠 Model Details

- **Algorithm**: XGBoost Classifier
- **Input features**:
  - Age
  - Systolic Blood Pressure
  - Diastolic Pressure
  - Blood Sugar (BS)
  - Body Temperature
  - BMI
  - Previous Complications
  - Preexisting Diabetes
  - Gestational Diabetes
  - Mental Health Condition
  - Heart Rate
- **Missingness Handling**:
  - Missing values are replaced with `-999`
  - Binary `_missing` flags are appended for each missing feature

---

## 🛠️ Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/sys6-exe/SanRaksha.git
cd SanRaksha
```
### 2. Create a virtual environment
```bash
python -m venv venv
source venv/bin/activate   # On Windows: venv\Scripts\activate
```
### 3. Install dependencies
```bash
pip install -r requirements.txt
```
### 4. Run the API
```bash
uvicorn app:app --reload
```


## Use Cases

- Rural Healthworkers, including ASHA workers
- Public Health Centres
- NGOs
- Regional Researchers
