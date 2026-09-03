import os
import re

# Dictionary of strings to add
strings_to_add = {
    # Headers
    "post_job_employer_info": {"en": "1. Employer Info", "hi": "1. नियोक्ता की जानकारी", "bn": "১. নিয়োগকর্তার তথ্য"},
    "post_job_details": {"en": "2. Job Details", "hi": "2. नौकरी का विवरण", "bn": "২. চাকরির বিবরণ"},
    "post_job_salary": {"en": "4. Salary", "hi": "4. वेतन", "bn": "৪. বেতন"},
    "post_job_candidate_reqs": {"en": "5. Candidate Requirements", "hi": "5. उम्मीदवार की आवश्यकताएं", "bn": "৫. প্রার্থীর প্রয়োজনীয়তা"},
    "post_job_description": {"en": "7. Description", "hi": "7. विवरण", "bn": "৭. বিবরণ"},
    "post_job_skills": {"en": "8. Skills & Responsibilities", "hi": "8. कौशल और जिम्मेदारियां", "bn": "৮. দক্ষতা এবং দায়িত্ব"},
    "post_job_docs": {"en": "9. Documents Required", "hi": "9. आवश्यक दस्तावेज", "bn": "৯. প্রয়োজনীয় কাগজপত্র"},
    "post_job_perks": {"en": "10. Perks & Preferences", "hi": "10. भत्ते और प्राथमिकताएं", "bn": "১০. সুযোগ-সুবিধা এবং পছন্দ"},

    # Categories
    "cat_cook": {"en": "Cook", "hi": "रसोइया", "bn": "রাঁধুনি"},
    "cat_maid": {"en": "Maid", "hi": "नौकरानी", "bn": "কাজের লোক"},
    "cat_driver": {"en": "Driver", "hi": "ड्राइवर", "bn": "চালক"},
    "cat_office_boy": {"en": "Office Boy", "hi": "ऑफिस बॉय", "bn": "অফিস বয়"},
    "cat_security_guard": {"en": "Security Guard", "hi": "सुरक्षा गार्ड", "bn": "নিরাপত্তারক্ষী"},
    "cat_delivery": {"en": "Delivery", "hi": "डिलीवरी", "bn": "ডেলিভারি"},
    "cat_electrician": {"en": "Electrician", "hi": "इलेक्ट्रीशियन", "bn": "ইলেকট্রিশিয়ান"},
    "cat_plumber": {"en": "Plumber", "hi": "प्लंबर", "bn": "প্লাম্বার"},
    "cat_carpenter": {"en": "Carpenter", "hi": "बढ़ई", "bn": "কাঠমিস্ত্রি"},
    "cat_painter": {"en": "Painter", "hi": "पेंटर", "bn": "রংমিস্ত্রি"},
    "cat_cleaner": {"en": "Cleaner", "hi": "सफाईवाला", "bn": "পরিচ্ছন্নতাকর্মী"},
    "cat_mechanic": {"en": "Mechanic", "hi": "मैकेनिक", "bn": "মেকানিক"},
    "cat_welder": {"en": "Welder", "hi": "वेल्डर", "bn": "ওয়েল্ডার"},
    "cat_mason": {"en": "Mason", "hi": "राजमिस्त्री", "bn": "রাজমিস্ত্রি"},
    "cat_technician": {"en": "Technician", "hi": "तकनीशियन", "bn": "টেকনিশিয়ান"},
    "cat_beautician": {"en": "Beautician", "hi": "ब्यूटीशियन", "bn": "বিউটিশিয়ান"},
    "cat_tutor": {"en": "Tutor", "hi": "ट्यूटर", "bn": "শিক্ষক"},
    "cat_data_entry": {"en": "Data Entry", "hi": "डेटा एंट्री", "bn": "ডেটা এন্ট্রি"},
    "cat_accountant": {"en": "Accountant", "hi": "एकाउंटेंट", "bn": "হিসাবরক্ষক"},
    "cat_sales_exec": {"en": "Sales Executive", "hi": "सेल्स एग्जीक्यूटिव", "bn": "সেলস এক্সিকিউটিভ"},
    "cat_other": {"en": "Other", "hi": "अन्य", "bn": "অন্যান্য"},

    # Experience Options
    "exp_fresher": {"en": "Fresher / No Experience", "hi": "फ्रेशर / कोई अनुभव नहीं", "bn": "নতুন / কোনো অভিজ্ঞতা নেই"},
    "exp_0_1": {"en": "0 - 1 Year", "hi": "0 - 1 वर्ष", "bn": "০ - ১ বছর"},
    "exp_1_3": {"en": "1 - 3 Years", "hi": "1 - 3 वर्ष", "bn": "১ - ৩ বছর"},
    "exp_3_plus": {"en": "3+ Years", "hi": "3+ वर्ष", "bn": "৩+ বছর"},

    # Education Options
    "edu_not_required": {"en": "Not Required", "hi": "आवश्यक नहीं", "bn": "প্রয়োজন নেই"},
    "edu_10th": {"en": "10th Pass", "hi": "10वीं पास", "bn": "১০ম পাস"},
    "edu_12th": {"en": "12th Pass", "hi": "12वीं पास", "bn": "১২শ পাস"},
    "edu_graduate": {"en": "Graduate", "hi": "स्नातक", "bn": "স্নাতক"},

    # Gender Options
    "gender_any": {"en": "Any", "hi": "कोई भी", "bn": "যেকোনো"},
    "gender_male": {"en": "Male Only", "hi": "केवल पुरुष", "bn": "শুধুমাত্র পুরুষ"},
    "gender_female": {"en": "Female Only", "hi": "केवल महिला", "bn": "শুধুমাত্র মহিলা"},

    # Documents
    "doc_aadhaar": {"en": "Aadhaar Card", "hi": "आधार कार्ड", "bn": "আধার কার্ড"},
    "doc_driving_license": {"en": "Driving License", "hi": "ड्राइविंग लाइसेंस", "bn": "ড্রাইভিং লাইসেন্স"},
    "doc_resume": {"en": "CV / Resume", "hi": "सीवी / बायोडाटा", "bn": "সিভি / জীবনবৃত্তান্ত"},

    # Perks
    "perk_free_food": {"en": "Free Food", "hi": "मुफ्त भोजन", "bn": "বিনামূল্যে খাবার"},
    "perk_accommodation": {"en": "Accommodation", "hi": "आवास", "bn": "বাসস্থান"},
    "perk_weekly_off": {"en": "Weekly Off", "hi": "साप्ताहिक अवकाश", "bn": "সাপ্তাহिक ছুটি"},
    "perk_travel_allowance": {"en": "Travel Allowance", "hi": "यात्रा भत्ता", "bn": "ভ্রমণ ভাতা"}
}

def update_strings_xml(lang_code, filepath):
    if not os.path.exists(filepath): return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    end_tag = "</resources>"
    new_xml = ""
    for key, values in strings_to_add.items():
        if f'name="{key}"' not in content:
            val = values.get(lang_code, values["en"]).replace("'", "\\'").replace("&", "&amp;")
            new_xml += f'    <string name="{key}">{val}</string>\n'
    
    if new_xml:
        updated = content.replace(end_tag, new_xml + end_tag)
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(updated)
        print(f"Updated {filepath} with {new_xml.count('<string')} new strings.")

# 1. Update XMLs
update_strings_xml("en", "app/src/main/res/values/strings.xml")
update_strings_xml("hi", "app/src/main/res/values-hi/strings.xml")
update_strings_xml("bn", "app/src/main/res/values-bn/strings.xml")


# 2. Patch employer_company_star in hi and bn
for filepath in ["app/src/main/res/values-hi/strings.xml", "app/src/main/res/values-bn/strings.xml"]:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    content = content.replace('नौकरी देने वाले / कंपनी का नाम *', 'कंपनी का नाम *')
    content = content.replace('চাকরিদাতার / কোম্পানির নাম *', 'কোম্পানির নাম *')
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
print("Updated employer_company_star in hi and bn")


# 3. Patch PostJobScreen.kt
kt_path = "app/src/main/java/com/example/localconnect/PostJobScreen.kt"
with open(kt_path, 'r', encoding='utf-8') as f:
    kt_content = f.read()

# Replace Headers
kt_content = kt_content.replace('FormSection("1. Employer Info")', 'FormSection(stringResource(R.string.post_job_employer_info))')
kt_content = kt_content.replace('FormSection("2. Job Details")', 'FormSection(stringResource(R.string.post_job_details))')
kt_content = kt_content.replace('FormSection("4. Salary")', 'FormSection(stringResource(R.string.post_job_salary))')
kt_content = kt_content.replace('FormSection("5. Candidate Requirements")', 'FormSection(stringResource(R.string.post_job_candidate_reqs))')
kt_content = kt_content.replace('FormSection("7. Description")', 'FormSection(stringResource(R.string.post_job_description))')
kt_content = kt_content.replace('FormSection("8. Skills & Responsibilities")', 'FormSection(stringResource(R.string.post_job_skills))')
kt_content = kt_content.replace('FormSection("9. Documents Required")', 'FormSection(stringResource(R.string.post_job_docs))')
kt_content = kt_content.replace('FormSection("10. Perks & Preferences")', 'FormSection(stringResource(R.string.post_job_perks))')

# Replace lists
kt_content = kt_content.replace('listOf("Cook", "Maid", "Driver", "Office Boy", "Security Guard", "Delivery", "Electrician", "Plumber", "Carpenter", "Painter", "Cleaner", "Mechanic", "Welder", "Mason", "Technician", "Beautician", "Tutor", "Data Entry", "Accountant", "Sales Executive", "Other")', 
    'listOf(stringResource(R.string.cat_cook), stringResource(R.string.cat_maid), stringResource(R.string.cat_driver), stringResource(R.string.cat_office_boy), stringResource(R.string.cat_security_guard), stringResource(R.string.cat_delivery), stringResource(R.string.cat_electrician), stringResource(R.string.cat_plumber), stringResource(R.string.cat_carpenter), stringResource(R.string.cat_painter), stringResource(R.string.cat_cleaner), stringResource(R.string.cat_mechanic), stringResource(R.string.cat_welder), stringResource(R.string.cat_mason), stringResource(R.string.cat_technician), stringResource(R.string.cat_beautician), stringResource(R.string.cat_tutor), stringResource(R.string.cat_data_entry), stringResource(R.string.cat_accountant), stringResource(R.string.cat_sales_exec), stringResource(R.string.cat_other))')

kt_content = kt_content.replace('listOf("Fresher / No Experience", "0 - 1 Year", "1 - 3 Years", "3+ Years")',
    'listOf(stringResource(R.string.exp_fresher), stringResource(R.string.exp_0_1), stringResource(R.string.exp_1_3), stringResource(R.string.exp_3_plus))')

kt_content = kt_content.replace('listOf("Not Required", "10th Pass", "12th Pass", "Graduate")',
    'listOf(stringResource(R.string.edu_not_required), stringResource(R.string.edu_10th), stringResource(R.string.edu_12th), stringResource(R.string.edu_graduate))')

kt_content = kt_content.replace('listOf("Any", "Male Only", "Female Only")',
    'listOf(stringResource(R.string.gender_any), stringResource(R.string.gender_male), stringResource(R.string.gender_female))')

kt_content = kt_content.replace('listOf("Free Food", "Accommodation", "Weekly Off", "Travel Allowance")',
    'listOf(stringResource(R.string.perk_free_food), stringResource(R.string.perk_accommodation), stringResource(R.string.perk_weekly_off), stringResource(R.string.perk_travel_allowance))')

# Replace Document checkboxes
kt_content = kt_content.replace('CheckboxOption("Aadhaar Card", documentsRequired.contains("Aadhaar Card")',
    'CheckboxOption(stringResource(R.string.doc_aadhaar), documentsRequired.contains(stringResource(R.string.doc_aadhaar))')
kt_content = kt_content.replace('documentsRequired.add("Aadhaar Card")', 'documentsRequired.add(stringResource(R.string.doc_aadhaar))')
kt_content = kt_content.replace('documentsRequired.remove("Aadhaar Card")', 'documentsRequired.remove(stringResource(R.string.doc_aadhaar))')

kt_content = kt_content.replace('CheckboxOption("Driving License", documentsRequired.contains("Driving License")',
    'CheckboxOption(stringResource(R.string.doc_driving_license), documentsRequired.contains(stringResource(R.string.doc_driving_license))')
kt_content = kt_content.replace('documentsRequired.add("Driving License")', 'documentsRequired.add(stringResource(R.string.doc_driving_license))')
kt_content = kt_content.replace('documentsRequired.remove("Driving License")', 'documentsRequired.remove(stringResource(R.string.doc_driving_license))')

kt_content = kt_content.replace('CheckboxOption("CV / Resume", documentsRequired.contains("CV / Resume")',
    'CheckboxOption(stringResource(R.string.doc_resume), documentsRequired.contains(stringResource(R.string.doc_resume))')
kt_content = kt_content.replace('documentsRequired.add("CV / Resume")', 'documentsRequired.add(stringResource(R.string.doc_resume))')
kt_content = kt_content.replace('documentsRequired.remove("CV / Resume")', 'documentsRequired.remove(stringResource(R.string.doc_resume))')

kt_content = kt_content.replace('listOf("Aadhaar Card", "Driving License", "CV / Resume")',
    'listOf(stringResource(R.string.doc_aadhaar), stringResource(R.string.doc_driving_license), stringResource(R.string.doc_resume))')

# 4. Remove Live Preview Card
preview_start = kt_content.find('            // 13. Live Preview')
if preview_start != -1:
    preview_end = kt_content.find('            Spacer(modifier = Modifier.height(32.dp)) // Padding for bottom bar', preview_start)
    if preview_end != -1:
        kt_content = kt_content[:preview_start] + kt_content[preview_end:]
        print("Removed Live Preview Card")

with open(kt_path, 'w', encoding='utf-8') as f:
    f.write(kt_content)
print("Updated PostJobScreen.kt")
