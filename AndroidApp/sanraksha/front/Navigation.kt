package com.example.sanraksha.front

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation(
    viewModel:PatientViewModel = viewModel(),
    navController : NavHostController = rememberNavController()
){
    NavHost(navController = navController,
        startDestination = Screens.Home.route
        ){

        composable(Screens.Home.route){
            val patients by viewModel.getAllPatients.collectAsState(initial = emptyList())

            HomeScreen(patients = patients,
                onPatientClick = {patient ->
                    navController.navigate(Screens.Profile.passId(patient.id))
                },
                onAddClick = {
                    navController.navigate(Screens.AddPatient.route)
                }
                )
        }

        composable(Screens.AddPatient.route) {
            AddPatientScreen(onSaveClick = {name,week,date ->
                viewModel.addPatient(
                    Patient(
                        name = name,
                        pregnancyWeek = week,
                        lastCheckup = date
                    )
                )
                navController.popBackStack()

            }, onBackClick = {
                navController.popBackStack()
            })
        }

        composable(
            route=Screens.Profile.route,
            arguments = listOf(navArgument("patientId"){type = NavType.LongType})
            ) {backStackEntry ->
            val patientId = backStackEntry.arguments?.getLong("patientId")?: 0L
            ProfileScreen(patientId = patientId,navController = navController)
        }

        composable(
            route = Screens.RecordVitals.route,
            arguments = listOf(navArgument("patientId"){
                type = NavType.LongType
            })
        ){ backStackEntry->
            val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
            RecordVitalsScreen(patientId = patientId, navController = navController)

        }

    }




}