import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_toast = 'android.widget.Toast.makeText(context, "No app or browser found", android.widget.Toast.LENGTH_SHORT).show()'
new_toast = 'android.widget.Toast.makeText(context, "Error: ", android.widget.Toast.LENGTH_LONG).show()'

content = content.replace(old_toast, new_toast)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated toast to show error message")
