import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove Save Draft button
target_save_draft = """                    OutlinedButton(
                        onClick = { Toast.makeText(context.applicationContext, "Draft Saved", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, primaryColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                    ) {
                        Text(stringResource(R.string.save_draft_btn), fontWeight = FontWeight.Bold)
                    }"""
content = content.replace(target_save_draft, "")

# 2. Remove Icon and Spacer from Use Current Location button
target_icon = """                                if (isFetchingLocation) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Filled.MyLocation, contentDescription = "Current Location", modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))"""
replacement_icon = """                                if (isFetchingLocation) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                }"""
content = content.replace(target_icon, replacement_icon)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Removed Save Draft button and MyLocation icon.")
