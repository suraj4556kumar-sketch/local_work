import os

file_path = 'app/src/main/java/com/example/localconnect/JobApplicationScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_block = """                                val uploadedDocsMap = mutableMapOf<String, String>()
                                var finalDocUrl = ""

                                for ((index, uri) in docUris) {"""

new_block = """                                if (docUris.size < requiredDocs.size) {
                                    android.widget.Toast.makeText(context.applicationContext, "Please upload all required documents (\ required)", android.widget.Toast.LENGTH_SHORT).show()
                                    isSubmitting = false
                                    return@launch
                                }
                                val uploadedDocsMap = mutableMapOf<String, String>()
                                var finalDocUrl = ""

                                for ((index, uri) in docUris) {"""

content = content.replace(old_block, new_block)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated JobApplicationScreen to validate all documents")
