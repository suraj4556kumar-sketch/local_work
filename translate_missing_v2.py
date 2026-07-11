import os
import time
import xml.etree.ElementTree as ET
try:
    from googletrans import Translator
except ImportError:
    os.system('pip install googletrans==4.0.0-rc1')
    from googletrans import Translator

res_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\res'

def translate_xml(lang_code, folder):
    path = os.path.join(res_dir, folder, 'strings.xml')
    tree = ET.parse(path)
    root = tree.getroot()
    
    en_tree = ET.parse(os.path.join(res_dir, 'values', 'strings.xml'))
    en_root = en_tree.getroot()
    en_dict = {elem.attrib['name']: elem.text for elem in en_root}
    
    translator = Translator()
    
    changed = False
    for elem in root:
        name = elem.attrib['name']
        if name in en_dict and elem.text == en_dict[name]:
            # It's English!
            try:
                print(f"Translating {name} to {lang_code}...")
                translated = translator.translate(elem.text, dest=lang_code).text
                elem.text = translated
                changed = True
                time.sleep(0.5)
            except Exception as e:
                print(f"Error translating {name}: {e}")
                
    if changed:
        tree.write(path, encoding='utf-8', xml_declaration=True)
        print(f"Updated {path}")

translate_xml('hi', 'values-hi')
translate_xml('bn', 'values-bn')
