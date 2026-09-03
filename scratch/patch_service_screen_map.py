import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Lowercase providerName
content = content.replace('var providerName by remember { mutableStateOf(session.name) }', 'var providerName by remember { mutableStateOf(session.name.lowercase()) }')

# Add height restriction to CustomDropdown ExposedDropdownMenu
target_dropdown = """            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {"""
replacement_dropdown = """            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White).heightIn(max = 200.dp)
                ) {"""
content = content.replace(target_dropdown, replacement_dropdown)

# Update LocationSelectorMap usage
# In PostServiceScreen.kt, currently the LocationSelectorMap doesn't use the slot!
# We need to move the CustomTextFields for City, Locality, Pincode, Radius inside `contentBeforeSave`.
target_map = """                LocationSelectorMap(
                    addressQuery = "$locality $city $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->"""

replacement_map = """                LocationSelectorMap(
                    addressQuery = "$locality $city $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    contentBeforeSave = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(stringResource(R.string.city_label), stringResource(R.string.ph_kol), city) { city = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(stringResource(R.string.area_loc), stringResource(R.string.ph_salt), locality) { locality = it }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(stringResource(R.string.pincode_label), stringResource(R.string.ph_pin), pincode, keyboardType = KeyboardType.Number) { pincode = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CustomDropdown(stringResource(R.string.srv_radius), listOf("2 km", "5 km", "10 km", "20 km"), serviceRadius) { serviceRadius = it }
                            }
                        }
                    },
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->"""
content = content.replace(target_map, replacement_map)

# And remove those text fields from below LocationSelectorMap
target_remove = """                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(stringResource(R.string.city_label), stringResource(R.string.ph_kol), city) { city = it }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(stringResource(R.string.area_loc), stringResource(R.string.ph_salt), locality) { locality = it }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(stringResource(R.string.pincode_label), stringResource(R.string.ph_pin), pincode, keyboardType = KeyboardType.Number) { pincode = it }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdown(stringResource(R.string.srv_radius), listOf("2 km", "5 km", "10 km", "20 km"), serviceRadius) { serviceRadius = it }
                    }
                }"""
content = content.replace(target_remove, "                )")


with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated PostServiceScreen.kt")
