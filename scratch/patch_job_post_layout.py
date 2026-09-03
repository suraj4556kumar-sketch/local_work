import os

post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'

with open(post_job_file, 'r', encoding='utf-8') as f:
    content = f.read()

# We need to extract the "Use Current Location" block and the "Address Fields" block
# and put them inside `contentBetweenMapAndButton = { ... }` of `LocationSelectorMap`.

# The original block structure looks like this:
"""
            // 3. Location
            FormSection(stringResource(R.string.post_job_location)) {
                Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                LocationSelectorMap(
                    addressQuery = "$workAddress $city $state $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->
...
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
...
                        Text(stringResource(R.string.use_current_location), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = workAddress, onValueChange = { workAddress = it },
...
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = pincode, onValueChange = { pincode = it },
                        label = { Text(stringResource(R.string.pincode_star)) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = landmark, onValueChange = { landmark = it },
                        label = { Text(stringResource(R.string.landmark_opt)) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // 4. Salary
"""

# Wait, before the `git checkout`, the Use Current Location button was below the LocationSelectorMap!
# Let's extract everything from `Spacer(modifier = Modifier.height(12.dp))` after LocationSelectorMap
# down to the end of the location section (before `// 4. Salary`)

search_start = '                )\n                Spacer(modifier = Modifier.height(12.dp))\n                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {'
search_end = '                }\n            }\n\n            // 4. Salary'

start_idx = content.find(search_start)
end_idx = content.find(search_end)

if start_idx != -1 and end_idx != -1:
    extracted_block = content[start_idx + 18:end_idx + 17] # keep the closing brace for the row
    
    # We remove it from its original place
    new_content = content[:start_idx + 17] + '\n            }\n\n            // 4. Salary' + content[end_idx + 39:]
    
    # Now we insert it into LocationSelectorMap as contentBetweenMapAndButton
    insertion_point_str = '                    currentLon = longitude,\n                    \n                    onLocationSelected ='
    
    insertion_content = '                    contentBetweenMapAndButton = {\n' + extracted_block + '\n                    },\n                    onLocationSelected ='
    
    new_content = new_content.replace(insertion_point_str, insertion_content)
    
    with open(post_job_file, 'w', encoding='utf-8') as f:
        f.write(new_content)
    print("Successfully patched PostJobScreen.kt")
else:
    print("Could not find blocks. start_idx:", start_idx, "end_idx:", end_idx)

