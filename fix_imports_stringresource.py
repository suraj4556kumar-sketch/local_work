import os
import re

kt_dir = r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect'

for root, dirs, files in os.walk(kt_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            if 'stringResource(' in content and 'import androidx.compose.ui.res.stringResource' not in content:
                # Add import after the package declaration
                content = re.sub(r'package com\.example\.localconnect\n', 'package com.example.localconnect\n\nimport androidx.compose.ui.res.stringResource\nimport com.example.localconnect.R\n', content)
            elif 'R.string.' in content and 'import com.example.localconnect.R' not in content:
                content = re.sub(r'package com\.example\.localconnect\n', 'package com.example.localconnect\n\nimport com.example.localconnect.R\n', content)
                
            if content != original_content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Added imports to {file}")
