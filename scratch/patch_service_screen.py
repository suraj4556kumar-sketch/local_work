import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Clear default category
content = content.replace('var category by remember { mutableStateOf("Plumber") }', 'var category by remember { mutableStateOf("") }')

# 2. Remove "charge per km" UI block
block_to_remove = """                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.charge_fees_km), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = false, onClick = null, enabled = false, colors = RadioButtonDefaults.colors(disabledSelectedColor = Color.Gray, disabledUnselectedColor = Color.LightGray))
                        Text(stringResource(R.string.yes), fontSize = 14.sp, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { hasFeesPerKm = false }) {
                        RadioButton(selected = !hasFeesPerKm, onClick = { hasFeesPerKm = false }, colors = RadioButtonDefaults.colors(selectedColor = primaryColor))
                        Text(stringResource(R.string.no_btn), fontSize = 14.sp)
                    }
                }
                
                if (hasFeesPerKm) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(stringResource(R.string.fees_per_km), stringResource(R.string.ph_10), feesPerKm, keyboardType = KeyboardType.Number) { feesPerKm = it }
                }"""
content = content.replace(block_to_remove, '')

# 3. Make static text labels bold
# In CustomTextField
content = content.replace('Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray', 'Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray')
# In CustomDropdown is already covered since it uses the same string snippet, but let's be sure:
# Wait, they are exactly the same.
content = content.replace('Text(stringResource(R.string.available_on), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)', 'Text(stringResource(R.string.available_on), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)')
content = content.replace('Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, color = Color.DarkGray)', 'Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostServiceScreen.kt")
