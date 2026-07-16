import os

homescreen_path = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(homescreen_path, 'r', encoding='utf-8') as f:
    homescreen_content = f.read()

new_effect = """
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
"""

if 'ActivityResultContracts.RequestPermission()' not in homescreen_content:
    target = 'val context = androidx.compose.ui.platform.LocalContext.current'
    homescreen_content = homescreen_content.replace(target, target + '\n' + new_effect)
    with open(homescreen_path, 'w', encoding='utf-8') as f:
        f.write(homescreen_content)
print("Added runtime permission request for notifications")
