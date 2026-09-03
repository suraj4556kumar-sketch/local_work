import os
import re

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update onGetOtp logic in PhoneEntry to check if phone exists first
target_phone = """                    onGetOtp = { phone, firebaseUid ->
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
                                    val intentType = activity.intent.getStringExtra("type")
                                    val intentRefId = activity.intent.getStringExtra("referenceId")
                                    currentScreen = Screen.Home(completeSession, intentType, intentRefId)
                                } else {
                                    currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = firebaseUid, phone = phone)) 
                                }
                            }
                            .addOnFailureListener {
                                currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = firebaseUid, phone = phone))
                            }
                    }"""

replacement_phone = """                    onGetOtp = { phone, anonymousUid ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").whereEqualTo("phone", phone).get()
                            .addOnSuccessListener { querySnapshot ->
                                val activeUid = if (!querySnapshot.isEmpty) {
                                    querySnapshot.documents[0].id
                                } else {
                                    anonymousUid
                                }
                                
                                val sharedPrefs = activity.getSharedPreferences("LocalConnectPrefs", android.content.Context.MODE_PRIVATE)
                                sharedPrefs.edit().putString("active_uid", activeUid).apply()

                                db.collection("users").document(activeUid).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists() && document.contains("location")) {
                                            val languageCode = document.getString("language") ?: "en"
                                            val language = supportedLanguages.find { it.code == languageCode } ?: supportedLanguages[0]
                                            val fetchedName = document.getString("name") ?: ""
                                            
                                            val completeSession = UserSession(
                                                language = language,
                                                phone = document.getString("phone") ?: phone,
                                                firebaseUid = activeUid,
                                                name = fetchedName,
                                                dob = document.getString("dob") ?: "",
                                                gender = document.getString("gender") ?: "",
                                                location = document.getString("location") ?: ""
                                            )
                                            val intentType = activity.intent.getStringExtra("type")
                                            val intentRefId = activity.intent.getStringExtra("referenceId")
                                            currentScreen = Screen.Home(completeSession, intentType, intentRefId)
                                        } else {
                                            currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = activeUid, phone = phone)) 
                                        }
                                    }
                                    .addOnFailureListener {
                                        currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = activeUid, phone = phone))
                                    }
                            }
                            .addOnFailureListener {
                                currentScreen = Screen.ProfileSetup(screen.session.copy(firebaseUid = anonymousUid, phone = phone))
                            }
                    }"""

content = content.replace(target_phone, replacement_phone)


# 2. Update auto-login check in LaunchedEffect
target_auto = """        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User logged in. Fetch their profile to see if onboarding is done.
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser.uid).get()"""

replacement_auto = """        val sharedPrefs = activity.getSharedPreferences("LocalConnectPrefs", android.content.Context.MODE_PRIVATE)
        val activeUid = sharedPrefs.getString("active_uid", null)
        val uidToCheck = activeUid ?: FirebaseAuth.getInstance().currentUser?.uid
        if (uidToCheck != null) {
            // User logged in. Fetch their profile to see if onboarding is done.
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uidToCheck).get()"""
content = content.replace(target_auto, replacement_auto)

# Update the uid used for Screen.ProfileSetup on auto-login failure 
target_auto_fail_1 = """currentScreen = Screen.ProfileSetup(UserSession(firebaseUid = currentUser.uid, phone = currentUser.phoneNumber?.replace("+91", "") ?: ""))"""
replacement_auto_fail_1 = """currentScreen = Screen.ProfileSetup(UserSession(firebaseUid = uidToCheck, phone = FirebaseAuth.getInstance().currentUser?.phoneNumber?.replace("+91", "") ?: ""))"""
content = content.replace(target_auto_fail_1, replacement_auto_fail_1)


with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated MainActivity.kt successfully.")
