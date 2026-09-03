import os
import re

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove prefilled Plumber description and LaunchedEffect
target_desc = """    var description by remember { mutableStateOf(predefinedDescriptions["Plumber"] ?: "") }
    
    LaunchedEffect(Unit) {
        com.example.localconnect.utils.TranslationManager.translate(predefinedDescriptions["Plumber"] ?: "", targetLanguage) { res ->
            description = res
        }
    }"""
replacement_desc = """    var description by remember { mutableStateOf("") }"""
content = content.replace(target_desc, replacement_desc)

# 2. Add isMapScrollEnabled
target_scroll_state = """    // Form States
    var providerName by remember { mutableStateOf(session.name.lowercase()) }"""
replacement_scroll_state = """    var isMapScrollEnabled by remember { mutableStateOf(true) }

    // Form States
    var providerName by remember { mutableStateOf(session.name.lowercase()) }"""
content = content.replace(target_scroll_state, replacement_scroll_state)

# 3. Use isMapScrollEnabled in verticalScroll
target_scroll_mod = """.verticalScroll(rememberScrollState())"""
replacement_scroll_mod = """.verticalScroll(rememberScrollState(), enabled = isMapScrollEnabled)"""
content = content.replace(target_scroll_mod, replacement_scroll_mod)

# 4. Pass onMapTouch to LocationSelectorMap
target_map = """                LocationSelectorMap(
                    addressQuery = "$locality $city $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    contentBelowMap = {"""
replacement_map = """                LocationSelectorMap(
                    addressQuery = "$locality $city $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    onMapTouch = { isMapScrollEnabled = !it },
                    contentBelowMap = {"""
content = content.replace(target_map, replacement_map)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostServiceScreen map scroll and description.")
