package com.example.localconnect

import com.example.localconnect.R
import androidx.compose.ui.res.stringResource

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.clip
import android.net.Uri
import coil.compose.AsyncImage


import com.example.localconnect.ui.theme.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.ServiceTask
import com.example.localconnect.data.ServiceRequest
import com.example.localconnect.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    session: UserSession,
    service: ServiceTask,
    selectedDate: String,
    selectedTimeSlot: String,
    selectedServices: Set<String>,
    problemDescription: String,
    photoUri: Uri? = null,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    // Instead of AppStrings, we use simple mapped strings or hardcoded for the prototype
    val langCode = session.language.code
    val isHi = langCode == "hi"
    val isBn = langCode == "bn"

    val customerDetailsTitle = if (isHi) "ग्राहक विवरण" else if (isBn) "গ্রাহকের বিবরণ" else "Customer Details"
    val bookingSummary = if (isHi) "बुकिंग सारांश" else if (isBn) "বুকিং সারসংক্ষেপ" else "Booking Summary"
    val serviceCategory = if (isHi) "सेवा" else if (isBn) "সেবা" else "Service"
    val selectedWork = if (isHi) "चुना गया काम" else if (isBn) "নির্বাচিত কাজ" else "Selected Work"
    val providerNameStr = if (isHi) "प्रदाता" else if (isBn) "প্রদানকারী" else "Provider"
    val dateTimeLabel = if (isHi) "तारीख और समय" else if (isBn) "তারিখ এবং সময়" else "Date & Time"
    val visitChargeStr = if (isHi) "विजिट शुल्क:" else if (isBn) "ভিজিট চার্জ:" else "Visit Charge:"
    val cancelStr = if (isHi) "रद्द करें" else if (isBn) "বাতিল করুন" else "Cancel"
    val confirmBookingStr = if (isHi) "बुकिंग पक्की करें" else if (isBn) "বুকিং নিশ্চিত করুন" else "Confirm Booking"
    val fullNameStr = if (isHi) "पूरा नाम" else if (isBn) "সম্পূর্ণ নাম" else "Full Name"
    val phoneStr = if (isHi) "फ़ोन नंबर" else if (isBn) "ফোন নম্বর" else "Phone Number"
    val altPhoneStr = if (isHi) "वैकल्पिक फ़ोन नंबर" else if (isBn) "বিকল্প ফোন নম্বর" else "Alternate Phone Number"
    val optionalStr = if (isHi) "वैकल्पिक" else if (isBn) "ঐচ্ছিক" else "Optional"
    val serviceLocationStr = if (isHi) "सेवा का स्थान" else if (isBn) "পরিষেবার অবস্থান" else "Service Location"
    val addressLabelStr = if (isHi) "पता" else if (isBn) "ঠিকানা" else "Address"
    val landmarkLabelStr = if (isHi) "लैंडमार्क" else if (isBn) "ল্যান্ডমার্ক" else "Landmark"
    val cityAreaStr = if (isHi) "शहर / क्षेत्र" else if (isBn) "শহর / এলাকা" else "City / Area"
    val contactPrefStr = if (isHi) "संपर्क प्राथमिकता" else if (isBn) "যোগাযোগের পছন্দ" else "Contact Preference"
    val contactPrefDescStr = if (isHi) "प्रदाता आपसे कैसे संपर्क करे?" else if (isBn) "কিভাবে প্রদানকারী আপনার সাথে যোগাযোগ করবে?" else "How should the provider contact you?"
    val langPrefStr = if (isHi) "भाषा प्राथमिकता" else if (isBn) "ভাষার পছন্দ" else "Language Preference"
    val langPrefDescStr = if (isHi) "पसंदीदा भाषा चुनें:" else if (isBn) "পছন্দের ভাষা নির্বাচন করুন:" else "Choose preferred language:"
    val specialInstructionsStr = if (isHi) "विशेष निर्देश" else if (isBn) "বিশেষ নির্দেশাবলী" else "Special Instructions"
    val safetyConfStr = if (isHi) "सुरक्षा पुष्टि" else if (isBn) "নিরাপত্তা নিশ্চিতকরণ" else "Safety Confirmation"
    val agreeShareDetailsStr = if (isHi) "मैं सेवा प्रदाता के साथ अपना फोन नंबर और पता साझा करने के लिए सहमत हूं।" else if (isBn) "আমি পরিষেবা প্রদানকারীর সাথে আমার ফোন নম্বর এবং ঠিকানা শেয়ার করতে সম্মত।" else "I agree to share my phone number and address with the service provider."
    val agreeFinalPriceStr = if (isHi) "मैं समझता/समझती हूं कि अंतिम मूल्य काम की स्थिति पर निर्भर हो सकता है।" else if (isBn) "আমি বুঝতে পেরেছি যে চূড়ান্ত মূল্য কাজের অবস্থার উপর নির্ভর করতে পারে।" else "I understand that final price may depend on work condition."
    val finalConfStr = if (isHi) "अंतिम पुष्टि" else if (isBn) "চূড়ান্ত নিশ্চিতকরণ" else "Final Confirmation"
    val payNowStr = if (isHi) "न्यूनतम भुगतान" else if (isBn) "ন্যূনতম পেমেন্ট" else "Min Pay"

    var name by remember { mutableStateOf(session.name) }
    var phone by remember { mutableStateOf(session.phone) }
    var altPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf(session.location) }
    var landmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Kharagpur") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }


    var langPref by remember { mutableStateOf("Hindi") }
    var instructions by remember { mutableStateOf("") }

    var agreeShare by remember { mutableStateOf(false) }
    var agreePrice by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    var isBooking by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
val settingResultRequest = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == android.app.Activity.RESULT_OK) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val addr = addresses[0]
                                        val fullAddress = addr.getAddressLine(0) ?: ""
                                        val cityName = addr.locality ?: addr.subAdminArea ?: ""
                                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                            address = fullAddress
                                            city = cityName
                                            latitude = location.latitude
                                            longitude = location.longitude
                                            Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        } else {
                            Toast.makeText(context, "Could not fetch location", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: SecurityException) { e.printStackTrace() }
            }
        } else {
            Toast.makeText(context, "GPS is required for current location", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkGpsAndFetchLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = com.google.android.gms.location.LocationServices.getSettingsClient(context)
        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            try {
                fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                if (!addresses.isNullOrEmpty()) {
                                    val addr = addresses[0]
                                    val fullAddress = addr.getAddressLine(0) ?: ""
                                    val cityName = addr.locality ?: addr.subAdminArea ?: ""
                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        address = fullAddress
                                        city = cityName
                                        latitude = location.latitude
                                        longitude = location.longitude
                                        Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    } else {
                        Toast.makeText(context, "Could not fetch location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) { e.printStackTrace() }
        }.addOnFailureListener { exception ->
            if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                try {
                    settingResultRequest.launch(androidx.activity.result.IntentSenderRequest.Builder(exception.resolution).build())
                } catch (sendEx: Exception) { }
            } else {
                Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                checkGpsAndFetchLocation()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun requestLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGpsAndFetchLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val contactOptions: List<Pair<String, androidx.compose.ui.graphics.vector.ImageVector>> = listOf(
        Pair("Call", Icons.Filled.Phone),
        Pair("Chat", Icons.Filled.ChatBubble),
        Pair("WhatsApp", Icons.Filled.Email)
    )

    val languageOptions = listOf("Hindi", "Bengali", "English")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        customerDetailsTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back), fontWeight = FontWeight.SemiBold, color = androidx.compose.ui.graphics.Color.White, fontSize = 14.sp)
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
                        Text(cancelStr, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            isBooking = true
                            coroutineScope.launch {
                                // One booking per user check
                                val seekerIdentifier = session.phone.takeIf { it.isNotBlank() } ?: session.firebaseUid
                                val existingBookings = com.example.localconnect.data.FirebaseRepository.getMyServiceBookings(seekerIdentifier)
                                val hasActiveBooking = existingBookings.any { it.serviceId == service.id && it.status.lowercase() != "rejected" && it.status.lowercase() != "completed" }
                                
                                if (hasActiveBooking) {
                                    android.widget.Toast.makeText(context, "You already have an active booking for this service.", android.widget.Toast.LENGTH_LONG).show()
                                    isBooking = false
                                    return@launch
                                }
                                
                                var finalImageUrl = ""
                                if (photoUri != null) {
                                    val uploadedUrl = com.example.localconnect.data.FirebaseRepository.uploadServiceImage(photoUri, context)
                                    if (uploadedUrl != null) {
                                        finalImageUrl = uploadedUrl
                                    } else {
                                        android.widget.Toast.makeText(context, "Failed to upload image. Check Firebase Storage rules or try again.", android.widget.Toast.LENGTH_LONG).show()
                                        isBooking = false
                                        return@launch
                                    }
                                }

                                val request = ServiceRequest(
                                    seekerId = seekerIdentifier,
                                    providerId = service.providerId,
                                    serviceId = service.id,
                                    serviceTitle = service.serviceTitle,
                                    status = "Pending",
                                    scheduledDate = selectedDate,
                                    scheduledTime = selectedTimeSlot,
                                    problemDescription = problemDescription, // Separated out instructions
                                    appliedOn = System.currentTimeMillis(),
                                    customerName = name,
                                    customerPhone = phone,
                                    customerAltPhone = altPhone,
                                    customerAddress = address,
                                    customerLandmark = landmark,
                                    customerCity = city,
                                    latitude = latitude,
                                    longitude = longitude,
                                    instructions = instructions,
                                    imageUrl = finalImageUrl
                                )
                                val success = com.example.localconnect.data.FirebaseRepository.bookService(request)
                                if (success) {
                                    android.widget.Toast.makeText(context, "Service Booked Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    onConfirm()
                                } else {
                                    android.widget.Toast.makeText(context, "Failed to book service.", android.widget.Toast.LENGTH_SHORT).show()
                                    isBooking = false
                                }
                            }
                        },
                        enabled = agreeShare && agreePrice && !isBooking,
                        modifier = Modifier.weight(2f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary, // Blue-600
                            disabledContainerColor = Primary.copy(alpha = 0.5f) // Blue-300
                        )
                    ) {
                        if (isBooking) {
                            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.booking_progress), fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Text(confirmBookingStr, fontWeight = FontWeight.Bold, color = Color.White)
                        }
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
            // 1. Booking Summary
            Column(modifier = Modifier.background(Color(0xFFF9FAFB)).padding(16.dp).fillMaxWidth()) {
                Text(bookingSummary.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(serviceCategory, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(service.category, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(providerNameStr, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(service.providerName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(dateTimeLabel, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(2.dp))
                                val displayDate = if (selectedDate.contains("Choose Date")) "Not Selected" else selectedDate
                                Text("$displayDate, $selectedTimeSlot", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(visitChargeStr, fontSize = 14.sp, color = Color.DarkGray)
                            Text("₹${service.price}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE5E7EB))

            // 2. Customer Details
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(customerDetailsTitle.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 16.dp))
                
                Text(fullNameStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(stringResource(R.string.enter_your_name), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(phoneStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text(stringResource(R.string.enter_phone), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(altPhoneStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    Text(optionalStr, fontSize = 12.sp, color = Color.Gray)
                }
                OutlinedTextField(
                    value = altPhone,
                    onValueChange = { altPhone = it },
                    placeholder = { Text(stringResource(R.string.enter_alt_number), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )
            }

            HorizontalDivider(color = Color(0xFFE5E7EB))

            // 3. Service Location
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(serviceLocationStr.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp)
                }

                LocationSelectorMap(
                    addressQuery = "$landmark $address $city",
                    currentLat = latitude,
                    currentLon = longitude,
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->
                        latitude = lat
                        longitude = lon
                        if (forceOverwrite) {
                            if (street.isNotBlank()) address = street
                            if (newCity.isNotBlank()) city = newCity
                        } else {
                            if (address.isBlank() && street.isNotBlank()) address = street
                            if (city.isBlank() && newCity.isNotBlank()) city = newCity
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(addressLabelStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = { Text(stringResource(R.string.enter_house_address), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(landmarkLabelStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            placeholder = { Text(stringResource(R.string.nearby_landmark), color = Color.Gray, fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF97316),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                unfocusedContainerColor = Color(0xFFF9FAFB),
                                focusedContainerColor = Color.White,
                                focusedTextColor = Color(0xFF111827),
                                unfocusedTextColor = Color(0xFF111827)
                            )
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cityAreaStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFE5E7EB),
                                disabledContainerColor = Color(0xFFF3F4F6),
                                disabledTextColor = Color.DarkGray
                            ),
                            enabled = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { requestLocation() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF7ED), contentColor = Primary)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.use_current_loc), fontWeight = FontWeight.SemiBold)
                }
            }

            HorizontalDivider(color = Color(0xFFE5E7EB))



            HorizontalDivider(color = Color(0xFFE5E7EB))

            // 5. Special Instructions
            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(specialInstructionsStr.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    placeholder = { Text(stringResource(R.string.write_extra_inst), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF7ED), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFFF7ED), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(16.dp).padding(top = 2.dp), tint = Color(0xFFF97316))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Example: Please call before coming. My house is near IIT Kharagpur main gate.",
                        fontSize = 12.sp,
                        color = Color(0xFFF97316),
                        lineHeight = 18.sp
                    )
                }
            }

            // 6. Safety Confirmation
            Column(modifier = Modifier.background(Color(0xFFF9FAFB)).padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(18.dp), tint = Primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(safetyConfStr.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp)
                }

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { agreeShare = !agreeShare }, verticalAlignment = Alignment.Top) {
                    Checkbox(checked = agreeShare, onCheckedChange = { agreeShare = it })
                    Text(agreeShareDetailsStr, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 14.dp))
                }

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).clickable { agreePrice = !agreePrice }, verticalAlignment = Alignment.Top) {
                    Checkbox(checked = agreePrice, onCheckedChange = { agreePrice = it })
                    Text(agreeFinalPriceStr, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 14.dp))
                }

                // Final Box
                Surface(
                    color = Color(0xFF0F172A), // Slate-900
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFF97316))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(finalConfStr, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(12.dp))

                        val displayDate = if (selectedDate.contains("Choose Date")) "Not Selected" else selectedDate
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.service_label), color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Text(service.category, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.date_time_label), color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Text("$displayDate, $selectedTimeSlot", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.address_label), color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Text(city, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(payNowStr, color = Color(0xFFCBD5E1), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text("₹${service.price}", color = Color(0xFFF97316), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
