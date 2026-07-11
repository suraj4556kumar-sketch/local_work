package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.Job
import com.example.localconnect.data.JobApplication
import com.example.localconnect.ui.theme.OrangePrimary
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantListScreen(
    session: UserSession,
    jobId: String,
    onBack: () -> Unit,
    onNavigateToPostNew: () -> Unit
) {
    var applications by remember { mutableStateOf<List<JobApplication>>(emptyList()) }
    var currentFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    
    var job by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(jobId) {
        job = FirebaseRepository.getJob(jobId)
    }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedApplicant by remember { mutableStateOf<JobApplication?>(null) }
    var showProfileSheet by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(jobId) {
        applications = FirebaseRepository.getJobApplicationsForJob(jobId)
        isLoading = false
    }
    
    val filteredApplicants = applications.filter { app ->
        val statusMatch = when (currentFilter) {
            "all" -> true
            "new" -> app.status == "Pending"
            "shortlisted" -> app.status == "Shortlisted"
            "accepted" -> app.status == "Selected" || app.status == "Offer Accepted"
            "rejected" -> app.status == "Rejected" || app.status == "Offer Rejected"
            else -> false
        }
        val searchMatch = app.applicantName.contains(searchQuery, ignoreCase = true) || app.applicantIntro.contains(searchQuery, ignoreCase = true)
        statusMatch && searchMatch
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(OrangePrimary)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.offset(x = (-8).dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.applicants_for), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha=0.8f), letterSpacing = 1.sp)
                        Text(job?.title ?: "Loading...", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                
                // Job Summary Box
                Surface(
                    color = Color(0xFFF9FAFB),
                    border = BorderStroke(1.dp, Color(0xFFF3F4F6)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Home, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(job?.companyName ?: "Loading...", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF1F2937), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Filled.LocationOn, null, tint = Color.LightGray, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(job?.location ?: "", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFFF97316), modifier = Modifier.size(14.dp)) // Money icon mock
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(job?.salary ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4B5563))
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.total), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray, letterSpacing = 1.sp)
                            Text("${filteredApplicants.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }
                }
                
                HorizontalDivider(color = Color(0xFFF3F4F6))
            }
        },
        
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            
            // Sticky Filter & Search Area
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Column {
                    // Filters
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterPill(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.filter_all), currentFilter == "all") { currentFilter = "all" }
                        FilterPill(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.filter_new), currentFilter == "new") { currentFilter = "new" }
                        FilterPill(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.shortlist), currentFilter == "shortlisted") { currentFilter = "shortlisted" }
                        FilterPill(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.accepted), currentFilter == "accepted") { currentFilter = "accepted" }
                        FilterPill(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.rejected), currentFilter == "rejected") { currentFilter = "rejected" }
                    }
                    
                    // Search
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.search_name_or_skill), fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        // Mock Sort Dropdown
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.height(50.dp).clickable { }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.recent), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                                Icon(Icons.Filled.ArrowDropDown, null, tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Applicant List
            if (filteredApplicants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.no_applicants_found), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.try_adjusting_filters), color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredApplicants) { app ->
                        ApplicantCard(
                            app = app,
                            onViewProfile = { 
                                selectedApplicant = app
                                showProfileSheet = true
                            },
                            onStatusChange = { newStatus ->
                                val index = applications.indexOfFirst { it.id == app.id }
                                if (index != -1) {
                                    val updated = applications[index].copy(status = newStatus)
                                    val mutable = applications.toMutableList()
                                    mutable[index] = updated
                                    applications = mutable
                                    coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                                }
                                Toast.makeText(context, "Applicant marked as $newStatus", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Modals,
    if (showProfileSheet && selectedApplicant != null) {
        ProfileBottomSheet(
            applicant = selectedApplicant!!,
            onDismiss = { showProfileSheet = false },
            onStatusChange = { newStatus ->
                val index = applications.indexOfFirst { it.id == selectedApplicant!!.id }
                if (index != -1) {
                        val updated = applications[index].copy(status = newStatus)
                        val mutable = applications.toMutableList()
                        mutable[index] = updated
                        applications = mutable
                        selectedApplicant = updated
                        coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                }
                showProfileSheet = false
                Toast.makeText(context, "Applicant marked as $newStatus", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) OrangePrimary else Color(0xFFF3F4F6),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else Color(0xFF4B5563),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ApplicantCard(
    app: JobApplication,
    onViewProfile: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    val statusCfg = getStatusConfig(app.status)
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF3F4F6)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFEDD5)), contentAlignment = Alignment.Center) {
                        Text(app.applicantName.first().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(app.applicantName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Text("${app.applicantGender}, ${app.applicantAge} years", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
                Surface(color = statusCfg.bgColor, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, statusCfg.borderColor)) {
                    Text(androidx.compose.ui.res.stringResource(statusCfg.labelId).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusCfg.textColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Condensed Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Experience: ${app.applicantExperience} years", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            val context = LocalContext.current
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onViewProfile, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)) {
                    Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.profile), fontSize = 14.sp)
                }
                Button(onClick = {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${app.applicantPhone}"))
                    context.startActivity(callIntent)
                }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)) {
                    Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.call), fontSize = 14.sp, color = Color.White)
                }
                Button(onClick = {
                    var waPhone = app.applicantPhone
                    if (waPhone.length == 10 && !waPhone.startsWith("+")) waPhone = "+91$waPhone"
                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$waPhone")).apply { setPackage("com.whatsapp") }
                    try { context.startActivity(waIntent) } catch(e: Exception) { Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.weight(1.2f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)) {
                    Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.whatsapp), fontSize = 14.sp, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (app.status) {
                    "Pending" -> {
                        OutlinedButton(onClick = { onStatusChange("Shortlisted") }, modifier = Modifier.weight(1f).height(40.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, OrangePrimary)) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.shortlist), color = OrangePrimary)
                        }
                        OutlinedButton(onClick = { onStatusChange("Rejected") }, modifier = Modifier.weight(1f).height(40.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.reject), color = Color.Gray)
                        }
                    }
                    "Shortlisted" -> {
                        Button(onClick = { onStatusChange("Selected") }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)), shape = RoundedCornerShape(8.dp)) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.select))
                        }
                        OutlinedButton(onClick = { onStatusChange("Rejected") }, modifier = Modifier.weight(1f).height(40.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.reject), color = Color.Gray)
                        }
                    }
                    "Selected" -> {
                        Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)), shape = RoundedCornerShape(8.dp), enabled = false) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.waiting_for_applicant_accept), color = Color.Gray)
                        }
                    }
                    "Offer Accepted" -> {
                        Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), shape = RoundedCornerShape(8.dp), enabled = false) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.applicant_accepted_offer), color = Color.White)
                        }
                    }
                    "Offer Rejected" -> {
                        Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), shape = RoundedCornerShape(8.dp)) {
                            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.applicant_rejected_offer), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(applicant: JobApplication, onDismiss: () -> Unit, onStatusChange: (String) -> Unit) {
    ModalBottomSheet(
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFFFEDD5)), contentAlignment = Alignment.Center) {
                    Text(applicant.applicantName.first().toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(applicant.applicantName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    val statusCfg = getStatusConfig(applicant.status)
                    Surface(color = statusCfg.bgColor, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Text(androidx.compose.ui.res.stringResource(statusCfg.labelId), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = statusCfg.textColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }

            // Contact
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val context = LocalContext.current
                Button(onClick = {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${applicant.applicantPhone}"))
                    context.startActivity(callIntent)
                }, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), shape = RoundedCornerShape(12.dp)) {
                    Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.call_applicant), fontSize = 14.sp, color = Color.White)
                }
                Button(onClick = {
                    var waPhone = applicant.applicantPhone
                    if (waPhone.length == 10 && !waPhone.startsWith("+")) waPhone = "+91$waPhone"
                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$waPhone")).apply { setPackage("com.whatsapp") }
                    try { context.startActivity(waIntent) } catch(e: Exception) { Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), shape = RoundedCornerShape(12.dp)) {
                    Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.whatsapp), fontSize = 14.sp, color = Color.White)
                }
            }

            // Details
            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.applicant_details), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
            ProfileDetailRow("Phone", applicant.applicantPhone)
            ProfileDetailRow("Gender", applicant.applicantGender)
            ProfileDetailRow("Age", "${applicant.applicantAge} years")
            ProfileDetailRow("Experience", "${applicant.applicantExperience} years")
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.introduction), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
            Text(applicant.applicantIntro, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 24.dp))

            val docsToDisplay = if (applicant.uploadedDocuments.isNotEmpty()) {
                applicant.uploadedDocuments
            } else if (applicant.uploadedDocumentUri.isNotEmpty()) {
                mapOf("Uploaded Document" to applicant.uploadedDocumentUri)
            } else {
                emptyMap()
            }

            if (docsToDisplay.isNotEmpty()) {
                val context = LocalContext.current
                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.documents_title), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                
                docsToDisplay.forEach { (docName, uriString) ->
                    Button(
                        onClick = {
                            try {
                                var viewUri: Uri? = null
                                var mimeType = "application/octet-stream"

                                if (uriString.startsWith("data:")) {
                                    val commaIndex = uriString.indexOf(",")
                                    if (commaIndex != -1) {
                                        val header = uriString.substring(0, commaIndex)
                                        mimeType = header.substringAfter("data:").substringBefore(";")
                                        var ext = ""
                                        if (header.contains("name=")) {
                                            val name = header.substringAfter("name=").substringBefore(";")
                                            if (name.contains(".")) ext = name.substring(name.lastIndexOf("."))
                                        }
                                        val base64Data = uriString.substring(commaIndex + 1)
                                        val bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                                        if (ext.isEmpty()) {
                                            ext = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.let { ".$it" } ?: ""
                                            if (ext.isEmpty()) {
                                                ext = if (mimeType.contains("jpeg") || mimeType.contains("jpg")) ".jpg"
                                                      else if (mimeType.contains("png")) ".png"
                                                      else if (mimeType.contains("pdf")) ".pdf"
                                                      else if (mimeType.contains("word")) ".docx"
                                                      else ""
                                            }
                                        }
                                        val sanitizedDocName = docName.replace("[^A-Za-z0-9]".toRegex(), "_")
                                        val tempFile = java.io.File(context.filesDir, "shared_$sanitizedDocName$ext")
                                        tempFile.writeBytes(bytes)
                                        viewUri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
                                    }
                                } else if (uriString.startsWith("http")) {
                                    viewUri = Uri.parse(uriString)
                                } else {
                                    val file = java.io.File(uriString)
                                    viewUri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                }

                                if (viewUri != null) {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(viewUri, mimeType)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    val chooser = Intent.createChooser(intent, "Open $docName")
                                    context.startActivity(chooser)
                                } else {
                                    Toast.makeText(context, "Invalid document format", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open $docName: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Description, contentDescription = null, tint = Color(0xFF4B5563), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View $docName", color = Color(0xFF4B5563), fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.documents_title), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.no_documents_uploaded), fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 24.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            when (applicant.status) {
                "Pending" -> {
                    Button(onClick = { onStatusChange("Shortlisted") }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), shape = RoundedCornerShape(12.dp)) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.shortlist_applicant), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { onStatusChange("Rejected") }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE5E7EB))) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.reject_application), fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
                "Shortlisted" -> {
                    Button(onClick = { onStatusChange("Selected") }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)), shape = RoundedCornerShape(12.dp)) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.select_applicant), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { onStatusChange("Rejected") }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE5E7EB))) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.reject_application), fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

data class StatusConfig(val labelId: Int, val bgColor: Color, val textColor: Color, val borderColor: Color)

fun getStatusConfig(status: String): StatusConfig {
    return when (status) {
        "Pending" -> StatusConfig(R.string.status_pending, Color(0xFFDBEAFE), Color(0xFF1E3A8A), Color(0xFFBFDBFE))
        "Shortlisted" -> StatusConfig(R.string.shortlist, Color(0xFFFEF9C3), Color(0xFFA16207), Color(0xFFFEF08A))
        "Selected" -> StatusConfig(R.string.select, Color(0xFFFFF7ED), Color(0xFFF97316), Color(0xFFFFEDD5))
        "Offer Accepted" -> StatusConfig(R.string.offer_accepted, Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFFBBF7D0))
        "Offer Rejected" -> StatusConfig(R.string.offer_rejected, Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFFECACA))
        "Rejected" -> StatusConfig(R.string.rejected, Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFFECACA))
        else -> StatusConfig(R.string.status_unknown, Color.LightGray, Color.DarkGray, Color.Gray)
    }
}
