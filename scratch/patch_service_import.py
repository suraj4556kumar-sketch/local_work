import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

target = "import androidx.compose.material.icons.filled.ArrowBack"
replacement = "import androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.material.icons.filled.Warning"
content = content.replace(target, replacement)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Added Warning import")
