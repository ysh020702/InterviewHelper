package com.haedal.interviewhelper.presentation.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.haedal.interviewhelper.R


/*
안드로이드 클린 프로젝트 구조

1. Domain (UI나 데이터 저장소에 전혀 의존하지 않으며, 앱의 규칙/정책이 바뀌면 이 계층이 수정)
    - repository(interface)
    - usecase (repository interface 를 통해 데이터 요청)
2. Data (외부 시스템과 통신, DB, 서버, 파일 등)
    - repository(interface implement) = repository 의 실제 구현체
    - datasource
    - mapper
3. Presentation (사용자의 입력을 받고, 출력)
    - viewModel 에서 usecase 호출
    - ui : activity등 화면 구현

4. Retrofit & OkHttp를 사용하여 해달 서버와 직접 통신

5. 해달 서버는 FastAPI로 구현
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}