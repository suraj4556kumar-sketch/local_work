import os
import re
from googletrans import Translator

try:
    translator = Translator()
except:
    translator = None

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'
res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

strings_xml_path = os.path.join(res_dir, 'values', 'strings.xml')
with open(strings_xml_path, 'r', encoding='utf-8') as f:
    xml_content = f.read()

existing_keys = set(re.findall(r'<string name="([^"]+)">', xml_content))

missing_keys = set()
for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            # find R.string.something
            keys = re.findall(r'R\.string\.([a-zA-Z0-9_]+)', content)
            for k in keys:
                if k not in existing_keys:
                    missing_keys.add(k)

print(f"Found {len(missing_keys)} missing keys: {missing_keys}")

if missing_keys:
    en_fields = {}
    hi_fields = {}
    bn_fields = {}
    
    for k in missing_keys:
        # Create a decent english string from the snake_case key
        en_str = ' '.join(word.capitalize() for word in k.split('_'))
        en_fields[k] = en_str
        
        try:
            if translator:
                hi_fields[k] = translator.translate(en_str, src='en', dest='hi').text
                bn_fields[k] = translator.translate(en_str, src='en', dest='bn').text
            else:
                hi_fields[k] = en_str
                bn_fields[k] = en_str
        except:
            hi_fields[k] = en_str
            bn_fields[k] = en_str

    def append_strings(file_path, fields_dict):
        with open(file_path, 'r', encoding='utf-8') as f:
            xml = f.read()
        
        insertion_idx = xml.rfind('</resources>')
        if insertion_idx == -1: return
        
        new_strings = ""
        for key, val in fields_dict.items():
            if not val or val.strip() == "": continue
            safe_val = val.replace('&', '&amp;').replace("'", r"\'").replace("<", "&lt;").replace(">", "&gt;")
            if f'name="{key}"' not in xml:
                new_strings += f'    <string name="{key}">{safe_val}</string>\n'
        
        if new_strings:
            xml = xml[:insertion_idx] + new_strings + xml[insertion_idx:]
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(xml)
            print(f'Appended {len(missing_keys)} strings to {file_path}')
            
    append_strings(os.path.join(res_dir, 'values', 'strings.xml'), en_fields)
    append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), hi_fields)
    append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), bn_fields)
