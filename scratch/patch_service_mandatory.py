import os
import re

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update Validation Logic
target_validation = """                            if (visitingFees.isBlank() || city.isBlank()) {
                                Toast.makeText(context.applicationContext, "Please fill Visiting Fees and City", Toast.LENGTH_SHORT).show()
                                return@Button
                            }"""
replacement_validation = """                            if (providerName.isBlank() || category.isBlank() || description.isBlank() || visitingFees.isBlank() || city.isBlank() || locality.isBlank() || pincode.isBlank() || serviceRadius.isBlank() || availableDays.isEmpty()) {
                                Toast.makeText(context.applicationContext, "Please fill in all mandatory fields (*)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }"""
content = content.replace(target_validation, replacement_validation)

# 2. Add Disclaimer before closing of main Column (after PREVIEW CARD)
target_preview_end = """                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("⭐ Exp: $experience", fontSize = 12.sp, color = Color.Gray)
                            Text("🕒 ${availableDays.joinToString(", ").takeIf { it.isNotBlank() } ?: "None"}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }"""
replacement_preview_end = """                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("⭐ Exp: $experience", fontSize = 12.sp, color = Color.Gray)
                            Text("🕒 ${availableDays.joinToString(", ").takeIf { it.isNotBlank() } ?: "None"}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // 7. DISCLAIMER
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Disclaimer: Posting fake or misleading services will result in an immediate account ban.",
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }"""
content = content.replace(target_preview_end, replacement_preview_end)

# 3. Add * to CustomTextField
target_ctf = """        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))"""
replacement_ctf = """        Text("$label *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))"""
content = content.replace(target_ctf, replacement_ctf)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostServiceScreen validation, disclaimer, and labels.")
