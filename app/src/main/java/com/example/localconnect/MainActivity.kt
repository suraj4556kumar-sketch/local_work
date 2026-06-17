package com.example.localconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.localconnect.ui.theme.LocalConnectTheme
import com.example.localconnect.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * All screens in the app.
 * Every screen carries UserSession — data is never lost between steps.
 */
sealed class Screen {
    object Splash : Screen()
    object LanguageSelection : Screen()
    data class PhoneEntry(val session: UserSession) : Screen()
    data class OtpVerification(val session: UserSession) : Screen()
    data class ProfileSetup(val session: UserSession) : Screen()
    data class LocationSetup(val session: UserSession) : Screen()
    data class RoleSelection(val session: UserSession) : Screen()
    data class Home(val session: UserSession) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalConnectTheme {
                AppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Splash)
    }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User logged in. Fetch their profile to see if onboarding is done.
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.contains("role")) {
                        // User completed onboarding, restore session and go Home
                        val roleStr = document.getString("role") ?: "NONE"
                        val userRole = runCatching { UserRole.valueOf(roleStr) }.getOrDefault(UserRole.NONE)
                        val languageCode = document.getString("language") ?: "en"
                        val language = supportedLanguages.find { it.code == languageCode } ?: supportedLanguages[0]

                        val session = UserSession(
                            language = language,
                            phone = document.getString("phone") ?: "",
                            firebaseUid = currentUser.uid,
                            name = document.getString("name") ?: "",
                            age = document.getString("age") ?: "",
                            gender = document.getString("gender") ?: "",
                            location = document.getString("location") ?: "",
                            role = userRole
                        )
                        currentScreen = Screen.Home(session)
                    } else {
                        // Logged in but no full profile (maybe dropped off at profile setup). Send them to Profile Setup.
                        currentScreen = Screen.ProfileSetup(UserSession(firebaseUid = currentUser.uid, phone = currentUser.phoneNumber?.replace("+91", "") ?: ""))
                    }
                }
                .addOnFailureListener {
                    currentScreen = Screen.LanguageSelection
                }
        } else {
            // Not logged in
            currentScreen = Screen.LanguageSelection
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            when (targetState) {
                is Screen.Home ->
                    slideInVertically(initialOffsetY = { it }, animationSpec = tween(380)) +
                    fadeIn(tween(380)) togetherWith fadeOut(tween(200))
                is Screen.LanguageSelection ->
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                else ->
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300))
            }
        },
        label = "app_nav"
    ) { screen ->
        when (screen) {

            is Screen.Splash -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = OrangePrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading LocalConnect...", color = Color.Gray)
                    }
                }
            }

            // 1 — Language
            is Screen.LanguageSelection -> LanguageSelectionScreen(
                onLanguageSelected = { lang ->
                    currentScreen = Screen.PhoneEntry(UserSession(language = lang))
                }
            )

            // 2 — Phone
            is Screen.PhoneEntry -> PhoneEntryScreen(
                session = screen.session,
                onBack = { currentScreen = Screen.LanguageSelection },
                onGetOtp = { phone, verificationId ->
                    currentScreen = Screen.OtpVerification(screen.session.copy(phone = phone, verificationId = verificationId))
                }
            )

            // 3 — OTP
            is Screen.OtpVerification -> OtpVerificationScreen(
                session = screen.session,
                onVerified = { firebaseUid -> 
                    currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = firebaseUid)) 
                },
                onBack = { currentScreen = Screen.PhoneEntry(screen.session) }
            )

            // 4 — Profile (name / age / gender)
            is Screen.ProfileSetup -> ProfileSetupScreen(
                session = screen.session,
                onBack = { currentScreen = Screen.OtpVerification(screen.session) },
                onContinue = { name, age, gender ->
                    currentScreen = Screen.LocationSetup(
                        screen.session.copy(name = name, age = age, gender = gender)
                    )
                }
            )

            // 5 — Location
            is Screen.LocationSetup -> LocationSetupScreen(
                session = screen.session,
                onBack = { currentScreen = Screen.ProfileSetup(screen.session) },
                onContinue = { location ->
                    currentScreen = Screen.RoleSelection(screen.session.copy(location = location))
                }
            )

            // 6 — Role selection (I want to Hire / I want Work)
            is Screen.RoleSelection -> RoleSelectionScreen(
                session = screen.session,
                onRoleSelected = { role ->
                    currentScreen = Screen.Home(screen.session.copy(role = role))
                }
            )

            // 7 — Personalised Home
            is Screen.Home -> HomeScreen(session = screen.session)
        }
    }
}