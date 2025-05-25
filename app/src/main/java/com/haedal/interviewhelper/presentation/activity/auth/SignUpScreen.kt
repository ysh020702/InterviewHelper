package com.haedal.interviewhelper.presentation.activity.auth

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel

@Composable
fun SignUpScreen(
    viewModel: UserViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val signUpResult = viewModel.signUpResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("회원가입", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("비밀번호 확인") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "회원가입",
            onClick = {
                if (password == confirm) {
                    viewModel.signUp(email, password, name)
                } else {
                    viewModel.signUpResult = Result.failure(Exception("비밀번호가 일치하지 않습니다."))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (signUpResult != null) {
            signUpResult.fold(
                onSuccess = {
                    viewModel.clearResults()
                    onSuccess()
                },
                onFailure = {
                    Text("회원가입 실패: ${it.message}", color = Color.Red)
                }
            )
        }

        TextButton(onClick = onBack) {
            Text("← 돌아가기")
        }
    }
}
