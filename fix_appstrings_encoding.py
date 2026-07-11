import os
import re
from googletrans import Translator

file_path = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\AppStrings.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# find the en block
en_block_match = re.search(r'else\s*->\s*AppStrings\(\s*// "en" default\s*(.*?)\)', content, re.DOTALL)
if not en_block_match:
    print("Could not find en block")
    exit(1)

en_block = en_block_match.group(1)

# parse key=value
# fields look like: continueBtn           = "Continue",
fields = re.findall(r'(\w+)\s*=\s*"([^"]*)",?', en_block)

translator = Translator()

hi_lines = []
bn_lines = []

for key, val in fields:
    if val.strip() == "":
        hi_lines.append(f'        {key.ljust(22)} = "",')
        bn_lines.append(f'        {key.ljust(22)} = "",')
        continue
        
    try:
        hi_val = translator.translate(val, src='en', dest='hi').text
        bn_val = translator.translate(val, src='en', dest='bn').text
    except Exception as e:
        print(f"Failed to translate {val}: {e}")
        hi_val = val
        bn_val = val
        
    hi_lines.append(f'        {key.ljust(22)} = "{hi_val}",')
    bn_lines.append(f'        {key.ljust(22)} = "{bn_val}",')

hi_block = '    "hi" -> AppStrings(\n' + '\n'.join(hi_lines) + '\n    )'
bn_block = '    "bn" -> AppStrings(\n' + '\n'.join(bn_lines) + '\n    )'

# Replace old blocks
content = re.sub(r'    "hi" -> AppStrings\([^)]+\)', hi_block, content, flags=re.DOTALL)
content = re.sub(r'    "bn" -> AppStrings\([^)]+\)', bn_block, content, flags=re.DOTALL)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Successfully rewrote AppStrings.kt")
