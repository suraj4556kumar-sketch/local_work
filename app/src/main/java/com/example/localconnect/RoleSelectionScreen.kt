package com.example.localconnect

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoleSelectionScreen(
    session: UserSession,
    onRoleSelected: (UserRole) -> Unit
) {
    val strings = getStrings(session.language.code)
    val displayName = session.name.ifBlank { session.phone }
    var isLoading by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    fun saveToFirestore(role: UserRole) {
        if (session.firebaseUid.isBlank()) {
            onRoleSelected(role)
            return
        }
        
        isLoading = true
        val db = FirebaseFirestore.getInstance()
        val userData = hashMapOf(
            "phone" to session.phone,
            "name" to session.name,
            "age" to session.age,
            "gender" to session.gender,
            "location" to session.location,
            "language" to session.language.code,
            "role" to role.name,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(session.firebaseUid)
            .set(userData)
            .addOnSuccessListener {
                isLoading = false
                onRoleSelected(role)
            }
            .addOnFailureListener { e ->
                isLoading = false
                Toast.makeText(context, "Error saving profile: ${e.message}", Toast.LENGTH_LONG).show()
                // Still proceed so user isn't stuck, or keep them here? Proceeding is safer for testing.
                onRoleSelected(role)
            }
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
            Spacer(modifier = Modifier.height(48.dp))

            // ---- Brand + avatar ----
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = session.name.firstOrNull()?.uppercaseChar()?.toString() ?: "👤",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hi, $displayName! 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.roleTitle,
                fontSize = 16.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
            Text(
                text = strings.roleSubtitle,
                fontSize = 13.sp,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ---- Hirer card ----
            RoleCard(
                icon = "🏢",
                title = strings.hirerCardTitle,
                description = strings.hirerCardDesc,
                gradientColors = listOf(OrangePrimary, Color(0xFFFF8A65)),
                onClick = { if (!isLoading) saveToFirestore(UserRole.HIRER) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Seeker card ----
            RoleCard(
                icon = "💼",
                title = strings.seekerCardTitle,
                description = strings.seekerCardDesc,
                gradientColors = listOf(Color(0xFF1565C0), Color(0xFF42A5F5)),
                onClick = { if (!isLoading) saveToFirestore(UserRole.SEEKER) }
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator(color = OrangePrimary)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "📍 ${session.location.ifBlank { "India" }}",
                fontSize = 12.sp,
                color = Color(0xFFBBBBBB),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun RoleCard(
    icon: String,
    title: String,
    description: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "role_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(24.dp)
        ) {
            // Decorative background icon
            Text(
                text = icon,
                fontSize = 90.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = 6.dp),
                color = Color.White.copy(alpha = 0.15f)
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Arrow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("→", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RoleSelectionPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        RoleSelectionScreen(
            session = UserSession(name = "Sumit", location = "Delhi"),
            onRoleSelected = {}
        )
    }
}
