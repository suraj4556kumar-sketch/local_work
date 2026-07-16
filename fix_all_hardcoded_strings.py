import os
import re
import xml.etree.ElementTree as ET

strings_xml_path = r'app/src/main/res/values/strings.xml'
kt_dir = r'app/src/main/java/com/example/localconnect'

tree = ET.parse(strings_xml_path)
root = tree.getroot()

string_map = {}
for child in root:
    if child.tag == 'string':
        name = child.attrib.get('name')
        text = child.text
        if text:
            # unescape XML
            text = text.replace(r"\'", "'").replace(r'\"', '"').replace(r'\n', '\n')
            string_map[text] = name

# Sort by length descending
sorted_texts = sorted(string_map.keys(), key=len, reverse=True)

import_statement = 'import androidx.compose.ui.res.stringResource\n'

files_modified = 0

for root_dir, _, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root_dir, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            original_content = content
            
            for text in sorted_texts:
                res_name = string_map[text]
                escaped_text = re.escape(text)
                
                # Text("...")
                content = re.sub(
                    r'Text\(\s*"' + escaped_text + r'"(\s*[,)])',
                    r'Text(stringResource(R.string.' + res_name + r')\1',
                    content
                )
                
                # title = "..."
                content = re.sub(
                    r'title\s*=\s*"' + escaped_text + r'"',
                    r'title = stringResource(R.string.' + res_name + r')',
                    content
                )
                
                # label = { Text("...") }
                content = re.sub(
                    r'label\s*=\s*\{\s*Text\(\s*"' + escaped_text + r'"\s*\)\s*\}',
                    r'label = { Text(stringResource(R.string.' + res_name + r')) }',
                    content
                )
                
                # placeholder = { Text("...") }
                content = re.sub(
                    r'placeholder\s*=\s*\{\s*Text\(\s*"' + escaped_text + r'"\s*\)\s*\}',
                    r'placeholder = { Text(stringResource(R.string.' + res_name + r')) }',
                    content
                )

                # text = { Text("...") }
                content = re.sub(
                    r'text\s*=\s*\{\s*Text\(\s*"' + escaped_text + r'"\s*\)\s*\}',
                    r'text = { Text(stringResource(R.string.' + res_name + r')) }',
                    content
                )
                
                # fontWeight = ..., etc (optional) handling for text = { Text("...", fontWeight=...) }
                content = re.sub(
                    r'Text\(\s*"' + escaped_text + r'"\s*,\s*([^)]+)\)',
                    r'Text(stringResource(R.string.' + res_name + r'), \1)',
                    content
                )

            if content != original_content:
                if 'import androidx.compose.ui.res.stringResource' not in content:
                    content = re.sub(r'(package\s+[^\n]+\n)', r'\1\n' + import_statement, content, count=1)
                
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f'Updated {file}')
                files_modified += 1

print(f'Modified {files_modified} files.')
