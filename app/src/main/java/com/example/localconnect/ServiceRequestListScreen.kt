package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MockServiceRequest(
    val id: String,
    val requesterName: String,
    val serviceRequired: String,
    val locality: String,
    val requestedTime: String,
    val status: String, // "pending", "accepted", "rejected"
    val rating: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestListScreen(
    session: UserSession,
    serviceId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    val mockRequests = remember {
        mutableStateListOf(
            MockServiceRequest("1", "Amit Kumar", "Need immediate repair", "Salt Lake", "Today, 4:00 PM", "pending", 4.5),
            MockServiceRequest("2", "Priya Singh", "Check the wiring", "New Town", "Tomorrow, 10:00 AM", "pending", 4.8),
            MockServiceRequest("3", "Rahul Das", "General inspection", "Sector V", "Friday, 2:00 PM", "pending", 4.2)
        )
    }

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.service_requests), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF97316), titleContentColor = androidx.compose.ui.graphics.Color.White, navigationIconContentColor = androidx.compose.ui.graphics.Color.White, actionIconContentColor = androidx.compose.ui.graphics.Color.White)
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFF97316))
            }
        } else if (mockRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.no_requests_found), color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(mockRequests) { index, req ->
                    ServiceRequestCard(
                        request = req,
                        onAccept = {
                            mockRequests[index] = mockRequests[index].copy(status = "accepted")
                            Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show()
                        },
                        onReject = {
                            mockRequests[index] = mockRequests[index].copy(status = "rejected")
                            Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceRequestCard(
    request: MockServiceRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(request.requesterName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFF97316), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(request.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("📍 ${request.locality}", fontSize = 14.sp, color = Color(0xFF4B5563))
            Text("🕒 ${request.requestedTime}", fontSize = 14.sp, color = Color(0xFF4B5563))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Note: ${request.serviceRequired}", fontSize = 14.sp, color = Color.DarkGray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (request.status) {
                "pending" -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF97316))
                        ) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.reject), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                        ) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.accept), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                "accepted" -> {
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF97316), RoundedCornerShape(8.dp)).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.accepted), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                "rejected" -> {
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF7ED), RoundedCornerShape(8.dp)).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.rejected), color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
