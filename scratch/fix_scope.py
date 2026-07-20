import os
home_path = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(home_path, 'r', encoding='utf-8') as f: content = f.read()

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

content = content.replace(deep_link_effect, '')

target = '        if (tabStack.size > 1) {\n            tabStack = tabStack.dropLast(1)\n        } else {\n            showExitDialog = true\n        }\n    }'
repl = target + '\n\n' + deep_link_effect

content = content.replace(target, repl)
with open(home_path, 'w', encoding='utf-8') as f: f.write(content)
print('Fixed scope')
