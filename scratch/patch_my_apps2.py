import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyApplicationsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add appToReview state
target_state = """        var selectedApp by remember { mutableStateOf<JobApplication?>(null) }
    var selectedJob by remember { mutableStateOf<com.example.localconnect.data.Job?>(null) }"""
replacement_state = """        var selectedApp by remember { mutableStateOf<JobApplication?>(null) }
    var selectedJob by remember { mutableStateOf<com.example.localconnect.data.Job?>(null) }
    var appToReview by remember { mutableStateOf<JobApplication?>(null) }"""
content = content.replace(target_state, replacement_state)

# 2. Fix myApplications[index] error
target_submit = """                    val index = myApplications.indexOfFirst { it.id == appToReview!!.id }
                    if (index != -1) {
                        myApplications[index] = myApplications[index].copy(isReviewed = true)
                    }"""
replacement_submit = """                    val index = myApplications.indexOfFirst { it.id == appToReview!!.id }
                    if (index != -1) {
                        myApplications = myApplications.toMutableList().apply {
                            this[index] = this[index].copy(isReviewed = true)
                        }
                    }"""
content = content.replace(target_submit, replacement_submit)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed MyApplicationsScreen errors.")
