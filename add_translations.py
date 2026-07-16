import os
import re
import xml.etree.ElementTree as ET

translations = {
    "exp_required": {"en": "Experience Required", "hi": "अनुभव आवश्यक", "bn": "অভিজ্ঞতা প্রয়োজন"},
    "yes": {"en": "Yes", "hi": "हाँ", "bn": "হ্যাঁ"},
    "available_on": {"en": "Available On", "hi": "पर उपलब्ध", "bn": "উপলব্ধ আছে"},
    "job_category": {"en": "Job Category", "hi": "नौकरी श्रेणी", "bn": "চাকরির বিভাগ"},
    "monthly_salary": {"en": "Monthly Salary (\u20b9)", "hi": "मासिक वेतन (\u20b9)", "bn": "মাসিক বেতন (\u20b9)"},
    "address_label": {"en": "Address:", "hi": "पता:", "bn": "ঠিকানা:"},
    "no_docs_mentioned": {"en": "No specific documents mentioned.", "hi": "कोई विशिष्ट दस्तावेज उल्लिखित नहीं।", "bn": "কোন নির্দিষ্ট নথিপত্র উল্লেখ করা হয়নি।"},
    "live_preview": {"en": "Live Preview", "hi": "लाइव पूर्वावलोकन", "bn": "লাইভ প্রিভিউ"},
    "view_job_details": {"en": "View Job Details", "hi": "नौकरी विवरण देखें", "bn": "চাকরির বিবরণ দেখুন"},
    "documents_optional": {"en": "Documents (Optional)", "hi": "दस्तावेज (वैकल्पिक)", "bn": "নথিপত্র (ঐচ্ছিক)"},
    "listen": {"en": "Listen", "hi": "सुनें", "bn": "শুনুন"},
    "enter_phone": {"en": "Enter phone number", "hi": "फोन नंबर दर्ज करें", "bn": "ফোন নম্বর লিখুন"},
    "describe_past_work": {"en": "Describe your past work responsibilities", "hi": "अपनी पिछली कार्य जिम्मेदारियों का वर्णन करें", "bn": "আপনার অতীত কাজের দায়িত্ব বর্ণনা করুন"},
    "find_jobs_tab": {"en": "Find Jobs", "hi": "नौकरियां खोजें", "bn": "চাকরি খুঁজুন"},
    "notifications_tab": {"en": "Notifications", "hi": "सूचनाएं", "bn": "বিজ্ঞপ্তি"},
    "docs_required": {"en": "Documents Required", "hi": "आवश्यक दस्तावेज", "bn": "প্রয়োজনীয় নথিপত্র"},
    "city_area": {"en": "City / Area *", "hi": "शहर / क्षेत्र *", "bn": "শহর / এলাকা *"},
    "add_other_doc": {"en": "Add other document", "hi": "अन्य दस्तावेज जोड़ें", "bn": "অন্য নথি যোগ করুন"},
    "new_name_label": {"en": "New Name", "hi": "नया नाम", "bn": "নতুন নাম"},
    "change_name_title": {"en": "Change Name", "hi": "नाम बदलें", "bn": "নাম পরিবর্তন করুন"},
    "write_extra_inst": {"en": "Write any extra instructions here...", "hi": "यहां कोई अतिरिक्त निर्देश लिखें...", "bn": "এখানে কোন অতিরিক্ত নির্দেশাবলী লিখুন..."},
    "confirm_app_correct": {"en": "I confirm that the information provided in this application is correct and accurate to the best of my knowledge.", "hi": "मैं पुष्टि करता हूं कि इस आवेदन में दी गई जानकारी मेरे सर्वोत्तम ज्ञान के अनुसार सही और सटीक है।", "bn": "আমি নিশ্চিত করছি যে এই আবেদনে প্রদত্ত তথ্য আমার সর্বোত্তম জ্ঞান অনুসারে সঠিক এবং নির্ভুল।"},
    "state_label": {"en": "State", "hi": "राज्य", "bn": "রাজ্য"},
    "enter_alt_number": {"en": "Enter alternate number", "hi": "वैकल्पिक नंबर दर्ज करें", "bn": "বিকল্প নম্বর লিখুন"},
    "enter_your_name": {"en": "Enter your name", "hi": "अपना नाम दर्ज करें", "bn": "আপনার নাম লিখুন"},
    "post_job_service_card": {"en": "Post Job/Service", "hi": "नौकरी/सेवा पोस्ट करें", "bn": "চাকরি/পরিষেবা পোস্ট করুন"},
    "applied_status": {"en": "Applied", "hi": "लागू किया", "bn": "আবেদন করা হয়েছে"},
    "exit_confirm_msg": {"en": "Are you sure you want to exit LocalConnect?", "hi": "क्या आप वाकई LocalConnect से बाहर निकलना चाहते हैं?", "bn": "আপনি কি নিশ্চিত যে আপনি LocalConnect থেকে প্রস্থান করতে চান?"},
    "loading_text": {"en": "Loading...", "hi": "लोड हो रहा है...", "bn": "লোড হচ্ছে..."},
    "verified_employer": {"en": "Verified Employer", "hi": "सत्यापित नियोक्ता", "bn": "যাচাইকৃত নিয়োগকর্তা"},
    "job_location": {"en": "Job Location", "hi": "नौकरी का स्थान", "bn": "চাকরির স্থান"},
    "eg_15000": {"en": "e.g. 15000", "hi": "उदाहरण 15000", "bn": "উদাহরণ 15000"},
    "confirm_genuine_job": {"en": "I confirm this is a genuine job posting representing an actual vacancy. *", "hi": "मैं पुष्टि करता हूं कि यह एक वास्तविक नौकरी पोस्टिंग है जो एक वास्तविक रिक्ति का प्रतिनिधित्व करती है। *", "bn": "আমি নিশ্চিত করছি যে এটি একটি প্রকৃত চাকরির পোস্টিং যা একটি প্রকৃত শূন্যপদের প্রতিনিধিত্ব করে। *"},
    "no_notifications": {"en": "No notifications yet", "hi": "अभी तक कोई सूचना नहीं", "bn": "এখনও কোন বিজ্ঞপ্তি নেই"},
    "tell_us_experience_best": {"en": "Tell us about your experience to find the best jobs.", "hi": "सर्वोत्तम नौकरियां खोजने के लिए हमें अपने अनुभव के बारे में बताएं।", "bn": "সেরা চাকরি খুঁজতে আমাদের আপনার অভিজ্ঞতা সম্পর্কে বলুন।"},
    "audio_desc": {"en": "AUDIO DESCRIPTION", "hi": "ऑडियो विवरण", "bn": "অডিও বর্ণনা"},
    "choose_photo_upload": {"en": "Choose an option to upload your photo", "hi": "अपनी फोटो अपलोड करने के लिए एक विकल्प चुनें", "bn": "আপনার ছবি আপলোড করার জন্য একটি বিকল্প চয়ন করুন"},
    "upload_cv_cert": {"en": "Upload CV or Certificate", "hi": "सीवी या प्रमाणपत्र अपलोड करें", "bn": "সিভি বা সার্টিফিকেট আপলোড করুন"},
    "phone_number_star": {"en": "Phone Number *", "hi": "फोन नंबर *", "bn": "ফোন নম্বর *"},
    "ok_btn": {"en": "OK", "hi": "ठीक है", "bn": "ঠিক আছে"},
    "delivery_exec": {"en": "Delivery Executive", "hi": "डिलीवरी एक्जीक्यूटिव", "bn": "ডেলিভারি এক্সিকিউটিভ"},
    "english_lang": {"en": "English", "hi": "अंग्रेज़ी", "bn": "ইংরেজি"},
    "save_draft_btn": {"en": "Save Draft", "hi": "ड्राफ्ट सहेजें", "bn": "খসড়া সংরক্ষণ করুন"},
    "use_current_loc": {"en": "Use Current Location", "hi": "वर्तमान स्थान का उपयोग करें", "bn": "বর্তমান অবস্থান ব্যবহার করুন"},
    "bengali_lang": {"en": "Bengali", "hi": "बंगाली", "bn": "বাংলা"},
    "service_label": {"en": "Service:", "hi": "सेवा:", "bn": "পরিষেবা:"},
    "key_resp_skills": {"en": "Key Responsibilities & Skills", "hi": "मुख्य जिम्मेदारियां और कौशल", "bn": "প্রধান দায়িত্ব এবং দক্ষতা"},
    "tell_us_exp_dots": {"en": "Tell us about your experience...", "hi": "हमें अपने अनुभव के बारे में बताएं...", "bn": "আমাদের আপনার অভিজ্ঞতা সম্পর্কে বলুন..."},
    "similar_jobs": {"en": "Similar Jobs", "hi": "समान नौकरियां", "bn": "অনুরূপ চাকরি"},
    "close_btn": {"en": "Close", "hi": "बंद करें", "bn": "বন্ধ করুন"},
    "pdf_image_max": {"en": "PDF or Image max 5MB", "hi": "पीडीएफ या छवि अधिकतम 5MB", "bn": "পিডিএফ বা ছবি সর্বোচ্চ 5MB"},
    "benefits_offered": {"en": "Benefits Offered", "hi": "प्रस्तावित लाभ", "bn": "প্রস্তাবিত সুবিধা"},
    "education_label": {"en": "Education", "hi": "शिक्षा", "bn": "শিক্ষা"},
    "reject_offer": {"en": "Reject Offer", "hi": "प्रस्ताव अस्वीकार करें", "bn": "অফার প্রত্যাখ্যান করুন"},
    "job_title_star": {"en": "Job Title *", "hi": "नौकरी का शीर्षक *", "bn": "চাকরির শিরোনাম *"},
    "verified_caps": {"en": "VERIFIED", "hi": "सत्यापित", "bn": "যাচাইকৃত"},
    "photo_selected": {"en": "Photo Selected", "hi": "फोटो चयनित", "bn": "ছবি নির্বাচিত"},
    "selected_loc": {"en": "Selected Location", "hi": "चयनित स्थान", "bn": "নির্বাচিত অবস্থান"},
    "gallery_opt": {"en": "Gallery", "hi": "गेलरी", "bn": "গ্যালারি"},
    "enter_full_name": {"en": "Enter your full name", "hi": "अपना पूरा नाम दर्ज करें", "bn": "আপনার পুরো নাম লিখুন"},
    "offer_service_nearby": {"en": "Offer your service to nearby customers", "hi": "आस-पास के ग्राहकों को अपनी सेवा प्रदान करें", "bn": "কাছাকাছি গ্রাহকদের আপনার পরিষেবা অফার করুন"},
    "charge_fees_km": {"en": "Do you charge fees per km?", "hi": "क्या आप प्रति किमी शुल्क लेते हैं?", "bn": "আপনি কি প্রতি কিলোমিটারে ফি নেন?"},
    "camera_opt": {"en": "Camera", "hi": "कैमरा", "bn": "ক্যামেরা"},
    "work_address_building": {"en": "Work Address / Building", "hi": "कार्य का पता / भवन", "bn": "কাজের ঠিকানা / ভবন"},
    "choose_video_upload": {"en": "Choose an option to upload your video", "hi": "अपना वीडियो अपलोड करने के लिए एक विकल्प चुनें", "bn": "আপনার ভিডিও আপলোড করার জন্য একটি বিকল্প চয়ন করুন"},
    "eg_2": {"en": "e.g. 2", "hi": "उदाहरण 2", "bn": "উদাহরণ ২"},
    "desc_job_role_env": {"en": "Describe the job role, environment...", "hi": "नौकरी की भूमिका, पर्यावरण का वर्णन करें...", "bn": "কাজের ভূমিকা, পরিবেশ বর্ণনা করুন..."},
    "accept_offer": {"en": "Accept Offer", "hi": "प्रस्ताव स्वीकार करें", "bn": "অফার গ্রহণ করুন"},
    "gender_pref": {"en": "Gender Preference", "hi": "लिंग प्राथमिकता", "bn": "লিঙ্গ পছন্দ"},
    "yes_exit": {"en": "Yes, Exit", "hi": "हाँ, बाहर निकलें", "bn": "হ্যাঁ, প্রস্থান করুন"},
    "prior_exp_years": {"en": "Prior Experience (Years)", "hi": "पूर्व अनुभव (वर्ष)", "bn": "পূর্ব অভিজ্ঞতা (বছর)"},
    "new_job_app": {"en": "New Job Application", "hi": "नया नौकरी आवेदन", "bn": "নতুন চাকরির আবেদন"},
    "employer_details": {"en": "Employer Details", "hi": "नियोक्ता विवरण", "bn": "নিয়োগকর্তার বিবরণ"},
    "remove_app": {"en": "Remove Application", "hi": "आवेदन हटाएं", "bn": "আবেদন সরান"},
    "landmark_opt": {"en": "Landmark (Optional)", "hi": "लैंडमार्क (वैकल्पिक)", "bn": "ল্যান্ডমার্ক (ঐচ্ছিক)"},
    "save_btn": {"en": "Save", "hi": "सहेजें", "bn": "সংরক্ষণ করুন"},
    "date_time_label": {"en": "Date & Time:", "hi": "दिनांक और समय:", "bn": "তারিখ এবং সময়:"},
    "view_btn": {"en": "View", "hi": "देखें", "bn": "দেখুন"},
    "work_label": {"en": "Work:", "hi": "काम:", "bn": "কাজ:"},
    "pincode_star": {"en": "Pincode *", "hi": "पिनकोड *", "bn": "পিনকোড *"},
    "desc_prior_job": {"en": "Description of Prior Job", "hi": "पिछली नौकरी का विवरण", "bn": "আগের চাকরির বিবরণ"},
    "hindi_lang": {"en": "Hindi", "hi": "हिन्दी", "bn": "হিন্দি"},
    "add_btn": {"en": "Add", "hi": "जोड़ें", "bn": "যোগ করুন"},
    "no_benefits_mentioned": {"en": "No specific benefits mentioned.", "hi": "कोई विशिष्ट लाभ उल्लिखित नहीं है।", "bn": "কোন নির্দিষ্ট সুবিধা উল্লেখ করা হয়নি।"},
    "edu_req": {"en": "Education Required", "hi": "शिक्षा आवश्यक", "bn": "শিক্ষা প্রয়োজন"},
    "expected_comp": {"en": "Expected Compensation (\u20b9 / month)", "hi": "अपेक्षित मुआवजा (\u20b9 / माह)", "bn": "প্রত্যাশিত ক্ষতিপূরণ (\u20b9 / মাস)"},
    "employer_company_star": {"en": "Employer / Company Name *", "hi": "नियोक्ता / कंपनी का नाम *", "bn": "নিয়োগকর্তা / কোম্পানির নাম *"},
    "agree_fake_suspend": {"en": "I agree that fake or misleading job posts may result in account suspension. *", "hi": "मैं सहमत हूं कि नकली या भ्रामक नौकरी पोस्ट के परिणामस्वरूप खाता निलंबित हो सकता है। *", "bn": "আমি একমত যে জাল বা বিভ্রান্তিকর চাকরির পোস্টের ফলে অ্যাকাউন্ট স্থগিত হতে পারে। *"},
    "loading_localconnect": {"en": "Loading LocalConnect...", "hi": "LocalConnect लोड हो रहा है...", "bn": "LocalConnect লোড হচ্ছে..."},
    "contact_person_star": {"en": "Contact Person Name *", "hi": "संपर्क व्यक्ति का नाम *", "bn": "যোগাযোগ ব্যক্তির নাম *"},
    "select_loc_map": {"en": "Select Location on Map", "hi": "मानचित्र पर स्थान चुनें", "bn": "মানচিত্রে অবস্থান নির্বাচন করুন"},
    "exit_app": {"en": "Exit App", "hi": "ऐप से बाहर निकलें", "bn": "অ্যাপ থেকে প্রস্থান করুন"},
    "category_star": {"en": "Category *", "hi": "श्रेणी *", "bn": "বিভাগ *"},
    "no_btn": {"en": "No", "hi": "नहीं", "bn": "না"},
    "save_continue": {"en": "Save & Continue", "hi": "सहेजें और जारी रखें", "bn": "সংরক্ষণ করুন এবং চালিয়ে যান"},
    "whatsapp_opt": {"en": "WhatsApp", "hi": "WhatsApp", "bn": "WhatsApp"},
    "add_skill_resp": {"en": "Add skill or responsibility", "hi": "कौशल या जिम्मेदारी जोड़ें", "bn": "দক্ষতা বা দায়িত্ব যোগ করুন"},
    "view_details_btn": {"en": "View Details", "hi": "विवरण देखें", "bn": "বিস্তারিত দেখুন"},
    "skills_resp": {"en": "Skills & Responsibilities", "hi": "कौशल और जिम्मेदारियां", "bn": "দক্ষতা এবং দায়িত্ব"},
    "call_employer": {"en": "Call Employer", "hi": "नियोक्ता को कॉल करें", "bn": "নিয়োগকর্তাকে কল করুন"},
    "driver_personal": {"en": "Driver (Personal)", "hi": "ड्राइवर (व्यक्तिगत)", "bn": "ড্রাইভার (ব্যক্তিগত)"},
    "build_work_profile": {"en": "Build Your Work Profile", "hi": "अपनी कार्य प्रोफ़ाइल बनाएं", "bn": "আপনার কাজের প্রোফাইল তৈরি করুন"},
    "find_services_title": {"en": "Find Services", "hi": "सेवाएं खोजें", "bn": "পরিষেবাগুলি সন্ধান করুন"},
    "video_selected": {"en": "Video Selected", "hi": "वीडियो चयनित", "bn": "ভিডিও নির্বাচিত"},
    "view_on_map": {"en": "View on Map", "hi": "मानचित्र पर देखें", "bn": "মানচিত্রে দেখুন"},
    "enter_house_address": {"en": "Enter house / room / shop address", "hi": "घर / कमरा / दुकान का पता दर्ज करें", "bn": "বাড়ি / রুম / দোকানের ঠিকানা লিখুন"},
    "create_job_hire": {"en": "Create a job post to hire workers", "hi": "श्रमिकों को काम पर रखने के लिए नौकरी पोस्ट बनाएं", "bn": "শ্রমিক নিয়োগের জন্য একটি চাকরির পোস্ট তৈরি করুন"},
    "nearby_landmark": {"en": "Nearby landmark", "hi": "निकटवर्ती लैंडमार्क", "bn": "কাছাকাছি ল্যান্ডমার্ক"},
    "post_a_job_new": {"en": "Post a Job", "hi": "नौकरी पोस्ट करें", "bn": "চাকরি পোস্ট করুন"},
    "post_a_service_new": {"en": "Post a Service", "hi": "सेवा पोस्ट करें", "bn": "পরিষেবা পোস্ট করুন"},
    "what_do_you_want_post": {"en": "What do you want to post?", "hi": "आप क्या पोस्ट करना चाहते हैं?", "bn": "আপনি কি পোস্ট করতে চান?"}
}

base_path = 'app/src/main/res/values/strings.xml'
hi_path = 'app/src/main/res/values-hi/strings.xml'
bn_path = 'app/src/main/res/values-bn/strings.xml'

def append_to_xml(filepath, lang_dict, lang_key):
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_strings = []
    for key, vals in lang_dict.items():
        if f'name="{key}"' not in content:
            val = vals[lang_key].replace('&', '&amp;')
            new_strings.append(f'    <string name="{key}">{val}</string>\n')
            
    if new_strings:
        content = content.replace('</resources>', ''.join(new_strings) + '</resources>')
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Added {len(new_strings)} items to {filepath}")

append_to_xml(base_path, translations, "en")
append_to_xml(hi_path, translations, "hi")
append_to_xml(bn_path, translations, "bn")
