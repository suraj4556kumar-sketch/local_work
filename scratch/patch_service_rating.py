import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyServiceBookingsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update the RATE SERVICE button visibility condition
target_rate = """            // --- RATE SERVICE button: visible only for Completed + not yet reviewed ---
            if (request.status.lowercase() == "completed" && !isReviewedLocally) {"""
replacement_rate = """            // --- RATE SERVICE button: visible only for Completed/Accepted + not yet reviewed ---
            if ((request.status.lowercase() == "completed" || request.status.lowercase() == "accepted") && !isReviewedLocally) {"""
content = content.replace(target_rate, replacement_rate)

# 2. Update the Reviewed badge visibility condition
target_badge = """            // Reviewed badge: shows after user has submitted a review
            if (isReviewedLocally && request.status.lowercase() == "completed") {"""
replacement_badge = """            // Reviewed badge: shows after user has submitted a review
            if (isReviewedLocally && (request.status.lowercase() == "completed" || request.status.lowercase() == "accepted")) {"""
content = content.replace(target_badge, replacement_badge)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated MyServiceBookingsScreen.kt to show rating option for Accepted status.")
