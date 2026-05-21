package com.example.studentmanageapp

import MainScaffold
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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

                MainScaffold(
                    navController = navController,
                    viewModel = studentViewModel
                )
            }
        }
    }
}
