package com.haedal.interviewhelper.presentation.activity.auth

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.haedal.interviewhelper.presentation.activity.home.HomeActivity
import com.haedal.interviewhelper.presentation.viewmodel.AuthViewModel

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onLogin = { navController.navigate("login") },
                onSignUp = { navController.navigate("signup") }
            )
        }
        composable("login") {
            val viewModel: AuthViewModel = hiltViewModel()

            LoginScreen(
                viewModel = viewModel,
                onSuccess = {
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as? Activity)?.finish()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("signup") {
            val viewModel: AuthViewModel = hiltViewModel()

            SignUpScreen(
                viewModel = viewModel,
                onSuccess = {
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as? Activity)?.finish()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}