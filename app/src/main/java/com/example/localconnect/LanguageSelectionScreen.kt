package com.example.localconnect

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*

data class Language(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val tagline: String
)

val supportedLanguages = listOf(
    Language("en", "English", "English", "Continue in English"),
    Language("hi", "हिंदी", "Hindi", "हिंदी में जारी रखें"),
    Language("bn", "বাংলা", "Bengali", "বাংলায় চালিয়ে যান"),
)

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (Language) -> Unit
) {
    var selectedCode by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8F5),
                        Color(0xFFFFF3EE),
                        Color(0xFFFFECE5)
                    )
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
            Spacer(modifier = Modifier.height(48.dp))

            // ---- App Brand Header ----
            BrandHeader()

            Spacer(modifier = Modifier.height(48.dp))

            // ---- Section Label ----
            Text(
                text = "Choose your language",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "भाषा चुनें  •  ভাষা বেছে নিন",
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ---- Language Cards ----
            supportedLanguages.forEach { lang ->
                LanguageCard(
                    language = lang,
                    isSelected = selectedCode == lang.code,
                    onSelect = { selectedCode = lang.code }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // ---- Continue Button ----
            val isEnabled = selectedCode != null
            Button(
                onClick = {
                    supportedLanguages.find { it.code == selectedCode }
                        ?.let(onLanguageSelected)
                },
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFFB0B0B0)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isEnabled) 4.dp else 0.dp
                )
            ) {
                Text(
                    text = "Continue →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo badge
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(OrangePrimary, OrangeLight)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LC",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LocalConnect",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A),
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Find work. Hire help. Near you.",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
private fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "card_scale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFFF3EE) else Color.White,
        animationSpec = tween(200),
        label = "card_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) OrangePrimary else Color(0xFFE8E8E8),
        animationSpec = tween(200),
        label = "card_border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onSelect() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language names
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language.nativeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) OrangeDark else Color(0xFF1A1A1A)
                )
                if (language.code != "en") {
                    Text(
                        text = language.englishName,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(OrangePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFF5F5F5))
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LanguageSelectionPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        LanguageSelectionScreen(onLanguageSelected = {})
    }
}
