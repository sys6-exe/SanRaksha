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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun BPSection(vitalsList: List<Vitals>) {
    val recentVitals = vitalsList.takeLast(10)
    val reversedVitals = recentVitals.reversed()

    val systolicPoints = recentVitals.mapIndexedNotNull { index, vitals ->
        vitals.Systolic_BP?.let { systolic ->
            Point(x = index.toFloat(), y = systolic, description = (index + 1).toString())
        }
    }

    val hasOnlyOnePoint = systolicPoints.size == 1
    val safePoints = if (systolicPoints.isEmpty()) listOf(Point(0f, 0f, "No data")) else systolicPoints
    val minY = (safePoints.minOfOrNull { it.y } ?: 80f).toInt()
    val maxY = (safePoints.maxOfOrNull { it.y } ?: 140f).toInt()
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
        lineStyle = if(hasOnlyOnePoint) {
            LineStyle(color = Color.Transparent, width = 0f)
        }else{
            LineStyle(color = Color(0xFF00796B), width = 3f)
        },
        intersectionPoint = IntersectionPoint(color = Color(0xFFB71C1C), radius = 5.dp),
        selectionHighlightPopUp = null
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
            Text(
                text = "🩺 Blood Pressure Trend",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF004D40)
            )

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

            Text(
                text = "📝 Recent Blood Pressure Readings",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            reversedVitals.forEachIndexed { index, vitals ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "BP Icon",
                            tint = Color(0xFF6200EE),
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 8.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reading ${recentVitals.size - index}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Systolic: ${vitals.Systolic_BP?.toInt() ?: "--"} mmHg | Diastolic: ${vitals.Diastolic?.toInt() ?: "--"} mmHg",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }

                        Text(
                            text = vitals.date ?: "",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}