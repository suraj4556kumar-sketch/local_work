import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/LocationSelectorMap.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update constructor signature
content = content.replace(
    '    contentBetweenMapAndButton: @Composable () -> Unit = {},\n    onLocationSelected: (lat: Double, lon: Double, street: String, city: String, state: String, pin: String, forceOverwrite: Boolean) -> Unit\n) {',
    '    contentBelowMap: @Composable () -> Unit = {},\n    contentBeforeSave: @Composable () -> Unit = {},\n    onLocationSelected: (lat: Double, lon: Double, street: String, city: String, state: String, pin: String, forceOverwrite: Boolean) -> Unit\n) {'
)

# 2. Update layout usage
content = content.replace(
    '        }\n        contentBetweenMapAndButton()\n        Spacer(modifier = Modifier.height(8.dp))',
    '        }\n        contentBelowMap()\n        contentBeforeSave()\n        Spacer(modifier = Modifier.height(8.dp))'
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated LocationSelectorMap.kt")
