"""
app.py  ·  SanRaksha PHC Dashboard
=====================================
Changes from original:
  • Authentication: simple OTP-style PIN login with session state.
    Two roles: "asha" (own patients only) and "doctor" (block-wide view).
    Replace USERS dict with your FastAPI /auth endpoint in production.
  • Referral acknowledgment loop: doctors can mark alerts as
    "Reviewed", "Referred", or "Dismissed" — status persists to
    acknowledged_cases.csv and syncs to the FastAPI backend.
  • Time-series trend view: week-over-week high-risk case count per
    block — doctors can see if a block is improving or worsening.
  • Data persistence: feedback/complaints now POST to FastAPI backend
    instead of writing to a local .txt file.
  • Backend calls wrapped in try/except — dashboard still works offline
    by falling back to local CSV reads.
  • Original UI styling and map/chart logic preserved.
"""

import os
import requests
import pandas as pd
import streamlit as st
import folium
import plotly.express as px
import plotly.graph_objects as go
from streamlit_folium import folium_static
from datetime import datetime

# ── Configuration ─────────────────────────────────────────────────────────────
# In production, replace with your deployed FastAPI base URL.
API_BASE = os.environ.get("SANRAKSHA_API", "http://localhost:8000")

DATA_PATH         = "data/risk_cases.csv"
ACK_PATH          = "data/acknowledged_cases.csv"   # local fallback for acks

# ── Hardcoded users for demo.  Replace with FastAPI /auth in production. ──────
# Format: { "username": ("pin", "role") }
# Role "doctor"  → sees all cases, can acknowledge alerts
# Role "asha"    → sees only rows where asha_id matches username
USERS = {
    "doctor1":  ("1234", "doctor"),
    "asha_riya": ("5678", "asha"),
}

# ── Page config ───────────────────────────────────────────────────────────────
st.set_page_config(
    page_title="SanRaksha PHC Dashboard",
    page_icon="https://raw.githubusercontent.com/sys6-exe/SanRaksha/main/assets/Sanraksha.png",
    layout="wide",
    initial_sidebar_state="expanded",
)


# ── CSS (original styling preserved) ─────────────────────────────────────────
def load_css():
    st.markdown("""
    <style>
        .block-container { padding-top: 2rem; }
        .stApp { background: linear-gradient(135deg, #141E30, #243B55); color: #E0E0E0; }
        .top-container { display: flex; align-items: center; margin-bottom: 2rem; }
        .logo { width: 75px; height: 75px; border-radius: 50%; margin-right: 20px; }
        .title-text h1 { font-size: 3rem; font-weight: bold; color: white;
                         margin: 0; padding: 0; line-height: 1.1; }
        [data-testid="stSidebar"] { background: rgba(20,30,48,0.6);
                                    backdrop-filter: blur(10px); }
        [data-testid="stMetric"] { background-color: rgba(255,255,255,0.08);
                                   border: 1px solid rgba(255,255,255,0.15);
                                   border-radius: 15px; padding: 25px; }
        [data-testid="stMetric"] > label { color: #00E6E6; }
        .stTabs [data-baseweb="tab-list"] button[aria-selected="true"] {
            color: white; background-color: rgba(0,230,230,0.2);
            border-bottom: 3px solid #00E6E6; }
        .section-title { font-size: 24px; font-weight: 600; color: white;
                         margin-bottom: 20px; border-left: 5px solid #00E6E6;
                         padding-left: 15px; }
        .ack-card { background: rgba(255,255,255,0.06); border-radius: 10px;
                    padding: 14px 18px; margin-bottom: 10px;
                    border-left: 4px solid #FF4B4B; }
        .ack-card.reviewed  { border-left-color: #00E6E6; }
        .ack-card.referred  { border-left-color: #28a745; }
        .ack-card.dismissed { border-left-color: #888; }
    </style>
    """, unsafe_allow_html=True)


# ── Data loading ──────────────────────────────────────────────────────────────
@st.cache_data(ttl=60)
def load_data(path: str) -> pd.DataFrame:
    df = pd.read_csv(path)
    df["timestamp"] = pd.to_datetime(df["timestamp"])
    return df


def load_ack() -> pd.DataFrame:
    """Load acknowledgment log — local CSV fallback."""
    if os.path.exists(ACK_PATH):
        return pd.read_csv(ACK_PATH)
    return pd.DataFrame(columns=["case_id", "status", "doctor", "ack_time"])


def save_ack(case_id: str, status: str, doctor: str):
    """
    Persist acknowledgment locally and attempt to POST to FastAPI.
    Falls back silently if API is unreachable (offline PHC).
    """
    ack_df = load_ack()
    row = {
        "case_id": case_id,
        "status":  status,
        "doctor":  doctor,
        "ack_time": datetime.now().isoformat(),
    }

    # Remove old ack for same case if it exists, then append
    ack_df = ack_df[ack_df["case_id"] != case_id]
    ack_df = pd.concat([ack_df, pd.DataFrame([row])], ignore_index=True)
    os.makedirs("data", exist_ok=True)
    ack_df.to_csv(ACK_PATH, index=False)

    # Try syncing to backend
    try:
        requests.post(f"{API_BASE}/referrals/acknowledge", json=row, timeout=3)
    except Exception:
        pass   # offline — local save is the source of truth


def post_feedback(name: str, issue: str):
    """POST feedback to FastAPI; fall back to local file if unreachable."""
    payload = {
        "name": name or "Anonymous",
        "issue": issue,
        "timestamp": datetime.now().isoformat(),
    }
    try:
        requests.post(f"{API_BASE}/feedback", json=payload, timeout=3)
    except Exception:
        # Offline fallback
        os.makedirs("reports", exist_ok=True)
        with open("reports/complaints.txt", "a") as f:
            f.write(f"{payload['timestamp']} - {payload['name']}: {issue}\n")


# ── Authentication ────────────────────────────────────────────────────────────
def login_screen():
    st.markdown(
        """
        <div style='display:flex;flex-direction:column;align-items:center;
                    margin-top:80px;'>
            <img src='https://raw.githubusercontent.com/sys6-exe/SanRaksha/main/assets/Sanraksha.png'
                 style='width:90px;border-radius:50%;margin-bottom:20px;'/>
            <h2 style='color:white;margin-bottom:4px;'>SanRaksha PHC Dashboard</h2>
            <p style='color:#aaa;'>Sign in to continue</p>
        </div>
        """,
        unsafe_allow_html=True,
    )

    col = st.columns([1, 2, 1])[1]
    with col:
        username = st.text_input("Username").strip()
        pin      = st.text_input("PIN", type="password").strip()
        if st.button("Sign In", use_container_width=True):
            if username in USERS and USERS[username][0] == pin:
                st.session_state["user"]   = username
                st.session_state["role"]   = USERS[username][1]
                st.session_state["logged"] = True
                st.rerun()
            else:
                st.error("Invalid username or PIN.")


# ── Trend chart ───────────────────────────────────────────────────────────────
def render_trend(df: pd.DataFrame, selected_state: str):
    """Week-over-week high-risk case count per block."""
    st.markdown("<p class='section-title'>Risk Trend — Week over Week</p>",
                unsafe_allow_html=True)

    scope = df if selected_state == "All States" else df[df["state"] == selected_state]
    if scope.empty:
        st.info("No data for trend analysis.")
        return

    scope = scope.copy()
    scope["week"] = scope["timestamp"].dt.to_period("W").astype(str)
    trend = (
        scope[scope["risk_level"] == "high"]
        .groupby(["week", "state"])
        .size()
        .reset_index(name="high_risk_count")
    )

    if trend.empty:
        st.info("No high-risk cases to trend.")
        return

    fig = px.line(
        trend, x="week", y="high_risk_count", color="state",
        title="High-risk cases per week by state",
        labels={"week": "Week", "high_risk_count": "High-Risk Cases"},
        template="plotly_dark",
    )
    fig.update_layout(
        paper_bgcolor="rgba(0,0,0,0)", plot_bgcolor="rgba(0,0,0,0)",
        font_color="white", legend_title_text="State",
    )
    st.plotly_chart(fig, use_container_width=True)


# ── Referral panel ────────────────────────────────────────────────────────────
def render_referral_panel(df: pd.DataFrame):
    """
    Shows unacknowledged high-risk cases with action buttons.
    Only visible to users with role == "doctor".
    """
    if st.session_state.get("role") != "doctor":
        return

    st.markdown("<p class='section-title'>Pending Alerts — Action Required</p>",
                unsafe_allow_html=True)

    ack_df  = load_ack()
    acked   = set(ack_df["case_id"].astype(str))
    pending = df[
        (df["risk_level"] == "high") &
        (~df.index.astype(str).isin(acked))
    ]

    if pending.empty:
        st.success("All high-risk alerts have been acknowledged.")
        return

    st.warning(f"{len(pending)} unacknowledged high-risk case(s).")

    for idx, row in pending.iterrows():
        case_id = str(idx)
        with st.container():
            st.markdown(
                f"""<div class='ack-card'>
                    <b>{row.get('state','Unknown')}</b> &nbsp;|&nbsp;
                    Lat: {row.get('latitude','?')}, Lon: {row.get('longitude','?')}
                    &nbsp;|&nbsp; {pd.to_datetime(row.get('timestamp','')).strftime('%d %b %Y %H:%M')
                                   if row.get('timestamp') else 'N/A'}
                </div>""",
                unsafe_allow_html=True,
            )
            c1, c2, c3, _ = st.columns([1, 1, 1, 3])
            if c1.button("✅ Reviewed",  key=f"rev_{case_id}"):
                save_ack(case_id, "reviewed",  st.session_state["user"])
                st.rerun()
            if c2.button("🏥 Referred",  key=f"ref_{case_id}"):
                save_ack(case_id, "referred",  st.session_state["user"])
                st.rerun()
            if c3.button("🗑 Dismissed", key=f"dis_{case_id}"):
                save_ack(case_id, "dismissed", st.session_state["user"])
                st.rerun()

    # Acknowledgment history (collapsible)
    with st.expander("View acknowledgment history"):
        if not ack_df.empty:
            st.dataframe(ack_df.sort_values("ack_time", ascending=False),
                         use_container_width=True)
        else:
            st.info("No acknowledgments yet.")


# ── Main app ──────────────────────────────────────────────────────────────────
def main():
    load_css()

    # Auth gate
    if not st.session_state.get("logged"):
        login_screen()
        return

    user = st.session_state["user"]
    role = st.session_state["role"]

    df = load_data(DATA_PATH)

    # Role-based data filter: ASHA workers see only their own patients
    if role == "asha" and "asha_id" in df.columns:
        df = df[df["asha_id"] == user]

    # ── Sidebar ───────────────────────────────────────────────────────────────
    with st.sidebar:
        st.markdown(f"**Logged in as:** `{user}` ({role})")
        if st.button("Sign out"):
            st.session_state.clear()
            st.rerun()

        st.write("---")
        st.header("Information")
        st.info(f"**Date:** {datetime.now().strftime('%d %B %Y')}")
        st.info(f"**Time:** {datetime.now().strftime('%I:%M %p')}")
        st.write("---")

        st.header("Actions")
        with st.expander("⚠️ Report or Feedback"):
            with st.form("complaint_form", clear_on_submit=True):
                name  = st.text_input("Name (Optional)")
                issue = st.text_area("Describe your issue or feedback")
                submitted = st.form_submit_button("Submit Feedback")
                if submitted and issue.strip():
                    post_feedback(name, issue)
                    st.success("Thank you! Feedback recorded.")

    # ── Header ────────────────────────────────────────────────────────────────
    st.markdown(
        """
        <div class="top-container">
            <img class="logo"
                 src="https://raw.githubusercontent.com/sys6-exe/SanRaksha/main/assets/Sanraksha.png"/>
            <div class="title-text"><h1>SanRaksha PHC Dashboard</h1></div>
        </div>
        """,
        unsafe_allow_html=True,
    )

    # ── Tabs ──────────────────────────────────────────────────────────────────
    tabs = ["📍 Overview", "📊 State Analysis", "📈 Trends"]
    if role == "doctor":
        tabs.append("🔔 Pending Alerts")
    tab_objects = st.tabs(tabs)

    # ── Tab 1: Overview (original logic preserved) ────────────────────────────
    with tab_objects[0]:
        st.markdown("<p class='section-title'>Real-time Health Summary</p>",
                    unsafe_allow_html=True)

        high_count    = (df["risk_level"] == "high").sum()
        low_count     = (df["risk_level"] == "low").sum()
        total_checkups = len(df)

        col1, col2, col3 = st.columns(3, gap="large")
        col1.metric("High Risk Cases",  f"🚨 {high_count}")
        col2.metric("Low Risk Cases",   f"✅ {low_count}")
        col3.metric("Total Checkups",   f"📋 {total_checkups}")

        st.write("---")
        st.markdown("<p class='section-title'>Geospatial & Risk Distribution</p>",
                    unsafe_allow_html=True)

        c1, c2 = st.columns([2, 3], gap="large")
        with c1:
            risk_counts = df["risk_level"].value_counts().reset_index()
            fig_pie = px.pie(
                risk_counts, values="count", names="risk_level",
                title="Risk Level Distribution", hole=0.5,
                color_discrete_map={"high": "#FF4B4B", "low": "#28a745"},
            )
            fig_pie.update_layout(
                paper_bgcolor="rgba(0,0,0,0)", plot_bgcolor="rgba(0,0,0,0)",
                font_color="white",
                legend=dict(orientation="h", yanchor="bottom", y=-0.2,
                            xanchor="center", x=0.5),
            )
            st.plotly_chart(fig_pie, use_container_width=True)

        with c2:
            m = folium.Map(location=[22.9734, 78.6569], zoom_start=4.5,
                           tiles="CartoDB dark_matter")
            for _, row in df.iterrows():
                color = "red" if row["risk_level"] == "high" else "green"
                folium.Marker(
                    location=[row["latitude"], row["longitude"]],
                    popup=f"<b>{row['state']}</b><br>Risk: {row['risk_level'].capitalize()}",
                    icon=folium.Icon(color=color, icon="heart", prefix="fa"),
                ).add_to(m)
            folium_static(m, width=None, height=450)

    # ── Tab 2: State analysis (original logic preserved) ──────────────────────
    with tab_objects[1]:
        st.markdown("<p class='section-title'>State-wise Health Analytics</p>",
                    unsafe_allow_html=True)

        states = df["state"].dropna().unique().tolist()
        selected_state = st.selectbox(
            "Select a State to Analyse",
            ["All States"] + sorted(states),
            label_visibility="collapsed",
        )
        filtered_df = df if selected_state == "All States" else df[df["state"] == selected_state]

        if not filtered_df.empty:
            risk_counts = (
                filtered_df.groupby(["state", "risk_level"])
                .size().unstack(fill_value=0)
            )
            stats = filtered_df.groupby("state").agg(
                systolic_bp_mean=("systolic_bp", "mean"),
                diastolic_bp_mean=("diastolic_bp", "mean"),
                blood_sugar_min=("blood_sugar", "min"),
                blood_sugar_max=("blood_sugar", "max"),
                bmi_min=("bmi", "min"),
                bmi_max=("bmi", "max"),
            ).round(1)

            summary = pd.merge(risk_counts, stats, on="state", how="left")
            if "high" not in summary: summary["high"] = 0
            if "low"  not in summary: summary["low"]  = 0
            summary["Total Cases"] = summary["high"] + summary["low"]
            summary = summary.reset_index()
            summary["Avg BP (Sys/Dia)"]    = (summary["systolic_bp_mean"].astype(str)
                                               + "/" + summary["diastolic_bp_mean"].astype(str))
            summary["Sugar Range (mg/dL)"] = (summary["blood_sugar_min"].astype(str)
                                               + " – " + summary["blood_sugar_max"].astype(str))
            summary["BMI Range"]           = (summary["bmi_min"].astype(str)
                                               + " – " + summary["bmi_max"].astype(str))

            final_cols = ["state", "high", "low", "Total Cases",
                          "Avg BP (Sys/Dia)", "Sugar Range (mg/dL)", "BMI Range"]
            st.dataframe(summary[final_cols], use_container_width=True)

            st.markdown("##### Risk Levels Breakdown")
            chart_data = summary.set_index("state")[["high", "low"]]
            st.bar_chart(chart_data, color=["#FF4B4B", "#28a745"])

            csv = summary[final_cols].to_csv(index=False).encode("utf-8")
            st.download_button(
                "Download State Report", csv,
                f"{selected_state.lower().replace(' ', '_')}_report.csv",
                "text/csv",
            )
        else:
            st.warning("No data available for the selected state.")

    # ── Tab 3: Trends ─────────────────────────────────────────────────────────
    with tab_objects[2]:
        states = df["state"].dropna().unique().tolist()
        sel = st.selectbox(
            "Filter by state",
            ["All States"] + sorted(states),
            key="trend_state",
        )
        render_trend(df, sel)

    # ── Tab 4: Pending alerts (doctors only) ──────────────────────────────────
    if role == "doctor":
        with tab_objects[3]:
            render_referral_panel(df)


# ── Entry point ───────────────────────────────────────────────────────────────
main()
