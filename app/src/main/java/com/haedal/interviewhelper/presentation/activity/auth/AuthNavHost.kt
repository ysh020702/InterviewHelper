package com.haedal.interviewhelper.presentation.activity.auth

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
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
                    onSuccess(context)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("signup") {
            val viewModel: AuthViewModel = hiltViewModel()

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
    val intent = Intent(context, HomeActivity::class.java)
    val options = ActivityOptions.makeCustomAnimation(
        context,
        android.R.anim.fade_in,
        android.R.anim.fade_out
    )
    context.startActivity(intent, options.toBundle())
    (context as? Activity)?.finish()

}