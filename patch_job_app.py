import os

file_path = 'app/src/main/java/com/example/localconnect/JobApplicationScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

target1 = '    var docUris by remember { mutableStateOf<Map<Int, android.net.Uri>>(emptyMap()) }'

replacement1 = '''    var docUris by remember { mutableStateOf<Map<Int, android.net.Uri>>(emptyMap()) }
    val requiredDocs = if (job.documentsRequired.isNotEmpty()) job.documentsRequired else listOf("Resume", "ID Proof")'''

content = content.replace(target1, replacement1)

target2 = '''                    val requiredDocs = if (job.documentsRequired.isNotEmpty()) job.documentsRequired else listOf("Resume", "ID Proof")
                    
                    requiredDocs.forEachIndexed { index, docName ->'''

replacement2 = '''                    requiredDocs.forEachIndexed { index, docName ->'''

content = content.replace(target2, replacement2)

target3 = '''                                var finalDocUrl = ""
                                if (firstDocUri.isNotEmpty() && firstDocUri.startsWith("content://")) {
                                    val uri = android.net.Uri.parse(firstDocUri)
                                    try {
                                        finalDocUrl = FirebaseRepository.uploadDocument(uri, context)
                                    } catch (e: Exception) {
                                        val errorMsg = e.localizedMessage ?: "Unknown Firebase Storage error"
                                        Toast.makeText(context.applicationContext, "Upload failed: errorMsg", Toast.LENGTH_LONG).show()
                                        isSubmitting = false
                                        return@launch
                                    }
                                } else {
                                    finalDocUrl = firstDocUri
                                }

                                val newApp = JobApplication(
                                    seekerId = session.firebaseUid,
                                    jobId = job.id,
                                    employerId = job.employerId,
                                    jobTitle = job.title,
                                    companyName = job.companyName,
                                    location = job.location,
                                    salary = job.salary,
                                    applicantName = fullName,
                                    applicantPhone = phone,
                                    applicantAge = age,
                                    applicantGender = gender,
                                    applicantExperience = if (hasExperience) expYears else "No Experience",
                                    applicantIntro = introduction,
                                    uploadedDocumentUri = finalDocUrl,
                                    appliedOnDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                                    appliedOn = System.currentTimeMillis()
                                )'''

replacement3 = '''                                val uploadedDocsMap = mutableMapOf<String, String>()
                                var finalDocUrl = ""

                                for ((index, uri) in docUris) {
                                    if (index < requiredDocs.size) {
                                        val docName = requiredDocs[index]
                                        val uriStr = uri.toString()
                                        if (uriStr.startsWith("content://")) {
                                            try {
                                                val url = FirebaseRepository.uploadDocument(uri, context)
                                                uploadedDocsMap[docName] = url
                                                if (finalDocUrl.isEmpty()) finalDocUrl = url
                                            } catch (e: Exception) {
                                                val errorMsg = e.localizedMessage ?: "Unknown Firebase Storage error"
                                                Toast.makeText(context.applicationContext, "Upload failed for docName: errorMsg", Toast.LENGTH_LONG).show()
                                                isSubmitting = false
                                                return@launch
                                            }
                                        } else {
                                            uploadedDocsMap[docName] = uriStr
                                            if (finalDocUrl.isEmpty()) finalDocUrl = uriStr
                                        }
                                    }
                                }

                                val newApp = JobApplication(
                                    seekerId = session.firebaseUid,
                                    jobId = job.id,
                                    employerId = job.employerId,
                                    jobTitle = job.title,
                                    companyName = job.companyName,
                                    location = job.location,
                                    salary = job.salary,
                                    applicantName = fullName,
                                    applicantPhone = phone,
                                    applicantAge = age,
                                    applicantGender = gender,
                                    applicantExperience = if (hasExperience) expYears else "No Experience",
                                    applicantIntro = introduction,
                                    uploadedDocumentUri = finalDocUrl,
                                    uploadedDocuments = uploadedDocsMap,
                                    appliedOnDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                                    appliedOn = System.currentTimeMillis()
                                )'''

if target3 in content:
    content = content.replace(target3, replacement3)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print('Replaced successfully')
else:
    print('Failed to find target3')
