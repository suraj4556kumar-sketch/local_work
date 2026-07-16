import re

filepath = r"app/src/main/java/com/example/localconnect/ServiceRequestListScreen.kt"

new_code = """package com.example.localconnect

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.ServiceRequest

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

    val requests = remember { mutableStateListOf<ServiceRequest>() }

    LaunchedEffect(serviceId) {
        isLoading = true
        val fetched = FirebaseRepository.getServiceRequestsForService(serviceId)
        requests.clear()
        requests.addAll(fetched.sortedByDescending { it.appliedOn })
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.service_requests), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF97316), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFF97316))
            }
        } else if (requests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_requests_found), color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(requests) { index, req ->
                    ServiceRequestCard(
                        request = req,
                        onAccept = {
                            coroutineScope.launch {
                                val success = FirebaseRepository.updateServiceRequestStatus(req.id, "Accepted")
                                if (success) {
                                    requests[index] = requests[index].copy(status = "Accepted")
                                    Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onReject = {
                            coroutineScope.launch {
                                val success = FirebaseRepository.updateServiceRequestStatus(req.id, "Rejected")
                                if (success) {
                                    requests[index] = requests[index].copy(status = "Rejected")
                                    Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceRequestCard(
    request: ServiceRequest,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(request.customerName.ifBlank { "Customer" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(request.customerPhone, fontSize = 14.sp, color = Color(0xFF4B5563))
                    if (request.customerAltPhone.isNotBlank()) {
                        Text(request.customerAltPhone + " (Alt)", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(request.customerAddress, fontSize = 14.sp, color = Color(0xFF4B5563))
                    if (request.customerLandmark.isNotBlank()) {
                        Text("Landmark: ${request.customerLandmark}", fontSize = 13.sp, color = Color.Gray)
                    }
                    Text(request.customerCity, fontSize = 13.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("${request.scheduledDate} at ${request.scheduledTime}", fontSize = 14.sp, color = Color(0xFF4B5563))
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Build, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(request.problemDescription, fontSize = 14.sp, color = Color.DarkGray)
            }
            
            if (request.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Instructions: ${request.instructions}", fontSize = 13.sp, color = Color.DarkGray, fontStyle = FontStyle.Italic, modifier = Modifier.padding(start = 24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            when (request.status.lowercase()) {
                "pending" -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEA580C))
                        ) {
                            Text(stringResource(R.string.reject), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                        ) {
                            Text(stringResource(R.string.accept), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                "accepted" -> {
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF97316), RoundedCornerShape(8.dp)).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.accepted), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                "rejected" -> {
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF7ED), RoundedCornerShape(8.dp)).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.rejected), color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
"""

with open(filepath, "w", encoding="utf-8") as f:
    f.write(new_code)
print("Updated ServiceRequestListScreen.kt successfully.")
