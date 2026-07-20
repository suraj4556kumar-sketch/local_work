import os
filepath = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(filepath, 'r', encoding='utf-8') as f: content = f.read()
content = content.replace('var showNotifications by remember { mutableStateOf(false) }', '')
content = content.replace('showNotifications = true', 'navigateTo(11)')
target = '10 -> selectedServiceId?.let { ServiceRequestListScreen(session = session, serviceId = it, onBack = { popBack() }) }'
repl = target + '\n' + \
'                11 -> NotificationsScreen(notifications, onBack = { popBack() }, onNotificationClick = { notif ->\n' + \
'                    when (notif.type) {\n' + \
'                        "job_application", "application_status_employer" -> { if (notif.referenceId.isNotBlank()) { selectedJobId = notif.referenceId; navigateTo(8) } }\n' + \
'                        "application_status", "status_update", "job_application_update", "application_status_applicant" -> { jobSearchInitialTab = 1; navigateTo(1) }\n' + \
'                        "service_request" -> { if (notif.referenceId.isNotBlank()) { selectedServiceId = notif.referenceId; navigateTo(10) } }\n' + \
'                        "new_offer", "offer_response", "service_status_update" -> { }\n' + \
'                    }\n' + \
'                }, onDeleteNotification = { notif -> coroutineScope.launch { com.example.localconnect.data.FirebaseRepository.deleteNotification(notif.id) } })\n'
content = content.replace(target, repl)

idx1 = content.find('if (showNotifications) {')
idx2 = content.find('@Composable\nprivate fun ActionCard')
if idx1 != -1 and idx2 != -1: content = content[:idx1] + content[idx2:]

idx3 = content.find('@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun NotificationsBottomSheet')
if idx3 != -1: content = content[:idx3]

with open(filepath, 'w', encoding='utf-8') as f: f.write(content)
