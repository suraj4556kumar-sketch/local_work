import os
import re

translations = {
    'Find available jobs in your area': ('find_available_jobs_area', 'अपने क्षेत्र में उपलब्ध नौकरियां खोजें', 'আপনার এলাকায় উপলব্ধ চাকরি খুঁজুন'),
    'Find Services': ('find_services', 'सेवाएं खोजें', 'পরিষেবা খুঁজুন'),
    'Find local service providers': ('find_local_service_providers', 'स्थानीय सेवा प्रदाता खोजें', 'স্থানীয় পরিষেবা প্রদানকারী খুঁজুন'),
    'Post Job/Service': ('post_job_service', 'नौकरी/सेवा पोस्ट करें', 'চাকরি/পরিষেবা পোস্ট করুন'),
    'Hire workers or provide a service': ('hire_workers_or_provide_service', 'श्रमिकों को काम पर रखें या सेवा प्रदान करें', 'শ্রমিক নিয়োগ করুন বা পরিষেবা প্রদান করুন')
}

res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

def append_strings(file_path, strings_dict, lang_index):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    insertion_idx = content.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for eng, vals in strings_dict.items():
        id = vals[0]
        val = vals[lang_index].replace('&', '&amp;').replace("'", r"\'")
        if f'name="{id}"' not in content:
            new_strings += f'    <string name="{id}">{val}</string>\n'
    
    if new_strings:
        content = content[:insertion_idx] + new_strings + content[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f'Added to {file_path}')

append_strings(os.path.join(res_dir, 'values', 'strings.xml'), {k: (v[0], k) for k, v in translations.items()}, 1)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), translations, 1)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), translations, 2)

home_screen_path = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\HomeScreen.kt'
with open(home_screen_path, 'r', encoding='utf-8') as f:
    content = f.read()

for eng, (id, hi, bn) in translations.items():
    pattern = r'"' + re.escape(eng) + r'"'
    replacement = r'androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.' + id + ')'
    content = re.sub(pattern, replacement, content)

with open(home_screen_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Updated HomeScreen.kt')
