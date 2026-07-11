import re

def fix_composable_calls(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    lines = content.split('\n')
    for i in range(len(lines)):
        line = lines[i]
        
        m = re.search(r'(var|val)\s+(\w+)\s+by\s+remember\s*\{\s*mutableStateOf\((stringResource\([^)]+\))\)\s*\}', line)
        if m:
            var_type, var_name, str_call = m.groups()
            new_line = f'    val default_{var_name} = {str_call}\n    {var_type} {var_name} by remember {{ mutableStateOf(default_{var_name}) }}'
            lines[i] = new_line
            
        m2 = re.search(r'val\s+(\w+)\s*=\s*remember\([^)]+\)\s*\{\s*(listOf\([^}]+\))\s*\}', line)
        if m2:
            var_name, list_call = m2.groups()
            new_line = f'    val list_items_{var_name} = {list_call}\n    val {var_name} = remember(list_items_{var_name}) {{ list_items_{var_name} }}'
            lines[i] = new_line
            
        if 'BottomNavBar(' in line and ', strings)' in line:
            lines[i] = line.replace(', strings)', ')')
            
        if 'BottomNavBar(' in line and 'strings,' in line:
            lines[i] = line.replace('strings,', '')
            
        if 'ServiceCard(' in line and ', strings)' in line:
            lines[i] = line.replace(', strings)', ')')

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
        
fix_composable_calls(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\BookServiceScreen.kt')
fix_composable_calls(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\JobSearchScreen.kt')
fix_composable_calls(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\ServiceSearchScreen.kt')
fix_composable_calls(r'c:\Users\dhani\AndroidStudioProjects\LocalWork\app\src\main\java\com\example\localconnect\HomeScreen.kt')
