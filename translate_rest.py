import os
import re

translations = {
    "APPLICANT DETAILS": ("applicant_details", "आवेदक विवरण", "আবেদনকারীর বিবরণ"),
    "AUDIO DESCRIPTION": ("audio_description", "ऑडियो विवरण", "অডিও বিবরণ"),
    "Add": ("add", "जोड़ें", "যোগ করুন"),
    "Add another benefit...": ("add_another_benefit", "एक और लाभ जोड़ें...", "অন্য একটি সুবিধা যোগ করুন..."),
    "Add another document...": ("add_another_document", "एक और दस्तावेज़ जोड़ें...", "অন্য একটি নথি যোগ করুন..."),
    "Add another language...": ("add_another_language", "एक और भाषा जोड़ें...", "অন্য একটি ভাষা যোগ করুন..."),
    "Add skill or responsibility": ("add_skill_or_responsibility", "कौशल या जिम्मेदारी जोड़ें", "দক্ষতা বা দায়িত্ব যোগ করুন"),
    "Address:": ("address_label", "पता:", "ঠিকানা:"),
    "Are you sure you want to exit LocalConnect?": ("confirm_exit", "क्या आप वाकई LocalConnect से बाहर निकलना चाहते हैं?", "আপনি কি নিশ্চিত যে আপনি LocalConnect থেকে প্রস্থান করতে চান?"),
    "Are you sure you want to remove this job?": ("confirm_remove_job", "क्या आप वाकई इस नौकरी को हटाना चाहते हैं?", "আপনি কি নিশ্চিত যে আপনি এই চাকরিটি সরাতে চান?"),
    "Available On": ("available_on", "पर उपलब्ध", "এ উপলব্ধ"),
    "Benefits Offered": ("benefits_offered", "प्रदान किए गए लाभ", "প্রদত্ত সুবিধাগুলি"),
    "Bengali": ("bengali", "बंगाली", "বাংলা"),
    "Build Your Work Profile": ("build_your_work_profile", "अपना कार्य प्रोफ़ाइल बनाएं", "আপনার কাজের প্রোফাইল তৈরি করুন"),
    "Category *": ("category_star", "श्रेणी *", "বিভাগ *"),
    "Change Name": ("change_name", "नाम बदलें", "নাম পরিবর্তন করুন"),
    "Choose an option to upload your photo": ("choose_photo_upload_option", "अपना फोटो अपलोड करने के लिए एक विकल्प चुनें", "আপনার ছবি আপলোড করার জন্য একটি বিকল্প বেছে নিন"),
    "Choose an option to upload your video": ("choose_video_upload_option", "अपना वीडियो अपलोड करने के लिए एक विकल्प चुनें", "আপনার ভিডিও আপলোড করার জন্য একটি বিকল্প বেছে নিন"),
    "City / Area *": ("city_area_star", "शहर / क्षेत्र *", "শহর / এলাকা *"),
    "Contact Person Name *": ("contact_person_name_star", "संपर्क व्यक्ति का नाम *", "যোগাযোগের ব্যক্তির নাম *"),
    "Create a job post to hire workers": ("create_job_post_to_hire", "श्रमिकों को काम पर रखने के लिए एक नौकरी पोस्ट बनाएं", "শ্রমিক নিয়োগের জন্য একটি চাকরি পোস্ট তৈরি করুন"),
    "Date & Time:": ("date_time_label", "दिनांक और समय:", "তারিখ ও সময়:"),
    "Describe the job role, environment...": ("describe_job_role_environment", "नौकरी की भूमिका, वातावरण का वर्णन करें...", "কাজের ভূমিকা, পরিবেশ বর্ণনা করুন..."),
    "Describe your past work responsibilities": ("describe_past_work_responsibilities", "अपने पिछले कार्य उत्तरदायित्वों का वर्णन करें", "আপনার অতীতের কাজের দায়িত্বগুলি বর্ণনা করুন"),
    "Description of Prior Job": ("description_of_prior_job", "पिछली नौकरी का विवरण", "আগের চাকরির বিবরণ"),
    "Do you charge fees per km?": ("charge_fees_per_km", "क्या आप प्रति किमी शुल्क लेते हैं?", "আপনি কি প্রতি কিমি ফি চার্জ করেন?"),
    "Documents (Optional)": ("documents_optional", "दस्तावेज़ (वैकल्पिक)", "নথিপত্র (ঐচ্ছিক)"),
    "Education": ("education", "शिक्षा", "শিক্ষা"),
    "Education Required": ("education_required", "शिक्षा आवश्यक", "শিক্ষা আবশ্যক"),
    "Employer / Company Name *": ("employer_company_name_star", "नियोक्ता / कंपनी का नाम *", "নিয়োগকর্তা / কোম্পানির নাম *"),
    "English": ("english", "अंग्रेज़ी", "ইংরেজি"),
    "Enter alternate number": ("enter_alternate_number", "वैकल्पिक नंबर दर्ज करें", "বিকল্প নম্বর লিখুন"),
    "Enter house / room / shop address": ("enter_house_room_shop_address", "मकान / कमरा / दुकान का पता दर्ज करें", "বাড়ি / ঘর / দোকানের ঠিকানা লিখুন"),
    "Enter phone number": ("enter_phone_number", "फोन नंबर दर्ज करें", "ফোন নম্বর লিখুন"),
    "Enter your full name": ("enter_full_name", "अपना पूरा नाम दर्ज करें", "আপনার পুরো নাম লিখুন"),
    "Enter your name": ("enter_your_name", "अपना नाम दर्ज करें", "আপনার নাম লিখুন"),
    "Expected Compensation (₹ / month)": ("expected_compensation", "अपेक्षित मुआवजा (₹ / माह)", "প্রত্যাশিত ক্ষতিপূরণ (₹ / মাস)"),
    "Experience": ("experience", "अनुभव", "অভিজ্ঞতা"),
    "Experience Required": ("experience_required", "अनुभव आवश्यक", "অভিজ্ঞতা আবশ্যক"),
    "Gender Preference": ("gender_preference", "लिंग प्राथमिकता", "লিঙ্গ পছন্দ"),
    "Hindi": ("hindi", "हिंदी", "হিন্দি"),
    "I agree that fake or misleading job posts may result in account suspension. *": ("agree_fake_job_suspension", "मैं सहमत हूं कि फर्जी या भ्रामक नौकरी पोस्ट से खाता निलंबित हो सकता है। *", "আমি সম্মত যে জাল বা বিভ্রান্তিকর চাকরি পোস্টের ফলে অ্যাকাউন্ট স্থগিত হতে পারে। *"),
    "I confirm that the information provided in this application is correct and accurate to the best of my knowledge.": ("confirm_info_accurate", "मैं पुष्टि करता हूं कि इस आवेदन में दी गई जानकारी मेरे सर्वोत्तम ज्ञान के अनुसार सही और सटीक है।", "আমি নিশ্চিত করছি যে এই আবেদনে প্রদত্ত তথ্য আমার সর্বোত্তম জ্ঞান অনুসারে সঠিক এবং নির্ভুল।"),
    "I confirm this is a genuine job posting representing an actual vacancy. *": ("confirm_genuine_job_posting", "मैं पुष्टि करता हूं कि यह एक वास्तविक रिक्ति का प्रतिनिधित्व करने वाली एक वास्तविक नौकरी पोस्टिंग है। *", "আমি নিশ্চিত করছি যে এটি একটি প্রকৃত শূন্যপদের প্রতিনিধিত্বকারী একটি প্রকৃত চাকরি পোস্টিং। *"),
    "Job Title *": ("job_title_star", "नौकरी का शीर्षक *", "চাকরির শিরোনাম *"),
    "Key Responsibilities & Skills": ("key_responsibilities_skills", "प्रमुख जिम्मेदारियां और कौशल", "মূল দায়িত্ব এবং দক্ষতা"),
    "Landmark (Optional)": ("landmark_optional", "लैंडमार्क (वैकल्पिक)", "ল্যান্ডমার্ক (ঐচ্ছিক)"),
    "Live Preview": ("live_preview", "लाइव पूर्वावलोकन", "লাইভ প্রিভিউ"),
    "Loading LocalConnect...": ("loading_localconnect", "LocalConnect लोड हो रहा है...", "LocalConnect লোড হচ্ছে..."),
    "Location Details": ("location_details", "स्थान विवरण", "অবস্থানের বিবরণ"),
    "Monthly Salary (₹)": ("monthly_salary_rupees", "मासिक वेतन (₹)", "মাসিক বেতন (₹)"),
    "Nearby landmark": ("nearby_landmark", "आसपास का लैंडमार्क", "কাছাকাছি ল্যান্ডমার্ক"),
    "New Name": ("new_name", "नया नाम", "নতুন নাম"),
    "No applicants found": ("no_applicants_found", "कोई आवेदक नहीं मिला", "কোন আবেদনকারী পাওয়া যায়নি"),
    "No documents uploaded": ("no_documents_uploaded", "कोई दस्तावेज़ अपलोड नहीं किया गया", "কোনো নথি আপলোড করা হয়নি"),
    "No notifications yet": ("no_notifications_yet", "अभी तक कोई सूचना नहीं", "এখনো কোনো বিজ্ঞপ্তি নেই"),
    "No requests found.": ("no_requests_found", "कोई अनुरोध नहीं मिला।", "কোনো অনুরোধ পাওয়া যায়নি।"),
    "No specific benefits mentioned.": ("no_specific_benefits_mentioned", "किसी विशिष्ट लाभ का उल्लेख नहीं है।", "কোনো নির্দিষ্ট সুবিধার কথা উল্লেখ করা হয়নি।"),
    "No specific documents mentioned.": ("no_specific_documents_mentioned", "किसी विशिष्ट दस्तावेज़ का उल्लेख नहीं है।", "কোনো নির্দিষ্ট নথির কথা উল্লেখ করা হয়নি।"),
    "Offer your service to nearby customers": ("offer_service_to_customers", "आसपास के ग्राहकों को अपनी सेवा प्रदान करें", "কাছাকাছি গ্রাহকদের আপনার পরিষেবা অফার করুন"),
    "PDF or Image max 5MB": ("pdf_or_image_max_5mb", "पीडीएफ या छवि अधिकतम 5एमबी", "পিডিএফ বা ছবি সর্বোচ্চ ৫এমবি"),
    "Phone Number *": ("phone_number_star", "फोन नंबर *", "ফোন নম্বর *"),
    "Photo Selected": ("photo_selected", "फोटो चुना गया", "ছবি নির্বাচিত"),
    "Pincode": ("pincode", "पिनकोड", "পিনকোড"),
    "Post New Job": ("post_new_job", "नई नौकरी पोस्ट करें", "নতুন চাকরি পোস্ট করুন"),
    "Post a Service": ("post_a_service", "एक सेवा पोस्ट करें", "একটি পরিষেবা পোস্ট করুন"),
    "Preferred Language": ("preferred_language", "पसंदीदा भाषा", "পছন্দের ভাষা"),
    "Prior Experience (Years)": ("prior_experience_years", "पिछला अनुभव (वर्ष)", "পূর্ব অভিজ্ঞতা (বছর)"),
    "Reject Offer": ("reject_offer", "प्रस्ताव अस्वीकार करें", "প্রস্তাব প্রত্যাখ্যান করুন"),
    "Remove": ("remove", "हटाएं", "সরান"),
    "Remove Application": ("remove_application", "आवेदन हटाएं", "আবেদন সরান"),
    "Remove Job": ("remove_job", "नौकरी हटाएं", "চাকরি সরান"),
    "Salary": ("salary", "वेतन", "বেতন"),
    "Save & Continue": ("save_continue", "सहेजें और जारी रखें", "সংরক্ষণ করুন এবং চালিয়ে যান"),
    "Save Draft": ("save_draft", "ड्राफ्ट सहेजें", "খসড়া সংরক্ষণ করুন"),
    "Search name or skill...": ("search_name_or_skill", "नाम या कौशल खोजें...", "নাম বা দক্ষতা অনুসন্ধান করুন..."),
    "Service Requests": ("service_requests", "सेवा अनुरोध", "পরিষেবা অনুরোধ"),
    "Service:": ("service_label", "सेवा:", "পরিষেবা:"),
    "State": ("state", "राज्य", "রাজ্য"),
    "Tell us about your experience to find the best jobs.": ("tell_us_about_experience_jobs", "सर्वोत्तम नौकरियां खोजने के लिए हमें अपने अनुभव के बारे में बताएं।", "সেরা চাকরি খুঁজতে আমাদের আপনার অভিজ্ঞতা সম্পর্কে বলুন।"),
    "Tell us about your experience...": ("tell_us_about_experience", "हमें अपने अनुभव के बारे में बताएं...", "আমাদের আপনার অভিজ্ঞতা সম্পর্কে বলুন..."),
    "Try adjusting your filters.": ("try_adjusting_filters", "अपने फ़िल्टर समायोजित करने का प्रयास करें।", "আপনার ফিল্টার সামঞ্জস্য করার চেষ্টা করুন।"),
    "Upload CV or Certificate": ("upload_cv_certificate", "सीवी या प्रमाणपत्र अपलोड करें", "সিভি বা সার্টিফিকেট আপলোড করুন"),
    "Use Current Location": ("use_current_location", "वर्तमान स्थान का उपयोग करें", "বর্তমান অবস্থান ব্যবহার করুন"),
    "VERIFIED": ("verified_caps", "सत्यापित", "যাচাইকৃত"),
    "Verified Employer": ("verified_employer", "सत्यापित नियोक्ता", "যাচাইকৃত নিয়োগকর্তा"),
    "Video Selected": ("video_selected", "वीडियो चुना गया", "ভিডিও নির্বাচিত"),
    "View Details": ("view_details", "विवरण देखें", "বিবরণ দেখুন"),
    "Waiting for Applicant to Accept...": ("waiting_for_applicant_accept", "आवेदक के स्वीकार करने की प्रतीक्षा में...", "আবেদনকারীর গ্রহণের অপেক্ষায়..."),
    "Work Address / Building": ("work_address_building", "कार्य पता / भवन", "কাজের ঠিকানা / ভবন"),
    "Work:": ("work_label", "काम:", "কাজ:"),
    "Write any extra instructions here...": ("write_extra_instructions", "यहां कोई भी अतिरिक्त निर्देश लिखें...", "এখানে কোনো অতিরিক্ত নির্দেশ লিখুন..."),
    "You haven't posted any jobs yet.": ("no_posted_jobs_yet", "आपने अभी तक कोई नौकरी पोस्ट नहीं की है।", "আপনি এখনও কোনো চাকরি পোস্ট করেননি।"),
    "You haven't posted any services yet.": ("no_posted_services_yet", "आपने अभी तक कोई सेवा पोस्ट नहीं की है।", "আপনি এখনও কোনো পরিষেবা পোস্ট করেননি।"),
    "e.g. 15000": ("eg_15000", "उदा. 15000", "উদাঃ ১৫০০০"),
    "e.g. 2": ("eg_2", "उदा. 2", "উদাঃ ২")
}

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'
res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

# Update strings.xml files FIRST so Android Studio can index them
def append_strings(file_path, strings_dict, lang_index):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    insertion_idx = content.rfind('</resources>')
    if insertion_idx == -1: return
    
    new_strings = ""
    for eng, vals in strings_dict.items():
        id = vals[0]
        val = vals[lang_index]
        # properly escape & and '
        val = val.replace('&', '&amp;').replace("'", r"\'")
        if f'name="{id}"' not in content:
            new_strings += f'    <string name="{id}">{val}</string>\n'
    
    if new_strings:
        content = content[:insertion_idx] + new_strings + content[insertion_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Added to {file_path}")

append_strings(os.path.join(res_dir, 'values', 'strings.xml'), {k: (v[0], k) for k, v in translations.items()}, 1)
append_strings(os.path.join(res_dir, 'values-hi', 'strings.xml'), translations, 1)
append_strings(os.path.join(res_dir, 'values-bn', 'strings.xml'), translations, 2)


# Update KT files
for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            modified = False
            for eng, (id, hi, bn) in translations.items():
                # regex to match Text("Exactly String")
                pattern = r'Text\(\s*\"' + re.escape(eng) + r'\"'
                replacement = r'Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.' + id + ')'
                if re.search(pattern, content):
                    content = re.sub(pattern, replacement, content)
                    modified = True
            
            if modified:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Updated {file}")

print("Done")
