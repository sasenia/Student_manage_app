package com.example.studentmanageapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.studentmanageapp.ui.navigation.NavigationGraph
import com.example.studentmanageapp.ui.component.BottomNavigationBar
import com.example.studentmanageapp.ui.theme.StudentManageAppTheme
import com.example.studentmanageapp.viewmodel.StudentViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentManageAppTheme {
                val navController = rememberNavController()
                val studentViewModel: StudentViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = androidx.compose.ui.Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavigationGraph(
                            navController = navController,
                            viewModel = studentViewModel
                        )
                    }
                }
            }
        }
    }
}