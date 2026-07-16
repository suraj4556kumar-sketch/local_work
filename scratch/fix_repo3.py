import re

filepath = r"app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

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

pattern = re.compile(r"fun getNotificationsFlow.*?awaitClose \{ listenerRegistration\.remove\(\) \}\n    \}", re.DOTALL)
content = pattern.sub(new_flow, content)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
print("Fixed getNotificationsFlow in FirebaseRepository.kt")
