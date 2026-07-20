package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.example.localconnect.ui.TranslatedText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Job
import com.example.localconnect.data.ServiceTask
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.Notification
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.activity.compose.BackHandler
import android.app.Activity
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.localconnect.ui.theme.*
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun HomeScreen(
    initialSession: UserSession,
    initialActionType: String? = null,
    initialReferenceId: String? = null,
    onLogout: () -> Unit = {},
    onSessionUpdate: (UserSession) -> Unit = {}
) {
    var session by remember { mutableStateOf(initialSession) }
    var tabStack by remember { mutableStateOf(listOf(0)) }
    val selectedTab = tabStack.last()
    var selectedJobId by remember { mutableStateOf<String?>(null) }
    var selectedServiceId by remember { mutableStateOf<String?>(null) }
    var jobSearchInitialTab by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val userIds = listOfNotNull(session.firebaseUid.takeIf { it.isNotBlank() }, session.phone.takeIf { it.isNotBlank() }).distinct()
    val notifications by com.example.localconnect.data.FirebaseRepository.getNotificationsFlow(userIds).collectAsState(initial = emptyList())

        val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    var serviceSearchInitialTab by remember { mutableIntStateOf(0) }


    fun navigateTo(tab: Int) {
        if (tabStack.lastOrNull() != tab) {
            tabStack = tabStack + tab
        }
    }

    fun popBack() {
        if (tabStack.size > 1) {
            tabStack = tabStack.dropLast(1)
        } else {
            showExitDialog = true
        }
    }

    LaunchedEffect(initialActionType, initialReferenceId) {
        if (!initialActionType.isNullOrBlank()) {
            when (initialActionType) {
                "job_application", "application_status_employer", "job_application_update" -> {
                    if (!initialReferenceId.isNullOrBlank()) {
                        selectedJobId = initialReferenceId
                        navigateTo(8)
                    }
                }
                "application_status", "status_update", "application_status_applicant" -> {
                    jobSearchInitialTab = 1
                    navigateTo(1)
                }
                "service_request" -> {
                    if (!initialReferenceId.isNullOrBlank()) {
                        selectedServiceId = initialReferenceId
                        navigateTo(10)
                    }
                }
                "service_status_update" -> {
                    serviceSearchInitialTab = 1
                    navigateTo(2)
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


    BackHandler {
        popBack()
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = com.example.localconnect.ui.theme.OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.exit_app),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1A1A1A)
                    )
                }
            },
            text = {
                Text(stringResource(R.string.exit_confirm_msg),
                    fontSize = 15.sp,
                    color = Color(0xFF757575)
                )
            },
            confirmButton = {
                Button(
                    onClick = { (context as? Activity)?.finish() },
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.localconnect.ui.theme.OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.yes_exit), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.cancel), color = Color(0xFF757575), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = { BottomNavBar(selectedTab) { tab -> if (tab == 0) tabStack = listOf(0) else navigateTo(tab) } }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeContent(session, onNavigateToJobs = { navigateTo(1) }, onNavigateToServices = { navigateTo(2) }, onNavigateToPostJob = { navigateTo(7) }, onNavigateToPostService = { navigateTo(6) }, onProfileClick = { navigateTo(4) }, onNavigateToApplicantList = { jobId -> selectedJobId = jobId; navigateTo(8) }, onNavigateToMyApplications = { jobSearchInitialTab = 1; navigateTo(1) }, unreadCount = notifications.count { !it.isRead }, notifications = notifications, onNavigateToNotifications = { navigateTo(11) }, onMarkNotificationsRead = { coroutineScope.launch { com.example.localconnect.data.FirebaseRepository.markNotificationsAsRead(userIds) } })
                1 -> JobSearchScreen(session = session, initialTab = jobSearchInitialTab)
                2 -> ServiceSearchScreen(session = session, initialTab = serviceSearchInitialTab)
                3 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("${stringResource(R.string.chats_tab)} Screen (${stringResource(R.string.coming_soon)})") }
                4 -> ProfileScreen(session, onUpdateSession = { session = it; onSessionUpdate(it) }, onLogout, onNavigateToPostJob = { navigateTo(5) }, onNavigateToPostService = { navigateTo(6) })
                5 -> PostJobScreen(session = session, onBack = { popBack() }, onJobPosted = { popBack() })
                6 -> MyPostedServicesScreen(session = session, onBack = { popBack() }, onNavigateToPostNew = { navigateTo(9) }, onServiceClick = { serviceId -> selectedServiceId = serviceId; navigateTo(10) })
                9 -> PostServiceScreen(session = session, onBack = { popBack() }, onServicePosted = { navigateTo(2) })
                7 -> MyPostedJobsScreen(session = session, onBack = { popBack() }, onNavigateToPostNew = { navigateTo(5) }, onJobClick = { jobId -> selectedJobId = jobId; navigateTo(8) })
                8 -> selectedJobId?.let { ApplicantListScreen(session = session, jobId = it, onBack = { popBack() }, onNavigateToPostNew = { navigateTo(5) }) }
                10 -> selectedServiceId?.let { ServiceRequestListScreen(session = session, serviceId = it, onBack = { popBack() }) }
                11 -> NotificationsScreen(
                    notifications = notifications,
                    onBack = { popBack() },
                    onNotificationClick = { notif ->
                        when (notif.type) {
                            "job_application", "application_status_employer", "job_application_update" -> {
                                if (notif.referenceId.isNotBlank()) {
                                    selectedJobId = notif.referenceId
                                    navigateTo(8)
                                }
                            }
                            "application_status", "status_update", "application_status_applicant" -> {
                                jobSearchInitialTab = 1
                                navigateTo(1)
                            }
                            "service_status_update" -> {
                                serviceSearchInitialTab = 1
                                navigateTo(2)
                            }
                            "new_offer", "offer_response" -> {
                                // Ignore or implement
                            }
                            "service_request" -> {
                                if (notif.referenceId.isNotBlank()) {
                                    selectedServiceId = notif.referenceId
                                    navigateTo(10)
                                }
                            }
                        }
                    },
                    onDeleteNotification = { notif ->
                        coroutineScope.launch { com.example.localconnect.data.FirebaseRepository.deleteNotification(notif.id) }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(session: UserSession, onNavigateToJobs: () -> Unit, onNavigateToServices: () -> Unit, onNavigateToPostJob: () -> Unit, onNavigateToPostService: () -> Unit, onProfileClick: () -> Unit, onNavigateToApplicantList: (String) -> Unit, onNavigateToMyApplications: () -> Unit, unreadCount: Int, notifications: List<com.example.localconnect.data.Notification>, onNavigateToNotifications: () -> Unit, onMarkNotificationsRead: () -> Unit) {
    var showPostActionSheet by remember { mutableStateOf(false) }
        val userIds = listOfNotNull(
        session.firebaseUid.takeIf { it.isNotBlank() },
        session.phone.takeIf { it.isNotBlank() }
    ).distinct()
    val notifications by FirebaseRepository.getNotificationsFlow(userIds).collectAsState(initial = emptyList())
    val unreadCount = notifications.count { !it.isRead }

    val context = androidx.compose.ui.platform.LocalContext.current

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        }
    }

    
    LaunchedEffect(Unit) {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users").document(uid).update("fcmToken", token).addOnFailureListener {}
                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }
    }



    val coroutineScope = rememberCoroutineScope()
    val initial = session.name.firstOrNull()?.uppercaseChar()?.toString()
        ?: session.phone.firstOrNull()?.toString() ?: "U"

    val headerGradient = listOf(
        OrangePrimary,
        Color(0xFFFB923C)  // Orange-400
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        // ---- Personalised header ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(headerGradient))
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LocalConnect",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = session.location.ifBlank { "India" },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notification Bell
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { 
                                onNavigateToNotifications()
                                if (unreadCount > 0) {
                                    onMarkNotificationsRead()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                                    .size(16.dp)
                                    .background(Color.Red, CircleShape)
                                    .border(1.dp, OrangePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (unreadCount > 9) "9+" else unreadCount.toString(), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Profile avatar (tappable)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        }

        // ---- Welcome banner ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(headerGradient.map { it.copy(alpha = 0.12f) }))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "${stringResource(R.string.greeting)}, ${session.name.ifBlank { session.phone }}!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_subtitle),
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---- Main Tiles ----
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainTile(
                icon = Icons.Filled.Search,
                title = stringResource(R.string.seeker_card_title), // "I want Work" or "Find Jobs"
                description = "Find available jobs in your area",
                gradientColors = listOf(OrangePrimary, Color(0xFFFB923C)),
                onClick = onNavigateToJobs
            )

            MainTile(
                icon = Icons.Filled.Build,
                title = stringResource(R.string.find_services_title), // Hardcoding as requested, can localize later
                description = "Find local service providers",
                gradientColors = listOf(Color(0xFFEA580C), OrangePrimary),
                onClick = onNavigateToServices
            )

            MainTile(
                icon = Icons.Filled.AddCircle,
                title = stringResource(R.string.post_job_service_card),
                description = "Hire workers or provide a service",
                gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
                onClick = { showPostActionSheet = true }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    if (showPostActionSheet) {
        PostActionSheet(
            
            onDismiss = { showPostActionSheet = false },
            onPostJob = {
                showPostActionSheet = false
                onNavigateToPostJob()
            },
            onPostService = {
                showPostActionSheet = false
                onNavigateToPostService()
            }
        )
    }

}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = com.example.localconnect.ui.theme.Primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Text(description, fontSize = 13.sp, color = Color(0xFF757575))
            }
        }
    }
}

@Composable
private fun MainTile(
    icon: ImageVector,
    title: String,
    description: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(24.dp)
        ) {
            // Background Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = 10.dp),
                tint = Color.White.copy(alpha = 0.15f)
            )

            // Content
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        Triple(Icons.Filled.Home, stringResource(R.string.home_tab), 0),
        Triple(Icons.Filled.Work, stringResource(R.string.tab_my_jobs), 7),
        Triple(Icons.Filled.Build, stringResource(R.string.tab_my_services), 6),
        Triple(Icons.Filled.Person, stringResource(R.string.profile_tab), 4)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { (icon: ImageVector, label: String, tabIndex: Int) ->
            val isSelected = selectedTab == tabIndex
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tabIndex) },
                icon = {
                    Icon(icon, contentDescription = label, modifier = Modifier.size(if (isSelected) 28.dp else 24.dp))
                },
                label = {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) OrangePrimary else Color(0xFF9E9E9E)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimary,
                    unselectedIconColor = Color(0xFF9E9E9E),
                    indicatorColor = Color(0xFFFFF3EE)
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        HomeScreen(initialSession = UserSession(name = "Sumit", location = "Delhi"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostActionSheet(
    
    onDismiss: () -> Unit,
    onPostJob: () -> Unit,
    onPostService: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "What do you want to post?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ActionCard(
                title = stringResource(R.string.post_job_title),
                description = "Hire local workers for your needs",
                icon = Icons.Filled.Work,
                onClick = onPostJob
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionCard(
                title = stringResource(R.string.post_service_title),
                description = "Offer your skills as a service provider",
                icon = Icons.Filled.Build,
                onClick = onPostService
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.cancel), color = Color(0xFF1A1A1A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


