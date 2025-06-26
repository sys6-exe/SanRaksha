package com.example.sanraksha.front

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.util.splitToIntList
import com.example.sanraksha.AndroidConnectivityObserver
import com.example.sanraksha.ConnectivityVIewModel
import com.example.sanraksha.DataStandardization
import com.example.sanraksha.HealthViewModel
import com.example.sanraksha.R
import com.example.sanraksha.RiskPredictor
import com.example.sanraksha.healthDataItem
import com.example.sanraksha.riskInput
import com.example.sanraksha.ui.theme.ConnectivityViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordVitalsScreen(
    patientId: Long,
    navController: NavHostController,
    vitalsViewModel: VitalsViewModel = viewModel(),
    healthViewModel: HealthViewModel = viewModel(),
    patientViewModel: PatientViewModel = viewModel()
) {
    val patient by patientViewModel.getAPatientById(patientId).collectAsState(initial = null)


    val context = LocalContext.current
    val connectivityObserver = remember{
        AndroidConnectivityObserver(context.applicationContext)
    }

    val connectivityViewModel: ConnectivityVIewModel = viewModel(
        factory = ConnectivityViewModelFactory(connectivityObserver)
    )
    val isConnected by connectivityViewModel.isConnected.collectAsState()

    var date by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var systolicBP by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var bs by remember { mutableStateOf("") }
    var bodyTemp by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }

    var previousComplications by remember { mutableStateOf<Int?>(null) }
    var preexistingDiabetes by remember { mutableStateOf<Int?>(null) }
    var gestationalDiabetes by remember { mutableStateOf<Int?>(null) }
    var mentalHealth by remember { mutableStateOf<Int?>(null) }


    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember{
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                date = String.format(Locale.US, "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
            },
            year, month, day
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Record New Vitals",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
                )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Date",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = date, onValueChange = {},leadingIcon = { Image(painter = painterResource(id= R.drawable.baseline_calendar_today_24),
                contentDescription = "Checkup Date"
            ) },
                placeholder = { Text("Enter date : DD-MM-YYYY") } ,
                             keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().clickable {  datePickerDialog.show()  },
                enabled = false,
                shape = RoundedCornerShape(12.dp)
                )
            Text("Age",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = age, onValueChange = { age = it }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Age") },
                placeholder = { Text("Enter age") } ,   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
                )

            Text("Systolic BP",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = systolicBP, onValueChange = { systolicBP = it }, leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = "Systolic BP") },
                placeholder = { Text("Enter systolic : mm Hg") } ,   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
                )

            Text("Diastolic",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = diastolic, onValueChange = { diastolic = it },  leadingIcon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Diastolic") },
                placeholder = { Text("Enter diastolic : mm Hg") },   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
                )

            Text("Blood Sugar",style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(value = bs,
                    onValueChange = { bs = it },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_bloodtype_24),
                            contentDescription = "Blood Sugar"
                        )
                    },
                    placeholder = { Text("Enter blood sugar : mmol/L") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )



            Text("Body Temp",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = bodyTemp, onValueChange = { bodyTemp = it },leadingIcon = { Image(painter = painterResource(id=R.drawable.baseline_device_thermostat_24), contentDescription = "Body Temperature") },
                placeholder = { Text("Enter temperature : °F") } ,   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
                )
            Text("BMI",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = bmi, onValueChange = { bmi = it }, leadingIcon = { Image(painter = painterResource(id=R.drawable.baseline_monitor_weight_24), contentDescription = "BMI") },
                placeholder = { Text("Enter BMI : Kg/m^2") },   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
                )
            Text("Heart Rate",style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = heartRate, onValueChange = { heartRate = it },  leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = "Heart Rate") },
                placeholder = { Text("Enter heart rate") },   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
                )
            Dropdown("Previous Complications", previousComplications) { previousComplications = it }
            Dropdown("Preexisting Diabetes", preexistingDiabetes) { preexistingDiabetes = it }
            Dropdown("Gestational Diabetes", gestationalDiabetes) { gestationalDiabetes = it }
            Dropdown("Mental Health Issues", mentalHealth) { mentalHealth = it }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if(isConnected){
                        //call api
                        val input = healthDataItem(
                            Age = age.toFloatOrNull()?.toInt()?:0,
                            Systolic_BP = systolicBP.toFloatOrNull(),
                            Diastolic = diastolic.toFloatOrNull(),
                            BS = bs.toFloatOrNull(),
                            Body_Temp = bodyTemp.toFloatOrNull(),
                            BMI = bmi.toFloatOrNull() ?: 0f,
                            Previous_Complications = previousComplications ?: 0,
                            Preexisting_Diabetes = preexistingDiabetes ?: 0,
                            Gestational_Diabetes = gestationalDiabetes ?: 0,
                            Mental_Health = mentalHealth ?: 0,
                            Heart_Rate = heartRate.toFloatOrNull()

                        )
                        healthViewModel.sendHealthDataToApi(input){prediction->
                            if(prediction != null){
                                Log.d("APIPrediction","Predicted Risk Score : $prediction")
                                val vitals = Vitals(
                                    patientId = patientId,
                                    date = date,
                                    Age = age.toIntOrNull()?:0,
                                    Systolic_BP = systolicBP.toFloatOrNull()?:0f,
                                    Diastolic = diastolic.toFloatOrNull()?:0f,
                                    BS = bs.toFloatOrNull()?:0f,
                                    Body_Temp = bodyTemp.toFloatOrNull()?:0f,
                                    BMI = bmi.toFloatOrNull() ?: 0f,
                                    Heart_Rate = heartRate.toFloatOrNull()?:0f,
                                    Previous_Complications = previousComplications ?: 0,
                                    Preexisting_Diabetes = preexistingDiabetes ?: 0,
                                    Gestational_Diabetes = gestationalDiabetes ?: 0,
                                    Mental_Health = mentalHealth ?: 0,
                                    predictedRisk = prediction
                                )
                                Log.d("InsertVitals", "Calling insert for patientId = ${vitals.patientId}")

                                // 🔍 AND THIS:
                                Log.d("InsertVitals", "Vitals Object = $vitals")
                                vitalsViewModel.insertVitals(vitals)


                                val week = getWeek(startDateStr = patient?.lastCheckup?:"", endDateStr = date)
                                val finalweek = (patient?.pregnancyWeek?:0) + week

                                patientViewModel.updatePatient(patient = Patient(patientId,patient?.name?:"",
                                    finalweek,date
                                    ))

                                 navController.popBackStack()

                            }else{
                               Log.d("APIPrediction","error: ")

                            }

                        }
                    }else{
                        //else ml model
                        val input = riskInput(
                            Age = age.toFloatOrNull()?.toInt()?:0,
                            Systolic_BP = systolicBP.toFloatOrNull()?.toInt()?:0,
                            Diastolic = diastolic.toFloatOrNull()?.toInt()?:0,
                            BS = bs.toFloatOrNull()?:0f,
                            Body_Temp = bodyTemp.toFloatOrNull()?:0f,
                            BMI = bmi.toFloatOrNull() ?: 0f,
                            Previous_Complications = previousComplications ?: 0,
                            Preexisting_Diabetes = preexistingDiabetes ?: 0,
                            Gestational_Diabetes = gestationalDiabetes ?: 0,
                            Mental_Health = mentalHealth ?: 0,
                            Heart_Rate = heartRate.toFloatOrNull()?.toInt()?:0
                        )
                        val standardizedInput = DataStandardization(input)
                        val inputforMlModel = floatArrayOf(
                    standardizedInput.BS.toFloat(),
                    standardizedInput.BMI.toFloat(),
                    standardizedInput.Age.toFloat(),
                    standardizedInput.HeartRate.toFloat(),
                    standardizedInput.SystolicBP.toFloat(),
                    standardizedInput.DiastolicBP.toFloat(),
                    standardizedInput.FinalRiskScore.toFloat()
                    )
                        val predictor = RiskPredictor(context)
                        val prediction = predictor.predict(inputforMlModel)
                        val intPrediction = if(prediction > 0.5f)1 else 0

                     Log.d("TFLitePrediction","Predicted Risk Score : $prediction")
                    Log.d("TFLitePredictioninINt","Predicted  Score : $intPrediction")
                        val vitals = Vitals(
                            patientId = patientId,
                            date = date,
                            Age = age.toIntOrNull()?:0,
                            Systolic_BP = systolicBP.toFloatOrNull()?:0f,
                            Diastolic = diastolic.toFloatOrNull()?:0f,
                            BS = bs.toFloatOrNull()?:0f,
                            Body_Temp = bodyTemp.toFloatOrNull()?:0f,
                            BMI = bmi.toFloatOrNull() ?: 0f,
                            Heart_Rate = heartRate.toFloatOrNull()?:0f,
                            Previous_Complications = previousComplications ?: 0,
                            Preexisting_Diabetes = preexistingDiabetes ?: 0,
                            Gestational_Diabetes = gestationalDiabetes ?: 0,
                            Mental_Health = mentalHealth ?: 0,
                            predictedRisk = intPrediction,
                        )

                          vitalsViewModel.insertVitals(vitals)

                        val week = getWeek(startDateStr = patient?.lastCheckup?:"", endDateStr = date)
                        val finalweek = (patient?.pregnancyWeek?:0) + week

                        patientViewModel.updatePatient(patient = Patient(patientId,patient?.name?:"",
                            finalweek,date
                        ))

                          navController.popBackStack()
                    }


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save & Predict Risk",style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    label: String,
    selectedValue: Int?,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Yes" to 1, "No" to 0)
    val selectedText = when (selectedValue) {
        1 -> "Yes"
        0 -> "No"
        else -> ""
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (text, value) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onValueChange(value)
                        expanded = false
                    }
                )
            }
        }
    }
}


fun getWeek(startDateStr:String,endDateStr:String):Int{

    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val startDate = sdf.parse(startDateStr)
    val endDate = sdf.parse(endDateStr)

    if(startDate == null || endDate == null ){
        return 0
    }


    val diffrence = endDate.time - startDate.time

     val week = (diffrence / (1000 * 60 * 60 * 24*7)).toInt()

    return week

}

















