import os
import glob
import re

directory = 'app/src/main/java/com/example/localconnect/'
files = glob.glob(directory + '*.kt')

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # We need to insert windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
    # inside ModalBottomSheet(...)
    
    # regex to find ModalBottomSheet( ... ) where we can safely insert it before containerColor or onDismissRequest
    if 'ModalBottomSheet(' in content and 'windowInsets' not in content:
        # A simple string replacement that matches common patterns in our code:
        content = content.replace(
            'onDismissRequest =',
            'windowInsets = androidx.compose.foundation.layout.WindowInsets(0),\n            onDismissRequest ='
        )
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
            print(f"Updated {file_path}")

print("Done")
