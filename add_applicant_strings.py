import os
import re

translations = {
    "filter_all": ("सभी", "সব"),
    "filter_new": ("नया", "নতুন"),
    "status_pending": ("लंबित", "অপেক্ষমাণ"),
    "status_unknown": ("अज्ञात", "অজানা"),
    "offer_accepted": ("प्रस्ताव स्वीकृत", "প্রস্তাব গৃহীত"),
    "offer_rejected": ("प्रस्ताव अस्वीकृत", "প্রস্তাব প্রত্যাখ্যাত"),
}

res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

def append_strings(file_path, lang_index):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    insertion_idx = content.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for id, vals in translations.items():
        val = vals[lang_index] if lang_index != -1 else id.replace('_', ' ').title()
        if f'name="{id}"' not in content:
            new_strings += f'    <string name="{id}">{val}</string>\n'
    
    if new_strings:
        content = content[:insertion_idx] + new_strings + content[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Added to {file_path}")

append_strings(os.path.join(res_dir, 'values', 'strings.xml'), -1)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), 0)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), 1)

print("Strings added.")
