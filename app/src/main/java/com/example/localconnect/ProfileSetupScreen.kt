package com.example.localconnect

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*

@Composable
fun ProfileSetupScreen(
    session: UserSession,
    onBack: () -> Unit,
    onContinue: (name: String, dob: String, gender: String) -> Unit
) {
    val strings = getStrings(session.language.code)
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }

    val genders = listOf(strings.male, strings.female, strings.other)
    val canContinue = name.isNotBlank() && dob.isNotBlank() && selectedGender.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8F5), Color(0xFFFFF3EE), Color(0xFFFFECE5))))
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
            Row(modifier = Modifier.fillMaxWidth().background(com.example.localconnect.ui.theme.OrangePrimary).padding(bottom=8.dp, top=8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 22.sp, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                // Step indicator
                StepIndicator(current = 1, total = 2)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Icon ----
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
                contentAlignment = Alignment.Center
            ) { Text("👤", fontSize = 36.sp) }

            Spacer(modifier = Modifier.height(24.dp))

            Text(strings.profileTitle, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(6.dp))
            Text(strings.profileSubtitle, fontSize = 14.sp, color = Color(0xFF9E9E9E))

            Spacer(modifier = Modifier.height(36.dp))

            // ---- Name field ----
            OnboardingTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = strings.namePlaceholder,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ---- DOB field ----
            OnboardingTextField(
                value = dob,
                onValueChange = { dob = it },
                placeholder = strings.dobPlaceholder,
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Gender selection ----
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    strings.selectGender,
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    genders.forEach { g ->
                        GenderChip(
                            label = g,
                            isSelected = selectedGender == g,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedGender = g }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onContinue(name.trim(), dob.trim(), selectedGender) },
                enabled = canContinue,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFFB0B0B0)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (canContinue) 4.dp else 0.dp)
            ) {
                Text(strings.continueBtn, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ---- Reusable styled text field ----
@Composable
fun OnboardingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFCCCCCC)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = imeAction
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF1A1A1A),
            unfocusedTextColor = Color(0xFF1A1A1A),
            cursorColor = OrangePrimary
        )
    )
}

// ---- Gender chip ----
@Composable
private fun GenderChip(label: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val bg by animateColorAsState(if (isSelected) OrangePrimary else Color.White, tween(200), label = "gender_bg")
    val textColor by animateColorAsState(if (isSelected) Color.White else Color(0xFF555555), tween(200), label = "gender_text")
    val borderColor by animateColorAsState(if (isSelected) OrangePrimary else Color(0xFFE0E0E0), tween(200), label = "gender_border")

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = textColor)
    }
}

// ---- Step indicator ----
@Composable
fun StepIndicator(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { index ->
            val isActive = index + 1 == current
            val isDone = index + 1 < current
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (isActive) 24.dp else 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isActive || isDone) OrangePrimary else Color(0xFFE0E0E0))
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        ProfileSetupScreen(session = UserSession(), onBack = {}, onContinue = { _, _, _ -> })
    }
}
