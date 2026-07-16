import os

filepath = r"app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

new_methods = """
    suspend fun getServiceRequestsForService(serviceId: String): List<ServiceRequest> {
        return try {
            val snapshot = serviceBookingsRef.whereEqualTo("serviceId", serviceId).get().await()
            snapshot.toObjects(ServiceRequest::class.java)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error getting requests for service", e)
            emptyList()
        }
    }

    suspend fun updateServiceRequestStatus(requestId: String, newStatus: String): Boolean {
        return try {
            serviceBookingsRef.document(requestId).update("status", newStatus).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error updating request status", e)
            false
        }
    }

    // Notifications
"""

content = content.replace("    // Notifications", new_methods.strip("\n"))

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated FirebaseRepository.kt successfully.")
