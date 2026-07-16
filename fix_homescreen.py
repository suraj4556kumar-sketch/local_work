import re

file_path = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix 1: update HomeContent signature
old_homecontent_sig = 'private fun HomeContent(session: UserSession, onNavigateToJobs: () -> Unit, onNavigateToServices: () -> Unit, onNavigateToPostJob: () -> Unit, onNavigateToPostService: () -> Unit, onProfileClick: () -> Unit)'
new_homecontent_sig = 'private fun HomeContent(session: UserSession, onNavigateToJobs: () -> Unit, onNavigateToServices: () -> Unit, onNavigateToPostJob: () -> Unit, onNavigateToPostService: () -> Unit, onProfileClick: () -> Unit, onNavigateToApplicantList: (String) -> Unit, onNavigateToMyApplications: () -> Unit)'
content = content.replace(old_homecontent_sig, new_homecontent_sig)

# Fix 2: update where HomeContent is called
old_call = '0 -> HomeContent(session, onNavigateToJobs = { navigateTo(1) }, onNavigateToServices = { navigateTo(2) }, onNavigateToPostJob = { navigateTo(7) }, onNavigateToPostService = { navigateTo(6) }) { navigateTo(4) }'
new_call = '0 -> HomeContent(session, onNavigateToJobs = { navigateTo(1) }, onNavigateToServices = { navigateTo(2) }, onNavigateToPostJob = { navigateTo(7) }, onNavigateToPostService = { navigateTo(6) }, onProfileClick = { navigateTo(4) }, onNavigateToApplicantList = { jobId -> selectedJobId = jobId; navigateTo(8) }, onNavigateToMyApplications = { jobSearchInitialTab = 1; navigateTo(1) })'
content = content.replace(old_call, new_call)

# Add jobSearchInitialTab to HomeScreen
old_homescreen_vars = '    var selectedServiceId by remember { mutableStateOf<String?>(null) }\n    var tabStack by remember { mutableStateOf(listOf(0)) }'
new_homescreen_vars = '    var selectedServiceId by remember { mutableStateOf<String?>(null) }\n    var tabStack by remember { mutableStateOf(listOf(0)) }\n    var jobSearchInitialTab by remember { mutableStateOf(0) }'
if 'var jobSearchInitialTab' not in content:
    content = content.replace(old_homescreen_vars, new_homescreen_vars)

# Fix JobSearchScreen call in HomeScreen
old_jobsearch_call = '1 -> JobSearchScreen(session = session)'
new_jobsearch_call = '1 -> JobSearchScreen(session = session, initialTab = jobSearchInitialTab)'
content = content.replace(old_jobsearch_call, new_jobsearch_call)

# Fix 3: NotificationsBottomSheet logic
old_notif = '''                    "new_application", "application_status_employer" -> {
                        if (notif.referenceId.isNotBlank()) {
                            navController.navigate("applicantList/\")
                        }
                    }
                    "application_status_applicant" -> {
                        navController.navigate("myApplications")
                    }'''
new_notif = '''                    "new_application", "application_status_employer" -> {
                        if (notif.referenceId.isNotBlank()) {
                            onNavigateToApplicantList(notif.referenceId)
                        }
                    }
                    "application_status_applicant" -> {
                        onNavigateToMyApplications()
                    }'''
# need to handle escape char carefully in python multiline strings, replacing via regex
content = re.sub(r'navController\.navigate\("applicantList/\$\{notif\.referenceId\}"\)', r'onNavigateToApplicantList(notif.referenceId)', content)
content = re.sub(r'navController\.navigate\("myApplications"\)', r'onNavigateToMyApplications()', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("HomeScreen updated")
