import os
import glob
import re

directory = 'app/src/main/java/com/example/localconnect/'
files = glob.glob(directory + '*.kt')

# Regex to find ModalBottomSheet and modify the first Column inside it
for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if 'ModalBottomSheet(' in content:
        # We will add LocalConfiguration.current to the file imports if not present
        if 'import androidx.compose.ui.platform.LocalConfiguration' not in content:
            content = content.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.platform.LocalConfiguration\nimport androidx.compose.ui.unit.dp')

        # Replace ModalBottomSheet( ... ) { Column( modifier = Modifier.fillMaxWidth() ) with fixed height
        # This is a bit tricky with regex, we can just replace the standard pattern we see.
        # Actually, let's just replace ModalBottomSheet( with a block that captures screenHeight
        # and then inject .height(screenHeight * 0.9f) into the child column.
        
        # A simpler way: just inject al screenHeight = LocalConfiguration.current.screenHeightDp.dp
        # right before ModalBottomSheet
        # and then replace .fillMaxWidth() with .fillMaxWidth().height(screenHeight * 0.9f) ONLY inside ModalBottomSheet.
        
        # Let's use a simpler approach: 
        pass
        
