package com.example.sanraksha.front

import android.adservices.ondevicepersonalization.LogReader
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Tab
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import kotlinx.serialization.descriptors.listSerialDescriptor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(patientId:Long,
                  patientViewModel:PatientViewModel = viewModel(),
                  vitalsViewModel:VitalsViewModel = viewModel(),
                  navController: NavHostController

) {
    val patient by patientViewModel.getAPatientById(patientId).collectAsState(initial = null)
    val vitalsList by vitalsViewModel.getVitalsByPatientId(patientId)
        .collectAsState(initial = emptyList())
    Log.d("vitalsList","$vitalsList")

    Scaffold(
        containerColor = Color(0xFFFDFDFD),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = patient?.name ?: "",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { Divider() }
            item { patient?.let { PatientHeader(it) } }

            item {
                if (vitalsList.isNotEmpty()) {
                    val latestRisk = vitalsList.last().predictedRisk
                    Log.d("latestRisk","$latestRisk")
                    val riskText = if (latestRisk == 1) "High Risk" else "Low Risk"
                    val riskColor = if (latestRisk == 1) Color.Red else Color(0xFF4CAF50)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = riskColor,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Recent Risk Prediction: $riskText",
                                color = riskColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                val tabTitles = listOf("BP", "BMI", "HR")
                var selectedTabIndex by remember { mutableStateOf(0) }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        backgroundColor = Color.Transparent,
                        contentColor = Color.Black,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF6200EE))
                            )
                        },
                        divider = {}
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedTabIndex == index) Color(0xFF6200EE) else Color.DarkGray
                                    )
                                }
                            )
                        }
                    }
                }

                when (selectedTabIndex) {
                    0 -> BPSection(vitalsList)
                    1 -> BMISection(vitalsList)
                    2 -> HRSection(vitalsList)
                }
            }

//            item {
//                if (vitalsList.isNotEmpty()) {
//                    val latestRisk = vitalsList.last().predictedRisk
//                    Log.d("latestRisk","$latestRisk")
//                    val riskText = if (latestRisk == 1) "High Risk" else "Low Risk"
//                    val riskColor = if (latestRisk == 1) Color.Red else Color(0xFF4CAF50)
//
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        elevation = CardDefaults.cardElevation(4.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .padding(16.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Favorite,
//                                contentDescription = null,
//                                tint = riskColor,
//                                modifier = Modifier.padding(end = 8.dp)
//                            )
//                            Text(
//                                text = "Recent Risk Prediction: $riskText",
//                                color = riskColor,
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                        }
//                    }
//                }
//            }

            item {
                Button(
                    onClick = { navController.navigate(Screens.RecordVitals.passId(patientId)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Record new Data & Predict")
                }
            }
        }
    }
}



@Composable
fun PatientHeader(patient: Patient) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier.size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .padding(12.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = patient.name, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row {
                    Text(text = "Pregnancy Week: ",color= Color.Gray)
                    Text(text="${patient.pregnancyWeek}", fontWeight = FontWeight.Bold)
                }
                Row{
                    Text(
                    text = "Last Checkup:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                    Text(text = " ${patient.lastCheckup}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                }

            }

        }
    }
}