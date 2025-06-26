package com.example.sanraksha.front

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle

@Composable
fun BMISection(vitalsList: List<Vitals>) {
    val recentVitals = vitalsList.takeLast(10).reversed()
    val notreversedrecent = vitalsList.takeLast(10)

    val bmiPoints = notreversedrecent.mapIndexedNotNull { index, vitals ->
        vitals.BMI?.let { bmi ->
            Point(x = index.toFloat(), y = bmi, description = (index + 1).toString())
        }
    }

    val safePoints = if (bmiPoints.isEmpty()) listOf(Point(0f, 0f, "No data")) else bmiPoints
    val minY = (safePoints.minOfOrNull { it.y } ?: 15f).toInt()
    val maxY = (safePoints.maxOfOrNull { it.y } ?: 35f).toInt()
    val stepValue = ((maxY - minY) / 5).coerceAtLeast(1)

    val xAxisData = AxisData.Builder()
        .steps(safePoints.size - 1)
        .labelData { i -> (i + 1).toString() }
        .axisLabelColor(Color(0xFF37474F))
        .axisLineColor(Color(0xFF90A4AE))
        .axisLabelFontSize(12.sp)
        .axisStepSize(50.dp)
        .axisLabelAngle(0f)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { i -> "${minY + i * stepValue}" }
        .axisLabelColor(Color(0xFF37474F))
        .axisLineColor(Color(0xFF90A4AE))
        .axisLabelFontSize(12.sp)
        .axisStepSize(50.dp)
        .build()

    val line = Line(
        dataPoints = safePoints,
        lineStyle = LineStyle(color = Color(0xFF1A237E), width = 3f),
        intersectionPoint = IntersectionPoint(color = Color.Red, radius = 5.dp)
    )

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(lines = listOf(line)),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color(0xFFF7FAFC),
        gridLines = GridLines(
            color = Color(0xFFB0BEC5),
            lineWidth = 0.5.dp,
            alpha = 0.4f,
            enableHorizontalLines = true,
            enableVerticalLines = true
        ),
        isZoomAllowed = true
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📊 BMI Trend", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A237E))
            Spacer(modifier = Modifier.height(12.dp))

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                lineChartData = lineChartData
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("📝 Recent BMI Readings", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            recentVitals.forEachIndexed { index, vitals ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = "BMI Icon", tint = Color(0xFF1A237E), modifier = Modifier.size(28.dp).padding(end = 8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reading ${notreversedrecent.size - index}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("BMI: ${vitals.BMI?.toInt() ?: "--"} kg/m²", fontSize = 13.sp, color = Color.DarkGray)
                        }
                        Text(vitals.date ?: "", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
