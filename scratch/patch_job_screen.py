import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace contentBetweenMapAndButton with contentBelowMap and contentBeforeSave
target_str = """                    contentBetweenMapAndButton = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {"""

replacement_str = """                    contentBelowMap = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {"""

content = content.replace(target_str, replacement_str)

target_str2 = """                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),"""

replacement_str2 = """                            }
                        }
                    },
                    contentBeforeSave = {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),"""

content = content.replace(target_str2, replacement_str2)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated PostJobScreen.kt")
