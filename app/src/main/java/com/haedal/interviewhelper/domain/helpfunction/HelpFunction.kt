package com.haedal.interviewhelper.domain.helpfunction

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent

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