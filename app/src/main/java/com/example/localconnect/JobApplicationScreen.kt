package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import com.example.localconnect.ui.theme.*
import com.example.localconnect.data.FirebaseRepository
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Job
import com.example.localconnect.data.JobApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationScreen(
    job: Job,
    session: UserSession,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isSubmitting by remember { mutableStateOf(false) }

    // Form States
    var fullName by remember { mutableStateOf(session.name.lowercase()) }
    androidx.compose.runtime.LaunchedEffect(session.language.code) {
        if (session.language.code != "en" && fullName.equals(session.name, ignoreCase = true)) {
            com.example.localconnect.utils.TranslationManager.translate(session.name, session.language.code) { translatedName ->
            fullName = translatedName
        }
        }
    }
    var phone by remember { mutableStateOf(session.phone) }
    var age by remember { mutableStateOf("") }
    
    // Gender Dropdown
    var expandedGender by remember { mutableStateOf(false) }
    val defaultGender = androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.gender_male)
    var gender by remember(defaultGender) { mutableStateOf(defaultGender) }
    val genders = listOf(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.gender_male), androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.gender_female), androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.gender_other))

    // Experience Toggle
    var hasExperience by remember { mutableStateOf(true) }
    
    // Exp Years Dropdown
    var expandedYears by remember { mutableStateOf(false) }
    val defaultExp = androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.exp_select)
    var expYears by remember(defaultExp) { mutableStateOf(defaultExp) }
    val yearsOptions = listOf(
        androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.exp_select),
        androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.exp_0_1),
        androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.exp_1_3),
        androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.exp_3_plus)
    )

    // Language Radio
    var selectedLang by remember { mutableStateOf("Hindi") }

    // Introduction
    var introduction by remember { mutableStateOf("") }
    var firstDocUri by remember { mutableStateOf<String>("") }
    var confirmed by remember { mutableStateOf(false) }

    var currentPickingIndex by remember { mutableStateOf(-1) }
    var docUris by remember { mutableStateOf<Map<Int, Uri>>(emptyMap()) }
    var showDocUploadDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val defaultResumeDoc = androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.doc_resume)
    val requiredDocs = remember(job.documentsRequired, defaultResumeDoc) {
        if (job.documentsRequired.isNotEmpty()) job.documentsRequired else listOf(defaultResumeDoc, "ID Proof")
    }

    // Helper: create a temp file URI for camera capture
    fun createTempDocUri(): Uri {
        val dir = File(context.cacheDir, "doc_captures")
        dir.mkdirs()
        val file = File.createTempFile("doc_", ".jpg", dir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // Camera launcher — captures a photo and stores it in docUris
    val cameraDocLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPickingIndex != -1 && tempCameraUri != null) {
            docUris = docUris + (currentPickingIndex to tempCameraUri!!)
            if (firstDocUri.isEmpty()) firstDocUri = tempCameraUri.toString()
        }
        currentPickingIndex = -1
        tempCameraUri = null
    }

    // Gallery / file picker launcher — picks any document or image
    val docLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && currentPickingIndex != -1) {
            docUris = docUris + (currentPickingIndex to uri)
            if (firstDocUri.isEmpty()) firstDocUri = uri.toString()
        }
        currentPickingIndex = -1
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            Surface(
                color = OrangePrimary,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${job.title}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${job.companyName}  ${job.location}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha=0.9f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${job.salary}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Surface(
                            color = PrimaryContainer,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.clickable { onBack() }.padding(start = 12.dp)
                        ) {
                            Text(stringResource(R.string.back),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF374151)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB))
                    ) {
                        Text(stringResource(R.string.cancel), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSubmitting = true
                                
                                if (fullName.isBlank() || phone.isBlank() || age.isBlank()) {
                                    android.widget.Toast.makeText(context.applicationContext, "Please fill all mandatory fields (*)", android.widget.Toast.LENGTH_SHORT).show()
                                    isSubmitting = false
                                    return@launch
                                }
                                
                                if (docUris.size < requiredDocs.size) {
                                    android.widget.Toast.makeText(context.applicationContext, "Please upload all required documents (${requiredDocs.size} required)", android.widget.Toast.LENGTH_SHORT).show()
                                    isSubmitting = false
                                    return@launch
                                }
                                val uploadedDocsMap = mutableMapOf<String, String>()
                                var finalDocUrl = ""

                                for ((index, uri) in docUris) {
                                    if (index < requiredDocs.size) {
                                        val docName = requiredDocs[index]
                                        val uriStr = uri.toString()
                                        if (uriStr.startsWith("content://")) {
                                            try {
                                                val url = FirebaseRepository.uploadDocument(uri, context)
                                                uploadedDocsMap[docName] = url
                                                if (finalDocUrl.isEmpty()) finalDocUrl = url
                                            } catch (e: Exception) {
                                                val errorMsg = e.localizedMessage ?: "Unknown Firebase Storage error"
                                                Toast.makeText(context.applicationContext, "Upload failed for $docName: $errorMsg", Toast.LENGTH_LONG).show()
                                                isSubmitting = false
                                                return@launch
                                            }
                                        } else {
                                            uploadedDocsMap[docName] = uriStr
                                            if (finalDocUrl.isEmpty()) finalDocUrl = uriStr
                                        }
                                    }
                                }

                                val newApp = JobApplication(
                                    seekerId = session.firebaseUid,
                                    jobId = job.id,
                                    employerId = job.employerId,
                                    jobTitle = job.title,
                                    companyName = job.companyName,
                                    location = job.location,
                                    salary = job.salary,
                                    applicantName = fullName,
                                    applicantPhone = phone,
                                    applicantAge = age,
                                    applicantGender = gender,
                                    applicantExperience = if (hasExperience) expYears else "No Experience",
                                    applicantIntro = introduction,
                                    uploadedDocumentUri = finalDocUrl,
                                    uploadedDocuments = uploadedDocsMap,
                                    appliedOnDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                                    appliedOn = System.currentTimeMillis()
                                )
                                val success = FirebaseRepository.applyForJob(newApp)
                                isSubmitting = false
                                if (success) {
                                    Toast.makeText(context.applicationContext, "Application submitted!", Toast.LENGTH_SHORT).show()
                                    onSubmit()
                                } else {
                                    Toast.makeText(context.applicationContext, "Failed to submit application", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White
                        ),
                        enabled = !isSubmitting
                    ) {
                        Text(if (isSubmitting) "Submitting..." else "Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // --- Personal Details ---
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.personal_details), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(bottom = 16.dp))
                    
                    Text(stringResource(R.string.full_name) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text(stringResource(R.string.enter_full_name), color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = Primary
                        )
                    )
                    
                    Text(stringResource(R.string.phone_number) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text(stringResource(R.string.enter_phone), color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = Primary
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.age) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                placeholder = { Text("00", color = Color.Gray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedBorderColor = Primary
                                )
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.gender) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                            ExposedDropdownMenuBox(
                                expanded = expandedGender,
                                onExpandedChange = { expandedGender = it }
                            ) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF111827)),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color(0xFFD1D5DB),
                                        focusedBorderColor = Primary
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedGender,
                                    onDismissRequest = { expandedGender = false },
                                    containerColor = Color.White
                                ) {
                                    genders.forEach { sel ->
                                        DropdownMenuItem(
                                            text = { Text(sel, fontWeight = FontWeight.Medium, color = Color(0xFF111827)) },
                                            onClick = {
                                                gender = sel
                                                expandedGender = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Work Experience ---
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.work_exp) + " *", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(bottom = 16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                        Button(
                            onClick = { hasExperience = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasExperience) Primary else Color.Transparent,
                                contentColor = if (hasExperience) Color.White else Color(0xFF374151)
                            ),
                            border = if (!hasExperience) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)) else null
                        ) {
                            Text(stringResource(R.string.yes), fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { hasExperience = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!hasExperience) Primary else Color.Transparent,
                                contentColor = if (!hasExperience) Color.White else Color(0xFF374151)
                            ),
                            border = if (hasExperience) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)) else null
                        ) {
                            Text(stringResource(R.string.no_btn), fontWeight = FontWeight.Medium)
                        }
                    }

                    if (hasExperience) {
                        ExposedDropdownMenuBox(
                            expanded = expandedYears,
                            onExpandedChange = { expandedYears = it }
                        ) {
                            OutlinedTextField(
                                value = expYears,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYears) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF111827)),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedBorderColor = Primary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedYears,
                                onDismissRequest = { expandedYears = false },
                                containerColor = Color.White
                            ) {
                                yearsOptions.forEach { sel ->
                                    DropdownMenuItem(
                                        text = { Text(sel, fontWeight = FontWeight.Medium, color = Color(0xFF111827)) },
                                        onClick = {
                                            expYears = sel
                                            expandedYears = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Documents ---
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.documents) + " *", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(bottom = 16.dp))
                    
                    requiredDocs.forEachIndexed { index, docName ->
                        val docUri = docUris[index]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if(docUri != null) Color(0xFF10B981) else Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                                .clickable {
                                    // Show Camera / Gallery dialog instead of opening gallery directly
                                    currentPickingIndex = index
                                    showDocUploadDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (docUri != null) {
                                Text("${androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.uploaded)}: $docName", fontSize = 14.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            } else {
                                Text("${androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.upload_document)} $docName", fontSize = 14.sp, color = Color(0xFF6B7280))
                            }
                        }
                        if (index < requiredDocs.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    
                    Text(stringResource(R.string.pref_lang) + " *", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(top = 24.dp, bottom = 16.dp))
                    
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedLang == "Hindi", onClick = { selectedLang = "Hindi" }, modifier = Modifier.size(32.dp))
                            Text(stringResource(R.string.hindi_lang), fontSize = 13.sp, maxLines = 1)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedLang == "Bengali", onClick = { selectedLang = "Bengali" }, modifier = Modifier.size(32.dp))
                            Text(stringResource(R.string.bengali_lang), fontSize = 13.sp, maxLines = 1)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedLang == "English", onClick = { selectedLang = "English" }, modifier = Modifier.size(32.dp))
                            Text(stringResource(R.string.english_lang), fontSize = 13.sp, maxLines = 1)
                        }
                    }
                }
            }

            // --- Introduction ---
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.introduction), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(bottom = 16.dp))
                    
                    OutlinedTextField(
                        value = introduction,
                        onValueChange = { introduction = it },
                        placeholder = { Text(stringResource(R.string.tell_us_exp_dots), color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = Primary
                        )
                    )
                    
                    Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = Alignment.Top) {
                        Checkbox(
                            checked = confirmed,
                            onCheckedChange = { confirmed = it },
                            modifier = Modifier.padding(end = 8.dp).size(24.dp)
                        )
                        Text(stringResource(R.string.confirm_app_correct),
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Camera / Gallery chooser dialog for document upload
    if (showDocUploadDialog) {
        AlertDialog(
            onDismissRequest = {
                showDocUploadDialog = false
                currentPickingIndex = -1
            },
            title = {
                Text(
                    text = stringResource(R.string.upload_photo),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.choose_photo_upload),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDocUploadDialog = false
                    val uri = createTempDocUri()
                    tempCameraUri = uri
                    cameraDocLauncher.launch(uri)
                }) {
                    Text(text = stringResource(R.string.camera_opt), color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDocUploadDialog = false
                    docLauncher.launch("*/*")
                }) {
                    Text(text = stringResource(R.string.gallery_opt), color = Primary)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}
