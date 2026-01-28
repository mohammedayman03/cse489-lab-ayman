package com.example.cse489labapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import api.LandmarkRepository
import com.example.cse489labapp.ui.AppNavigation
import com.example.cse489labapp.ui.theme.CSE489LabAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CSE489LabAppTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = backStack?.destination?.route == "overview",
                    onClick = { navController.navigate("overview") },
                    label = { Text("Overview") },
                    icon = { Icon(Icons.Filled.Map, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = backStack?.destination?.route == "records",
                    onClick = { navController.navigate("records") },
                    label = { Text("Records") },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = backStack?.destination?.route == "form",
                    onClick = { navController.navigate("form") },
                    label = { Text("New Entry") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
    }

    // ------------------------------
    // API Test (run only once)
    LaunchedEffect(Unit) {
        try {
            val repo = LandmarkRepository()
            val response = repo.getAllLandmarks() // suspending call, safe in IO dispatcher internally
            if (response.isSuccessful) {
                Log.d("API_TEST", "Landmarks: ${response.body()}")
            } else {
                Log.e("API_TEST", "Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("API_TEST", "Exception: $e")
        }
    }
}