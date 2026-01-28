package com.example.cse489labapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cse489labapp.ui.screens.form.FormScreen
import com.example.cse489labapp.ui.screens.overview.OverviewScreen
import com.example.cse489labapp.ui.screens.records.RecordsScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "overview",
        modifier = modifier
    ) {
        composable("overview") { OverviewScreen(navController) }
        composable("records") { RecordsScreen(navController) }
        composable("form") { FormScreen(navController) }
    }
}