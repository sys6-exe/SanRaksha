package com.example.sanraksha.front

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sanraksha.R
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientScreen(
    onSaveClick:(String,Int,String)->Unit,
    onBackClick:()->Unit
){
    var name by remember {mutableStateOf("")}
    var week by remember {mutableStateOf("")}
    var lastCheckupDate by remember {mutableStateOf("")}

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember{
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                lastCheckupDate = String.format(Locale.US, "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
            },
            year, month, day
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Add New Patient")},
                navigationIcon = {
                    IconButton(onClick = {onBackClick()}){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                            )
                    }
                }
            )
        }
    ){padding ->
        Column(modifier = Modifier.padding(padding)
            .padding(horizontal = 24.dp , vertical = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text("Patient Name", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                leadingIcon = {
                    Icon(Icons.Default.Person,contentDescription = "Patient Name")
                },
                placeholder = {Text("Enter name")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Text("Pregnancy Week", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = week,
                onValueChange = { week = it },
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Week")
                },
                placeholder = { Text("Enter week") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Last Checkup Date", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = lastCheckupDate,
                onValueChange = { },
                leadingIcon = {
                    Image(painter = painterResource(id=R.drawable.baseline_calendar_today_24),
                        contentDescription = "Checkup Date"
                        )
                },
                placeholder = { Text("DD-MM-YYYY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .clickable { datePickerDialog.show() }
                ,
                enabled = false,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank() && week.isNotBlank() && lastCheckupDate.isNotBlank()) {
                        onSaveClick(name, week.toIntOrNull() ?: 0, lastCheckupDate)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotBlank() && week.isNotBlank() && lastCheckupDate.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", style = MaterialTheme.typography.labelLarge)
            }

        }

    }

}