import os
import re

app_strings_path = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\AppStrings.kt'
res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

with open(app_strings_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Extract the three blocks
en_match = re.search(r'else\s*->\s*AppStrings\(\s*// "en" default\s*(.*?)\)', content, re.DOTALL)
hi_match = re.search(r'"hi"\s*->\s*AppStrings\(\s*(.*?)\)', content, re.DOTALL)
bn_match = re.search(r'"bn"\s*->\s*AppStrings\(\s*(.*?)\)', content, re.DOTALL)

en_block = en_match.group(1)
hi_block = hi_match.group(1)
bn_block = bn_match.group(1)

en_fields = dict(re.findall(r'(\w+)\s*=\s*"([^"]*)",?', en_block))
hi_fields = dict(re.findall(r'(\w+)\s*=\s*"([^"]*)",?', hi_block))
bn_fields = dict(re.findall(r'(\w+)\s*=\s*"([^"]*)",?', bn_block))

def append_strings(file_path, fields_dict):
    with open(file_path, 'r', encoding='utf-8') as f:
        xml_content = f.read()
    
    insertion_idx = xml_content.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for key, val in fields_dict.items():
        if val.strip() == "": continue
        safe_val = val.replace('&', '&amp;').replace("'", r"\'")
        # convert camelCase to snake_case for XML key
        snake_key = re.sub(r'(?<!^)(?=[A-Z])', '_', key).lower()
        if f'name="{snake_key}"' not in xml_content:
            new_strings += f'    <string name="{snake_key}">{safe_val}</string>\n'
    
    if new_strings:
        xml_content = xml_content[:insertion_idx] + new_strings + xml_content[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(xml_content)
        print(f'Appended to {file_path}')

append_strings(os.path.join(res_dir, 'values', 'strings.xml'), en_fields)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), hi_fields)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), bn_fields)

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'

for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt') and file != 'AppStrings.kt':
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                kt_content = f.read()
                
            original_content = kt_content
            
            # Remove getStrings initialization
            kt_content = re.sub(r'val strings = getStrings\([^)]+\)\s*\n', '', kt_content)
            kt_content = re.sub(r'val strings\s*=\s*getStrings\([^)]+\)\s*\n', '', kt_content)
            kt_content = re.sub(r'var strings\s*=\s*getStrings\([^)]+\)\s*\n', '', kt_content)
            
            # Remove getStrings imports if any
            kt_content = re.sub(r'import com\.example\.localconnect\.getStrings\s*\n', '', kt_content)
            kt_content = re.sub(r'import com\.example\.localconnect\.AppStrings\s*\n', '', kt_content)

            # Some files take AppStrings as a parameter in Composable functions
            kt_content = re.sub(r',\s*strings:\s*AppStrings', '', kt_content)
            kt_content = re.sub(r'strings:\s*AppStrings,?', '', kt_content)
            
            # Remove strings = strings when passing it
            kt_content = re.sub(r',\s*strings\s*=\s*strings', '', kt_content)
            kt_content = re.sub(r'strings\s*=\s*strings,?', '', kt_content)

            # Replace strings.myProperty with stringResource
            for key in en_fields.keys():
                snake_key = re.sub(r'(?<!^)(?=[A-Z])', '_', key).lower()
                kt_content = re.sub(r'\bstrings\.' + key + r'\b', f'stringResource(R.string.{snake_key})', kt_content)
                
            if kt_content != original_content:
                # add imports if missing
                if 'stringResource(' in kt_content and 'import androidx.compose.ui.res.stringResource' not in kt_content:
                    kt_content = kt_content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable\nimport androidx.compose.ui.res.stringResource\nimport com.example.localconnect.R')
                
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(kt_content)
                print(f'Refactored {file}')

os.remove(app_strings_path)
print("Deleted AppStrings.kt")
