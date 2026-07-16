import re

filepath = r"app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

old_update = """    suspend fun updateApplicationStatus(applicationId: String, status: String): Boolean {
        return try {
            applicationsRef.document(applicationId).update("status", status).await()
            true
        } catch (e: Exception) {
            false
        }
    }"""

new_update = """    suspend fun updateApplicationStatus(application: JobApplication, status: String): Boolean {
        return try {
            jobAppsRef.document(application.id).update("status", status).await()
            true
        } catch (e: Exception) {
            false
        }
    }"""

content = content.replace(old_update, new_update)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
print("Fixed updateApplicationStatus in FirebaseRepository.kt")
