import os
import re

# Dictionary of strings to add
# Format: "key": {"en": "English", "hi": "Hindi", "bn": "Bengali"}
strings_to_add = {
    # Newly found static strings
    "work_address_colon": {"en": "Work address : ", "hi": "काम का पता : ", "bn": "কাজের ঠিকানা : "},
    "city_colon": {"en": "City : ", "hi": "शहर : ", "bn": "শহর : "},
    "state_colon": {"en": "State : ", "hi": "राज्य : ", "bn": "রাজ্য : "},
    "pincode_colon": {"en": "Pincode : ", "hi": "पिनकोड : ", "bn": "পিনকোড : "},
    "landmark_colon": {"en": "Landmark : ", "hi": "मील का पत्थर : ", "bn": "ল্যান্ডমার্ক : "},
    "booking_progress": {"en": "Booking...", "hi": "बुकिंग हो रही है...", "bn": "বুকিং হচ্ছে..."},
    "no_bookings_found": {"en": "No bookings found.", "hi": "कोई बुकिंग नहीं मिली।", "bn": "কোনো বুকিং পাওয়া যায়নি।"},
    "special_instructions": {"en": "Special Instructions", "hi": "विशेष निर्देश", "bn": "বিশেষ নির্দেশাবলী"},
    "attached_media": {"en": "Attached Media", "hi": "संलग्न मीडिया", "bn": "সংযুক্ত মিডিয়া"},
    "view_attached_picture": {"en": "View Attached Picture", "hi": "संलग्न चित्र देखें", "bn": "সংযুক্ত ছবি দেখুন"},
    "save_pin_location": {"en": "Save Pin Location", "hi": "पिन स्थान सहेजें", "bn": "পিন অবস্থান সংরক্ষণ করুন"},

    # The 23 missing strings
    "leave_a_review": {"en": "Leave a Review", "hi": "समीक्षा छोड़ें", "bn": "একটি পর্যালোচনা দিন"},
    "delete_service_title": {"en": "Delete Service", "hi": "सेवा हटाएं", "bn": "পরিষেবা মুছুন"},
    "be_first_review": {"en": "Be the first to review!", "hi": "समीक्षा करने वाले पहले व्यक्ति बनें!", "bn": "প্রথম পর্যালোচনা করুন!"},
    "review_failed": {"en": "Failed to submit review. Please try again.", "hi": "समीक्षा सबमिट करने में विफल। कृपया पुनः प्रयास करें।", "bn": "পর্যালোচনা জমা দিতে ব্যর্থ। আবার চেষ্টা করুন।"},
    "how_was_service": {"en": "How was the service?", "hi": "सेवा कैसी थी?", "bn": "পরিষেবা কেমন ছিল?"},
    "write_comment_hint": {"en": "Write your experience (optional)...", "hi": "अपना अनुभव लिखें (वैकल्पिक)...", "bn": "আপনার অভিজ্ঞতা লিখুন (ঐচ্ছিক)..."},
    "choose_doc_upload": {"en": "Choose an option to upload your document", "hi": "अपना दस्तावेज़ अपलोड करने के लिए एक विकल्प चुनें", "bn": "আপনার দস্তাবেজ আপলোড করার জন্য একটি বিকল্প চয়ন করুন"},
    "delete_service_confirmation": {"en": "Are you sure you want to remove this service?", "hi": "क्या आप वाकई इस सेवा को हटाना चाहते हैं?", "bn": "আপনি কি নিশ্চিত যে আপনি এই পরিষেবাটি সরাতে চান?"},
    "how_was_employer": {"en": "How was your experience with this employer?", "hi": "इस नियोक्ता के साथ आपका अनुभव कैसा रहा?", "bn": "এই নিয়োগকর্তার সাথে আপনার অভিজ্ঞতা কেমন ছিল?"},
    "review_count": {"en": "%d reviews", "hi": "%d समीक्षाएँ", "bn": "%dটি পর্যালোচনা"},
    
    # other missing ratings ones
    "rate_service": {"en": "Rate Service", "hi": "सेवा को रेट करें", "bn": "পরিষেবা রেট করুন"},
    "rate_employer": {"en": "Rate Employer", "hi": "नियोक्ता को रेट करें", "bn": "নিয়োগকর্তাকে রেট দিন"},
    "your_rating": {"en": "Your Rating", "hi": "आपकी रेटिंग", "bn": "আপনার রেটিং"},
    "submit_review": {"en": "Submit Review", "hi": "समीक्षा सबमिट करें", "bn": "পর্যালোচনা জমা দিন"},
    "skip": {"en": "Skip", "hi": "छोड़ें", "bn": "এড়িয়ে যান"},
    "reviewed": {"en": "Reviewed", "hi": "समीक्षा की गई", "bn": "পর্যালোচনা করা হয়েছে"},
    "review_submitted": {"en": "Review submitted successfully!", "hi": "समीक्षा सफलतापूर्वक सबमिट की गई!", "bn": "পর্যালোচনা সফলভাবে জমা দেওয়া হয়েছে!"},
    "tap_star_to_rate": {"en": "Tap a star to rate", "hi": "रेट करने के लिए स्टार पर टैप करें", "bn": "রেট দিতে একটি তারায় ট্যাপ করুন"},
    "reviews": {"en": "Reviews", "hi": "समीक्षाएँ", "bn": "পর্যালোচনাগুলি"},
    "no_reviews_yet": {"en": "No reviews yet", "hi": "अभी तक कोई समीक्षा नहीं", "bn": "এখনও কোনো পর্যালোচনা নেই"},
    "rating_badge": {"en": "%s ★", "hi": "%s ★", "bn": "%s ★"},
    "select_rating_error": {"en": "Please select a star rating.", "hi": "कृपया एक स्टार रेटिंग चुनें।", "bn": "দয়া করে একটি তারকা রেটিং চয়ন করুন।"}
}

def update_strings_xml(lang_code, filepath):
    if not os.path.exists(filepath):
        print(f"Skipping {filepath}, does not exist.")
        return
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find the closing </resources> tag
    end_tag = "</resources>"
    if end_tag not in content:
        print(f"Could not find </resources> in {filepath}")
        return

    # Prepare new strings
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


# 2. Patch Kotlin files
replacements = [
    # JobDetailsScreen.kt
    ('JobDetailsScreen.kt', 'Text("Work address : "', 'Text(stringResource(R.string.work_address_colon)'),
    ('JobDetailsScreen.kt', 'Text("City : "', 'Text(stringResource(R.string.city_colon)'),
    ('JobDetailsScreen.kt', 'Text("State : "', 'Text(stringResource(R.string.state_colon)'),
    ('JobDetailsScreen.kt', 'Text("Pincode : "', 'Text(stringResource(R.string.pincode_colon)'),
    ('JobDetailsScreen.kt', 'Text("Landmark : "', 'Text(stringResource(R.string.landmark_colon)'),
    
    # CustomerDetailsScreen.kt
    ('CustomerDetailsScreen.kt', 'Text("Booking..."', 'Text(stringResource(R.string.booking_progress)'),
    
    # MyServiceBookingsScreen.kt
    ('MyServiceBookingsScreen.kt', 'Text("No bookings found."', 'Text(stringResource(R.string.no_bookings_found)'),
    ('MyServiceBookingsScreen.kt', 'Text("Remove"', 'Text(stringResource(R.string.remove)'),
    ('MyServiceBookingsScreen.kt', 'Text("Cancel"', 'Text(stringResource(R.string.cancel)'),
    ('MyServiceBookingsScreen.kt', 'Text("Close"', 'Text(stringResource(R.string.close_btn)'),
    
    # ServiceRequestListScreen.kt
    ('ServiceRequestListScreen.kt', 'Text("Call"', 'Text(stringResource(R.string.call)'),
    ('ServiceRequestListScreen.kt', 'Text("WhatsApp"', 'Text(stringResource(R.string.whatsapp)'),
    ('ServiceRequestListScreen.kt', 'Text("Customer Details"', 'Text(stringResource(R.string.customer_details)'),
    ('ServiceRequestListScreen.kt', 'Text("Location"', 'Text(stringResource(R.string.location_lbl)'),
    ('ServiceRequestListScreen.kt', 'Text("Date"', 'Text(stringResource(R.string.date_lbl)'),
    ('ServiceRequestListScreen.kt', 'Text("Time"', 'Text(stringResource(R.string.time_lbl)'),
    ('ServiceRequestListScreen.kt', 'Text("Exact Location"', 'Text(stringResource(R.string.exact_location)'),
    ('ServiceRequestListScreen.kt', 'Text("Problem Description"', 'Text(stringResource(R.string.problem_desc)'),
    ('ServiceRequestListScreen.kt', 'Text("Special Instructions"', 'Text(stringResource(R.string.special_instructions)'),
    ('ServiceRequestListScreen.kt', 'Text("Attached Media"', 'Text(stringResource(R.string.attached_media)'),
    ('ServiceRequestListScreen.kt', 'Text("View Attached Picture"', 'Text(stringResource(R.string.view_attached_picture)'),
    
    # MyApplicationsScreen.kt
    ('MyApplicationsScreen.kt', 'Text("Rate Employer"', 'Text(stringResource(R.string.rate_employer)'),
    
    # LocationSelectorMap.kt
    ('LocationSelectorMap.kt', 'Text("Save Pin Location"', 'Text(stringResource(R.string.save_pin_location)')
]

def patch_kt_files():
    base_dir = "app/src/main/java/com/example/localconnect"
    for filename, old_text, new_text in replacements:
        filepath = os.path.join(base_dir, filename)
        if os.path.exists(filepath):
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            if old_text in content:
                content = content.replace(old_text, new_text)
                # also ensure stringResource and R is imported if not present (in Compose projects R is usually implicitly available via package, stringResource might need import but let's assume it's there or just use androidx.compose.ui.res.stringResource if needed)
                # We will use androidx.compose.ui.res.stringResource if stringResource is not imported, but it's usually imported. Let's just do a blanket check.
                if 'import androidx.compose.ui.res.stringResource' not in content:
                    content = content.replace('import androidx.compose.material3.Text', 'import androidx.compose.material3.Text\nimport androidx.compose.ui.res.stringResource')
                
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Patched {filename}: {old_text} -> {new_text}")
            else:
                print(f"Warning: {old_text} not found in {filename}")

patch_kt_files()
