import re

translations = {
    'Continue': ('जारी रखें', 'চালিয়ে যান'),
    'Enter your mobile number': ('अपना मोबाइल नंबर दर्ज करें', 'আপনার মোবাইল নম্বর লিখুন'),
    "We'll send you a verification code": ('हम आपको एक सत्यापन कोड भेजेंगे', 'আমরা আপনাকে একটি যাচাইকরণ কোড পাঠাব'),
    'Get OTP': ('OTP प्राप्त करें', 'OTP পান'),
    'Enter 10-digit number': ('10-अंकीय नंबर दर्ज करें', '১০-সংখ্যার নম্বর লিখুন'),
    'Verify your number': ('अपना नंबर सत्यापित करें', 'আপনার নম্বর যাচাই করুন'),
    'OTP sent to': ('OTP भेजा गया', 'OTP পাঠানো হয়েছে'),
    'Verify & Continue': ('सत्यापित करें और जारी रखें', 'যাচাই করুন এবং চালিয়ে যান'),
    'Resend OTP': ('OTP पुनः भेजें', 'OTP পুনরায় পাঠান'),
    'Resend in': ('में पुनः भेजें', 'এর মধ্যে পুনরায় পাঠান'),
    'Incorrect OTP. Please try again.': ('गलत OTP। कृपया पुनः प्रयास करें।', 'ভুল OTP। আবার চেষ্টা করুন।'),
    'Set up your profile': ('अपनी प्रोफ़ाइल सेट करें', 'আপনার প্রোফাইল সেট আপ করুন'),
    'Tell us about yourself': ('अपने बारे में बताएं', 'আপনার সম্পর্কে আমাদের বলুন'),
    'Your full name': ('आपका पूरा नाम', 'আপনার পুরো নাম'),
    'Your age': ('आपकी आयु', 'আপনার বয়স'),
    'Select gender': ('लिंग चुनें', 'লিঙ্গ নির্বাচন করুন'),
    'Male': ('पुरुष', 'পুরুষ'),
    'Female': ('महिला', 'মহিলা'),
    'Other': ('अन्य', 'অন্যান্য'),
    'Your location': ('आपका स्थान', 'আপনার অবস্থান'),
    'Where are you based?': ('आप कहाँ आधारित हैं?', 'আপনি কোথায় ভিত্তিক?'),
    'Enter city or area': ('शहर या क्षेत्र दर्ज करें', 'শহর বা এলাকা লিখুন'),
    '📍 Use current location': ('📍 वर्तमान स्थान का उपयोग करें', '📍 বর্তমান অবস্থান ব্যবহার করুন'),
    'Popular cities': ('लोकप्रिय शहर', 'জনপ্রিয় শহরগুলি'),
    'Hello': ('नमस्ते', 'হ্যালো'),
    'What would you like to do?': ('आप क्या करना चाहेंगे?', 'আপনি কি করতে চান?'),
    'Job Recruiter & Service Provider': ('नौकरी भर्तीकर्ता और सेवा प्रदाता', 'চাকরি নিয়োগকারী এবং পরিষেবা প্রদানকারী'),
    'Post jobs, find workers & clients': ('नौकरियां पोस्ट करें, श्रमिक और ग्राहक खोजें', 'চাকরি পোস্ট করুন, কর্মী এবং ক্লায়েন্ট খুঁজুন'),
    'Job Seeker & Service Seeker': ('नौकरी चाहने वाले और सेवा चाहने वाले', 'চাকরি প্রার্থী এবং পরিষেবা প্রার্থী'),
    'Find jobs & services near you': ('अपने आस-पास नौकरियां और सेवाएं खोजें', 'আপনার কাছাকাছি চাকরি এবং পরিষেবা খুঁজুন'),
    'Choose your role': ('अपनी भूमिका चुनें', 'আপনার ভূমিকা চয়ন করুন'),
    'I want to Hire': ('मैं काम पर रखना चाहता हूँ', 'আমি নিয়োগ করতে চাই'),
    'Post jobs or offer services': ('नौकरियां पोस्ट करें या सेवाएं दें', 'চাকরি পোস্ট করুন বা পরিষেবা দিন'),
    'I want Work': ('मुझे काम चाहिए', 'আমি কাজ চাই'),
    'Welcome, Employer!': ('स्वागत है, नियोक्ता!', 'স্বাগতম, নিয়োগকর্তা!'),
    'Post a Job': ('एक नौकरी पोस्ट करें', 'একটি চাকরি পোস্ট করুন'),
    'Reach thousands of candidates': ('हजारों उम्मीदवारों तक पहुंचें', 'হাজার হাজার প্রার্থীর কাছে পৌঁছান'),
    'Find Workers': ('श्रमिक खोजें', 'শ্রমিক খুঁজুন'),
    'Browse skilled people near you': ('अपने आस-पास कुशल लोगों को ब्राउज़ करें', 'আপনার কাছাকাছি দক্ষ ব্যক্তিদের ব্রাউজ করুন'),
    'My Postings': ('मेरी पोस्टिंग', 'আমার পোস্টিং'),
    'Welcome!': ('स्वागत है!', 'স্বাগতম!'),
    'Browse Jobs': ('नौकरियां ब्राउज़ करें', 'চাকরি ব্রাউজ করুন'),
    'Find jobs near your location': ('अपने स्थान के पास नौकरियां खोजें', 'আপনার অবস্থানের কাছাকাছি চাকরি খুঁজুন'),
    'Browse Services': ('सेवाएं ब्राउज़ करें', 'পরিষেবাগুলি ব্রাউজ করুন'),
    'Find services near you': ('अपने आस-पास सेवाएं खोजें', 'আপনার কাছাকাছি পরিষেবা খুঁজুন'),
    'My Applications': ('मेरे आवेदन', 'আমার আবেদনগুলি')
}

file_path = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\AppStrings.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

en_block_match = re.search(r'else\s*->\s*AppStrings\(\s*// "en" default\s*(.*?)\)', ''.join(lines), re.DOTALL)
en_block = en_block_match.group(1)
fields = re.findall(r'(\w+)\s*=\s*"([^"]*)",?', en_block)

hi_lines = []
bn_lines = []

for key, val in fields:
    if val.strip() == "":
        hi_lines.append(f'        {key.ljust(22)} = "",')
        bn_lines.append(f'        {key.ljust(22)} = "",')
        continue
        
    hi_val = translations.get(val, (val, val))[0]
    bn_val = translations.get(val, (val, val))[1]
        
    hi_lines.append(f'        {key.ljust(22)} = "{hi_val}",')
    bn_lines.append(f'        {key.ljust(22)} = "{bn_val}",')

hi_block = '    "hi" -> AppStrings(\n' + '\n'.join(hi_lines) + '\n    )\n'
bn_block = '    "bn" -> AppStrings(\n' + '\n'.join(bn_lines) + '\n    )\n'

new_lines = lines[:195] + [hi_block, bn_block] + lines[537:]

with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("Successfully replaced lines 196 to 537 in AppStrings.kt")
