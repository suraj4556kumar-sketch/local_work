import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/data/Models.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add rating to User
target_user = """    val createdAt: Timestamp? = null
)"""
replacement_user = """    val createdAt: Timestamp? = null,
    val rating: Double = 0.0,
    val completedJobs: Int = 0
)"""
content = content.replace(target_user, replacement_user)

# 2. Add rating to Job
target_job = """    val timestamp: Long = 0L
)"""
replacement_job = """    val timestamp: Long = 0L,
    val rating: Double = 0.0,
    val completedJobs: Int = 0
)"""
content = content.replace(target_job, replacement_job)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated Models.kt with rating fields.")
