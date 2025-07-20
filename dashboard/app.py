import os
import pandas as pd
import streamlit as st
import folium
import plotly.express as px
from streamlit_folium import folium_static
from datetime import datetime

# --- Page Configuration ---
st.set_page_config(
    page_title="SanRaksha PHC Dashboard",
    page_icon="https://raw.githubusercontent.com/sys6-exe/SanRaksha/main/assets/Sanraksha.png",
    layout="wide",
    initial_sidebar_state="expanded"
)

# --- Data Loading (Cached for performance) ---
@st.cache_data
def load_data(path):
    """Loads and prepares the risk cases data from a CSV file."""
    df = pd.read_csv(path)
    df['timestamp'] = pd.to_datetime(df['timestamp'])
    return df

# --- CSS Styling ---
def load_css():
    """Injects custom CSS for a modern, functional design."""
    st.markdown("""
    <style>
        /* Remove the top padding to make the header flush with the top */
        .block-container {
            padding-top: 2rem;
        }

        /* Main App Styling */
        .stApp {
            background: linear-gradient(135deg, #141E30, #243B55);
            color: #E0E0E0;
        }

        /* --- Main Page Header Styling --- */
        .top-container {
            display: flex;
            align-items: center;
            margin-bottom: 2rem;
        }
        .logo {
            width: 75px;  /* Increased size */
            height: 75px; /* Increased size */
            border-radius: 50%;
            margin-right: 20px;
        }
        .title-text h1 {
            font-size: 3rem; /* Increased size */
            font-weight: bold;
            color: white;
            margin: 0;
            padding: 0;
            line-height: 1.1;
        }

        /* --- Sidebar Styling --- */
        [data-testid="stSidebar"] {
            background: rgba(20, 30, 48, 0.6);
            backdrop-filter: blur(10px);
        }

        /* --- Component Styling --- */
        [data-testid="stMetric"] {
            background-color: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.15);
            border-radius: 15px;
            padding: 25px;
        }
        [data-testid="stMetric"] > label {
            color: #00E6E6; /* Cyan accent color */
        }
        .stTabs [data-baseweb="tab-list"] button[aria-selected="true"] {
            color: white;
            background-color: rgba(0, 230, 230, 0.2);
            border-bottom: 3px solid #00E6E6;
        }
        .section-title {
            font-size: 24px;
            font-weight: 600;
            color: white;
            margin-bottom: 20px;
            border-left: 5px solid #00E6E6;
            padding-left: 15px;
        }
    </style>
    """, unsafe_allow_html=True)

# --- App Execution ---
load_css()
df = load_data("data/risk_cases.csv") 

# --- Sidebar ---
with st.sidebar:
    st.header("Information")
    st.info(f"**Date:** {datetime.now().strftime('%d %B %Y')}")
    st.info(f"**Time:** {datetime.now().strftime('%I:%M %p')}")
    
    st.write("---")
    
    st.header("Actions")
    with st.expander("⚠️ Report or Feedback"):
        with st.form("complaint_form", clear_on_submit=True):
            name = st.text_input("Name (Optional)")
            issue = st.text_area("Describe your issue or feedback")
            submitted = st.form_submit_button("Submit Feedback")

            if submitted:
                os.makedirs("reports", exist_ok=True)
                with open("reports/complaints.txt", "a") as f:
                    f.write(f"{datetime.now()} - {name}: {issue}\n")
                st.success("Thank you! Feedback recorded.")

# --- Main Page ---

st.markdown(
    """
    <div class="top-container">
        <img class="logo" src="https://raw.githubusercontent.com/sys6-exe/SanRaksha/main/assets/Sanraksha.png" />
        <div class="title-text"><h1>SanRaksha PHC Dashboard</h1></div>
    </div>
    """,
    unsafe_allow_html=True
)

# Tabs for Content
tab1, tab2 = st.tabs(["📍Overview", "📊 State Analysis"])

with tab1:
    st.markdown("<p class='section-title'>Real-time Health Summary</p>", unsafe_allow_html=True)
    
    high_count = (df["risk_level"] == "high").sum()
    low_count = (df["risk_level"] == "low").sum()
    total_checkups = len(df)
    
    col1, col2, col3 = st.columns(3, gap="large")
    col1.metric("High Risk Cases", f"🚨 {high_count}")
    col2.metric("Low Risk Cases", f"✅ {low_count}")
    col3.metric("Total Checkups", f"📋 {total_checkups}")
    
    st.write("---")
    st.markdown("<p class='section-title'>Geospatial & Risk Distribution</p>", unsafe_allow_html=True)
    
    col1, col2 = st.columns([2, 3], gap="large")

    with col1:
        risk_counts = df['risk_level'].value_counts().reset_index()
        fig_pie = px.pie(
            risk_counts, values='count', names='risk_level', title='Risk Level Distribution', hole=0.5,
            color_discrete_map={'high': '#FF4B4B', 'low': '#28a745'}
        )
        fig_pie.update_layout(paper_bgcolor='rgba(0,0,0,0)', plot_bgcolor='rgba(0,0,0,0)', font_color='white',
                              legend=dict(orientation="h", yanchor="bottom", y=-0.2, xanchor="center", x=0.5))
        st.plotly_chart(fig_pie, use_container_width=True)

    with col2:
        m = folium.Map(location=[22.9734, 78.6569], zoom_start=4.5, tiles='CartoDB dark_matter')
        for _, row in df.iterrows():
            color = 'red' if row['risk_level'] == 'high' else 'green'
            folium.Marker(
                location=[row['latitude'], row['longitude']],
                popup=f"<b>{row['state']}</b><br>Risk: {row['risk_level'].capitalize()}",
                icon=folium.Icon(color=color, icon="heart", prefix="fa")
            ).add_to(m)
        folium_static(m, width=None, height=450)

with tab2:
    st.markdown("<p class='section-title'>State-wise Health Analytics</p>", unsafe_allow_html=True)
    
    states = df['state'].dropna().unique().tolist()
    selected_state = st.selectbox("Select a State to Analyze", ["All States"] + sorted(states), label_visibility="collapsed")
    
    filtered_df = df if selected_state == "All States" else df[df['state'] == selected_state]

    if not filtered_df.empty:
        risk_counts = filtered_df.groupby(['state', 'risk_level']).size().unstack(fill_value=0)
        stats = filtered_df.groupby('state').agg(
            systolic_bp_mean=('systolic_bp', 'mean'), diastolic_bp_mean=('diastolic_bp', 'mean'),
            blood_sugar_min=('blood_sugar', 'min'), blood_sugar_max=('blood_sugar', 'max'),
            bmi_min=('bmi', 'min'), bmi_max=('bmi', 'max')
        ).round(1)
        
        summary = pd.merge(risk_counts, stats, on='state', how='left')
        if 'high' not in summary: summary['high'] = 0
        if 'low' not in summary: summary['low'] = 0
        
        summary['Total Cases'] = summary['high'] + summary['low']
        summary = summary.reset_index()

        summary["Avg BP (Sys/Dia)"] = summary["systolic_bp_mean"].astype(str) + "/" + summary["diastolic_bp_mean"].astype(str)
        summary["Sugar Range (mg/dL)"] = summary["blood_sugar_min"].astype(str) + " – " + summary["blood_sugar_max"].astype(str)
        summary["BMI Range"] = summary["bmi_min"].astype(str) + " – " + summary["bmi_max"].astype(str)

        final_cols = ["state", "high", "low", "Total Cases", "Avg BP (Sys/Dia)", "Sugar Range (mg/dL)", "BMI Range"]
        st.dataframe(summary[final_cols], use_container_width=True)

        st.markdown("##### Risk Levels Breakdown")
        chart_data = summary.set_index("state")[["high", "low"]]
        st.bar_chart(chart_data, color=["#FF4B4B", "#28a745"])
        
        csv = summary[final_cols].to_csv(index=False).encode("utf-8")
        st.download_button("Download State Report", csv, f"{selected_state.lower().replace(' ', '_')}_report.csv", "text/csv")
    else:
        st.warning("No data available for the selected state.")