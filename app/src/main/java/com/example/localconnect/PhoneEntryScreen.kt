package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*

@Composable
fun PhoneEntryScreen(
    session: UserSession,
    onBack: () -> Unit,
    onGetOtp: (phone: String, verificationId: String) -> Unit
) {
        var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val isValid = phoneNumber.length == 10

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun handleGetOtp() {
        isLoading = true
        AuthManager.sendOtp(
            activity = context as Activity,
            phoneNumber = phoneNumber,
            onCodeSent = { verificationId ->
                isLoading = false
                onGetOtp(phoneNumber, verificationId)
            },
            onVerificationFailed = { exception ->
                isLoading = false
                Toast.makeText(context.applicationContext, "Verification failed: ${exception.message}", Toast.LENGTH_LONG).show()
            },
            onVerificationCompleted = {
                // Auto verification success. You can handle this by jumping to Home, but for now we'll rely on the OTP screen.
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF8F5), Color(0xFFFFF3EE), Color(0xFFFFECE5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Top bar ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 22.sp, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                // Language badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFE0D0))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = session.language.nativeName,
                        fontSize = 13.sp,
                        color = OrangeDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ---- Icon ----
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
                contentAlignment = Alignment.Center
            ) {
                Text("📱", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Title ----
            Text(
                text = stringResource(R.string.enter_mobile),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.otp_subtitle),
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ---- Phone Input ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = if (phoneNumber.isNotEmpty()) 2.dp else 1.dp,
                        color = if (phoneNumber.isNotEmpty()) OrangePrimary else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color(0xFFFFF3EE))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+91", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                }
                Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color(0xFFE0E0E0)))
                TextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phoneNumber = it },
                    modifier = Modifier.weight(1f).focusRequester(focusRequester),
                    placeholder = { Text(stringResource(R.string.digits_hint), color = Color(0xFFCCCCCC), fontSize = 14.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); if (isValid) handleGetOtp() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color(0xFF1A1A1A),
                        unfocusedTextColor = Color(0xFF1A1A1A),
                        cursorColor = OrangePrimary
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${phoneNumber.length}/10",
                fontSize = 12.sp,
                color = if (isValid) Color(0xFFF97316) else Color(0xFFB0B0B0),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Button ----
            Button(
                onClick = { focusManager.clearFocus(); handleGetOtp() },
                enabled = isValid && !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFFB0B0B0)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isValid && !isLoading) 4.dp else 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.get_otp), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFFB0B0B0), fontSize = 12.sp)) { append("By continuing, you agree to our ") }
                    withStyle(SpanStyle(color = OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)) { append("Terms") }
                    withStyle(SpanStyle(color = Color(0xFFB0B0B0), fontSize = 12.sp)) { append(" and ") }
                    withStyle(SpanStyle(color = OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)) { append("Privacy Policy") }
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhoneEntryPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        PhoneEntryScreen(session = UserSession(), onBack = {}, onGetOtp = { _, _ -> })
    }
}
