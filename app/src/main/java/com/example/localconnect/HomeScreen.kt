package com.example.localconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*

@Composable
fun HomeScreen(session: UserSession) {
    val strings = getStrings(session.language.code)
    val isHirer = session.role == UserRole.HIRER
    val initial = session.name.firstOrNull()?.uppercaseChar()?.toString()
        ?: session.phone.firstOrNull()?.toString() ?: "U"

    val welcomeText = if (isHirer) strings.hirerWelcome else strings.seekerWelcome
    val headerGradient = if (isHirer)
        listOf(OrangePrimary, Color(0xFFFF7043))
    else
        listOf(Color(0xFF1565C0), Color(0xFF1976D2))

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = { BottomNavBar(isHirer) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                            Text("📍", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = session.location.ifBlank { "India" },
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    // Profile avatar (tappable)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { /* TODO: open profile screen */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
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
                        text = "${strings.greeting}, ${session.name.ifBlank { session.phone }}! 👋",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = welcomeText,
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                    // Role badge
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isHirer) OrangePrimary else Color(0xFF1565C0))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isHirer) "🏢 ${strings.hirerCardTitle}" else "💼 ${strings.seekerCardTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // ---- Section label ----
            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))

            // ---- Quick action cards ----
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isHirer) {
                    QuickActionCard(icon = "📝", title = strings.postJob, desc = strings.postJobDesc, color = OrangePrimary)
                    QuickActionCard(icon = "🔍", title = strings.findWorkers, desc = strings.findWorkersDesc, color = Color(0xFF2E7D32))
                    QuickActionCard(icon = "📋", title = strings.myPostings, desc = "Manage your active listings", color = Color(0xFF6A1B9A))
                } else {
                    QuickActionCard(icon = "💼", title = strings.browseJobs, desc = strings.browseJobsDesc, color = Color(0xFF1565C0))
                    QuickActionCard(icon = "🔧", title = strings.browseServices, desc = strings.browseServicesDesc, color = Color(0xFF2E7D32))
                    QuickActionCard(icon = "📄", title = strings.myApplications, desc = "Track your applications", color = Color(0xFF6A1B9A))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionCard(icon: String, title: String, desc: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(3.dp))
                Text(desc, fontSize = 12.sp, color = Color(0xFF9E9E9E))
            }
            Text("→", fontSize = 18.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BottomNavBar(isHirer: Boolean) {
    val items = if (isHirer)
        listOf("🏠" to "Home", "➕" to "Post", "📋" to "Listings", "💬" to "Chats", "👤" to "Profile")
    else
        listOf("🏠" to "Home", "🔍" to "Search", "📄" to "Applied", "💬" to "Chats", "👤" to "Profile")

    var selectedTab by remember { mutableIntStateOf(0) }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        items.forEachIndexed { index, (icon, label) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { selectedTab = index },
                icon = {
                    if (index == 1 && isHirer) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(13.dp))
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) { Text(icon, fontSize = 20.sp) }
                    } else {
                        Text(icon, fontSize = if (isSelected) 22.sp else 20.sp)
                    }
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
                    indicatorColor = Color(0xFFFFF3EE)
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeHirerPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        HomeScreen(session = UserSession(name = "Sumit", location = "Delhi", role = UserRole.HIRER))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeSeekerPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        HomeScreen(session = UserSession(name = "Rahul", location = "Mumbai", role = UserRole.SEEKER))
    }
}
