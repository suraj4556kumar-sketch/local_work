import os
filepath = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(filepath, 'r', encoding='utf-8') as f: content = f.read()

target1 = '    var jobSearchInitialTab by remember { mutableIntStateOf(0) }'
repl1 = '''    var jobSearchInitialTab by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val userIds = listOfNotNull(session.firebaseUid.takeIf { it.isNotBlank() }, session.phone.takeIf { it.isNotBlank() }).distinct()
    val notifications by com.example.localconnect.data.FirebaseRepository.getNotificationsFlow(userIds).collectAsState(initial = emptyList())'''
content = content.replace(target1, repl1)

target2 = '0 -> HomeContent(session, onNavigateToJobs = { navigateTo(1) }, onNavigateToServices = { navigateTo(2) }, onNavigateToPostJob = { navigateTo(7) }, onNavigateToPostService = { navigateTo(6) }, onProfileClick = { navigateTo(4) }, onNavigateToApplicantList = { jobId -> selectedJobId = jobId; navigateTo(8) }, onNavigateToMyApplications = { jobSearchInitialTab = 1; navigateTo(1) })'
repl2 = '0 -> HomeContent(session, onNavigateToJobs = { navigateTo(1) }, onNavigateToServices = { navigateTo(2) }, onNavigateToPostJob = { navigateTo(7) }, onNavigateToPostService = { navigateTo(6) }, onProfileClick = { navigateTo(4) }, onNavigateToApplicantList = { jobId -> selectedJobId = jobId; navigateTo(8) }, onNavigateToMyApplications = { jobSearchInitialTab = 1; navigateTo(1) }, unreadCount = notifications.count { !it.isRead }, onNavigateToNotifications = { navigateTo(11) }, onMarkNotificationsRead = { coroutineScope.launch { com.example.localconnect.data.FirebaseRepository.markNotificationsAsRead(userIds) } })'
content = content.replace(target2, repl2)

# Update HomeContent signature and internals
import re
# Remove the old state initialization inside HomeContent
sig_target = 'fun HomeContent(session: UserSession, onNavigateToJobs: () -> Unit, onNavigateToServices: () -> Unit, onNavigateToPostJob: () -> Unit, onNavigateToPostService: () -> Unit, onProfileClick: () -> Unit, onNavigateToApplicantList: (String) -> Unit, onNavigateToMyApplications: () -> Unit)'
sig_repl = 'fun HomeContent(session: UserSession, onNavigateToJobs: () -> Unit, onNavigateToServices: () -> Unit, onNavigateToPostJob: () -> Unit, onNavigateToPostService: () -> Unit, onProfileClick: () -> Unit, onNavigateToApplicantList: (String) -> Unit, onNavigateToMyApplications: () -> Unit, unreadCount: Int, onNavigateToNotifications: () -> Unit, onMarkNotificationsRead: () -> Unit)'
content = content.replace(sig_target, sig_repl)

# Remove local notifications state in HomeContent
content = re.sub(r'\s*val userIds = listOfNotNull\([^)]*\)\.distinct\(\)\s*val notifications by FirebaseRepository\.getNotificationsFlow\(userIds\)\.collectAsState\(initial = emptyList\(\)\)\s*val unreadCount = notifications\.count \{ !it\.isRead \}', '', content)

# Remove the LaunchedEffect for notifications inside HomeContent (since it will just use unreadCount, but wait, the LaunchedEffect needs `notifications`. Actually, let's keep the LaunchedEffect, but we don't have `notifications` inside HomeContent anymore. Let's just remove the local system notification logic for now, or just pass `notifications` to HomeContent too. 
# Let's pass `notifications` to HomeContent.
sig_repl_with_notifs = 'fun HomeContent(session: UserSession, onNavigateToJobs: () -> Unit, onNavigateToServices: () -> Unit, onNavigateToPostJob: () -> Unit, onNavigateToPostService: () -> Unit, onProfileClick: () -> Unit, onNavigateToApplicantList: (String) -> Unit, onNavigateToMyApplications: () -> Unit, unreadCount: Int, notifications: List<com.example.localconnect.data.Notification>, onNavigateToNotifications: () -> Unit, onMarkNotificationsRead: () -> Unit)'
content = content.replace(sig_repl, sig_repl_with_notifs)
repl2_with_notifs = '0 -> HomeContent(session, onNavigateToJobs = { navigateTo(1) }, onNavigateToServices = { navigateTo(2) }, onNavigateToPostJob = { navigateTo(7) }, onNavigateToPostService = { navigateTo(6) }, onProfileClick = { navigateTo(4) }, onNavigateToApplicantList = { jobId -> selectedJobId = jobId; navigateTo(8) }, onNavigateToMyApplications = { jobSearchInitialTab = 1; navigateTo(1) }, unreadCount = notifications.count { !it.isRead }, notifications = notifications, onNavigateToNotifications = { navigateTo(11) }, onMarkNotificationsRead = { coroutineScope.launch { com.example.localconnect.data.FirebaseRepository.markNotificationsAsRead(userIds) } })'
content = content.replace(repl2, repl2_with_notifs)

# Now remove the coroutineScope definition in HomeContent as it's not needed (or we just change the bell icon onClick)
# Replace bell icon onClick in HomeContent:
content = content.replace('''                                showNotifications = true
                                if (unreadCount > 0) {
                                    val userIdsList = listOfNotNull(
                                        session.firebaseUid.takeIf { it.isNotBlank() },
                                        session.phone.takeIf { it.isNotBlank() }
                                    ).distinct()
                                    coroutineScope.launch { FirebaseRepository.markNotificationsAsRead(userIdsList) }
                                }''', '''                                onNavigateToNotifications()
                                if (unreadCount > 0) {
                                    onMarkNotificationsRead()
                                }''')

with open(filepath, 'w', encoding='utf-8') as f: f.write(content)
