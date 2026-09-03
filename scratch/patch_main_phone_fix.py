import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    'db.collection("users").document(currentUser.uid).update("name", "suraj")',
    'db.collection("users").document(uidToCheck).update("name", "suraj")'
)

content = content.replace(
    'firebaseUid = currentUser.uid,',
    'firebaseUid = uidToCheck,'
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed currentUser unresolved references in MainActivity.kt")
