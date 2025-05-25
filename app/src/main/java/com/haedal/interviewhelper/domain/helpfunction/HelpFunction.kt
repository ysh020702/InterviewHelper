package com.haedal.interviewhelper.domain.helpfunction

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

inline fun <reified T : Activity> moveActivity(context: Context, finishFlag: Boolean) {
    val intent = Intent(context, T::class.java)
    val options = ActivityOptions.makeCustomAnimation(
        context,
        android.R.anim.fade_in,
        android.R.anim.fade_out
    )
    context.startActivity(intent, options.toBundle())
    if(finishFlag) (context as? Activity)?.finish()
}

suspend fun loadGlobalContents(): List<Pair<String, String>> {
    val snapshot = FirebaseDatabase.getInstance().getReference("contents").get().await()
    return snapshot.children.mapNotNull {
        val title = it.child("title").getValue(String::class.java)
        val desc = it.child("description").getValue(String::class.java)
        if (title != null && desc != null) title to desc else null
    }
}

suspend fun loadDailyQuestion(): String {
    val snapshot = FirebaseDatabase.getInstance().getReference("dailyQuestion").child("text").get().await()
    return snapshot.getValue(String::class.java) ?: "오늘의 질문을 불러올 수 없습니다."
}

const val WEB_CLIENT_ID = "627581359813-pbtaot4qthmbu9pjie97tu8d2rnt0g9o.apps.googleusercontent.com"