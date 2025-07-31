# SanRaksha PHC Dashboard

## 1. Overview

The **SanRaksha PHC Dashboard** is a specialized data visualization and health monitoring interface developed with Streamlit. It is engineered to process and display maternal health risk assessments from across India, transforming raw data into actionable intelligence for Primary Health Centre (PHC) staff, community health workers, NGOs, and public health researchers.

The dashboard serves as the central visualization component of the SanRaksha ecosystem, designed for clarity, performance, and ease of use in operational environments.

---

## 2. Core Objectives

The primary goal of the dashboard is to facilitate effective, real-time monitoring of high-risk pregnancy cases. This is achieved through four key functional areas:

-   **Risk Stratification:** Clear visualization of patient risk levels as determined by backend analytical models.
-   **Statistical Analysis:** Aggregation and display of vital health metrics, including Blood Pressure (BP), Blood Sugar, and Body Mass Index (BMI).
-   **Geospatial Intelligence:** Real-time mapping of case data to identify high-risk clusters and geographical trends.
-   **Operational Feedback Loop:** An integrated system for field workers and users to report operational issues or submit feedback directly.

---

## 3. Technology Stack

The dashboard is built using a curated stack of technologies selected for rapid development, robust data processing, and interactive data visualization.

| Component         | Technology                      |
| ----------------- | ------------------------------- |
| **Frontend UI** | Streamlit                       |
| **Backend Engine** | Python 3.x                      |
| **Data Processing** | Pandas                          |
| **Mapping** | Folium & Streamlit-Folium       |
| **Charts & Plots** | Plotly, Streamlit Native Charts |
| **Data Source** | CSV (`risk_cases.csv`)          |

---

## 4. System Features

The dashboard's features are organized into logical modules accessible through a tab-based interface.

#### 4.1. Real-Time Health Summary

This section provides a top-level, at-a-glance view of the most critical operational metrics.

-   **High-Risk Cases:** A live count of all patients currently classified as high-risk.
-   **Low-Risk Cases:** A live count of all patients classified as low-risk.
-   **Total Checkups:** A cumulative total of all assessments logged in the system.

#### 4.2. State-Wise Analytics

This module offers an interactive interface for detailed, region-specific analysis.

-   **State Filter:** A dropdown menu allows users to filter the entire dataset for a specific state or view aggregate national data.
-   **Detailed Data Table:** Presents a granular breakdown for the selected region, including:
    -   High, Low, and Total case counts.
    -   Mean Systolic/Diastolic Blood Pressure.
    -   Minimum and Maximum recorded Blood Sugar and BMI values.
-   **Comparative Bar Chart:** Renders a visual comparison of high-risk versus low-risk case volumes for the selected states.

#### 4.3. National Risk Map

A geospatial visualization plots all recorded cases across the country, providing insights into the geographical distribution of risk.

-   **Color-Coded Markers:** High-risk cases are marked in red and low-risk cases in green for immediate visual differentiation.
-   **Interactive Popups:** Clicking on a map marker reveals the state and risk level for that specific data point.
-   **Optimized Map Theme:** The map utilizes the `CartoDB dark_matter` theme for enhanced visual clarity and consistency with the dashboard's design.

#### 4.4. Feedback and Reporting Panel

An integrated form for seamless feedback collection from field operatives.

-   **Accessible Form:** Located in the application sidebar, allowing users to submit feedback without interrupting their workflow.
-   **Local File Logging:** All submissions are automatically timestamped and appended to a local text file (`reports/complaints.txt`) for administrative review.
-   **Submission Confirmation:** The system provides a success message to the user upon a successful submission.

#### 4.5. Data Export

Functionality to export the current data view for offline analysis or reporting.

-   **CSV Export:** A download button allows users to generate and save a CSV file of the data currently displayed in the State-Wise Analytics table.
-   **Utility:** This feature is essential for PHC workers needing to compile reports, maintain offline records, or integrate data with other local systems.

---

## 5. Project File Structure

The project is organized into a standardized directory structure for maintainability.

```
SanRaksha/
├── app.py                     # Main Streamlit application script
├── .gitignore/data/
│              └── risk_cases.csv    # Primary health risk data file
├── reports/
│   └── complaints.txt         # Log file for user-submitted feedback
├── requirements.txt           # Python package dependencies
└── README.md                  # Project documentation
```

---

## 6. Input Data Schema (`data/risk_cases.csv`)

The dashboard ingests a CSV file with a mandatory structure. Each row corresponds to a single patient assessment.

**Required Columns:**
`id,risk_level,state,latitude,longitude,timestamp,systolic_bp,diastolic_bp,blood_sugar,bmi`

**Example Row:**
```csv
1,high,Uttar Pradesh,26.8467,80.9462,2023-05-20T09:00:00,150,95,180,32.1
```

---

## 7. Local Deployment

To run the dashboard in a local environment, follow these steps:

1.  **Clone the Repository:** Obtain the source code and navigate to the root directory.
2.  **Install Dependencies:** Install all required Python packages using pip.
    ```bash
    pip install -r requirements.txt
    ```
3.  **Run the Application:** Execute the main script using Streamlit.
    ```bash
    streamlit run app.py
    ```
The application will be accessible via a local URL provided in the terminal.

---

## 8. Future Enhancements

The application is designed for extensibility. Potential future modules include:

-   **Authenticated Logins:** A secure login system for different user roles (e.g., Worker, Doctor, Administrator).
-   **Automated Alerting:** System-level notifications for when patient data exceeds critical clinical thresholds.
-   **Advanced Analytics:** Integration of more complex statistical models and time-series analysis.

---

