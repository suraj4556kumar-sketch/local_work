package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import com.example.localconnect.ui.TranslatedText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import android.location.Geocoder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Job
import com.example.localconnect.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    job: Job,
    session: UserSession,
    onBack: () -> Unit,
    onApply: () -> Unit,
    onSimilarJobClick: (Job) -> Unit
) {
    val scrollState = rememberScrollState()
        var hasApplied by remember { mutableStateOf(false) }
    var checkingApplication by remember { mutableStateOf(true) }

    LaunchedEffect(job.id, session.firebaseUid) {
        val myApps = com.example.localconnect.data.FirebaseRepository.getMyJobApplications(session.firebaseUid)
        hasApplied = myApps.any { it.jobId == job.id }
        checkingApplication = false
    }

    LaunchedEffect(job) {
        scrollState.scrollTo(0)
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            Surface(
                color = OrangePrimary,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.job_details),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = stringResource(R.string.back),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(8.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (checkingApplication) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color.Gray),
                            enabled = false
                        ) {
                            Text(stringResource(R.string.loading_text), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else if (hasApplied) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.applied_status), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = onApply,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316), contentColor = Color.White)
                        ) {
                            Text(stringResource(R.string.apply_now), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp)
        ) {
            
            // --- Hero Section ---
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            TranslatedText(text = job.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                lineHeight = 30.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TranslatedText(text = job.companyName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Text(" • ", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                
                                Row(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF7ED), RoundedCornerShape(4.dp))
                                        .border(1.dp, Color(0xFFFFF7ED), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFF97316))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.verified_employer), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFFEDD5), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Restaurant, contentDescription = null, modifier = Modifier.size(28.dp), tint = com.example.localconnect.ui.theme.Primary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        TranslatedText(job.location, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        TranslatedText(job.postingDate, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Quick Highlights Grid ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Salary
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Text("₹", fontSize = 20.sp, color = Color(0xFFF97316), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.salary), fontSize = 12.sp, color = Color(0xFF64748B))
                                TranslatedText(job.salary, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                    
                    // Experience
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Work, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.experience), fontSize = 12.sp, color = Color(0xFF64748B))
                                TranslatedText(job.experienceReq, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Education
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.School, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.education_label), fontSize = 12.sp, color = Color(0xFF64748B))
                                TranslatedText(job.educationReq.ifBlank { "Not Required" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                    
                    // Empty placeholder to keep Education aligned
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Job Description ---
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.job_desc_hint), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    // Audio Controls
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(stringResource(R.string.audio_desc), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF334155)),
                                    border = borderStroke(Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp), tint = com.example.localconnect.ui.theme.Primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.listen), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF334155)),
                                    border = borderStroke(Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.Headphones, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.hindi_lang), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF334155)),
                                    border = borderStroke(Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.Headphones, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.bengali_lang), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    
                    val displayDescription = if (job.isCustomDescription) {
                        when (session.language.code) {
                            "hi" -> job.descriptionHi.ifEmpty { job.description }
                            "bn" -> job.descriptionBn.ifEmpty { job.description }
                            else -> job.descriptionEn.ifEmpty { job.description }
                        }
                    } else {
                        job.description
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    TranslatedText(
                        displayDescription,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        lineHeight = 22.sp
                    )
                    
                    val displayResponsibilities = if (job.isCustomResponsibilities) {
                        when (session.language.code) {
                            "hi" -> job.responsibilitiesHi.ifEmpty { job.responsibilities }
                            "bn" -> job.responsibilitiesBn.ifEmpty { job.responsibilities }
                            else -> job.responsibilitiesEn.ifEmpty { job.responsibilities }
                        }
                    } else {
                        job.responsibilities
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    if (displayResponsibilities.isNotEmpty()) {
                        Text(stringResource(R.string.skills_resp), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            displayResponsibilities.forEach { res ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp).padding(top = 4.dp), tint = com.example.localconnect.ui.theme.Primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(res, fontSize = 14.sp, color = Color(0xFF475569))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Documents Required ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.docs_required), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    val reqs = job.requirements + job.documentsRequired
                    
                    if (reqs.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reqs.forEach { req ->
                                ReqChip(req, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    } else {
                        Text(stringResource(R.string.no_docs_mentioned), fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Benefits ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.benefits), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    if (job.benefits.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            job.benefits.forEach { benefit ->
                                BenefitChip(benefit, Color(0xFFFFF7ED), Color(0xFFF97316), Color(0xFFFFF7ED))
                            }
                        }
                    } else {
                        Text(stringResource(R.string.no_benefits_mentioned), fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Employer Details ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.employer_details), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFF1F5F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            TranslatedText(job.companyName.take(2).uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            TranslatedText(job.companyName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFB300))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("4.6", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(" • 15 jobs posted", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                    
                    // Removed call and chat options
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Job Location ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text(stringResource(R.string.job_location), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Box(
                            modifier = Modifier.background(Color(0xFFFFF7ED), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.LocationOn, contentDescription=null, modifier=Modifier.size(10.dp), tint=Color(0xFFF97316)); Text(" ${job.distanceKm} km away", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316)) }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Row { Text("Work address : ", fontSize = 14.sp, color = Color(0xFF64748B)); TranslatedText(job.workAddress.ifBlank { "N/A" }, fontSize = 14.sp, color = Color(0xFF1E293B)) }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.weight(1f)) { Text("City : ", fontSize = 14.sp, color = Color(0xFF64748B)); TranslatedText(job.city.ifBlank { "N/A" }, fontSize = 14.sp, color = Color(0xFF1E293B)) }
                            Row(modifier = Modifier.weight(1f)) { Text("State : ", fontSize = 14.sp, color = Color(0xFF64748B)); TranslatedText(job.state.ifBlank { "N/A" }, fontSize = 14.sp, color = Color(0xFF1E293B)) }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.weight(1f)) { Text("Pincode : ", fontSize = 14.sp, color = Color(0xFF64748B)); TranslatedText(job.pincode.ifBlank { "N/A" }, fontSize = 14.sp, color = Color(0xFF1E293B)) }
                            Row(modifier = Modifier.weight(1f)) { Text("Landmark : ", fontSize = 14.sp, color = Color(0xFF64748B)); TranslatedText(job.landmark.ifBlank { "N/A" }, fontSize = 14.sp, color = Color(0xFF1E293B)) }
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inline Map with Clickable Intent
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            val context = LocalContext.current
                            var markerPosition by remember { mutableStateOf<LatLng?>(null) }
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 5f) // Default India
                            }
                            val mapAddress = job.workAddress.ifBlank { job.location }
                            
                            LaunchedEffect(mapAddress, job.latitude, job.longitude) {
                                if (job.latitude != 0.0 && job.longitude != 0.0) {
                                    val pt = LatLng(job.latitude, job.longitude)
                                    markerPosition = pt
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 14f))
                                } else if (mapAddress.isNotBlank()) {
                                    var pt: LatLng? = null
                                    var showNotFound = false
                                    var showEmulatorFallback = false
                                    
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses = geocoder.getFromLocationName(mapAddress, 1)
                                            if (!addresses.isNullOrEmpty()) {
                                                pt = LatLng(addresses[0].latitude, addresses[0].longitude)
                                            } else {
                                                showNotFound = true
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            pt = LatLng(19.0760, 72.8777) // Mumbai default
                                            showEmulatorFallback = true
                                        }
                                    }
                                    
                                    if (pt != null) {
                                        markerPosition = pt
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt!!, 14f))
                                    }
                                    if (showNotFound) {
                                        android.widget.Toast.makeText(context, "Location not found on map", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                    if (showEmulatorFallback) {
                                        android.widget.Toast.makeText(context, "Using Mumbai fallback for Emulator", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = com.google.maps.android.compose.MapUiSettings(
                                    zoomControlsEnabled = false, 
                                    scrollGesturesEnabled = false, 
                                    zoomGesturesEnabled = false,
                                    rotationGesturesEnabled = false,
                                    tiltGesturesEnabled = false
                                )
                            ) {
                                markerPosition?.let { pt ->
                                    Marker(
                                        state = MarkerState(position = pt),
                                        title = mapAddress
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.9f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.clickable {
                                    val encodedLocation = java.net.URLEncoder.encode(mapAddress, "UTF-8")
                                    val geoUri = android.net.Uri.parse("geo:0,0?q=$encodedLocation")
                                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, geoUri)
                                    mapIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        try {
                                            val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedLocation")
                                            val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                            webIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                            context.startActivity(webIntent)
                                        } catch (innerException: Exception) {
                                            android.widget.Toast.makeText(context, "No application found to open maps.", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.view_on_map), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Similar Jobs ---
            Column(modifier = Modifier.padding(vertical = 24.dp)) {
                Text(stringResource(R.string.similar_jobs), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val similarJobs = getDummyJobs(session.language.code).filter { it.id != job.id }.take(3)
                    
                    similarJobs.forEach { simJob ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = borderStroke(Color(0xFFF1F5F9)),
                            shadowElevation = 1.dp,
                            modifier = Modifier.width(220.dp).clickable { onSimilarJobClick(simJob) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(simJob.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(simJob.companyName, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(bottom = 12.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                                    Text(simJob.salary, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(stringResource(R.string.view_btn), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for fixed bottom bar
        }
    }
}

@Composable
fun borderStroke(color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(1.dp, color)
}

@Composable
fun ReqChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFF97316))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 12.sp, color = Color(0xFF475569))
        }
    }
}

@Composable
fun BenefitChip(text: String, bgColor: Color, textColor: Color, borderColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(50))
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}
