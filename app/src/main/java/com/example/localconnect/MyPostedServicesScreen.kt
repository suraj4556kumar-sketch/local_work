package com.example.localconnect

import com.example.localconnect.ui.TranslatedText
import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.ServiceTask
import com.example.localconnect.ui.theme.OrangePrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostedServicesScreen(
    session: UserSession,
    onBack: () -> Unit,
    onNavigateToPostNew: () -> Unit,
    onServiceClick: (String) -> Unit
) {
    var services by remember { mutableStateOf<List<ServiceTask>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var serviceToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        services = FirebaseRepository.getMyPostedServices(session.phone)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.my_posted_services), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.ui.graphics.Color.White) },
                actions = {
                    TextButton(onClick = onBack) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.back), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF97316), titleContentColor = androidx.compose.ui.graphics.Color.White, navigationIconContentColor = androidx.compose.ui.graphics.Color.White, actionIconContentColor = androidx.compose.ui.graphics.Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                PaddingValues(16.dp).let {
                    Button(
                        onClick = onNavigateToPostNew,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.post_service), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (services.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.no_posted_services_yet), color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(services) { service ->
                    PostedServiceCard(
                        service = service, 
                        onClick = { onServiceClick(service.id) },
                        onDelete = {
                            serviceToDelete = service.id
                        }
                    )
                }
            }
        }
    }

    if (serviceToDelete != null) {
        AlertDialog(
            onDismissRequest = { serviceToDelete = null },
            title = { Text(stringResource(R.string.delete_service_title)) },
            text = { Text(stringResource(R.string.delete_service_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = serviceToDelete
                        serviceToDelete = null
                        if (id != null) {
                            scope.launch {
                                val success = FirebaseRepository.deleteService(id)
                                if (success) {
                                    services = FirebaseRepository.getMyPostedServices(session.phone)
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.remove), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { serviceToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun PostedServiceCard(service: ServiceTask, onClick: () -> Unit, onDelete: (() -> Unit)? = null, buttonText: String = stringResource(R.string.view_bookings), isButtonEnabled: Boolean = true) {
    val primaryColor = Color(0xFFF97316)
    val primaryDarkColor = Color(0xFFEA580C)
    val lightOrangeColor = Color(0xFFFFF7ED)

    Surface(
        color = lightOrangeColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFED7AA)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isButtonEnabled) { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    TranslatedText(service.serviceTitle.ifBlank { "Service Title" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                    TranslatedText(service.providerName.ifBlank { "Provider Name" }, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Icon(androidx.compose.material.icons.Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp).padding(end = 2.dp), tint = Color(0xFFFFB300))
                            Text("${service.rating}", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), fontSize = 14.sp)
                        }
                    }
                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(androidx.compose.material.icons.Icons.Filled.Delete, contentDescription = "Delete Service", tint = Color.Red)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TranslatedText("📍 ${service.locality.ifBlank { "Area" }}, ${service.city.ifBlank { "City" }}", fontSize = 14.sp, color = Color(0xFF4B5563), modifier = Modifier.weight(1f))
                if (service.distanceKm > 0.0) {
                    TranslatedText("${String.format("%.1f", service.distanceKm)} km away", fontSize = 12.sp, color = primaryDarkColor, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                TranslatedText("${stringResource(R.string.visiting_fee)}${service.price}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryDarkColor)
                if (service.feesPerKm > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    TranslatedText("+ ₹${service.feesPerKm}/km", fontSize = 14.sp, color = primaryDarkColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TranslatedText("⭐ ${stringResource(R.string.exp_years)} ${service.experience}", fontSize = 12.sp, color = Color.Gray)
                TranslatedText("🕒 ${service.availableDays.joinToString(", ").takeIf { it.isNotBlank() } ?: "None"}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClick,
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isButtonEnabled) Color(0xFFF3F4F6) else Color.LightGray,
                    contentColor = if (isButtonEnabled) Color(0xFF374151) else Color.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}