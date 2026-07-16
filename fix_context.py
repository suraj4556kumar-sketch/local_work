import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Add import if needed
if 'import androidx.compose.ui.platform.LocalContext' not in content:
    content = content.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.platform.LocalContext')

# We'll put al context = LocalContext.current right before ar markerPosition
old_str = 'var markerPosition by remember { mutableStateOf<LatLng?>(null) }'
new_str = 'val context = LocalContext.current\n                            var markerPosition by remember { mutableStateOf<LatLng?>(null) }'

content = content.replace(old_str, new_str)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated context")
