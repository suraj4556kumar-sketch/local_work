import os
import re

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/LeaveReviewScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove comment state
content = content.replace('    var comment by remember { mutableStateOf("") }\n', '')

# 2. Remove Comment OutlinedTextField
target_textfield = """            // Comment text field — STATIC placeholder
            OutlinedTextField(
                value = comment,
                onValueChange = { if (it.length <= 300) comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = {
                    Text(
                        stringResource(R.string.write_comment_hint),
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF97316),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedContainerColor = Color(0xFFFFFBF7),
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                maxLines = 4
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("${comment.length}/300", fontSize = 11.sp, color = Color(0xFF9CA3AF), modifier = Modifier.padding(top = 4.dp))
            }"""
content = content.replace(target_textfield, "")

# 3. Update FirebaseRepository.submitReview call (remove comment)
target_submit = """                                rating = selectedRating.toFloat(),
                                comment = comment.trim(),
                                serviceTitle = serviceTitle"""
replacement_submit = """                                rating = selectedRating.toFloat(),
                                comment = "",
                                serviceTitle = serviceTitle"""
content = content.replace(target_submit, replacement_submit)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated LeaveReviewScreen.kt to remove comments.")
