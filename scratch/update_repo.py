import re

filepath = r"app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

import_lines = "import com.google.firebase.storage.FirebaseStorage\nimport java.util.UUID\n"
content = content.replace("import com.google.firebase.firestore.FirebaseFirestore\n", "import com.google.firebase.firestore.FirebaseFirestore\n" + import_lines)

storage_init = "    private val storage = FirebaseStorage.getInstance()\n"
content = content.replace("    private val db = FirebaseFirestore.getInstance()\n", "    private val db = FirebaseFirestore.getInstance()\n" + storage_init)

upload_fun = """
    suspend fun uploadServiceImage(uri: Uri): String? {
        return try {
            val ref = storage.reference.child("service_requests/${UUID.randomUUID()}")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error uploading image", e)
            null
        }
    }
}
"""

content = content.rsplit("}", 1)[0] + upload_fun

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated FirebaseRepository.kt")
