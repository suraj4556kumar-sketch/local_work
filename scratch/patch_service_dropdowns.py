import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add readOnly = true to OutlinedTextField
target_tf = """            OutlinedTextField(
                value = displayOption,
                onValueChange = { 
                    displayOption = it
                    onOptionSelected(it)
                    expanded = true 
                },
                shape = RoundedCornerShape(8.dp),"""
replacement_tf = """            OutlinedTextField(
                readOnly = true,
                value = displayOption,
                onValueChange = { 
                    displayOption = it
                    onOptionSelected(it)
                    expanded = true 
                },
                shape = RoundedCornerShape(8.dp),"""
content = content.replace(target_tf, replacement_tf)

# 2. Remove the filter logic and use options directly
target_filter = """            val filteredOptions = options.filter { it.contains(selectedOption, ignoreCase = true) }
            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White).heightIn(max = 200.dp)
                ) {
                    filteredOptions.forEach { selectionOption ->"""

replacement_filter = """            if (options.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White).heightIn(max = 200.dp)
                ) {
                    options.forEach { selectionOption ->"""
content = content.replace(target_filter, replacement_filter)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostServiceScreen dropdowns.")
