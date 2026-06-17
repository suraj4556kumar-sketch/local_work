package com.example.localconnect

/** The user's chosen role in the app */
enum class UserRole { NONE, HIRER, SEEKER }

/**
 * Carries user data through the entire onboarding flow.
 * Add new fields here as the app grows.
 */
data class UserSession(
    val language: Language = supportedLanguages[0],
    val phone: String = "",
    val verificationId: String = "",
    val firebaseUid: String = "",
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val location: String = "",
    val role: UserRole = UserRole.NONE
)
