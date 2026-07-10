package com.example.localconnect

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


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.ServiceTask
import com.example.localconnect.data.ServiceRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookServiceScreen(session: UserSession, service: ServiceTask, onBack: () -> Unit) {
    val strings = getStrings(session.language.code)
    val coroutineScope = rememberCoroutineScope()
    var isBooking by remember { mutableStateOf(false) }
    var selectedServices by remember { mutableStateOf(setOf<String>()) }
    var problemDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(strings.today) }
    var selectedTimeSlot by remember { mutableStateOf("") }
    var showCustomerDetails by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // Media Upload State
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempVideoUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoUploadDialog by remember { mutableStateOf(false) }
    var showVideoUploadDialog by remember { mutableStateOf(false) }

    fun createTempFileUri(isVideo: Boolean): Uri {
        val dir = File(context.cacheDir, if (isVideo) "videos" else "pictures")
        dir.mkdirs()
        val ext = if (isVideo) ".mp4" else ".jpg"
        val file = File.createTempFile("capture_", ext, dir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) photoUri = tempImageUri
    }
    val pickPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) photoUri = uri
    }
    val takeVideoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) videoUri = tempVideoUri
    }
    val pickVideoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) videoUri = uri
    }

    // DatePicker State
    var showDatePicker by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }}
    val todayMillis = remember { calendar.timeInMillis }
    val maxDateMillis = remember {
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        calendar.timeInMillis
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in todayMillis..maxDateMillis
            }
        }
    )
    
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }}

    // Dummy Sub-services for prototype
    val subServices = listOf(
        "Pipe Leakage Repair", "Tap Installation", "Bathroom Fitting Repair",
        "Kitchen Sink Repair", "Drainage Blockage", "Water Tank Pipe Repair", "Other"
    )

    val dates = listOf(strings.today, strings.tomorrow, strings.chooseDate)
    val timeSlots = listOf("9 AM - 11 AM", "11 AM - 1 PM", "2 PM - 4 PM", "4 PM - 6 PM", "6 PM - 8 PM")

    fun toggleService(serviceName: String) {
        selectedServices = if (selectedServices.contains(serviceName)) {
            selectedServices - serviceName
        } else {
            selectedServices + serviceName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = strings.bookServiceTitle, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 20.sp, 
                        color = androidx.compose.ui.graphics.Color.White
                    ) 
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text(strings.back, fontWeight = FontWeight.SemiBold, color = androidx.compose.ui.graphics.Color.White, fontSize = 14.sp)
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
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                    ) {
                        Text(strings.cancel, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showCustomerDetails = true },
                        modifier = Modifier.weight(2f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary) // Blue-600
                    ) {
                        Text(strings.bookServiceBtn, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("→", fontSize = 20.sp, color = Color.White)
                    }
                }
            }
        },
        containerColor = Color(0xFFF3F4F6) // bg-gray-100
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            
            // 1. Booking For
            Column(modifier = Modifier.background(PrimaryContainer).padding(16.dp)) { // bg-blue-50/50 roughly
                Text(strings.bookingFor.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(56.dp).background(PrimaryContainer, CircleShape), // bg-blue-100
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.DarkGray)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.serviceTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                            Text(service.providerName, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.DarkGray)
                            
                            Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFFFF7ED), // green-50
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFF97316))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${service.rating} Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${service.distanceKm} km away", fontSize = 12.sp, color = Color.DarkGray)
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(strings.visitCharge, fontSize = 14.sp, color = Color.Gray)
                                Text("₹${service.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }

            
            // 3. Problem Details
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(strings.problemDetails.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp)
                Text(strings.describeProblem, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
                
                OutlinedTextField(
                    value = problemDescription,
                    onValueChange = { problemDescription = it },
                    placeholder = { Text(strings.writeIssueHint, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    )
                )
                
                Surface(
                    color = PrimaryContainer, // bg-blue-50
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(16.dp).padding(top = 2.dp), tint = OnPrimaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.example} ${strings.exampleDesc}",
                            fontSize = 12.sp,
                            color = OnPrimaryContainer, // text-blue-800
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            HorizontalDivider(color = Color(0xFFF3F4F6))
            
            // 4. Upload Photo / Video
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(strings.uploadMedia.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Photo Upload (Dummy)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                            .border(2.dp, if(photoUri != null) Color(0xFFF97316) else Color(0xFFD1D5DB), RoundedCornerShape(12.dp))
                            .clickable { showPhotoUploadDialog = true }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (photoUri != null) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color(0xFFF97316))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Photo Selected", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316), textAlign = TextAlign.Center)
                        } else {
                            Surface(shape = CircleShape, color = Color.White, shadowElevation = 1.dp) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(24.dp).padding(4.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(strings.uploadPhoto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, textAlign = TextAlign.Center)
                        }
                    }
                    
                    // Video Upload (Dummy)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                            .border(2.dp, if(videoUri != null) Color(0xFFF97316) else Color(0xFFD1D5DB), RoundedCornerShape(12.dp))
                            .clickable { showVideoUploadDialog = true }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (videoUri != null) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color(0xFFF97316))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Video Selected", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316), textAlign = TextAlign.Center)
                        } else {
                            Surface(shape = CircleShape, color = Color.White, shadowElevation = 1.dp) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp).padding(4.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${strings.uploadVideo}\n${strings.optional}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            
            HorizontalDivider(color = Color(0xFFF3F4F6))

            // 5. Select Date
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(strings.selectDate.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp)
                Text(strings.whenNeedService, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    dates.forEach { date ->
                        val isSelected = selectedDate == date
                        Surface(
                            modifier = Modifier.weight(1f).height(60.dp).clickable { 
                                selectedDate = date 
                                if (date == strings.chooseDate) {
                                    showDatePicker = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Primary else Color.White,
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            shadowElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (date == strings.chooseDate) {
                                    Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(date, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else Color.DarkGray)
                            }
                        }
                    }
                }
                
                Surface(
                    color = Color(0xFFF9FAFB),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF3F4F6)),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.selectedDateLabel, fontSize = 14.sp, color = Color.DarkGray)
                        }
                        val dateText = if (selectedDate == strings.chooseDate) {
                            if (datePickerState.selectedDateMillis != null) {
                                dateFormatter.format(java.util.Date(datePickerState.selectedDateMillis!!))
                            } else {
                                strings.notSelected
                            }
                        } else if (selectedDate == strings.today) {
                            dateFormatter.format(java.util.Date(todayMillis))
                        } else {
                            dateFormatter.format(java.util.Date(todayMillis + 86400000L))
                        }
                        Text(
                            text = dateText, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.Black
                        )
                    }
                }
            }
            
            HorizontalDivider(color = Color(0xFFF3F4F6))
            
            // 6. Select Time Slot
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(strings.selectTimeSlot.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp)
                Text(strings.availableTimeSlots, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 2,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    timeSlots.forEach { slot ->
                        val isSelected = selectedTimeSlot == slot
                        Surface(
                            modifier = Modifier.weight(1f).height(50.dp).clickable { selectedTimeSlot = slot },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Primary else Color.White,
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            shadowElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isSelected) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.DarkGray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(slot, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else Color.DarkGray)
                            }
                        }
                    }
                }
            }
            
            // 7. Price Details
            Column(modifier = Modifier.background(Color(0xFFF9FAFB)).padding(20.dp).fillMaxWidth()) {
                Text(strings.priceDetails.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 16.dp))
                
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFF3F4F6)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.visitCharge, fontSize = 14.sp, color = Color.DarkGray)
                            Text("₹${service.price}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.serviceCharge, fontSize = 14.sp, color = Color.DarkGray)
                            Text(strings.decidedAfterInspection, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.materialCost, fontSize = 14.sp, color = Color.DarkGray)
                            Text(strings.extraIfRequired, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE5E7EB)) // border-dashed effectively
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.totalPayableNow, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("₹${service.price}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Primary)
                        }
                    }
                }
                
                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = "${strings.note} ${strings.priceNoteDesc}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }

    // Dialogs
    if (showPhotoUploadDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoUploadDialog = false },
            title = { Text("Upload Photo") },
            text = { Text("Choose an option to upload your photo") },
            confirmButton = {
                TextButton(onClick = { 
                    showPhotoUploadDialog = false
                    tempImageUri = createTempFileUri(false)
                    takePhotoLauncher.launch(tempImageUri!!)
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoUploadDialog = false
                    pickPhotoLauncher.launch("image/*")
                }) { Text("Gallery") }
            }
        )
    }

    if (showVideoUploadDialog) {
        AlertDialog(
            onDismissRequest = { showVideoUploadDialog = false },
            title = { Text("Upload Video") },
            text = { Text("Choose an option to upload your video") },
            confirmButton = {
                TextButton(onClick = { 
                    showVideoUploadDialog = false
                    tempVideoUri = createTempFileUri(true)
                    takeVideoLauncher.launch(tempVideoUri!!)
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showVideoUploadDialog = false
                    pickVideoLauncher.launch("video/*")
                }) { Text("Gallery") }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCustomerDetails) {
        CustomerDetailsScreen(
            session = session,
            service = service,
            selectedDate = selectedDate,
            selectedTimeSlot = selectedTimeSlot,
            selectedServices = selectedServices,
            onBack = { showCustomerDetails = false },
            onConfirm = { showCustomerDetails = false }
        )
    }
}
