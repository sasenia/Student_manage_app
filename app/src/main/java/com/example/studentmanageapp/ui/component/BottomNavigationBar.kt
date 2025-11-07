package com.example.studentmanageapp.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studentmanageapp.ui.navigation.Screen
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.StudentList,
        Screen.Attendance,
        Screen.Homework,
        Screen.Activity,
        Screen.Praise,
        Screen.Options
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondary // 연한 연두색 배경
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        tint = if (currentRoute == screen.route)
                            MaterialTheme.colorScheme.primary // 선택된 아이콘: 연두 포인트
                        else
                            MaterialTheme.colorScheme.onSecondary // 선택 안됨: 대비 텍스트
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (currentRoute == screen.route)
                            MaterialTheme.colorScheme.primary

                        else
                            MaterialTheme.colorScheme.onSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
                    indicatorColor = MaterialTheme.colorScheme.tertiary // 선택된 배경 강조
                )
            )
        }
    }
}