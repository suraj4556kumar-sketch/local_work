package com.example.localconnect


import com.example.localconnect.ui.TranslatedText
import androidx.compose.ui.res.stringResource
import com.example.localconnect.R
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.ServiceRequest
import com.example.localconnect.data.User
import com.example.localconnect.data.ServiceTask
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person

@Composable
fun MyServiceBookingsScreen(session: UserSession) {
    var bookings by remember { mutableStateOf<List<ServiceRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var requestToRemove by remember { mutableStateOf<ServiceRequest?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val seekerId = session.phone.takeIf { it.isNotBlank() } ?: session.firebaseUid
    
    LaunchedEffect(Unit) {
        isLoading = true
        bookings = FirebaseRepository.getMyServiceBookings(seekerId)
        isLoading = false
    }
    
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFF97316))
        }
    } else if (bookings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No bookings found.", color = Color.Gray, fontSize = 16.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookings.sortedByDescending { it.appliedOn }) { request ->
                SeekerBookingCard(request = request, onRemove = { requestToRemove = request })
            }
        }
    }

    if (requestToRemove != null) {
        AlertDialog(
            onDismissRequest = { requestToRemove = null },
            title = { Text(stringResource(R.string.cancel_booking_title)) },
            text = { Text(stringResource(R.string.cancel_booking_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    val reqId = requestToRemove!!.id
                    requestToRemove = null
                    coroutineScope.launch {
                        val success = FirebaseRepository.deleteServiceRequest(reqId)
                        if (success) {
                            bookings = bookings.filter { it.id != reqId }
                            Toast.makeText(context, "Booking removed", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { requestToRemove = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeekerBookingCard(request: ServiceRequest, onRemove: () -> Unit) {
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

    var providerUser by remember { mutableStateOf<User?>(null) }
    var serviceTask by remember { mutableStateOf<ServiceTask?>(null) }
    var showDetails by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(request.status, request.providerId, request.serviceId) {
        if (request.status.lowercase() == "accepted") {
            providerUser = FirebaseRepository.getUser(request.providerId)
            serviceTask = FirebaseRepository.getServiceTask(request.serviceId)
        }
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().combinedClickable(
            onClick = {},
            onLongClick = onRemove
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TranslatedText(request.serviceTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
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
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(request.customerAddress, fontSize = 14.sp, color = Color(0xFF4B5563))
            }

            if (request.status.lowercase() == "accepted") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        try { val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${request.providerId}")); callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; context.startActivity(callIntent) } catch(e: Throwable) { android.widget.Toast.makeText(context, "Cannot open dialer", android.widget.Toast.LENGTH_SHORT).show() }
                    }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), shape = RoundedCornerShape(8.dp)) {
                        Text(stringResource(R.string.call_btn), fontSize = 12.sp, color = Color.White)
                    }
                    Button(onClick = {
                        var waPhone = request.providerId
                        if (waPhone.length == 10 && !waPhone.startsWith("+")) waPhone = "+91$waPhone"
                        try { val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$waPhone")).apply { setPackage("com.whatsapp") }; waIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; context.startActivity(waIntent) } catch(e: Throwable) { android.widget.Toast.makeText(context, "WhatsApp not installed", android.widget.Toast.LENGTH_SHORT).show() }
                    }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), shape = RoundedCornerShape(8.dp)) {
                        Text(stringResource(R.string.whatsapp_btn), fontSize = 12.sp, color = Color.White)
                    }
                    Button(onClick = {
                        showDetails = true
                    }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF374151)), shape = RoundedCornerShape(8.dp)) {
                        Text(stringResource(R.string.details_btn), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("${stringResource(R.string.requested_on)} ${java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault()).format(java.util.Date(request.appliedOn))}", fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }
        }
    }

    if (showDetails) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showDetails = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.provider_details), fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(providerUser?.name ?: serviceTask?.providerName ?: "Unknown", fontSize = 16.sp)
                }
                
                if (serviceTask?.rating != null && serviceTask?.rating!! > 0.0) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${String.format("%.1f", serviceTask?.rating)} Rating", fontSize = 16.sp)
                    }
                }
                
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 12.dp)) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(serviceTask?.location ?: providerUser?.location ?: "No location", fontSize = 16.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Applied on ${java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault()).format(java.util.Date(request.appliedOn))}", fontSize = 16.sp)
                }
                
                Button(
                    onClick = { showDetails = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF374151)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
