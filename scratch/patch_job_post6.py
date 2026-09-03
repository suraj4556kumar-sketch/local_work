import re
import os

loc_map_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/LocationSelectorMap.kt'
post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'

# 1. Update LocationSelectorMap.kt
with open(loc_map_file, 'r', encoding='utf-8') as f:
    content = f.read()

# signature
content = content.replace(
    'onMapTouch: (Boolean) -> Unit = {},',
    'onMapTouch: (Boolean) -> Unit = {},\n    contentBetweenMapAndButton: @Composable () -> Unit = {},\n'
)

# layout
content = content.replace(
    '        }\n        Spacer(modifier = Modifier.height(8.dp))\n        Button(',
    '        }\n        contentBetweenMapAndButton()\n        Spacer(modifier = Modifier.height(8.dp))\n        Button('
)

with open(loc_map_file, 'w', encoding='utf-8') as f:
    f.write(content)


# 2. Update PostJobScreen.kt
with open(post_job_file, 'r', encoding='utf-8') as f:
    content = f.read()

# The Use Current Location block (from my previous patch) is right above LocationSelectorMap
# The Address fields are right below LocationSelectorMap.
# I need to find the whole block starting from "Spacer(modifier = Modifier.height(12.dp))" before Use Current Location,
# and ending at the last Row of Address fields.
# But since regex on such a large block is brittle, let's just parse lines.
lines = content.split('\n')

# Find LocationSelectorMap invocation in PostJobScreen
start_idx = -1
for i, line in enumerate(lines):
    if 'LocationSelectorMap(' in line:
        start_idx = i
        break

if start_idx != -1:
    # Look backwards to find where Use Current Location starts (the Spacer)
    # The previous patch placed it right above LocationSelectorMap.
    # So we go up until we see `// 3. Location` or `Text(stringResource(R.string.select_loc_map)`
    use_curr_loc_start = -1
    for i in range(start_idx, 0, -1):
        if 'Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {' in lines[i]:
            use_curr_loc_start = i - 1 # The Spacer before it
            break

    # Look forwards to find where Location tab ends (the `}` before `// 4. Salary`)
    form_end = -1
    for i in range(start_idx, len(lines)):
        if '// 4. Salary' in lines[i]:
            form_end = i - 1
            break

    if use_curr_loc_start != -1 and form_end != -1:
        # We know LocationSelectorMap starts at start_idx
        # Let's find where LocationSelectorMap arguments end.
        loc_map_end = -1
        for i in range(start_idx, len(lines)):
            if ')' in lines[i] and 'Spacer(modifier = Modifier.height(12.dp))' in lines[i+1]:
                # The trailing lambda might end with `}` and then `)`
                pass
            if ')' in lines[i] and lines[i].strip() == ')':
                loc_map_end = i
                break
        
        # Okay, let's just construct the new Location tab body.
        # We want:
        # Text(stringResource(R.string.select_loc_map), ...)
        # Spacer(8.dp)
        # LocationSelectorMap(
        #     ... arguments ...
        #     contentBetweenMapAndButton = {
        #         [Use Current Location block]
        #         [Address text fields block]
        #     },
        #     onLocationSelected = { ... }
        # )
        
        # Let's extract the exact text of Use Current Location block:
        use_current_loc_block = "\\n".join(lines[use_curr_loc_start:start_idx])
        
        # Address fields block:
        address_fields_block = "\\n".join(lines[loc_map_end+1:form_end-1]) # form_end is `            }`
        
        # LocationSelectorMap args before onLocationSelected:
        loc_args_start = start_idx + 1
        on_loc_selected_idx = -1
        for i in range(start_idx, loc_map_end):
            if 'onLocationSelected = {' in lines[i]:
                on_loc_selected_idx = i
                break
                
        loc_args_block = "\\n".join(lines[loc_args_start:on_loc_selected_idx])
        on_loc_selected_block = "\\n".join(lines[on_loc_selected_idx:loc_map_end+1])
        
        new_location_tab = f"""            // 3. Location
            FormSection(stringResource(R.string.post_job_location)) {{
                Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                LocationSelectorMap(
{loc_args_block}
                    contentBetweenMapAndButton = {{
{use_current_loc_block}
{address_fields_block}
                    }},
{on_loc_selected_block}
            }}
"""
        
        # Find where `// 3. Location` starts
        loc_tab_start = -1
        for i in range(use_curr_loc_start, 0, -1):
            if '// 3. Location' in lines[i]:
                loc_tab_start = i
                break
                
        if loc_tab_start != -1:
            # Replace the old location tab with the new one
            old_location_tab = "\\n".join(lines[loc_tab_start:form_end])
            
            # Since joining arrays with \\n loses the trailing \\n, it's safer to just slice the list
            lines[loc_tab_start:form_end] = new_location_tab.strip('\\n').split('\\n')
            
            with open(post_job_file, 'w', encoding='utf-8') as f:
                f.write('\\n'.join(lines))
            print("Patch applied to PostJobScreen.kt successfully.")
        else:
            print("Could not find // 3. Location")
    else:
        print("Could not find blocks boundaries.")
else:
    print("Could not find LocationSelectorMap")
