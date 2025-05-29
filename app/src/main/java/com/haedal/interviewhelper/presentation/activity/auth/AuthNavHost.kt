package com.haedal.interviewhelper.presentation.activity.auth

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.home.HomeActivity
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel

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
            val viewModel: UserViewModel = hiltViewModel()

            LoginScreen(
                viewModel = viewModel,
                onSuccess = {
                    onSuccess(context)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("signup") {
            val viewModel: UserViewModel = hiltViewModel()

            SignUpScreen(
                viewModel = viewModel,
                onSuccess = {
                    onSuccess(context)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

fun onSuccess(context: Context) {
    moveActivity<HomeActivity>(context = context, finishFlag = true)
}