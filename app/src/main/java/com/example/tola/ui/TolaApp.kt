package com.example.tola.ui

import androidx.compose.ui.tooling.preview.Preview
import com.example.tola.ui.theme.TolaTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import com.example.tola.ui.screens.ClaimOwnershipScreen
import com.example.tola.ui.screens.FoundItemReportScreen
import com.example.tola.ui.screens.HomeScreen
import com.example.tola.ui.screens.ItemDetailsScreen
import com.example.tola.ui.screens.ProfileScreen
import com.example.tola.ui.screens.ReportItemScreen
import com.example.tola.ui.screens.SearchScreen
import com.example.tola.ui.screens.SplashScreen
import com.example.tola.ui.screens.WelcomeLoginScreen
import com.example.tola.ui.screens.AdminCategoryManagementScreen
import com.example.tola.ui.screens.AdminClaimReviewScreen
import com.example.tola.ui.screens.AdminNotificationsScreen
import com.example.tola.ui.screens.AdminReportManagementScreen
import com.example.tola.ui.screens.AdminUserManagementScreen
import com.example.tola.ui.screens.AdminDashboardScreen
import com.example.tola.ui.TolaUserRole

sealed class Screen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {

    data object Splash : Screen(
        route = "splash",
        title = "Splash"
    )

    data object WelcomeLogin : Screen(
        route = "welcome_login",
        title = "Login"
    )

    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Search : Screen(
        route = "search",
        title = "Search",
        icon = Icons.Default.Search
    )

    data object Profile : Screen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )

    data object ReportItem : Screen(
        route = "report_item?isLost={isLost}",
        title = "Report Item"
    ) {
        fun createRoute(isLost: Boolean = true): String {
            return "report_item?isLost=$isLost"
        }
    }

    data object ItemDetails : Screen(
        route = "item_details/{itemId}",
        title = "Item Details"
    ) {
        fun createRoute(itemId: String): String {
            return "item_details/$itemId"
        }
    }

    data object ClaimOwnership : Screen(
        route = "claim_ownership/{itemId}",
        title = "Claim Ownership"
    ) {
        fun createRoute(itemId: String): String {
            return "claim_ownership/$itemId"
        }
    }

    data object FoundItemReport : Screen(
        route = "found_item_report/{itemId}",
        title = "Found Item Report"
    ) {
        fun createRoute(itemId: String): String {
            return "found_item_report/$itemId"
        }
    }

    data object AdminDashboard : Screen(
        route = "admin_dashboard",
        title = "Admin Dashboard"
    )

    object AdminUsers : Screen(
        route = "admin_users",
        title = "Admin Users"
    )

    object AdminReports : Screen(
        route = "admin_reports",
        title = "Admin Reports"
    )

    object AdminClaims : Screen(
        route = "admin_claims",
        title = "Admin Claims"
    )

    object AdminCategories : Screen(
        route = "admin_categories",
        title = "Admin Categories"
    )

    object AdminNotifications : Screen(
        route = "admin_notifications",
        title = "Admin Notifications"
    )

}

@Composable
fun TolaApp() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    var currentRole by rememberSaveable {
        mutableStateOf<TolaUserRole?>(null)
    }

    val redirectToAllowedScreen: () -> Unit = {
        val destination = when (currentRole) {
            TolaUserRole.STUDENT ->
                Screen.Home.route
            TolaUserRole.ADMIN ->
                Screen.AdminDashboard.route
            null -> Screen.WelcomeLogin.route
        }

        navController.navigate(destination) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {

                    val tabs = listOf(
                        Screen.Home,
                        Screen.Search,
                        Screen.Profile
                    )

                    tabs.forEach { screen ->

                        NavigationBarItem(
                            selected = currentRoute == screen.route,

                            onClick = {
                                navController.navigate(screen.route) {

                                    popUpTo(
                                        navController.graph.findStartDestination().id
                                    ) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },

                            icon = {
                                Icon(
                                    imageVector = screen.icon!!,
                                    contentDescription = screen.title
                                )
                            },

                            label = {
                                Text(screen.title)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,

            startDestination = Screen.Splash.route,

            modifier = Modifier.padding(innerPadding)
        ) {

            // Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.WelcomeLogin.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // Login
            composable(Screen.WelcomeLogin.route) {

                WelcomeLoginScreen(
                    onLoginSuccess = { verfiedRole ->
                        currentRole = verfiedRole

                        val destination = when (verfiedRole) {
                            TolaUserRole.STUDENT ->
                                Screen.Home.route

                            TolaUserRole.ADMIN ->
                                Screen.AdminDashboard.route
                        }

                        navController.navigate(Screen.Home.route) {

                            popUpTo(Screen.WelcomeLogin.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }

            // Home
            composable(Screen.Home.route) {
                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {
                    HomeScreen(
                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        },

                        onItemClick = { itemId ->
                            navController.navigate(
                                Screen.ItemDetails.createRoute(itemId)
                            )
                        },

                        onReportLost = {
                            navController.navigate(Screen.ReportItem.createRoute(isLost = true))
                        },

                        onReportFound = {
                            navController.navigate(Screen.ReportItem.createRoute(isLost = false))
                        },

                        onProfileClick = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }
            }

            // Search
            composable(Screen.Search.route) {

                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {

                    SearchScreen(
                        onItemClick = { itemId ->
                            navController.navigate(
                                Screen.ItemDetails.createRoute(itemId)
                            )
                        },

                        onProfileClick = {
                            navController.navigate(Screen.Profile.route)
                        },

                        onHomeClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }

            // Profile
            composable(Screen.Profile.route) {

                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {
                    ProfileScreen(
                        onHomeClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = true
                                }
                            }
                        },
                        onLogout = {

                            navController.navigate(Screen.WelcomeLogin.route) {

                                popUpTo(0) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        },

                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }
            }

            // Report Item
            composable(
                route = Screen.ReportItem.route,
                arguments = listOf(
                    navArgument("isLost") {
                        type = NavType.BoolType
                        defaultValue = true
                    }
                )
            ) { backStackEntry ->
                val isLost = backStackEntry.arguments?.getBoolean("isLost") ?: true

                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {
                    ReportItemScreen(
                        initialIsLost = isLost,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            // Item Details
            composable(Screen.ItemDetails.route) { backStackEntry ->

                val itemId =
                    backStackEntry.arguments?.getString("itemId")
                        ?: ""

                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {
                    ItemDetailsScreen(
                        itemId = itemId,

                        onBack = {
                            navController.popBackStack()
                        },

                        onClaimClick = {
                            navController.navigate(
                                Screen.ClaimOwnership.createRoute(itemId)
                            )
                        },

                        onReportFoundItemClick = { id ->
                            navController.navigate(
                                Screen.FoundItemReport.createRoute(id)
                            )
                        }
                    )
                }
            }

            // Claim Ownership
            composable(Screen.ClaimOwnership.route) { backStackEntry ->

                val itemId =
                    backStackEntry.arguments?.getString("itemId")
                        ?: ""

                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {

                    ClaimOwnershipScreen(
                        itemId = itemId,

                        onBack = {
                            navController.popBackStack()
                        },

                        onClaimSubmitted = {

                            navController.navigate(Screen.Home.route) {

                                popUpTo(Screen.Home.route) {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            // Found Item Report
            composable(Screen.FoundItemReport.route) { backStackEntry ->

                val itemId =
                    backStackEntry.arguments?.getString("itemId")
                        ?: ""

                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.STUDENT,
                    onAccessDenied = redirectToAllowedScreen
                ) {
                    FoundItemReportScreen(
                        itemId = itemId,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSubmitReport = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            // Admin Dashboard
            composable(Screen.AdminDashboard.route) {
                RoleGate(
                    currentRole = currentRole,
                    requiredRole = TolaUserRole.ADMIN,
                    onAccessDenied = redirectToAllowedScreen
                ) {
                    AdminDashboardScreen(
                        onUsersClick = {
                            navController.navigate(Screen.AdminUsers.route)
                        },
                        onReportsClick = {
                            navController.navigate(Screen.AdminReports.route)
                        },
                        onClaimsClick = {
                            navController.navigate(Screen.AdminClaims.route)
                        },
                        onCategoriesClick = {
                            navController.navigate(Screen.AdminCategories.route)
                        },
                        onNotificationsClick = {
                            navController.navigate(Screen.AdminNotifications.route)
                        },
                        onLogoutClick = {
                            currentRole = null
                            navController.navigate(Screen.WelcomeLogin.route) {
                                popUpTo(navController.graph.id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            // Admin User Management
            composable(Screen.AdminUsers.route) {
                RoleGate(currentRole, TolaUserRole.ADMIN, redirectToAllowedScreen) {
                    AdminUserManagementScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Admin Report Management
            composable(Screen.AdminReports.route) {
                RoleGate(currentRole, TolaUserRole.ADMIN, redirectToAllowedScreen) {
                    AdminReportManagementScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Admin Claim Review
            composable(Screen.AdminClaims.route) {
                RoleGate(currentRole, TolaUserRole.ADMIN, redirectToAllowedScreen) {
                    AdminClaimReviewScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Admin category management
            composable(Screen.AdminCategories.route) {
                RoleGate(currentRole, TolaUserRole.ADMIN, redirectToAllowedScreen) {
                    AdminCategoryManagementScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Admin notifications management
            composable(Screen.AdminNotifications.route) {
                RoleGate(currentRole, TolaUserRole.ADMIN, redirectToAllowedScreen) {
                    AdminNotificationsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleGate(
    currentRole: TolaUserRole?,
    requiredRole: TolaUserRole,
    onAccessDenied: () -> Unit,
    content: @Composable () -> Unit
){
    if (currentRole == requiredRole) {
        content()
    } else {
        LaunchedEffect(currentRole, requiredRole) {
            onAccessDenied()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TolaAppPreview() {
    TolaTheme {
        TolaApp()
    }
}

