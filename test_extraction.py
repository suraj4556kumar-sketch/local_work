import re
app_strings_path = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\AppStrings.kt'
with open(app_strings_path, 'r', encoding='utf-8') as f: content = f.read()
data_class_match = re.search(r'data class AppStrings\((.*?)\)', content, re.DOTALL)
if data_class_match:
    property_matches = re.findall(r'val\s+(\w+):\s*String(?:.*?=\s*"([^"]*)")?', data_class_match.group(1))
    keys = [k for k,v in property_matches]
    print('filters' in keys)
    print('searchHint' in keys)
    print('Total keys extracted:', len(keys))
