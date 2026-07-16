import os
import re
import json
import xml.etree.ElementTree as ET

strings_xml_path = r'app/src/main/res/values/strings.xml'
kt_dir = r'app/src/main/java/com/example/localconnect'

tree = ET.parse(strings_xml_path)
root = tree.getroot()
existing_texts = set()
for child in root:
    if child.tag == 'string' and child.text:
        text = child.text.replace(r"\'", "'").replace(r'\"', '"').replace(r'\n', '\n')
        existing_texts.add(text)

patterns_to_check = [
    re.compile(r'Text\(\s*"([^"]+)"'),
    re.compile(r'title\s*=\s*"([^"]+)"'),
    re.compile(r'label\s*=\s*\{\s*Text\(\s*"([^"]+)"\s*\)\s*\}'),
    re.compile(r'placeholder\s*=\s*\{\s*Text\(\s*"([^"]+)"\s*\)\s*\}'),
    re.compile(r'text\s*=\s*\{\s*Text\(\s*"([^"]+)"\s*\)\s*\}'),
]

missing_strings = set()

for root_dir, _, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root_dir, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
                
            for pattern in patterns_to_check:
                for match in pattern.findall(content):
                    if match and match not in existing_texts and not match.startswith('$'):
                        # Ignore single characters or pure symbols if any
                        if len(match) > 1 and re.search(r'[a-zA-Z]', match):
                            missing_strings.add(match)

with open('missing_strings.json', 'w', encoding='utf-8') as f:
    json.dump(list(missing_strings), f, indent=4)
