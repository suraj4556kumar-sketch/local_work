import os
import re

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'
for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            matches = re.findall(r'Text\(\s*"([^"]+)"', content)
            for m in matches:
                if len(m.strip()) > 1 and not re.match(r'^[\d\W]+$', m):
                    print(f'{file}: Text("{m}")')

            labels = re.findall(r'label\s*=\s*{\s*Text\(\s*"([^"]+)"', content)
            for m in labels:
                print(f'{file}: label: "{m}"')
                
            placeholders = re.findall(r'placeholder\s*=\s*{\s*Text\(\s*"([^"]+)"', content)
            for m in placeholders:
                print(f'{file}: placeholder: "{m}"')
