import os
import re

app_strings_path = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\AppStrings.kt'
res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

with open(app_strings_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Extract all properties by finding all "val name: String"
property_matches = re.findall(r'val\s+(\w+):\s*String(?:[^="]*=\s*"([^"]*)")?', content)

en_all = {}
for key, default_val in property_matches:
    en_all[key] = default_val # might be empty string if no default

# 2. Extract values from en block to override defaults
en_match = re.search(r'else\s*->\s*AppStrings\(\s*// "en" default\s*(.*?)\)', content, re.DOTALL)
if en_match:
    en_fields = dict(re.findall(r'(\w+)\s*=\s*"([^"]*)",?', en_match.group(1)))
    for k, v in en_fields.items():
        en_all[k] = v

hi_match = re.search(r'"hi"\s*->\s*AppStrings\(\s*(.*?)\)\s*"bn"', content, re.DOTALL)
bn_match = re.search(r'"bn"\s*->\s*AppStrings\(\s*(.*?)\)\s*else', content, re.DOTALL)

hi_fields = {}
if hi_match:
    hi_fields = dict(re.findall(r'(\w+)\s*=\s*"([^"]*)",?', hi_match.group(1)))
    
bn_fields = {}
if bn_match:
    bn_fields = dict(re.findall(r'(\w+)\s*=\s*"([^"]*)",?', bn_match.group(1)))

from googletrans import Translator
try:
    translator = Translator()
except:
    translator = None

hi_all = {}
bn_all = {}

for key, en_val in en_all.items():
    if key in hi_fields and hi_fields[key].strip():
        hi_all[key] = hi_fields[key]
    else:
        # Fallback translation if missing
        try:
            if translator and en_val.strip():
                hi_all[key] = translator.translate(en_val, src='en', dest='hi').text
            else:
                hi_all[key] = en_val
        except:
            hi_all[key] = en_val
            
    if key in bn_fields and bn_fields[key].strip():
        bn_all[key] = bn_fields[key]
    else:
        try:
            if translator and en_val.strip():
                bn_all[key] = translator.translate(en_val, src='en', dest='bn').text
            else:
                bn_all[key] = en_val
        except:
            bn_all[key] = en_val

def append_strings(file_path, fields_dict):
    with open(file_path, 'r', encoding='utf-8') as f:
        xml_content = f.read()
    
    insertion_idx = xml_content.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for key, val in fields_dict.items():
        if not val or val.strip() == "": continue
        safe_val = val.replace('&', '&amp;').replace("'", r"\'").replace("<", "&lt;").replace(">", "&gt;")
        snake_key = re.sub(r'(?<!^)(?=[A-Z])', '_', key).lower()
        
        # Check if already exists in xml
        if f'name="{snake_key}"' not in xml_content:
            new_strings += f'    <string name="{snake_key}">{safe_val}</string>\n'
    
    if new_strings:
        xml_content = xml_content[:insertion_idx] + new_strings + xml_content[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(xml_content)
        print(f'Appended {len(re.findall(r"<string", new_strings))} strings to {file_path}')

append_strings(os.path.join(res_dir, 'values', 'strings.xml'), en_all)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), hi_all)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), bn_all)

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'

for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt') and file != 'AppStrings.kt':
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                kt_content = f.read()
                
            original_content = kt_content
            
            kt_content = re.sub(r'val strings\s*=\s*getStrings\([^)]+\)\s*\n', '', kt_content)
            kt_content = re.sub(r'var strings\s*=\s*getStrings\([^)]+\)\s*\n', '', kt_content)
            kt_content = re.sub(r'import com\.example\.localconnect\.getStrings\s*\n', '', kt_content)
            kt_content = re.sub(r'import com\.example\.localconnect\.AppStrings\s*\n', '', kt_content)

            kt_content = re.sub(r',\s*strings:\s*AppStrings', '', kt_content)
            kt_content = re.sub(r'strings:\s*AppStrings,?', '', kt_content)
            kt_content = re.sub(r',\s*strings\s*=\s*strings', '', kt_content)
            kt_content = re.sub(r'strings\s*=\s*strings,?', '', kt_content)

            # Extra specific cases
            kt_content = re.sub(r'strings:\s*AppStrings', '', kt_content)

            # Replace strings.myProperty with stringResource
            for key in en_all.keys():
                snake_key = re.sub(r'(?<!^)(?=[A-Z])', '_', key).lower()
                kt_content = re.sub(r'\bstrings\.' + key + r'\b', f'stringResource(R.string.{snake_key})', kt_content)
                
            if kt_content != original_content:
                if 'stringResource(' in kt_content and 'import androidx.compose.ui.res.stringResource' not in kt_content:
                    kt_content = kt_content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable\nimport androidx.compose.ui.res.stringResource\nimport com.example.localconnect.R')
                
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(kt_content)
                print(f'Refactored {file}')

os.remove(app_strings_path)
print("Deleted AppStrings.kt")
