import re

post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'
with open(post_job_file, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Lowercase session name
content = content.replace(
    'var employerName by remember { mutableStateOf(session.name) }',
    'var employerName by remember { mutableStateOf(session.name.lowercase()) }'
)
content = content.replace(
    'var contactPersonName by remember { mutableStateOf(session.name) }',
    'var contactPersonName by remember { mutableStateOf(session.name.lowercase()) }'
)

# 2. Swap Use Current Location and LocationSelectorMap
# The Use Current Location block looks like this:
#                 Spacer(modifier = Modifier.height(12.dp))
#                 Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
#                     Button( ... ) { ... }
#                 }
# We'll extract this block using regex.
current_loc_pattern = re.compile(
    r'(                Spacer\(modifier = Modifier\.height\(12\.dp\)\)\n                Row\(modifier = Modifier\.fillMaxWidth\(\), horizontalArrangement = Arrangement\.End\) \{\n                    Button\(\n                        modifier = Modifier\.fillMaxWidth\(\),\n.*?                    \} \{\n.*?\n.*?\n.*?\n.*?\n                    \}\n                \}\n)',
    re.DOTALL
)

match = current_loc_pattern.search(content)
if match:
    current_loc_block = match.group(1)
    # Remove it from its original place
    content = content.replace(current_loc_block, '')
    
    # Insert it right before LocationSelectorMap
    location_selector_pattern = r'(                LocationSelectorMap\()'
    content = re.sub(
        location_selector_pattern,
        lambda m: current_loc_block + m.group(1),
        content
    )
else:
    print("Could not find Use Current Location block")

with open(post_job_file, 'w', encoding='utf-8') as f:
    f.write(content)


# 3. strings.xml update
strings_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/res/values/strings.xml'
with open(strings_file, 'r', encoding='utf-8') as f:
    s_content = f.read()

s_content = s_content.replace('Landmark (Optional)', 'Landmark')

with open(strings_file, 'w', encoding='utf-8') as f:
    f.write(s_content)


# 4. LocationSelectorMap.kt update
loc_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/LocationSelectorMap.kt'
with open(loc_file, 'r', encoding='utf-8') as f:
    l_content = f.read()

# Comment out Marker(...)
marker_pattern = re.compile(r'(                Marker\(\n                    state = MarkerState\(position = markerPosition\),\n                    title = stringResource\(R\.string\.selected_loc\),\n                    draggable = true\n                \))', re.DOTALL)

l_content = marker_pattern.sub(r'/* \1 */', l_content)

with open(loc_file, 'w', encoding='utf-8') as f:
    f.write(l_content)

print("Patch applied successfully.")
