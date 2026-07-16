import os
import glob

directory = 'app/src/main/java/com/example/localconnect/'
files = glob.glob(directory + '*.kt')

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # We want to insert modifier = Modifier.fillMaxHeight(), into ModalBottomSheet calls.
    if 'ModalBottomSheet(' in content:
        # Check if we need to add the import
        if 'import androidx.compose.foundation.layout.fillMaxHeight' not in content:
            content = content.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.foundation.layout.fillMaxHeight')
        
        # We only replace those that don't already have the modifier right before sheetState
        content = content.replace(
            '(\n        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),',
            '(\n        modifier = Modifier.fillMaxHeight(),\n        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),'
        )
        content = content.replace(
            '(\n            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),',
            '(\n            modifier = Modifier.fillMaxHeight(),\n            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),'
        )
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
            print(f"Updated {file_path}")

print("Done")
