import os

kt_file = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\HomeScreen.kt'
with open(kt_file, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('title = "Find Services", // Hardcoding as requested, can localize later', 'title = stringResource(R.string.find_services_title),')
content = content.replace('description = "Find local service providers",', 'description = stringResource(R.string.find_services_desc),')
content = content.replace('title = "Post Job/Service",', 'title = stringResource(R.string.post_job_service_title),')
content = content.replace('description = "Hire workers or provide a service",', 'description = stringResource(R.string.post_job_service_desc),')
content = content.replace('Triple(Icons.Filled.Work, "My Jobs", 7)', 'Triple(Icons.Filled.Work, stringResource(R.string.my_jobs), 7)')
content = content.replace('Triple(Icons.Filled.Build, "My Services", 6)', 'Triple(Icons.Filled.Build, stringResource(R.string.my_services), 6)')

with open(kt_file, 'w', encoding='utf-8') as f:
    f.write(content)
print('HomeScreen.kt updated.')

def append_strings(file_path, strings_to_add):
    if not os.path.exists(file_path): return
    with open(file_path, 'r', encoding='utf-8') as f:
        xml = f.read()
    insertion_idx = xml.rfind('</resources>')
    new_strings = ''
    for key, val in strings_to_add.items():
        if f'name="{key}"' not in xml:
            new_strings += f'    <string name="{key}">{val}</string>\n'
    if new_strings:
        xml = xml[:insertion_idx] + new_strings + xml[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(xml)

en_strings = {
    'find_services_title': 'Find Services',
    'find_services_desc': 'Find local service providers',
    'post_job_service_title': 'Post Job/Service',
    'post_job_service_desc': 'Hire workers or provide a service',
    'my_jobs': 'My Jobs',
    'my_services': 'My Services'
}
hi_strings = {
    'find_services_title': 'सेवाएँ खोजें',
    'find_services_desc': 'स्थानीय सेवा प्रदाताओं को खोजें',
    'post_job_service_title': 'नौकरी/सेवा पोस्ट करें',
    'post_job_service_desc': 'कर्मचारियों को काम पर रखें या सेवा प्रदान करें',
    'my_jobs': 'मेरी नौकरियां',
    'my_services': 'मेरी सेवाएँ'
}
bn_strings = {
    'find_services_title': 'পরিষেবা খুঁজুন',
    'find_services_desc': 'স্থানীয় পরিষেবা প্রদানকারীদের খুঁজুন',
    'post_job_service_title': 'কাজ/পরিষেবা পোস্ট করুন',
    'post_job_service_desc': 'কর্মী নিয়োগ করুন বা একটি পরিষেবা প্রদান করুন',
    'my_jobs': 'আমার কাজ',
    'my_services': 'আমার পরিষেবাগুলি'
}

append_strings(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res\values\strings.xml', en_strings)
append_strings(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res\values-hi\strings.xml', hi_strings)
append_strings(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res\values-bn\strings.xml', bn_strings)
print('XMLs updated.')
