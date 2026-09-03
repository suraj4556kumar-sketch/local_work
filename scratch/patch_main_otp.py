import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace PhoneEntry callback
target_phone = """                    onGetOtp = { phone, verificationId ->
                        currentScreen = Screen.OtpVerification(screen.session.copy(phone = phone, verificationId = verificationId))
                    }"""

replacement_phone = """                    onGetOtp = { phone, firebaseUid ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(firebaseUid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists() && document.contains("location")) {
                                    val languageCode = document.getString("language") ?: "en"
                                    val language = supportedLanguages.find { it.code == languageCode } ?: supportedLanguages[0]
                                    val fetchedName = document.getString("name") ?: ""
                                    
                                    val completeSession = UserSession(
                                        language = language,
                                        phone = document.getString("phone") ?: phone,
                                        firebaseUid = firebaseUid,
                                        name = fetchedName,
                                        dob = document.getString("dob") ?: "",
                                        gender = document.getString("gender") ?: "",
                                        location = document.getString("location") ?: ""
                                    )
                                    val intentType = this@MainActivity.intent.getStringExtra("type")
                                    val intentRefId = this@MainActivity.intent.getStringExtra("referenceId")
                                    currentScreen = Screen.Home(completeSession, intentType, intentRefId)
                                } else {
                                    currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = firebaseUid, phone = phone)) 
                                }
                            }
                            .addOnFailureListener {
                                currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = firebaseUid, phone = phone))
                            }
                    }"""

content = content.replace(target_phone, replacement_phone)

# Change ProfileSetup onBack
target_profile_back = "onBack = { currentScreen = Screen.OtpVerification(screen.session) },"
replacement_profile_back = "onBack = { currentScreen = Screen.PhoneEntry(screen.session) },"
content = content.replace(target_profile_back, replacement_profile_back)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated MainActivity.kt")
