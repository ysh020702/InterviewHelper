package com.haedal.interviewhelper.presentation.activity.auth

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.White
import com.haedal.interviewhelper.presentation.viewmodel.AuthViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val loginResult = viewModel.loginResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding(), // 키보드 대응
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로그인",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "이메일과 비밀번호를 입력해주세요",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("이메일") },
            singleLine = true,
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text("비밀번호") },
            singleLine = true,
            isError = passwordError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = "비밀번호 보기")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (emailError || passwordError) {
            Text(
                text = "이메일과 비밀번호를 모두 입력해주세요",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        loginResult?.exceptionOrNull()?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "로그인 실패: ${it.message}",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "로그인",
            onClick = {
                if (email.isBlank()) emailError = true
                if (password.isBlank()) passwordError = true
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

       PrimaryButton(
            text = "Google 계정으로 로그인",
            onClick = { /* TODO: Google 로그인 연동 */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onBack) {
            Text("계정이 없으신가요? 회원가입", color = Color.Gray)
        }

        // 로그인 성공 처리
        loginResult?.getOrNull()?.let {
            viewModel.clearResults()
            onSuccess()
        }
    }
}
