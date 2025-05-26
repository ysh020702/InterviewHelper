package com.haedal.interviewhelper.presentation.activity.interview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme

@Composable
fun InterviewScreen(name: String){
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InterviewHelperTheme {
        InterviewScreen("Android")
    }
}