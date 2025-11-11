package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.studentmanageapp.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("옵션", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = {
                navController.navigate(Screen.StudentAdd.route) {
                    popUpTo("optionScreen") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(
                "학생 관리",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }

    }
}