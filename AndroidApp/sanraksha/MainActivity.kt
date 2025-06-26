package com.example.sanraksha

import android.os.Bundle
import android.security.identity.AccessControlProfileId
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sanraksha.front.HomeScreen
import com.example.sanraksha.front.Navigation
import com.example.sanraksha.front.Patient
import com.example.sanraksha.ui.theme.SanrakshaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanrakshaTheme {
                    // ApiTestScreen()
                //                        Age = 30,
                //                        Systolic_BP = 120f,
                //                        Diastolic = 80f,
                //                        BS = 6.3f,
                //                        Body_Temp = 99.1f,
                //                        BMI = 28f,
                //                        Previous_Complications = 1,
                //                        Preexisting_Diabetes = 1,
                //                        Gestational_Diabetes = 0,
                //                        Mental_Health = 1,
                //                        Heart_Rate = 85f

//                val dummyRiskInput = riskInput(
//                    30,120,80,6.3f,
//                    99.1f,28f,1,
//                    1,0,1,80
//                )
//                val standardizedInput = DataStandardization(dummyRiskInput)
//
//                val dummyInput = floatArrayOf(
//                    standardizedInput.BS.toFloat(),
//                    standardizedInput.BMI.toFloat(),
//                    standardizedInput.Age.toFloat(),
//                    standardizedInput.HeartRate.toFloat(),
//                    standardizedInput.SystolicBP.toFloat(),
//                    standardizedInput.DiastolicBP.toFloat(),
//                    standardizedInput.FinalRiskScore.toFloat()
//                )
//
//
//                val predictor = RiskPredictor(this)
//                val prediction = predictor.predict(dummyInput)
//
//                val IntPrediction = if(prediction > 0.5f)1 else 0
//
//                Log.d("TFLitePrediction","Predicted Risk Score : $prediction")
//                Log.d("TFLitePredictioninINt","Predicted  Score : $IntPrediction")

                Surface {
                  //  val pateint1 = Patient("atul",43,"23/2/2")
                  //  val pateint2 = Patient("atul",43,"23/2/2")
                   // val pateint3 = Patient("atul",43,"23/2/2")

                  // HomeScreen(patients = listOf(pateint1,pateint2,pateint3),{},{})

                    Navigation()
                }
            }
        }
    }
}

