import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PhoneEntryScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

target = """    fun handleGetOtp() {
        isLoading = true
        com.google.firebase.auth.FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { result ->
                isLoading = false
                onGetOtp(phoneNumber, result.user?.uid ?: "")
            }
            .addOnFailureListener { exception ->
                isLoading = false
                Toast.makeText(context.applicationContext, "Sign-in failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }"""

replacement = """    fun handleGetOtp() {
        isLoading = true
        // Bypass Firebase Auth entirely and generate a unique ID for new users.
        // Existing users will have their real UID fetched from the database in MainActivity.
        val fallbackUid = java.util.UUID.randomUUID().toString()
        isLoading = false
        onGetOtp(phoneNumber, fallbackUid)
    }"""

content = content.replace(target, replacement)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PhoneEntryScreen.kt to bypass Anonymous Auth")
