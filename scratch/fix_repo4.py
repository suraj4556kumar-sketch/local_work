import re

filepath = r"app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

old_mark = """    suspend fun markNotificationsAsRead(userId: String) {
        try {
            val unread = notificationsRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            db.runBatch { batch ->
                for (doc in unread.documents) {
                    batch.update(doc.reference, "isRead", true)
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error marking notifications read", e)
        }
    }"""

new_mark = """    suspend fun markNotificationsAsRead(userIds: List<String>) {
        if (userIds.isEmpty()) return
        try {
            val unread = notificationsRef
                .whereIn("userId", userIds)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            db.runBatch { batch ->
                for (doc in unread.documents) {
                    batch.update(doc.reference, "isRead", true)
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error marking notifications read", e)
        }
    }"""

content = content.replace(old_mark, new_mark)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
print("Fixed markNotificationsAsRead")
