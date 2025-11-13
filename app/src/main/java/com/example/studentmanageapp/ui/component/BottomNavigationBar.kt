package com.example.studentmanageapp.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studentmanageapp.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.StudentList,
        Screen.Attendance,
        Screen.Praise,
        Screen.Homework,
        Screen.Activity,

        Screen.Options
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = when (screen) {
                    Screen.Attendance -> currentRoute?.startsWith(Screen.Attendance.route) == true
                    else -> currentRoute == screen.route
                },
                onClick = {
                    val targetRoute = if (screen == Screen.Attendance) {
                        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        Screen.Attendance.routeWithDate(today)
                    } else {
                        screen.route
                    }

                    if (currentRoute != targetRoute) {
                        navController.navigate(targetRoute) {
                            when {
                                screen == Screen.Options -> {
                                    // ✅ 옵션 화면은 항상 초기화
                                    popUpTo(Screen.Main.route) { inclusive = false }
                                }
                                screen == Screen.Attendance && currentRoute == Screen.Check.route -> {
                                    // ✅ CheckScreen에서 출석 누르면 CheckScreen 제거
                                    popUpTo(Screen.Check.route) { inclusive = true }
                                }
                                else -> {
                                    // ✅ 다른 화면은 상태 복원
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    restoreState = true
                                }
                            }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
                    indicatorColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}