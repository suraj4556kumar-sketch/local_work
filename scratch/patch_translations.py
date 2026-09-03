import os

# Update TranslationManager.kt
tm_path = "app/src/main/java/com/example/localconnect/utils/TranslationManager.kt"
with open(tm_path, 'r', encoding='utf-8') as f:
    tm_content = f.read()

tm_old = """        if (text.isBlank() || targetLanguage == "en") {
            onResult(text)
            return
        }"""
tm_new = """        if (text.isBlank()) {
            onResult(text)
            return
        }"""
tm_content = tm_content.replace(tm_old, tm_new)

with open(tm_path, 'w', encoding='utf-8') as f:
    f.write(tm_content)

# Update TranslatedText.kt
tt_path = "app/src/main/java/com/example/localconnect/ui/TranslatedText.kt"
with open(tt_path, 'r', encoding='utf-8') as f:
    tt_content = f.read()

tt_old = """    LaunchedEffect(text, targetLanguage) {
        if (targetLanguage == "en") {
            translatedText = text
        } else {
            translatedText = text // Start with original text while loading
            TranslationManager.translate(text, targetLanguage) { result ->
                translatedText = result
            }
        }
    }"""
tt_new = """    LaunchedEffect(text, targetLanguage) {
        translatedText = text // Start with original text while loading
        TranslationManager.translate(text, targetLanguage) { result ->
            translatedText = result
        }
    }"""
tt_content = tt_content.replace(tt_old, tt_new)

with open(tt_path, 'w', encoding='utf-8') as f:
    f.write(tt_content)

print("Updated translation files successfully.")
