import os
import re
import sys

sys.stdout.reconfigure(encoding='utf-8')
texts = set()
for root, dirs, files in os.walk(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                matches = re.findall(r'Text\(\s*\"([^\"]+)\"', content)
                for m in matches:
                    if '{' not in m and '$' not in m:
                        texts.add(m)

for t in sorted(list(texts)):
    print(t)
