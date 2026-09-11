package app.hallyu.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.hallyu.ui.auth.LoginScreen
import app.hallyu.ui.auth.SignupScreen
import app.hallyu.ui.main.HomeScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val isAlreadyLoggedIn = remember {
        try {
            Firebase.auth.currentUser != null
        } catch (e: Exception) {
            false
        }
    }

    val startDest = if (isAlreadyLoggedIn) "home" else "login"

    NavHost(navController = navController, startDestination = startDest) {
        composable("login") {
            LoginScreen(
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = { 
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onSignupSuccess = { 
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
