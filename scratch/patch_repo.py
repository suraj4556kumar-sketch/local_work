import re

filepath = r"app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

# Fix getNotificationsFlow
old_flow = """fun getNotificationsFlow(userId: String): kotlinx.coroutines.flow.Flow<List<Notification>> = kotlinx.coroutines.flow.callbackFlow {
        val listenerRegistration = notificationsRef.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.toObjects(Notification::class.java).sortedByDescending { it.timestamp }
                    trySend(notifications)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }"""

new_flow = """fun getNotificationsFlow(userIds: List<String>): kotlinx.coroutines.flow.Flow<List<Notification>> = kotlinx.coroutines.flow.callbackFlow {
        if (userIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listenerRegistration = notificationsRef.whereIn("userId", userIds)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.toObjects(Notification::class.java).sortedByDescending { it.timestamp }
                    trySend(notifications)
                }
            }
        kotlinx.coroutines.channels.awaitClose { listenerRegistration.remove() }
    }"""

content = content.replace(old_flow, new_flow)

# Add missing methods
missing_methods = """
    suspend fun updateApplicationStatus(applicationId: String, status: String): Boolean {
        return try {
            applicationsRef.document(applicationId).update("status", status).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteService(serviceId: String): Boolean {
        return try {
            servicesRef.document(serviceId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
"""

content = content.replace("    // Notifications", missing_methods + "\n    // Notifications")

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
print("Patched FirebaseRepository.kt successfully.")
