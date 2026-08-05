package com.example.localconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Notification
import com.example.localconnect.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<Notification>,
    onBack: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onDeleteNotification: (Notification) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications_tab), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.no_notifications),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(notifications) { notif ->
                        NotificationItem(notif, onNotificationClick, onDeleteNotification)
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: (Notification) -> Unit,
    onDelete: (Notification) -> Unit
) {
    val df = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
    val timeString = remember(notification.timestamp) { df.format(Date(notification.timestamp)) }
    val isUnread = !notification.isRead

    val bgColor = if (isUnread) Color(0xFFFFF3E0) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick(notification) }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (isUnread) OrangePrimary.copy(alpha = 0.1f) else Color(0xFFF5F5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (notification.type) {
                "job_application" -> Icons.Filled.Person
                "application_status", "status_update", "job_application_update" -> Icons.Filled.Info
                "new_offer", "offer_response", "service_status_update" -> Icons.Filled.CheckCircle
                "service_request" -> Icons.Filled.Build
                else -> Icons.Filled.Notifications
            }
            val tint = if (isUnread) OrangePrimary else Color.Gray
            Icon(icon, contentDescription = null, tint = tint)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                notification.title,
                fontSize = 16.sp,
                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            com.example.localconnect.ui.TranslatedText(notification.message, fontSize = 14.sp, color = Color(0xFF4B5563))
            Spacer(modifier = Modifier.height(6.dp))
            Text(timeString, fontSize = 12.sp, color = Color(0xFF9CA3AF))
        }
        IconButton(onClick = { onDelete(notification) }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Gray)
        }
    }
}
