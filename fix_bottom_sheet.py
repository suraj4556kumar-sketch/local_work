import os

dir_path = 'app/src/main/java/com/example/localconnect'

for root, _, files in os.walk(dir_path):
    for f in files:
        if f.endswith('.kt'):
            path = os.path.join(root, f)
            with open(path, 'r', encoding='utf-8') as file:
                content = file.read()
            
            parts = content.split('ModalBottomSheet(')
            if len(parts) > 1:
                result = parts[0]
                for i in range(1, len(parts)):
                    part = parts[i]
                    prefix = part[:150]
                    if 'sheetState' not in prefix:
                        last_line = parts[i-1].split('\n')[-1]
                        indent = len(last_line) - len(last_line.lstrip(' \t'))
                        spaces = ' ' * indent
                        if not part.startswith('\n'):
                            # sometimes it's ModalBottomSheet(onDismissRequest...
                            result += 'ModalBottomSheet(\n' + spaces + '    sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),\n' + spaces + '    ' + part
                        else:
                            result += 'ModalBottomSheet(\n' + spaces + '    sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),' + part
                    else:
                        result += 'ModalBottomSheet(' + part
                
                if result != content:
                    with open(path, 'w', encoding='utf-8') as file:
                        file.write(result)
                    print(f"Updated {f}")
