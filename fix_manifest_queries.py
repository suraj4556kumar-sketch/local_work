import os

file_path = 'app/src/main/AndroidManifest.xml'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

queries_block = """
    <queries>
        <package android:name="com.google.android.apps.maps" />
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="geo" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="https" />
        </intent>
    </queries>
"""

if '<queries>' not in content:
    content = content.replace('</application>', '</application>\n' + queries_block)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Added queries to AndroidManifest")
else:
    print("Queries already exist")
