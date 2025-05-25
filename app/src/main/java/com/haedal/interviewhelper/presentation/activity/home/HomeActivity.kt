package com.haedal.interviewhelper.presentation.activity.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.domain.helpfunction.*
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.activity.interview.InterviewActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            val name = viewModel.loadUserName()
            val feedbackList = viewModel.loadUserFeedbacks()
            val contentList = loadGlobalContents()
            val dailyQuestion = loadDailyQuestion()

            withContext(Dispatchers.Main) {
                setContent {
                    InterviewHelperTheme {
                        HomeScreen(
                            userName = name,
                            feedbackList = feedbackList,
                            contentList = contentList,
                            onStartInterview = {
                                moveActivity<InterviewActivity>(context = this@HomeActivity, finishFlag = false)
                            },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                moveActivity<AuthActivity>(context = this@HomeActivity, finishFlag = true)
                            }
                        )
                    }
                }
            }
        }
    }


}