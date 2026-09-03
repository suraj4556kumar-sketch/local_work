import xml.etree.ElementTree as ET
import os

def get_keys(file_path):
    if not os.path.exists(file_path): return set()
    tree = ET.parse(file_path)
    return set(child.attrib.get('name') for child in tree.getroot() if child.tag == 'string')

en_keys = get_keys('app/src/main/res/values/strings.xml')
hi_keys = get_keys('app/src/main/res/values-hi/strings.xml')
bn_keys = get_keys('app/src/main/res/values-bn/strings.xml')

missing_hi = en_keys - hi_keys
missing_bn = en_keys - bn_keys

print(f"Total English strings: {len(en_keys)}")
print(f"Missing in Hindi: {len(missing_hi)}")
print(f"Missing in Bengali: {len(missing_bn)}")

print("\nSample missing in Hindi:")
for k in list(missing_hi)[:10]:
    print(k)

print("\nSample missing in Bengali:")
for k in list(missing_bn)[:10]:
    print(k)
