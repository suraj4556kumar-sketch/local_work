import os
import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'

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
                    
for text in sorted(list(hardcoded)):
    print(f'{text}')
