package com.example.sanraksha

import androidx.compose.runtime.Composable
import kotlin.math.exp

//['BS', 'BMI', 'Age', 'Heart Rate','Systolic BP','Diastolic', 'Final_Risk_Score']
    data class StandardizedOutput(
        val BS: Double,
        val BMI: Double,
        val Age: Double,
        val HeartRate: Double,
        val SystolicBP: Double,
        val DiastolicBP: Double,
        val FinalRiskScore: Double
    )

fun DataStandardization(riskInput: riskInput):StandardizedOutput{

    //model1
    val weight1ofmodel1 = 2.2844805234162426
    val weight2ofmodel1 = 4.302126981282638
    val weight3ofmodel1 = 4.80806462798848
    val weight4ofmodel1 = 2.1557354423347066

    val biasingofmodel1 = -3.3629417153571874

    //col1 = ['Previous Complications','Preexisting Diabetes','Gestational Diabetes','Mental Health']
    val model1result = weight1ofmodel1*riskInput.Previous_Complications +
                        weight2ofmodel1*riskInput.Preexisting_Diabetes +
                        weight3ofmodel1*riskInput.Gestational_Diabetes +
                        weight4ofmodel1*riskInput.Mental_Health + biasingofmodel1

    val  riskScore =  sigmoid(model1result)

    //model2
    //cols = ['is_low_bmi', 'is_high_bmi', 'is_low_bp', 'is_high_bp', 'is_high_bs', 'is_high_hr', 'is_low_hr']

    //X_train_imputed['is_low_bmi'] = (X_train_imputed['BMI'] < 18.5).astype(int)
    //X_train_imputed['is_high_bmi'] = (X_train_imputed['BMI'] > 30).astype(int)
    //X_train_imputed['is_low_bp'] = ((X_train_imputed['Systolic BP'] < 90) | (X_train_imputed['Diastolic'] < 60)).astype(int)
    //X_train_imputed['is_high_bp'] = ((X_train_imputed['Systolic BP'] > 140) | (X_train_imputed['Diastolic'] > 90)).astype(int)
    //X_train_imputed['is_high_bs'] = (X_train_imputed['BS'] > 7.8).astype(int)
    //X_train_imputed['is_high_hr'] = (X_train_imputed['Heart Rate'] > 100).astype(int)
    //X_train_imputed['is_low_hr'] = (X_train_imputed['Heart Rate'] < 60).astype(int)

    val is_low_bmi =   if (riskInput.BMI <= 18.5) 1 else 0
    val is_high_bmi = if (riskInput.BMI >= 30) 1 else 0
    val is_low_bp = if(riskInput.Systolic_BP <= 90 || riskInput.Diastolic <= 60)1 else 0
    val is_high_bp = if(riskInput.Systolic_BP >= 140 || riskInput.Diastolic >= 90)1 else 0
    val is_high_bs = if(riskInput.BS >= 7.8)1 else 0
    val is_high_hr = if(riskInput.Heart_Rate >= 100)1 else 0
    val is_low_hr = if(riskInput.Heart_Rate <= 60)1 else 0

    val weight1ofmodel2 = 2.9534001075263436
    val weight2ofmodel2 = 2.541888001487591
    val weight3ofmodel2 = -0.1546863106840409
    val weight4ofmodel2 = 1.288581542577755
    val weight5ofmodel2 = 4.129221296613417
    val weight6ofmodel2 = 0.0
    val weight7ofmodel2 = 1.274482764741299

    val biasingofmodel2 = -1.7934540919858957

    val model2result = weight1ofmodel2*is_low_bmi + weight2ofmodel2*is_high_bmi +
                       weight3ofmodel2*is_low_bp + weight4ofmodel2*is_high_bp +
                       weight5ofmodel2*is_high_bs + weight6ofmodel2*is_high_hr +
                       weight7ofmodel2*is_low_hr + biasingofmodel2

    val riskScore_Abn = sigmoid(model2result)

    //model3
    //['Risk Score','Risk Score Abn']
    val weight1ofmodel3 = 6.011838573897318
    val weight2ofmodel3 = 3.571762088775957

    val biasingofmodel3 = -4.352072036198044

    val model3result = weight1ofmodel3*riskScore + weight2ofmodel3*riskScore_Abn + biasingofmodel3

    val final_risk_score = sigmoid(model3result)


//{"1": [2.2844805234162426, 4.302126981282638, 4.80806462798848, 2.1557354423347066],
// "2": [2.9534001075263436, 2.541888001487591, -0.1546863106840409, 1.288581542577755, 4.129221296613417, 0.0, 1.274482764741299],
// "3": [6.011838573897318, 3.571762088775957]}
// {"1": -3.3629417153571874, "2": -1.7934540919858957, "3": -4.352072036198044}



    //Standardization for the final result
    //['BS', 'BMI', 'Age', 'Heart Rate','Systolic BP','Diastolic', 'Final_Risk_Score']
    // (x - mean)/standard deviation


    val  standard_BS = (riskInput.BS - 7.47609921082299)/3.029857870760345
    val  standard_BMI = (riskInput.BMI - 23.3991312967708 )/3.795296658503613
    val  standard_Age = (riskInput.Age -  27.067643742953777)/8.946034682534425
    val  standard_heart_rate = (riskInput.Heart_Rate - 75.87260428410372)/7.2294391475673185
    val  standard_Systolic_BP = (riskInput.Systolic_BP - 116.61104847801579)/18.416513216607225
    val standard_Diastolic_BP = (riskInput.Diastolic - 77.07215332581737 )/14.206847544098215

    return StandardizedOutput(
        BS = standard_BS,
        BMI = standard_BMI,
        Age = standard_Age,
        HeartRate = standard_heart_rate,
        SystolicBP = standard_Systolic_BP,
        DiastolicBP = standard_Diastolic_BP,
        FinalRiskScore = final_risk_score
    )


}


fun sigmoid(x: Double): Double {
    return 1.0 / (1.0 + exp(-x))
}