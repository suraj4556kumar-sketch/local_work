package com.example.localconnect

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.ServiceRequest
import com.example.localconnect.ui.theme.Primary

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
    var selectedRequest by remember { mutableStateOf<ServiceRequest?>(null) }
    var selectedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(serviceId) {
        isLoading = true
        val fetched = FirebaseRepository.getServiceRequestsForService(serviceId)
        requests.clear()
        requests.addAll(fetched.sortedByDescending { it.appliedOn })
        isLoading = false
    }

    if (selectedRequest != null) {
        ServiceRequestDetailsScreen(
            request = selectedRequest!!,
            onBack = { selectedRequest = null },
            onAccept = {
                coroutineScope.launch {
                    val success = FirebaseRepository.updateServiceRequestStatus(selectedRequest!!.id, "Accepted")
                    if (success) {
                        requests[selectedIndex] = requests[selectedIndex].copy(status = "Accepted")
                        selectedRequest = null
                        Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onReject = {
                coroutineScope.launch {
                    val success = FirebaseRepository.updateServiceRequestStatus(selectedRequest!!.id, "Rejected")
                    if (success) {
                        requests[selectedIndex] = requests[selectedIndex].copy(status = "Rejected")
                        selectedRequest = null
                        Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
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
                    CompactServiceRequestCard(
                        request = req,
                        onViewDetails = {
                            selectedRequest = req
                            selectedIndex = index
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CompactServiceRequestCard(
    request: ServiceRequest,
    onViewDetails: () -> Unit
) {
    val statusBgColor = when (request.status.lowercase()) {
        "accepted" -> Color(0xFFFFF7ED)
        "rejected" -> Color(0xFFFFF7ED)
        else -> Color(0xFFFEF9C3)
    }
    val statusTextColor = when (request.status.lowercase()) {
        "accepted" -> Color(0xFFF97316)
        "rejected" -> Color(0xFFF97316)
        else -> Color(0xFFA16207)
    }
    val statusBorderColor = when (request.status.lowercase()) {
        "accepted" -> Color(0xFFFFF7ED)
        "rejected" -> Color(0xFFFFF7ED)
        else -> Color(0xFFFEF08A)
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onViewDetails() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.customerName.ifBlank { "Customer" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(request.customerCity, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6B7280))
                }
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusBorderColor)
                ) {
                    Text(
                        request.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text("${request.scheduledDate}, ${request.scheduledTime}", fontSize = 14.sp, color = Color(0xFF4B5563))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF374151))
            ) {
                Text("View Details", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val context = LocalContext.current
                Button(onClick = {
                    try { val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${request.customerPhone}")); callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; context.startActivity(callIntent) } catch(e: Throwable) { android.widget.Toast.makeText(context, "Cannot open dialer", android.widget.Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), shape = RoundedCornerShape(8.dp)) {
                    Text("Call", fontSize = 12.sp, color = Color.White)
                }
                Button(onClick = {
                    var waPhone = request.customerPhone
                    if (waPhone.length == 10 && !waPhone.startsWith("+")) waPhone = "+91$waPhone"
                    try { val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$waPhone")).apply { setPackage("com.whatsapp") }; waIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; context.startActivity(waIntent) } catch(e: Throwable) { android.widget.Toast.makeText(context, "WhatsApp not installed", android.widget.Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), shape = RoundedCornerShape(8.dp)) {
                    Text("WhatsApp", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestDetailsScreen(
    request: ServiceRequest,
    onBack: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    androidx.compose.material3.ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onBack,
        containerColor = Color.White
    ) {
        val context = LocalContext.current
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp).verticalScroll(rememberScrollState())) {
            
            // Header: Name and Status
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFFFEDD5)), contentAlignment = Alignment.Center) {
                    Text(request.customerName.ifBlank { "C" }.first().toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(request.customerName.ifBlank { "Customer" }, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    
                    val (statusBg, statusText) = when (request.status.lowercase()) {
                        "pending" -> Color(0xFFDBEAFE) to Color(0xFF1E3A8A)
                        "accepted" -> Color(0xFFD1FAE5) to Color(0xFF065F46)
                        "rejected" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
                        else -> Color(0xFFF3F4F6) to Color.Gray
                    }
                    
                    Surface(color = statusBg, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Text("Status ${request.status}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = statusText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            
            // Contact Buttons
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    try { val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${request.customerPhone}")); callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; context.startActivity(callIntent) } catch(e: Throwable) { android.widget.Toast.makeText(context, "Cannot open dialer", android.widget.Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), shape = RoundedCornerShape(12.dp)) {
                    Text("Call Customer", fontSize = 14.sp, color = Color.White)
                }
                Button(onClick = {
                    var waPhone = request.customerPhone
                    if (waPhone.length == 10 && !waPhone.startsWith("+")) waPhone = "+91$waPhone"
                    try { val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$waPhone")).apply { setPackage("com.whatsapp") }; waIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; context.startActivity(waIntent) } catch(e: Throwable) { android.widget.Toast.makeText(context, "WhatsApp not installed", android.widget.Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), shape = RoundedCornerShape(12.dp)) {
                    Text("Whatsapp", fontSize = 14.sp, color = Color.White)
                }
            }
            
            // Applicant Details (Mapped to Customer Location/Details)
            Text("Customer Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
            
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Phone", fontSize = 14.sp, color = Color.Gray)
                    Text(request.customerPhone, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Location", fontSize = 14.sp, color = Color.Gray)
                    Text(request.customerCity, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Date", fontSize = 14.sp, color = Color.Gray)
                    Text(request.scheduledDate, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Time", fontSize = 14.sp, color = Color.Gray)
                    Text(request.scheduledTime, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (request.latitude != 0.0 && request.longitude != 0.0) {
                Text("Exact Location", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
                val customerLocation = com.google.android.gms.maps.model.LatLng(request.latitude, request.longitude)
                val cameraPositionState = com.google.maps.android.compose.rememberCameraPositionState {
                    position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(customerLocation, 15f)
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) {
                    com.google.maps.android.compose.GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = com.google.maps.android.compose.MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false, zoomGesturesEnabled = false, rotationGesturesEnabled = false)
                    ) {
                        com.google.maps.android.compose.Marker(
                            state = com.google.maps.android.compose.MarkerState(position = customerLocation),
                            title = "Customer Location"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Introduction (Mapped to Problem Description)
            Text("Problem Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            Text(request.problemDescription, fontSize = 14.sp, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 24.dp))
            
            if (request.instructions.isNotBlank()) {
                Text("Special Instructions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                Text(request.instructions, fontSize = 14.sp, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 24.dp))
            }
            
            // Documents Title (Mapped to Attached Picture)
            if (request.imageUrl.isNotBlank()) {
                Text("Attached Media", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
                
                Button(onClick = {
                    try {
                        var viewUri: android.net.Uri? = null
                        var mimeType = "image/jpeg"
                        if (request.imageUrl.startsWith("data:image/")) {
                            val commaIndex = request.imageUrl.indexOf(",")
                            if (commaIndex != -1) {
                                val header = request.imageUrl.substring(0, commaIndex)
                                mimeType = header.substringAfter("data:").substringBefore(";")
                                var ext = ""
                                if (header.contains("name=")) {
                                    val name = header.substringAfter("name=").substringBefore(";")
                                    if (name.contains(".")) ext = name.substring(name.lastIndexOf("."))
                                }
                                if (ext.isEmpty()) ext = ".jpg"
                                
                                val base64Data = request.imageUrl.substring(commaIndex + 1)
                                val bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                                
                                val tempFile = java.io.File(context.filesDir, "shared_service_pic$ext")
                                tempFile.writeBytes(bytes)
                                viewUri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
                            }
                        } else {
                            viewUri = android.net.Uri.parse(request.imageUrl)
                        }
                        
                        if (viewUri != null) {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                            intent.setDataAndType(viewUri, mimeType)
                            intent.flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error opening picture", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF374151))) {
                    Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Attached Picture", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            // Bottom Action Bar
            if (request.status.equals("pending", ignoreCase = true)) {
                Spacer(modifier = Modifier.weight(1f, fill = false))
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.Gray)) {
                        Text(stringResource(R.string.reject), color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = onAccept, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))) {
                        Text(stringResource(R.string.accept), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
