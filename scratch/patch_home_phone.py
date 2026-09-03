import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

target = "val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid"
replacement = "val uid = session.firebaseUid"

content = content.replace(target, replacement)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated HomeScreen.kt")
