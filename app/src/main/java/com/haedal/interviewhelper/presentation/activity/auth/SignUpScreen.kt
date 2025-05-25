package com.haedal.interviewhelper.presentation.activity.auth

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.common.SignInButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.domain.helpfunction.WEB_CLIENT_ID
import com.haedal.interviewhelper.presentation.activity.onboarding.OnboardingScreen
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sign

@Composable
fun SignUpScreen(
    viewModel: UserViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val signUpResult = viewModel.signUpResult

    val credentialManager = remember { CredentialManager.create(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .imePadding(),
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
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("비밀번호 확인") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
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

        Spacer(modifier = Modifier.height(12.dp))

        AndroidView(
            factory = { ctx ->
                SignInButton(ctx).apply {
                    setSize(SignInButton.SIZE_WIDE)
                    setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setServerClientId(WEB_CLIENT_ID)
                                .setFilterByAuthorizedAccounts(false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            try {
                                val result = credentialManager.getCredential(context, request)
                                val credential = result.credential as GoogleIdTokenCredential
                                val idToken = credential.idToken
                                val name = credential.displayName ?: "사용자"
                                val email = credential.id

                                if (!idToken.isNullOrEmpty()) {
                                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                    viewModel.signUpWithGoogleCredential(firebaseCredential, name, email, onSuccess)
                                }
                            } catch (e: GetCredentialException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val success = signUpResult?.isSuccess == true
        var alreadyHandled by remember { mutableStateOf(false) }

        LaunchedEffect(success) {
            if (success && !alreadyHandled) {
                alreadyHandled = true
                viewModel.clearResults()
                onSuccess(context)  // context를 매개변수로 넘김
            }
        }



        TextButton(onClick = onBack) {
            Text("← 돌아가기")
        }
    }
}