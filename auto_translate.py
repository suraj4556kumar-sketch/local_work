import os
import re

translations = {
    "APPLICANTS FOR": ("applicants_for", "के लिए आवेदक", "এর জন্য আবেদনকারী"),
    "TOTAL": ("total", "कुल", "মোট"),
    "Accept": ("accept", "स्वीकार करें", "গ্রহণ করুন"),
    "Accept Offer": ("accept_offer", "प्रस्ताव स्वीकार करें", "প্রস্তাব গ্রহণ করুন"),
    "Accepted": ("accepted", "स्वीकृत", "গৃহীত"),
    "Applicant Accepted Offer": ("applicant_accepted_offer", "आवेदक ने प्रस्ताव स्वीकार किया", "আবেদনকারী প্রস্তাব গ্রহণ করেছেন"),
    "Applicant Rejected Offer": ("applicant_rejected_offer", "आवेदक ने प्रस्ताव अस्वीकार किया", "আবেদনকারী প্রস্তাব প্রত্যাখ্যান করেছেন"),
    "Applied": ("applied", "लागू किया", "আবেদন করা হয়েছে"),
    "Apply Now": ("apply_now", "अभी आवेदन करें", "এখনই আবেদন করুন"),
    "Back": ("back", "पीछे", "পিছনে"),
    "Benefits": ("benefits", "लाभ", "সুবিধা"),
    "Call": ("call", "कॉल करें", "কল করুন"),
    "Call Applicant": ("call_applicant", "आवेदक को कॉल करें", "আবেদনকারীকে কল করুন"),
    "Call Employer": ("call_employer", "नियोक्ता को कॉल करें", "নিয়োগকর্তাকে কল করুন"),
    "Camera": ("camera", "कैमरा", "ক্যামেরা"),
    "Cancel": ("cancel", "रद्द करें", "বাতिल করুন"),
    "Close": ("close", "बंद करें", "বন্ধ করুন"),
    "DOCUMENTS": ("documents_title", "दस्तावेज़", "নথিপত্র"),
    "Documents Required": ("documents_required", "आवश्यक दस्तावेज़", "প্রয়োজনীয় নথিপত্র"),
    "Employer Details": ("employer_details", "नियोक्ता विवरण", "নিয়োগকর্তার বিবরণ"),
    "Exit App": ("exit_app", "ऐप से बाहर निकलें", "অ্যাপ থেকে প্রস্থান করুন"),
    "Gallery": ("gallery", "गैलरी", "গ্যালারি"),
    "INTRODUCTION": ("introduction", "परिचय", "ভূমিকা"),
    "Job Category": ("job_category", "नौकरी श्रेणी", "কাজের বিভাগ"),
    "Job Description": ("job_description", "नौकरी का विवरण", "কাজের বিবরণ"),
    "Job Location": ("job_location", "नौकरी का स्थान", "কাজের অবস্থান"),
    "Loading...": ("loading", "लोड हो रहा है...", "লোড হচ্ছে..."),
    "My Posted Jobs": ("my_posted_jobs", "मेरी पोस्ट की गई नौकरियां", "আমার পোস্ট করা চাকরি"),
    "My Posted Services": ("my_posted_services", "मेरी पोस्ट की गई सेवाएँ", "আমার পোস্ট করা পরিষেবা"),
    "No": ("no", "नहीं", "না"),
    "Notifications": ("notifications", "सूचनाएं", "বিজ্ঞপ্তি"),
    "OK": ("ok", "ठीक है", "ঠিক আছে"),
    "Post Job": ("post_job", "नौकरी पोस्ट करें", "চাকরি পোস্ট করুন"),
    "Post Service": ("post_service", "सेवा पोस्ट करें", "পরিষেবা পোস্ট করুন"),
    "Profile": ("profile", "प्रोफ़ाइल", "প্রোফাইল"),
    "Recent": ("recent", "हाल का", "সাম্প্রতিক"),
    "Reject": ("reject", "अस्वीकार करें", "প্রত্যাখ্যান করুন"),
    "Reject Application": ("reject_application", "आवेदन अस्वीकार करें", "আবেদন প্রত্যাখ্যান করুন"),
    "Rejected": ("rejected", "अस्वीकृत", "প্রত্যাখ্যাত"),
    "Save": ("save", "सहेजें", "সংরক্ষণ করুন"),
    "Select": ("select", "चुनें", "নির্বাচন করুন"),
    "Select Applicant": ("select_applicant", "आवेदक चुनें", "আবেদনকারী নির্বাচন করুন"),
    "Shortlist": ("shortlist", "शॉर्टलिस्ट", "শর্টলিস্ট"),
    "Shortlist Applicant": ("shortlist_applicant", "आवेदक को शॉर्टलिस्ट करें", "আবেদনকারীকে শর্টলিস্ট করুন"),
    "Similar Jobs": ("similar_jobs", "समान नौकरियां", "অনুরূপ চাকরি"),
    "Skills & Responsibilities": ("skills_responsibilities", "कौशल और जिम्मेदारियां", "দক্ষতা এবং দায়িত্ব"),
    "Upload Photo": ("upload_photo", "फोटो अपलोड करें", "ছবি আপলোড করুন"),
    "Upload Video": ("upload_video", "वीडियो अपलोड करें", "ভিডিও আপলোড করুন"),
    "View": ("view", "देखें", "দেখুন"),
    "View Job Details": ("view_job_details", "नौकरी का विवरण देखें", "চাকরির বিবরণ দেখুন"),
    "WhatsApp": ("whatsapp", "व्हाट्सएप", "হোয়াটসঅ্যাপ"),
    "Yes": ("yes", "हाँ", "হ্যাঁ"),
    "Yes, Exit": ("yes_exit", "हाँ, बाहर निकलें", "হ্যাঁ, প্রস্থান করুন"),
}

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'

# Update KT files
for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            modified = False
            for eng, (id, hi, bn) in translations.items():
                pattern = r'Text\(\s*\"' + re.escape(eng) + r'\"'
                replacement = r'Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.' + id + ')'
                if re.search(pattern, content):
                    content = re.sub(pattern, replacement, content)
                    modified = True
            
            if modified:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Updated {file}")

# Update strings.xml files
def append_strings(file_path, strings_dict, lang_index):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    insertion_idx = content.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for eng, vals in strings_dict.items():
        id = vals[0]
        val = vals[lang_index]
        if f'name="{id}"' not in content:
            new_strings += f'    <string name="{id}">{val}</string>\n'
    
    if new_strings:
        content = content[:insertion_idx] + new_strings + content[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Added to {file_path}")

res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'
append_strings(os.path.join(res_dir, 'values', 'strings.xml'), {k: (v[0], k) for k, v in translations.items()}, 1)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), translations, 1)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), translations, 2)

print("Done")
