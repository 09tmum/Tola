package com.example.tola.ui

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
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tola.ui.screens.ClaimOwnershipScreen
import com.example.tola.ui.screens.HomeScreen
import com.example.tola.ui.screens.ItemDetailsScreen
import com.example.tola.ui.screens.ProfileScreen
import com.example.tola.ui.screens.ReportItemScreen
import com.example.tola.ui.screens.SearchScreen
import com.example.tola.ui.screens.WelcomeLoginScreen

sealed class Screen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {

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
        route = "report_item",
        title = "Report Item"
    )

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

            startDestination = Screen.WelcomeLogin.route,

            modifier = Modifier.padding(innerPadding)
        ) {

            // Login
            composable(Screen.WelcomeLogin.route) {

                WelcomeLoginScreen(
                    onLoginSuccess = {

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
                        navController.navigate(Screen.ReportItem.route)
                    },

                    onReportFound = {
                        navController.navigate(Screen.ReportItem.route)
                    },

                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            // Search
            composable(Screen.Search.route) {

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

            // Profile
            composable(Screen.Profile.route) {

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

            // Report Item
            composable(Screen.ReportItem.route) {

                ReportItemScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Item Details
            composable(Screen.ItemDetails.route) { backStackEntry ->

                val itemId =
                    backStackEntry.arguments?.getString("itemId")
                        ?: ""

                ItemDetailsScreen(
                    itemId = itemId,

                    onBack = {
                        navController.popBackStack()
                    },

                    onClaimClick = {
                        navController.navigate(
                            Screen.ClaimOwnership.createRoute(itemId)
                        )
                    }
                )
            }

            // Claim Ownership
            composable(Screen.ClaimOwnership.route) { backStackEntry ->

                val itemId =
                    backStackEntry.arguments?.getString("itemId")
                        ?: ""

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
    }
}

