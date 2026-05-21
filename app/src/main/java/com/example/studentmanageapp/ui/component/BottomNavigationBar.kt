package com.example.studentmanageapp.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
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
                                    popUpTo(Screen.Main.route) { inclusive = false }
                                }
                                screen == Screen.Attendance && currentRoute == Screen.Check.route -> {
                                    popUpTo(Screen.Check.route) { inclusive = true }
                                }
                                else -> {
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
                        maxLines = 1,                 // ✅ 1줄 고정
                        overflow = TextOverflow.Ellipsis, // ✅ 길면 … 처리
                        style = MaterialTheme.typography.labelSmall // ✅ 글자 크기 낮춤(겹침 방지)
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
