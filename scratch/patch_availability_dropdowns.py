import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

target = """                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(stringResource(R.string.from_label), "09:00", availableFrom) { availableFrom = it }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(stringResource(R.string.to_label), "18:00", availableTo) { availableTo = it }
                    }
                }"""

replacement = """                val timeOptions = listOf("06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdown(stringResource(R.string.from_label), timeOptions, availableFrom) { availableFrom = it }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdown(stringResource(R.string.to_label), timeOptions, availableTo) { availableTo = it }
                    }
                }"""

content = content.replace(target, replacement)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated availability fields to dropdowns.")
