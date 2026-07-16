import os
import glob

directory = 'app/src/main/java/com/example/localconnect/'
files = glob.glob(directory + '*.kt')

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'windowInsets = androidx.compose.foundation.layout.WindowInsets(0),' in content:
        content = content.replace(
            'windowInsets = androidx.compose.foundation.layout.WindowInsets(0),\n            onDismissRequest =',
            'onDismissRequest ='
        )
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
            print(f"Reverted {file_path}")

print("Done")
