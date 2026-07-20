import os
import re

main_path = 'app/src/main/java/com/example/localconnect/MainActivity.kt'
with open(main_path, 'r', encoding='utf-8') as f: content = f.read()

# Update Screen.Home definition
content = content.replace('data class Home(val session: UserSession) : Screen()', 'data class Home(val session: UserSession, val intentType: String? = null, val intentReferenceId: String? = null) : Screen()')

# Update the launch of Screen.Home in AppNavigation for the completeSession
old_home_launch = '''                                    currentScreen = Screen.Home(completeSession)'''
new_home_launch = '''                                    val intentType = activity.intent.getStringExtra("type")
                                    val intentRefId = activity.intent.getStringExtra("referenceId")
                                    currentScreen = Screen.Home(completeSession, intentType, intentRefId)'''
content = content.replace(old_home_launch, new_home_launch)

# And when passing to HomeScreen:
old_home_call = 'is Screen.Home -> HomeScreen('
new_home_call = 'is Screen.Home -> HomeScreen(initialActionType = screen.intentType, initialReferenceId = screen.intentReferenceId, '
content = content.replace(old_home_call, new_home_call)

with open(main_path, 'w', encoding='utf-8') as f: f.write(content)

home_path = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(home_path, 'r', encoding='utf-8') as f: home_content = f.read()

old_home_sig = 'fun HomeScreen(\n    initialSession: UserSession,\n    onLogout: () -> Unit = {},\n    onSessionUpdate: (UserSession) -> Unit = {}\n) {'
new_home_sig = 'fun HomeScreen(\n    initialSession: UserSession,\n    initialActionType: String? = null,\n    initialReferenceId: String? = null,\n    onLogout: () -> Unit = {},\n    onSessionUpdate: (UserSession) -> Unit = {}\n) {'
home_content = home_content.replace(old_home_sig, new_home_sig)

deep_link_effect = '''    LaunchedEffect(initialActionType, initialReferenceId) {
        if (!initialActionType.isNullOrBlank()) {
            when (initialActionType) {
                "job_application", "application_status_employer" -> {
                    if (!initialReferenceId.isNullOrBlank()) {
                        selectedJobId = initialReferenceId
                        navigateTo(8)
                    }
                }
                "application_status", "status_update", "application_status_applicant", "job_application_update" -> {
                    jobSearchInitialTab = 1
                    navigateTo(1)
                }
                "service_request" -> {
                    if (!initialReferenceId.isNullOrBlank()) {
                        selectedServiceId = initialReferenceId
                        navigateTo(10)
                    }
                }
            }
        }
    }
    
    val sharedPreferences = context.getSharedPreferences("NotificationsPrefs", android.content.Context.MODE_PRIVATE)
    LaunchedEffect(notifications) {
        val unreadNotifications = notifications.filter { !it.isRead }
        for (notif in unreadNotifications) {
            val key = "notified_${notif.id}"
            if (!sharedPreferences.getBoolean(key, false)) {
                com.example.localconnect.utils.NotificationHelper.showNotification(context, notif.title, notif.message, notif.referenceId, notif.type)
                sharedPreferences.edit().putBoolean(key, true).apply()
            }
        }
    }
'''

# Insert right after `var showExitDialog by remember { mutableStateOf(false) }`
old_dialog = '    var showExitDialog by remember { mutableStateOf(false) }'
new_dialog = old_dialog + '\n\n' + deep_link_effect
home_content = home_content.replace(old_dialog, new_dialog)

with open(home_path, 'w', encoding='utf-8') as f: f.write(home_content)

print("Updates completed successfully")
