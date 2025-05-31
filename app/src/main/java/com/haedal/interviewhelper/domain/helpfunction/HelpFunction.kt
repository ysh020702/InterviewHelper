package com.haedal.interviewhelper.domain.helpfunction

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

const val WEB_CLIENT_ID = "627581359813-pbtaot4qthmbu9pjie97tu8d2rnt0g9o.apps.googleusercontent.com"


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

suspend fun loadPopularQuestions(): List<Pair<String, String>>{
    val snapshot = FirebaseDatabase.getInstance().getReference("popularQuestions").get().await()
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

@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Vibrator::class.java)
    if (vibrator != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}


fun insertDummyDataForTestUser() {
    val db = FirebaseDatabase.getInstance().reference
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // 1. Daily Question
    db.child("dailyQuestion").child("text").setValue(
        "최근에 당신이 도전한 일은 무엇인가요?"
    )

    // 2. Global Contents (4개)
    val contents = mapOf(
        "item1" to mapOf(
            "title" to "모범 답변 듣기",
            "description" to "실제 인터뷰 예시 음성을 들어보세요."
        ),
        "item2" to mapOf(
            "title" to "면접관이 좋아하는 키워드",
            "description" to "자주 언급되는 핵심 표현들을 확인하세요."
        ),
        "item3" to mapOf(
            "title" to "AI 피드백 예시 보기",
            "description" to "AI가 어떻게 응답을 평가하는지 알아보세요."
        ),
        "item4" to mapOf(
            "title" to "면접 체크리스트",
            "description" to "준비사항을 빠짐없이 확인해보세요."
        )
    )
    db.child("contents").setValue(contents)

    // 3. 개인별 Feedbacks (4개)
    val feedbacks = mapOf(
        "item1" to mapOf(
            "question" to "자신의 단점을 말해보세요.",
            "feedback" to "좀 더 자신감 있는 태도로 말해보세요."
        ),
        "item2" to mapOf(
            "question" to "리더십 경험을 말해보세요.",
            "feedback" to "구체적인 팀 상황을 예로 들면 좋아요."
        ),
        "item3" to mapOf(
            "question" to "성공적인 프로젝트 경험은?",
            "feedback" to "성과를 수치로 표현해보면 좋습니다."
        ),
        "item4" to mapOf(
            "question" to "갈등을 어떻게 해결하나요?",
            "feedback" to "본인의 역할을 중심으로 설명해보세요."
        )
    )
    db.child("users").child(uid).child("feedbacks").setValue(feedbacks)

    // 4. 인기 질문 (popularQuestions)
    val popularQuestions = mapOf(
        "item1" to mapOf(
            "title" to "지원 동기를 말해주세요.",
            "description" to "왜 이 회사에 지원했는지 간결하고 진정성 있게 설명해보세요."
        ),
        "item2" to mapOf(
            "title" to "가장 기억에 남는 경험은?",
            "description" to "직무와 관련된 경험 중 인상 깊은 사례를 중심으로 설명해보세요."
        ),
        "item3" to mapOf(
            "title" to "5년 후 본인의 모습은?",
            "description" to "장기적인 커리어 목표와 회사에서의 성장 계획을 보여주세요."
        ),
        "item4" to mapOf(
            "title" to "협업 중 갈등이 생겼을 때 어떻게 대응하나요?",
            "description" to "문제 해결 방식과 소통 능력을 드러낼 수 있는 사례를 들어보세요."
        )
    )
    db.child("popularQuestions").setValue(popularQuestions)
}
