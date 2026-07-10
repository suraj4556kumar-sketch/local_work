package com.example.localconnect.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors - Vibrant Orange
val Primary = Color(0xFFF97316) // Orange 500
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFFFEDD5) // Orange 100
val OnPrimaryContainer = Color(0xFF7C2D12) // Orange 900

// Secondary Colors - Warm Amber/Yellow
val Secondary = Color(0xFFF97316) // Amber 400
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFFEF3C7) // Amber 100
val OnSecondaryContainer = Color(0xFF78350F) // Amber 900

// Background & Surface - Light and clean
val Background = Color(0xFFFAFAFA) // Neutral 50
val OnBackground = Color(0xFF1F2937) // Gray 800
val Surface = Color(0xFFFFFFFF) // Pure White
val OnSurface = Color(0xFF1F2937) // Gray 800

// Error Colors
val Error = Color(0xFFF97316) // Red 500
val OnError = Color(0xFFFFFFFF)

// Custom Semantic Colors
val TextHint = Color(0xFF9CA3AF) // Gray 400
val Divider = Color(0xFFE5E7EB) // Gray 200
val CardUnselected = Color(0xFFFFFFFF)
val CardSelectedBg = Color(0xFFFFF7ED) // Orange 50
val CardBorderSelected = Color(0xFFF97316) // Orange 500
val CardBorderUnselected = Color(0xFFD1D5DB) // Gray 300

// Dark Theme Variants
val PrimaryDark = Color(0xFFFB923C) // Orange 400
val PrimaryContainerDark = Color(0xFF9A3412) // Orange 800
val SecondaryDark = Color(0xFFFCD34D) // Amber 300
val SecondaryContainerDark = Color(0xFFB45309) // Amber 700
val BackgroundDark = Color(0xFF111827) // Gray 900
val SurfaceDark = Color(0xFF1F2937) // Gray 800
val OnBackgroundDark = Color(0xFFF9FAFB) // Gray 50
val OnSurfaceDark = Color(0xFFF3F4F6) // Gray 100

// Legacy Compatibility Aliases to prevent build errors in un-migrated code
val OrangePrimary        = Primary
val OrangeLight          = PrimaryContainer
val OrangeDark           = PrimaryDark
val OrangeContainer      = PrimaryContainer


val BackgroundCream      = Background
val SurfaceWhite         = Surface
val TextPrimary          = OnBackground
val TextSecondary        = Color(0xFF6B7280) // Gray 500

val OrangePrimaryDark    = PrimaryDark
val OrangeDarkContainer  = PrimaryContainerDark