import os

jobs = [
    ("cook", 
     "Looking for an experienced cook to prepare delicious and healthy meals. Must maintain hygiene and manage the kitchen efficiently.",
     "टेस्टी और हेल्दी खाना बनाने के लिए अनुभवी कुक चाहिए। किचन साफ-सुथरा रखना और अच्छे से मैनेज करना आना चाहिए।",
     "সুস্বাদু এবং স্বাস্থ্যকর খাবার তৈরির জন্য একজন অভিজ্ঞ রাঁধুনি চাই। রান্নাঘর পরিষ্কার-পরিচ্ছন্ন রাখা এবং ভালোভাবে পরিচালনা করা আবশ্যক।",
     [
         ("Prepare daily meals as per requirements", "रोजाना जरूरत के हिसाब से खाना बनाना", "প্রতিদিন প্রয়োজন অনুযায়ী খাবার তৈরি করা"),
         ("Maintain kitchen cleanliness and hygiene", "किचन की सफाई और हाइजीन का ध्यान रखना", "রান্নাঘরের পরিচ্ছন্নতা বজায় রাখা"),
         ("Manage groceries and kitchen supplies", "किचन के सामान और राशन का हिसाब रखना", "রান্নাঘরের জিনিসপত্র এবং মুদি বাজার পরিচালনা করা"),
         ("Ensure timely preparation of food", "समय पर खाना तैयार करना", "সময়মতো খাবার তৈরি করা নিশ্চিত করা")
     ]),
    ("maid",
     "Seeking a reliable and hardworking maid for household chores including cleaning, dusting, and washing. Must be punctual and trustworthy.",
     "घर के कामों जैसे सफाई, डस्टिंग और कपड़े धोने के लिए भरोसेमंद और मेहनती मेड चाहिए। समय का पाबंद होना जरूरी है।",
     "ঘর পরিষ্কার করা, মোছা এবং কাপড় ধোয়ার মতো কাজের জন্য একজন নির্ভরযোগ্য এবং পরিশ্রমী কাজের লোক চাই। সময়নিষ্ঠ এবং বিশ্বস্ত হওয়া জরুরি।",
     [
         ("Daily sweeping, mopping, and dusting", "रोजाना झाड़ू, पोंछा और डस्टिंग करना", "প্রতিদিন ঝাড়ু, মোছা এবং ধুলো পরিষ্কার করা"),
         ("Washing dishes and cleaning the kitchen", "बर्तन धोना और किचन साफ करना", "থালাবাসন ধোয়া এবং রান্নাঘর পরিষ্কার করা"),
         ("Washing and folding clothes", "कपड़े धोना और तय करना", "কাপড় ধোয়া এবং গুছিয়ে রাখা"),
         ("General household maintenance", "घर के बाकी छोटे-मोटे काम करना", "ঘরের অন্যান্য সাধারণ কাজ দেখাশোনা করা")
     ]),
    ("driver",
     "Looking for a responsible and experienced driver. Must prioritize safety, follow traffic rules strictly, and maintain the vehicle in good condition.",
     "जिम्मेदार और अनुभवी ड्राइवर चाहिए। सेफ ड्राइविंग करना, ट्रैफिक रूल्स मानना और गाड़ी को अच्छी कंडीशन में रखना जरूरी है।",
     "দায়িত্বশীল এবং অভিজ্ঞ ড্রাইভার চাই। নিরাপদ ড্রাইভিং, ট্রাফিক নিয়ম মানা এবং গাড়ি ভালো অবস্থায় রাখা জরুরি।",
     [
         ("Drive passengers safely to destinations", "यात्रियों को सुरक्षित उनकी जगह पर पहुंचाना", "যাত্রীদের নিরাপদে গন্তব্যে পৌঁছে দেওয়া"),
         ("Perform basic maintenance and daily inspections", "गाड़ी की डेली चेकिंग और बेसिक मेंटेनेंस करना", "গাড়ির নিয়মিত চেকআপ এবং প্রাথমিক রক্ষণাবেক্ষণ করা"),
         ("Maintain a clean vehicle interior and exterior", "गाड़ी को अंदर और बाहर से साफ रखना", "গাড়ির ভেতর ও বাইরে পরিষ্কার রাখা"),
         ("Follow all traffic and safety regulations", "सभी ट्रैफिक और सेफ्टी रूल्स का पालन करना", "সকল ট্রাফিক এবং নিরাপত্তা নিয়ম মেনে চলা")
     ]),
    ("office_boy",
     "Seeking an active and honest office boy to assist with daily administrative tasks, file management, and serving tea/coffee to staff.",
     "डेली ऑफिस के कामों, फाइल मैनेजमेंट और स्टाफ को चाय/कॉफी देने के लिए एक्टिव और ईमानदार ऑफिस बॉय चाहिए।",
     "অফিসের দৈনন্দিন কাজ, ফাইল ব্যবস্থাপনা এবং কর্মীদের চা/কফি পরিবেশনের জন্য একজন সক্রিয় এবং সৎ অফিস বয় চাই।",
     [
         ("Serve tea/water to staff and guests", "स्टाफ और मेहमानों को चाय/पानी देना", "কর্মী এবং অতিথিদের চা/জল পরিবেশন করা"),
         ("File management and basic paperwork", "फाइलें संभालना और बेसिक पेपरवर्क करना", "ফাইল গুছিয়ে রাখা এবং সাধারণ কাগজপত্র দেখাশোনা করা"),
         ("Keep the office premises clean", "ऑफिस की जगह को साफ रखना", "অফিস পরিষ্কার-পরিচ্ছন্ন রাখা"),
         ("Assist in local market runs for office supplies", "ऑफिस के सामान के लिए आस-पास के मार्केट के काम करना", "অফিসের জিনিসপত্রের জন্য স্থানীয় বাজারে যাওয়া")
     ]),
    ("security_guard",
     "Looking for a vigilant and physically fit security guard to protect premises, monitor entrances, and ensure the safety of staff and property.",
     "प्रॉपर्टी की सुरक्षा, मेन गेट की निगरानी और स्टाफ की सेफ्टी के लिए चुस्त और फिट सिक्योरिटी गार्ड चाहिए।",
     "সম্পত্তির সুরক্ষা, প্রবেশদ্বার নজরদারি এবং কর্মীদের নিরাপত্তার জন্য একজন সতর্ক এবং শারীরিকভাবে ফিট নিরাপত্তাকর্মী চাই।",
     [
         ("Monitor and authorize entrance of vehicles/people", "आने-जाने वाले लोगों और गाड़ियों की चेकिंग और एंट्री करना", "মানুষ এবং গাড়ির প্রবেশ পরীক্ষা ও অনুমতি দেওয়া"),
         ("Patrol premises regularly", "प्रॉपर्टी में नियमित तौर पर गश्त लगाना (पेट्रोलिंग)", "নিয়মিত এলাকায় টহল দেওয়া"),
         ("Report any suspicious activities", "किसी भी संदिग्ध गतिविधि की रिपोर्ट करना", "কোনো সন্দেহজনক কার্যকলাপ রিপোর্ট করা"),
         ("Maintain visitor logbook", "विजिटर रजिस्टर/लॉगबुक मेंटेन करना", "ভিজিটর রেজিস্টার বা লগবুক বজায় রাখা")
     ]),
    ("delivery",
     "Seeking an energetic delivery partner for timely and safe delivery of packages. Must have a valid driving license and good area knowledge.",
     "सामान की समय पर और सुरक्षित डिलीवरी के लिए एनर्जेटिक डिलीवरी पार्टनर चाहिए। ड्राइविंग लाइसेंस और लोकल एरिया की अच्छी जानकारी होना जरूरी है।",
     "পার্সেল সময়মতো এবং নিরাপদে ডেলিভারি করার জন্য একজন উদ্যমী ডেলিভারি পার্টনার চাই। বৈধ ড্রাইভিং লাইসেন্স এবং এলাকার ভালো ধারণা থাকতে হবে।",
     [
         ("Deliver packages to customers on time", "कस्टमर्स को समय पर पैकेज डिलीवर करना", "গ্রাহকদের সময়মতো প্যাকেজ পৌঁছে দেওয়া"),
         ("Use navigation apps to find optimal routes", "सही रास्ता खोजने के लिए नेविगेशन ऐप इस्तेमाल करना", "সঠিক পথ খুঁজতে নেভিগেশন অ্যাপ ব্যবহার করা"),
         ("Collect payments if required", "जरूरत पड़ने पर पेमेंट लेना", "প্রয়োজনে পেমেন্ট সংগ্রহ করা"),
         ("Maintain delivery logs and records", "डिलीवरी का सही रिकॉर्ड और लॉग रखना", "ডেলিভারির সঠিক রেকর্ড বজায় রাখা")
     ]),
    ("electrician",
     "Looking for a skilled electrician to install, maintain, and repair electrical wiring and equipment. Must follow safety protocols.",
     "बिजली की वायरिंग और उपकरणों की फिटिंग व मरम्मत के लिए अनुभवी इलेक्ट्रीशियन चाहिए। सेफ्टी नियमों का पालन करना जरूरी है।",
     "বৈদ্যুতিক তার এবং যন্ত্রপাতি স্থাপন, রক্ষণাবেক্ষণ ও মেরামতের জন্য একজন দক্ষ ইলেকট্রিশিয়ান চাই। নিরাপত্তার নিয়ম মানা আবশ্যক।",
     [
         ("Install and repair electrical wiring and fixtures", "इलेक्ट्रिकल वायरिंग और फिक्स्चर लगाना और रिपेयर करना", "বৈদ্যুতিক তার ও সরঞ্জাম লাগানো এবং মেরামত করা"),
         ("Troubleshoot electrical issues", "बिजली की खराबियों का पता लगाना और उन्हें ठीक करना", "বিদ্যুতের সমস্যা শনাক্ত করা এবং সমাধান করা"),
         ("Ensure all work complies with safety codes", "यह पक्का करना कि सारा काम सेफ्टी कोड्स के हिसाब से हो", "সকল কাজ নিরাপত্তার নিয়ম অনুযায়ী হচ্ছে তা নিশ্চিত করা"),
         ("Perform regular maintenance checks", "रेगुलर मेंटेनेंस और चेकिंग करना", "নিয়মিত রক্ষণাবেক্ষণ ও চেকআপ করা")
     ]),
    ("plumber",
     "Looking for an experienced plumber to install and repair pipes, fixtures, and plumbing equipment. Must be able to read blueprints and diagnose issues.",
     "पाइप, फिक्स्चर और प्लंबिंग के उपकरणों की फिटिंग व मरम्मत के लिए अनुभवी प्लंबर चाहिए। काम की अच्छी समझ होनी चाहिए।",
     "পাইপ, ফিক্সচার এবং প্লাম্বিং সরঞ্জাম স্থাপন ও মেরামতের জন্য একজন অভিজ্ঞ প্লাম্বার চাই। কাজের ভালো ধারণা থাকতে হবে।",
     [
         ("Install and repair pipes and fixtures", "पाइप और फिक्स्चर लगाना और उनकी मरम्मत करना", "পাইপ ও ফিক্সচার লাগানো এবং মেরামত করা"),
         ("Clear obstructions from sink drains and toilets", "सिंक, ड्रेन और टॉयलेट की ब्लॉकेज हटाना", "সিঙ্ক, ড্রেন এবং টয়লেটের বাধা পরিষ্কার করা"),
         ("Troubleshoot plumbing issues", "प्लंबिंग की समस्याओं का पता लगाना और ठीक करना", "প্লাম্বিং সমস্যার সমাধান করা"),
         ("Ensure compliance with building codes", "बिल्डिंग रूल्स के हिसाब से काम करना", "ভবনের নিয়ম অনুযায়ী কাজ করা নিশ্চিত করা")
     ]),
    ("carpenter",
     "Seeking a skilled carpenter to construct, install, and repair wooden structures and furniture. Must have good attention to detail.",
     "लकड़ी का काम, फर्नीचर बनाने और रिपेयर करने के लिए कुशल बढ़ई (कारपेंटर) चाहिए। काम में फिनिशिंग होनी चाहिए।",
     "কাঠের কাঠামো এবং আসবাবপত্র তৈরি, স্থাপন এবং মেরামতের জন্য একজন দক্ষ কাঠমিস্ত্রি চাই। কাজে নিখুঁত ফিনিশিং থাকতে হবে।",
     [
         ("Read blueprints and design layouts", "डिजाइन और लेआउट को समझना", "ডিজাইন এবং লেআউট বুঝতে পারা"),
         ("Cut, shape, and smooth wooden materials", "लकड़ी को काटना, सही आकार देना और स्मूद करना", "কাঠ কাটা, আকার দেওয়া এবং মসৃণ করা"),
         ("Install cabinets, doors, and framework", "कैबिनेट, दरवाजे और फ्रेमवर्क लगाना", "ক্যাবিনেট, দরজা এবং ফ্রেমওয়ার্ক লাগানো"),
         ("Repair damaged furniture", "टूटे-फूटे फर्नीचर की मरम्मत करना", "ভাঙা আসবাবপত্র মেরামত করা")
     ]),
    ("painter",
     "Looking for a professional painter for interior and exterior painting projects. Must prepare surfaces, mix colors, and apply paint evenly.",
     "अंदर और बाहर की पेंटिंग के लिए प्रोफेशनल पेंटर चाहिए। दीवारों की पुट्टी करना, रंग मिलाना और अच्छी तरह पेंट करना आना चाहिए।",
     "ভেতরে ও বাইরের পেইন্টিং প্রজেক্টের জন্য একজন প্রফেশনাল পেইন্টার চাই। দেওয়ালের প্রস্তুতি, রং মেশানো এবং সমানভাবে রং লাগাতে জানতে হবে।",
     [
         ("Prepare walls and surfaces for painting", "पेंटिंग से पहले दीवारों और सतह को तैयार करना", "পেইন্টিংয়ের আগে দেওয়াল ও পৃষ্ঠ প্রস্তুত করা"),
         ("Mix and match paint colors", "रंगों को सही से मिक्स करना और मैच करना", "সঠিকভাবে রং মেশানো এবং ম্যাচ করা"),
         ("Apply paint, varnish, and other finishes", "पेंट, वार्निश और बाकी फिनिशिंग अच्छे से करना", "পেইন্ট, বার্নিশ এবং অন্যান্য ফিনিশিং দেওয়া"),
         ("Clean up work areas and equipment", "काम के बाद जगह और टूल्स की सफाई करना", "কাজ শেষে কাজের জায়গা ও সরঞ্জাম পরিষ্কার করা")
     ]),
    ("cleaner",
     "Seeking a dedicated cleaner for residential or commercial properties. Must maintain a high standard of cleanliness.",
     "घर या ऑफिस की सफाई के लिए मेहनती क्लीनर चाहिए। साफ-सफाई का पूरा ध्यान रखना होगा।",
     "বাড়ি বা অফিসের পরিষ্কার-পরিচ্ছন্নতার জন্য একজন নিবেদিত পরিচ্ছন্নতাকর্মী চাই। পরিষ্কার-পরিচ্ছন্নতার উচ্চ মান বজায় রাখতে হবে।",
     [
         ("Sweep, mop, and vacuum floors", "झाड़ू लगाना, पोंछा लगाना और वैक्यूम करना", "ঝাড়ু, মোছা এবং ফ্লোর ভ্যাকুয়াম করা"),
         ("Clean and sanitize restrooms", "वॉशरूम साफ करना और सैनिटाइज करना", "ওয়াশরুম পরিষ্কার ও স্যানিটাইজ করা"),
         ("Empty trash bins and replace liners", "डस्टबिन खाली करना और नए पॉलीथिन लगाना", "ময়লার ঝুড়ি খালি করা এবং নতুন পলিথিন লাগানো"),
         ("Dust furniture and equipment", "फर्नीचर और सामान की डस्टिंग करना", "আসবাবপত্র ও সরঞ্জামের ধুলো পরিষ্কার করা")
     ]),
    ("mechanic",
     "Looking for a skilled mechanic to inspect, maintain, and repair vehicles or machinery. Must have strong diagnostic skills.",
     "गाड़ियों या मशीनों की चेकिंग, मेंटेनेंस और रिपेयरिंग के लिए कुशल मैकेनिक चाहिए। फॉल्ट पकड़ने में माहिर होना चाहिए।",
     "গাড়ি বা যন্ত্রপাতি পরীক্ষা, রক্ষণাবেক্ষণ ও মেরামতের জন্য একজন দক্ষ মেকানিক চাই। ত্রুটি ধরার ভালো দক্ষতা থাকতে হবে।",
     [
         ("Inspect vehicle engines and mechanical parts", "इंजन और मैकेनिकल पार्ट्स की जांच करना", "গাড়ির ইঞ্জিন ও মেকানিক্যাল পার্টস পরীক্ষা করা"),
         ("Diagnose and troubleshoot issues", "खराबी का पता लगाना और उसे ठीक करना", "সমস্যা চিহ্নিত করা ও সমাধান করা"),
         ("Perform routine maintenance like oil changes", "ऑयल चेंज जैसी रेगुलर सर्विसिंग करना", "তেল পরিবর্তনের মতো নিয়মিত রক্ষণাবেক্ষণ করা"),
         ("Repair or replace defective parts", "खराब पार्ट्स को रिपेयर करना या बदलना", "ত্রুটিপূর্ণ যন্ত্রাংশ মেরামত বা পরিবর্তন করা")
     ]),
    ("welder",
     "Seeking an experienced welder to join metal parts together using various welding techniques. Must follow safety standards.",
     "लोहे/मेटल के काम के लिए अनुभवी वेल्डर चाहिए। अलग-अलग वेल्डिंग तकनीक और सेफ्टी की जानकारी होनी चाहिए।",
     "বিভিন্ন ওয়েল্ডিং কৌশল ব্যবহার করে ধাতুর কাজ করার জন্য একজন অভিজ্ঞ ওয়েল্ডার চাই। নিরাপত্তার নিয়ম মানা আবশ্যক।",
     [
         ("Read and interpret blueprints", "डिजाइन और ब्लूप्रिंट्स को समझना", "ডিজাইন ও ব্লুপ্রিন্ট বুঝতে পারা"),
         ("Operate welding equipment safely", "वेल्डिंग मशीन को सुरक्षित तरीके से चलाना", "ওয়েল্ডিং মেশিন নিরাপদে পরিচালনা করা"),
         ("Weld metal components accurately", "मेटल पार्ट्स को सही तरीके से जोड़ना/वेल्ड करना", "ধাতুর যন্ত্রাংশ নিখুঁতভাবে ঝালাই করা"),
         ("Inspect welds for durability and safety", "मजबूती और सेफ्टी के लिए वेल्डिंग चेक करना", "স্থায়িত্ব ও নিরাপত্তার জন্য ওয়েল্ডিং পরীক্ষা করা")
     ]),
    ("mason",
     "Looking for a skilled mason for bricklaying and concrete work. Must have physical strength and precision.",
     "ईंट जोड़ने, प्लास्टर और कंक्रीट के काम के लिए कुशल राजमिस्त्री चाहिए। काम में फिनिशिंग और सफाई होनी चाहिए।",
     "ইট গাঁথা এবং কংক্রিটের কাজের জন্য একজন দক্ষ রাজমিস্ত্রি চাই। কাজে নিখুঁত ফিনিশিং থাকতে হবে।",
     [
         ("Lay bricks, stones, and concrete blocks", "ईंटें, पत्थर और कंक्रीट ब्लॉक लगाना", "ইট, পাথর এবং কংক্রিট ব্লক বসানো"),
         ("Mix cement and mortar", "सीमेंट और मसाला (मोर्टार) बनाना", "সিমেন্ট এবং মশলা তৈরি করা"),
         ("Shape and cut materials as required", "जरूरत के हिसाब से मटीरियल काटना और सेट करना", "প্রয়োজন অনুযায়ী উপকরণ কাটা ও আকৃতি দেওয়া"),
         ("Ensure structures are level and secure", "पक्का करना कि दीवारें और स्ट्रक्चर सीधे और मजबूत हों", "কাঠামো সোজা ও মজবুত কিনা তা নিশ্চিত করা")
     ]),
    ("technician",
     "Seeking a reliable technician to install, troubleshoot, and repair equipment. Must have strong technical and problem-solving skills.",
     "मशीनों/उपकरणों को लगाने, खराबी पकड़ने और रिपेयर करने के लिए टेक्नीशियन चाहिए। अच्छी टेक्निकल नॉलेज होनी चाहिए।",
     "যন্ত্রপাতি স্থাপন, সমস্যা শনাক্তকরণ ও মেরামতের জন্য একজন নির্ভরযোগ্য টেকনিশিয়ান চাই। ভালো কারিগরি জ্ঞান থাকতে হবে।",
     [
         ("Install and configure new equipment", "नई मशीनें या उपकरण लगाना और सेट करना", "নতুন সরঞ্জাম স্থাপন ও কনফিগার করা"),
         ("Perform routine maintenance checks", "नियमित मेंटेनेंस और चेकिंग करना", "নিয়মিত রক্ষণাবেক্ষণ ও চেকআপ করা"),
         ("Troubleshoot hardware or software issues", "हार्डवेयर या सॉफ्टवेयर की समस्या दूर करना", "হার্ডওয়্যার বা সফ্টওয়্যার সমস্যার সমাধান করা"),
         ("Provide technical support to users", "कस्टमर्स/यूजर्स को टेक्निकल सपोर्ट देना", "ব্যবহারকারীদের কারিগরি সহায়তা প্রদান করা")
     ]),
    ("beautician",
     "Looking for a professional beautician to provide various beauty and grooming services to clients.",
     "कस्टमर्स को मेकअप, हेयर और ब्यूटी सर्विसेज देने के लिए प्रोफेशनल ब्यूटीशियन चाहिए।",
     "গ্রাহকদের বিভিন্ন রূপচর্চা ও গ্রুমিং পরিষেবা প্রদানের জন্য একজন প্রফেশনাল বিউটিশিয়ান চাই।",
     [
         ("Provide haircuts, styling, and coloring", "हेयर कट, स्टाइलिंग और बालों में कलर करना", "চুল কাটা, স্টাইলিং এবং কালার করা"),
         ("Perform facials and skincare treatments", "फेशियल और स्किनकेयर ट्रीटमेंट करना", "ফেসিয়াল ও স্কিনকেয়ার ট্রিটমেন্ট করা"),
         ("Offer manicure and pedicure services", "मेनीक्योर और पेडीक्योर सर्विस देना", "ম্যানিকিউর ও পেডিকিউর পরিষেবা দেওয়া"),
         ("Maintain cleanliness of salon equipment", "सैलून और मशीनों की साफ-सफाई रखना", "সেলুন ও সরঞ্জামের পরিচ্ছন্নতা বজায় রাখা")
     ]),
    ("tutor",
     "Seeking a knowledgeable tutor to help students understand subjects and improve academic performance.",
     "बच्चों को पढ़ाने और उनके रिजल्ट्स सुधारने में मदद के लिए अच्छे ट्यूटर की तलाश है।",
     "শিক্ষার্থীদের পড়াশোনায় সাহায্য করতে এবং ফলাফল উন্নত করার জন্য একজন ভালো শিক্ষক চাই।",
     [
         ("Prepare lesson plans and study materials", "पढ़ाने की तैयारी और स्टडी मटीरियल तैयार करना", "পড়ার প্রস্তুতি ও স্টাডি মেটেরিয়াল তৈরি করা"),
         ("Assess student progress and identify areas for improvement", "बच्चों की प्रोग्रेस देखना और कमियों को दूर करना", "শিক্ষার্থীর উন্নতি মূল্যায়ন করা এবং দুর্বল দিকগুলি চিহ্নিত করা"),
         ("Provide one-on-one or group instruction", "बच्चों को अकेले या ग्रुप में पढ़ाना", "একা বা গ্রুপে পড়ানো"),
         ("Encourage and motivate students", "बच्चों को पढ़ाई के लिए मोटीवेट करना", "শিক্ষার্থীদের পড়াশোনার প্রতি উৎসাহিত করা")
     ]),
    ("data_entry",
     "Looking for a detail-oriented data entry operator to input and update records in the database accurately.",
     "कंप्यूटर में डेटा डालने और अपडेट करने के लिए डेटा एंट्री ऑपरेटर चाहिए। टाइपिंग स्पीड अच्छी होनी चाहिए।",
     "কম্পিউটারে তথ্য ইনপুট এবং আপডেট করার জন্য একজন ডেটা এন্ট্রি অপারেটর চাই। টাইপিংয়ে ভালো দক্ষতা থাকতে হবে।",
     [
         ("Enter customer and account data accurately", "कस्टमर और अकाउंट का डेटा सही से भरना", "গ্রাহক ও অ্যাকাউন্টের তথ্য নির্ভুলভাবে ইনপুট করা"),
         ("Verify data for accuracy and completeness", "डेटा की एक्यूरेसी और स्पेलिंग चेक करना", "তথ্যের নির্ভুলতা এবং বানান যাচাই করা"),
         ("Maintain confidentiality of information", "जानकारी और डेटा को सेफ रखना", "তথ্য নিরাপদ ও গোপনীয় রাখা"),
         ("Generate reports as needed", "जरूरत पड़ने पर रिपोर्ट बनाकर देना", "প্রয়োজন অনুযায়ী রিপোর্ট তৈরি করা")
     ]),
    ("accountant",
     "Seeking a qualified accountant to manage financial records, prepare reports, and ensure compliance with regulations.",
     "हिसाब-किताब रखने, रिपोर्ट बनाने और टैक्स रिटर्न के काम के लिए योग्य एकाउंटेंट चाहिए।",
     "হিসাবরক্ষণ, রিপোর্ট তৈরি এবং নিয়মকানুন মেনে চলার জন্য একজন যোগ্য হিসাবরক্ষক চাই।",
     [
         ("Maintain and reconcile financial records", "रोज का हिसाब-किताब रखना और मैच करना", "দৈনন্দিন হিসাবরক্ষণ ও মেলানো"),
         ("Prepare tax returns and ensure compliance", "टैक्स रिटर्न फाइल करना और नियम फॉलो करना", "ট্যাক্স রিটার্ন প্রস্তুত ও নিয়মকানুন মেনে চলা নিশ্চিত করা"),
         ("Analyze financial statements", "फाइनेंशियल स्टेटमेंट्स को एनालाइज करना", "আর্থিক বিবৃতি বিশ্লেষণ করা"),
         ("Manage payroll and invoices", "सैलरी और बिल/इनवॉइस का हिसाब रखना", "বেতন ও ইনভয়েস পরিচালনা করা")
     ]),
    ("sales_exec",
     "Looking for an enthusiastic sales executive to drive business growth by identifying new opportunities and building client relationships.",
     "बिजनेस बढ़ाने, नए कस्टमर्स ढूंढने और अच्छी सेल्स करने के लिए सेल्स एग्जीक्यूटिव चाहिए।",
     "ব্যবসা বাড়াতে, নতুন গ্রাহক খুঁজতে এবং ক্লায়েন্ট সম্পর্ক গড়ার জন্য একজন সেলস এক্সিকিউটিভ চাই।",
     [
         ("Identify and reach out to potential clients", "नए कस्टमर्स ढूंढना और उनसे बात करना", "নতুন গ্রাহকদের চিহ্নিত করা এবং যোগাযোগ করা"),
         ("Present and promote products/services", "प्रोडक्ट या सर्विस के बारे में समझाना और बेचना", "পণ্য বা পরিষেবা উপস্থাপন এবং প্রচার করা"),
         ("Negotiate contracts and close sales", "डील्स नेगोशिएट करना और सेल पूरी करना", "ডিল নিয়ে আলোচনা করা এবং সেলস ক্লোজ করা"),
         ("Maintain customer relationships and follow up", "कस्टमर्स के साथ अच्छे संबंध बनाए रखना और फॉलो-अप लेना", "গ্রাহকদের সাথে ভালো সম্পর্ক বজায় রাখা এবং ফলো-আপ নেওয়া")
     ])
]

# Generate strings.xml
def add_to_xml(lang_idx, path):
    if not os.path.exists(path): return
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    new_xml = ""
    for job in jobs:
        key = job[0]
        desc = job[lang_idx].replace("'", "\\'").replace("&", "&amp;")
        new_xml += f'    <string name="tpl_desc_{key}">{desc}</string>\n'
        for i, resp in enumerate(job[4]):
            resp_str = resp[lang_idx-1].replace("'", "\\'").replace("&", "&amp;")
            new_xml += f'    <string name="tpl_resp_{key}_{i+1}">{resp_str}</string>\n'
            
    if new_xml:
        content = content.replace("</resources>", new_xml + "</resources>")
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {path}")

add_to_xml(1, "app/src/main/res/values/strings.xml")
add_to_xml(2, "app/src/main/res/values-hi/strings.xml")
add_to_xml(3, "app/src/main/res/values-bn/strings.xml")

# Now generate the kotlin code to replace defaultJobTemplates
kt_map = "    val defaultJobTemplates = mapOf(\n"
for job in jobs:
    key = job[0]
    cat_res = f"R.string.cat_{key}" if key != "sales_exec" else "R.string.cat_sales_exec" # careful with names
    # Wait, my names in strings.xml are cat_cook, cat_maid, etc.
    if key == "sales_exec": cat_res = "R.string.cat_sales_exec"
    kt_map += f'        stringResource({cat_res}) to Pair(\n'
    kt_map += f'            stringResource(R.string.tpl_desc_{key}),\n'
    kt_map += f'            listOf('
    resps = []
    for i in range(len(job[4])):
        resps.append(f'stringResource(R.string.tpl_resp_{key}_{i+1})')
    kt_map += ', '.join(resps)
    kt_map += ')\n        ),\n'

# Adding "Other"
kt_map += f'        stringResource(R.string.cat_other) to Pair("", emptyList())\n    )'

# We need to replace the big defaultJobTemplates map in PostJobScreen.kt
kt_path = "app/src/main/java/com/example/localconnect/PostJobScreen.kt"
with open(kt_path, 'r', encoding='utf-8') as f:
    kt_content = f.read()

start_idx = kt_content.find("val defaultJobTemplates = mapOf(")
if start_idx != -1:
    end_idx = kt_content.find("androidx.compose.runtime.LaunchedEffect(jobCategory)", start_idx)
    
    if end_idx != -1:
        # replace the map definition
        new_content = kt_content[:start_idx] + kt_map + "\n\n    " + kt_content[end_idx:]
        with open(kt_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print("Replaced defaultJobTemplates in PostJobScreen.kt")
