import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    'val intentType = this@MainActivity.intent.getStringExtra("type")',
    'val intentType = activity.intent.getStringExtra("type")'
)

content = content.replace(
    'val intentRefId = this@MainActivity.intent.getStringExtra("referenceId")',
    'val intentRefId = activity.intent.getStringExtra("referenceId")'
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed MainActivity.kt intent reference")
