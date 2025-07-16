import os
import streamlit as st
import pandas as pd
import folium
from streamlit_folium import folium_static
from collections import Counter
from datetime import datetime
import plotly.express as px

st.set_page_config(page_title="SanRaksha PHC Dashboard", layout="wide")

df = pd.read_csv("") # dataset path

st.markdown("""
<style>
/* Set full app background gradient */
.stApp {
    background: linear-gradient(135deg, #0f2027, #203a43, #2c5364);
    background-attachment: fixed;
    color: white;
}

</style>
""", unsafe_allow_html=True)

st.markdown(
    """
    <style>
        .top-container {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }
        .logo {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            margin-right: 15px;
        }
        .title-text {
            font-size: 30px;
            font-weight: bold;
            color: white;
            margin: 0;
            padding: 0;
        }
        
    </style>
    
    <div class="top-container">
        <img class="logo" src="https://raw.githubusercontent.com/sys6-exe/SanRaksha/main/assets/Sanraksha.png" />
        <p class="title-text"><h1>SanRaksha PHC Dashboard</h1></p>
    </div>
    """,
    unsafe_allow_html=True
   
)

tab1, tab2 = st.tabs(["Overview", "State Analysis"])

with tab1:
    high_count = (df["risk_level"] == "high").sum()
    low_count = (df["risk_level"] == "low").sum()
    total_checkups = len(df)

    today_str = datetime.today().date().isoformat()
    today_checkups = len(df[df['timestamp'].str.startswith(today_str)])

    st.markdown("""<h2 style="color:white;"> Health Risk Summary</h2>""", unsafe_allow_html=True)

    st.markdown("""
    <style>
    .card-container {
        display: flex;
        justify-content: space-around;
        gap: 1rem;
        margin-top: 20px;
    }

    .card {
        background-color: rgba(255, 255, 255, 0.07);
        padding: 20px;
        border-radius: 15px;
        text-align: center;
        flex: 1;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }

    .card h3 {
        color: #00e6e6;
        font-size: 20px;
        margin-bottom: 10px;
    }

    .card p {
        font-size: 28px;
        color: white;
        font-weight: bold;
    }
    </style>
    """, unsafe_allow_html=True)

    st.markdown(f"""
    <div class="card-container">
        <div class="card">
            <h3>🟥 High Risk Cases</h3>
            <p>{high_count}</p>
        </div>
        <div class="card">
            <h3>🟩 Low Risk Cases</h3>
            <p>{low_count}</p>
        </div>
        <div class="card">
            <h3>📋 Total Checkups</h3>
            <p>{total_checkups}</p>
        </div>
    </div>
    """, unsafe_allow_html=True)

    st.markdown("""
    <style>
    .card-style {
        background-color: rgba(255, 255, 255, 0.07);
        padding: 20px;
        border-radius: 15px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.25);
        margin: 10px;
    }
    </style>
    """, unsafe_allow_html=True)

    # Pie Chart Data
    risk_counts = df['risk_level'].value_counts().reset_index()
    risk_counts.columns = ['risk_level', 'count']

    fig_pie = px.pie(
        risk_counts,
        values='count',
        names='risk_level',
        title='Level Distribution',
        hole=0.45,
        color_discrete_map={'high': '#FF4B4B', 'low': '#1DB954'}
    )

    fig_pie.update_layout(
        paper_bgcolor='rgba(0,0,0,0)',
        plot_bgcolor='rgba(0,0,0,0)',
        font_color='white',
        title_font_size=20
    )

    # India-Focused Map
    m = folium.Map(location=[22.9734, 78.6569], zoom_start=5.3, min_zoom=4)
    m.fit_bounds([[8, 68], [37, 97]]) 

    high_risk_df = df[df['risk_level'] == 'high']
    location_counts = Counter(zip(high_risk_df['latitude'], high_risk_df['longitude']))
    most_common_location = location_counts.most_common(1)[0][0] if location_counts else None

    for _, row in df.iterrows():
        color = 'red' if row['risk_level'] == 'high' else 'green'
        folium.Marker(
            location=[row['latitude'], row['longitude']],
            popup=f"{row['state']} - {row['risk_level'].capitalize()} Risk",
            icon=folium.Icon(color=color, icon="info-sign")
        ).add_to(m)

    if most_common_location:
        folium.CircleMarker(
            location=most_common_location,
            radius=15,
            color='darkred',
            fill=True,
            fill_opacity=0.6,
            popup="Max High-Risk Zone"
        ).add_to(m)

    st.markdown("""
    <style>
    /* Section header styling */
    .section-title {
        font-size: 28px;
        font-weight: 600;
        color: white;
        margin: 20px 0 10px;
    }

    </style>
    """, unsafe_allow_html=True)

    st.markdown('<div class="section-title">Risk Analysis </div>', unsafe_allow_html=True)

    col1, col2 = st.columns([1, 1.5], gap="large")

    with col1:
        st.plotly_chart(fig_pie, use_container_width=True)
        st.markdown('</div>', unsafe_allow_html=True)

    with col2:

        folium_static(m, width=700, height=450)
        st.markdown('</div>', unsafe_allow_html=True)


with tab2:
    st.markdown('## <div class="section-title"><i class="fas fa-chart-bar icon"></i>  Risk Summary of States</div>', unsafe_allow_html=True)

    df = pd.read_csv("data/risk_cases.csv")

    states = df['state'].dropna().unique().tolist()
    selected_state = st.selectbox("Select State", options=["All"] + sorted(states))

    if selected_state != "All":
        filtered_df = df[df['state'] == selected_state]
    else:
        filtered_df = df.copy()

    risk_counts = filtered_df.groupby(['state', 'risk_level']).size().unstack(fill_value=0)

    stats = filtered_df.groupby('state').agg({
        'systolic_bp': ['mean'],
        'diastolic_bp': ['mean'],
        'blood_sugar': ['min', 'max'],
        'bmi': ['min', 'max']
    })

    stats.columns = ['_'.join(col).strip() for col in stats.columns.values]
    stats = stats.round(1)

    summary = risk_counts.merge(stats, on='state')
    summary['Total Cases'] = summary.sum(axis=1, numeric_only=True)

    summary = summary.reset_index()
    summary["Avg_BP"] = summary["systolic_bp_mean"].astype(str) + "/" + summary["diastolic_bp_mean"].astype(str)
    summary["Sugar Range"] = summary["blood_sugar_min"].astype(str) + "–" + summary["blood_sugar_max"].astype(str)
    summary["BMI Range"] = summary["bmi_min"].astype(str) + "–" + summary["bmi_max"].astype(str)

    final = summary[["state", "high", "low", "Total Cases", "Avg_BP", "Sugar Range", "BMI Range"]]

    st.dataframe(final.style.set_caption("PHC Overview with Min–Max Vitals").format(precision=1))

    st.markdown("### Risk Levels by State")
    st.bar_chart(summary.set_index("state")[["high", "low"]])

  # download   
    csv = df.to_csv(index=False).encode("utf-8")
    st.download_button(
            label="Download CSV Report",
            data=csv,
            file_name="sanraksha_risk_report.csv",
            mime="text/csv"
      )

# bug

if "show_bug_form" not in st.session_state:
    st.session_state.show_bug_form = False

col1, col2 = st.columns([2, 8])
with col1:
    if st.button("⚠️Report"):
        st.session_state.show_bug_form = True

if st.session_state.show_bug_form:
    st.markdown("""<hr><h3 style='color:white;'>Submit Complaint or Feedback</h3>""", unsafe_allow_html=True)

    with st.form("complaint_form"):
        name = st.text_input("Your Name")
        issue = st.text_area("Describe your issue")
        submitted = st.form_submit_button(" Submit")

        if submitted:
            os.makedirs("reports", exist_ok=True)
            with open("reports/complaints.txt", "a") as f:
                f.write(f"{datetime.now()} - {name}: {issue}\n")
            st.success("Thank you! Your complaint has been recorded.")
            st.session_state.show_bug_form = False 

elif st.session_state.show_bug_form:
    if st.button("Close Bug Report"):
        st.session_state.show_bug_form = False
