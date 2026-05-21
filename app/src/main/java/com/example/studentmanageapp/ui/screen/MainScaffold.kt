import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studentmanageapp.ui.component.ScrollBottomBar
import com.example.studentmanageapp.ui.navigation.NavigationGraph
import com.example.studentmanageapp.ui.navigation.Screen
import com.example.studentmanageapp.ui.navigation.bottomItems
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun MainScaffold(
    navController: NavHostController,
    viewModel: StudentViewModel
) {
    val currentBackStackEntry =
        navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        // ✅ 중요: Scaffold의 자동 시스템 인셋 처리 OFF
        contentWindowInsets = WindowInsets(0),

        bottomBar = {
            ScrollBottomBar(
                items = bottomItems,
                selectedRoute = currentRoute,
                onItemClick = { item ->
                    if (item.screen == Screen.Attendance) {
                        val today = java.time.LocalDate.now().toString()
                        navController.navigate(
                            Screen.Attendance.routeWithDate(today)
                        ) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                // ✅ bottomBar만 시스템 하단바 위로 밀어올림
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.navigationBars
                )
            )
        }
    ) { paddingValues ->
        // ✅ content는 Scaffold가 계산한 padding만 사용
        NavigationGraph(
            navController = navController,
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}
