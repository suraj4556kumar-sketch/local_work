import os
import glob

directory = 'app/src/main/java/com/example/localconnect/'
files = glob.glob(directory + '*.kt')

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'ModalBottomSheet(' in content and 'modifier = Modifier.fillMaxHeight()' not in content:
        # Check if the file already imports Modifier and fillMaxHeight
        if 'import androidx.compose.foundation.layout.fillMaxHeight' not in content:
            content = content.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.foundation.layout.fillMaxHeight')
        
        # Inject the modifier before sheetState
        content = content.replace(
            'sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),',
            'modifier = Modifier.fillMaxHeight(),\n            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),'
        )
        
        # Also handle cases where the prefix is not explicit
        content = content.replace(
            'sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),',
            'modifier = Modifier.fillMaxHeight(),\n            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),'
        )

        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
            print(f"Updated {file_path}")

print("Done")
