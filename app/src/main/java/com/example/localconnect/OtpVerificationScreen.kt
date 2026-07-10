package com.example.localconnect

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    session: UserSession,
    onVerified: (firebaseUid: String) -> Unit,
    onBack: () -> Unit
) {
    val strings = getStrings(session.language.code)
    var otpValue by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val isComplete = otpValue.length == 6

    fun handleVerifyOtp() {
        isLoading = true
        AuthManager.verifyOtp(
            verificationId = session.verificationId,
            code = otpValue,
            onSuccess = {
                isLoading = false
                val uid = AuthManager.auth.currentUser?.uid ?: ""
                onVerified(uid)
            },
            onError = {
                isLoading = false
                isError = true
            }
        )
    }

    LaunchedEffect(canResend) {
        if (!canResend) {
            secondsLeft = 60
            while (secondsLeft > 0) { delay(1000L); secondsLeft-- }
            canResend = true
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8F5), Color(0xFFFFF3EE), Color(0xFFFFECE5))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 22.sp, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
                contentAlignment = Alignment.Center
            ) { Text("✉️", fontSize = 36.sp) }

            Spacer(modifier = Modifier.height(28.dp))

            Text(strings.verifyTitle, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(8.dp))
            Text(strings.otpSentTo, fontSize = 14.sp, color = Color(0xFF9E9E9E))
            Spacer(modifier = Modifier.height(4.dp))
            Text("+91 ${session.phone}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)

            Spacer(modifier = Modifier.height(40.dp))

            OtpInputField(otpValue, isError, focusRequester) { value ->
                if (value.length <= 6 && value.all { it.isDigit() }) { otpValue = value; isError = false }
            }

            if (isError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(strings.incorrectOtp, fontSize = 13.sp, color = Color(0xFFF97316))
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { if (isComplete) handleVerifyOtp() else isError = true },
                enabled = isComplete && !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFFB0B0B0)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isComplete && !isLoading) 4.dp else 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(strings.verifyBtn, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (canResend) {
                TextButton(onClick = { otpValue = ""; isError = false; canResend = false }) {
                    Text(strings.resendOtp, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OrangePrimary)
                }
            } else {
                Text("${strings.resendIn}  ${secondsLeft}s", fontSize = 14.sp, color = Color(0xFF9E9E9E))
            }
        }
    }
}

@Composable
private fun OtpInputField(
    otpValue: String,
    isError: Boolean,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        BasicTextField(
            value = otpValue,
            onValueChange = onValueChange,
            modifier = Modifier.focusRequester(focusRequester).size(1.dp).background(Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush = SolidColor(Color.Transparent),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(6) { index ->
                val char = otpValue.getOrNull(index)
                val isFocused = index == otpValue.length
                val boxBg by animateColorAsState(
                    when { isError -> Color(0xFFFFEBEE); char != null -> Color(0xFFFFF3EE); else -> Color.White },
                    tween(200), label = "bg$index"
                )
                val boxBorder by animateColorAsState(
                    when { isError -> Color(0xFFF97316); isFocused -> OrangePrimary; char != null -> OrangeLight; else -> Color(0xFFE0E0E0) },
                    tween(200), label = "border$index"
                )
                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(boxBg)
                        .border(if (isFocused || char != null) 2.dp else 1.dp, boxBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char?.toString() ?: if (isFocused) "|" else "",
                        fontSize = if (char != null) 22.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (char != null) Color(0xFF1A1A1A) else Color(0xFFCCCCCC),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtpPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        OtpVerificationScreen(session = UserSession(phone = "9876543210"), onVerified = { _ -> }, onBack = {})
    }
}
