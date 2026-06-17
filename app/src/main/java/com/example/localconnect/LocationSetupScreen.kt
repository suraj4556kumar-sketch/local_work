package com.example.localconnect

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*

private val popularCities = listOf("Delhi", "Mumbai", "Kolkata", "Bangalore", "Chennai", "Hyderabad", "Pune", "Jaipur")

@Composable
fun LocationSetupScreen(
    session: UserSession,
    onBack: () -> Unit,
    onContinue: (location: String) -> Unit
) {
    val strings = getStrings(session.language.code)
    var city by remember { mutableStateOf("") }
    val canContinue = city.isNotBlank()

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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 22.sp, color = Color(0xFF1A1A1A), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                StepIndicator(current = 2, total = 2)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Icon ----
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
                contentAlignment = Alignment.Center
            ) { Text("📍", fontSize = 36.sp) }

            Spacer(modifier = Modifier.height(24.dp))

            Text(strings.locationTitle, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(6.dp))
            Text(strings.locationSubtitle, fontSize = 14.sp, color = Color(0xFF9E9E9E))

            Spacer(modifier = Modifier.height(36.dp))

            // ---- City text field ----
            OnboardingTextField(
                value = city,
                onValueChange = { city = it },
                placeholder = strings.cityPlaceholder
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Use current location button ----
            OutlinedButton(
                onClick = { city = "Current Location" },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary)
            ) {
                Text(strings.useCurrentLocation, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Popular cities ----
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    strings.popularCities,
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(popularCities) { c ->
                        CityChip(label = c, isSelected = city == c, onClick = { city = c })
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onContinue(city.trim()) },
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

@Composable
private fun CityChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(if (isSelected) OrangePrimary else Color.White, tween(200), label = "city_bg")
    val textColor by animateColorAsState(if (isSelected) Color.White else Color(0xFF555555), tween(200), label = "city_text")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(label, fontSize = 13.sp, color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        LocationSetupScreen(session = UserSession(name = "Sumit"), onBack = {}, onContinue = {})
    }
}
