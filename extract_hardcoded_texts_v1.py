import os
import re

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'
res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

# 1. Collect strings
hardcoded = set()
for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt') and 'ui' not in root:
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            matches = re.findall(r'Text\(\s*"([^"\n\r]*[a-zA-Z]+[^"\n\r]*)"', content)
            for m in matches:
                if not m.startswith('$') and '{' not in m and '}' not in m:
                    hardcoded.add(m.strip())

# Add some other known missing hardcoded buttons that might not be in Text("...")
# like text = "..." in OutlinedTextField placeholder, etc.
# Actually let's just stick to what we found first, and we can do a second pass if needed.

# 2. Create Keys
key_map = {}
for text in hardcoded:
    # convert to snake_case
    clean = re.sub(r'[^a-zA-Z0-9 ]', '', text.lower())
    key = '_'.join(clean.split())
    if not key:
        key = 'text_key'
    
    # Avoid collisions
    base_key = key
    counter = 1
    while key in key_map.values() and list(key_map.keys())[list(key_map.values()).index(key)] != text:
        key = f"{base_key}_{counter}"
        counter += 1
    key_map[text] = key

# 3. Replace in .kt files
for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt') and 'ui' not in root:
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            orig = content
            for text, key in key_map.items():
                # Replace Text("text" -> Text(stringResource(R.string.key)
                content = content.replace(f'Text("{text}"', f'Text(stringResource(R.string.{key})')
                content = content.replace(f'Text(" {text}"', f'Text(stringResource(R.string.{key})')
                # For `Text(text = "...")`
                content = content.replace(f'text = "{text}"', f'text = stringResource(R.string.{key})')

            if orig != content:
                # ensure imports
                if 'import androidx.compose.ui.res.stringResource' not in content:
                    content = content.replace('package com.example.localconnect\n', 'package com.example.localconnect\n\nimport androidx.compose.ui.res.stringResource\nimport com.example.localconnect.R\n')
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)

# 4. Append to strings.xml
def append_strings(file_path, fields_dict):
    if not os.path.exists(file_path): return
    with open(file_path, 'r', encoding='utf-8') as f:
        xml = f.read()
    
    insertion_idx = xml.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for text, key in fields_dict.items():
        if f'name="{key}"' not in xml:
            safe_val = text.replace('&', '&amp;').replace("'", r"\'").replace("<", "&lt;").replace(">", "&gt;")
            new_strings += f'    <string name="{key}">{safe_val}</string>\n'
    
    if new_strings:
        xml = xml[:insertion_idx] + new_strings + xml[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(xml)

append_strings(os.path.join(res_dir, 'values', 'strings.xml'), key_map)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), key_map)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), key_map)

print(f"Replaced {len(key_map)} strings.")
