package com.example.localconnect

import androidx.compose.ui.res.stringResource

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.ServiceTask
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import androidx.compose.material.icons.filled.MyLocation
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostServiceScreen(
    session: UserSession,
    onBack: () -> Unit,
    onServicePosted: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val targetLanguage = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]?.language ?: "en"
    
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.entries.any { it.value }
            if (!granted) {
                Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }
    )
    
    val primaryColor = Color(0xFFF97316)
    val primaryDarkColor = Color(0xFFEA580C)
    val lightOrangeColor = Color(0xFFFFF7ED)

    val predefinedDescriptions = mapOf(
        "Plumber" to "Expert plumbing services including pipe leakage repair, tap installation, and bathroom fittings.",
        "Electrician" to "Reliable electrical repairs for wiring, fans, lights, switchboards, and home appliances.",
        "Mechanic" to "Professional mechanic services for two-wheelers and four-wheelers, regular servicing, and breakdown assistance.",
        "Carpenter" to "Skilled carpentry for furniture assembly, door fixing, custom woodwork, and general repairs.",
        "Painter" to "Quality painting services for interior and exterior walls, including touch-ups and fresh coats.",
        "Cleaning" to "Thorough house cleaning, sweeping, mopping, and deep cleaning services."
    )

    var isMapScrollEnabled by remember { mutableStateOf(true) }

    // Form States
    var providerName by remember { mutableStateOf(session.name.lowercase()) }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    var visitingFees by remember { mutableStateOf("") }
    var hasFeesPerKm by remember { mutableStateOf(false) }
    var feesPerKm by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var locality by remember { mutableStateOf("") }
    var serviceRadius by remember { mutableStateOf("") }
    val availableDays = remember { mutableStateListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }
    var availableFrom by remember { mutableStateOf("09:00") }
    var availableTo by remember { mutableStateOf("18:00") }
    var experience by remember { mutableStateOf("3 Years") }
    var pincode by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    
    var isPosting by remember { mutableStateOf(false) }
        
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(primaryColor)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.post_service_title), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text(stringResource(R.string.offer_service_nearby), fontSize = 12.sp, color = Color(0xFFFFEDD5))
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = {
                            if (providerName.isBlank() || category.isBlank() || description.isBlank() || visitingFees.isBlank() || city.isBlank() || locality.isBlank() || pincode.isBlank() || serviceRadius.isBlank() || availableDays.isEmpty()) {
                                Toast.makeText(context.applicationContext, "Please fill in all mandatory fields (*)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isPosting = true
                            coroutineScope.launch {
                                val newService = ServiceTask(
                                    providerId = session.phone,
                                    providerName = providerName.ifBlank { session.name },
                                    serviceTitle = category,
                                    category = category,
                                    location = "$city, $locality",
                                    price = visitingFees.toIntOrNull() ?: 0,
                                    pricingType = "Visiting Fees",
                                    feesPerKm = feesPerKm.toIntOrNull() ?: 0,
                                    city = city,
                                    locality = locality,
                                    serviceRadius = serviceRadius,
                                    latitude = latitude,
                                    longitude = longitude,
                                    availableDays = availableDays.toList(),
                                    availableFrom = availableFrom,
                                    availableTo = availableTo,
                                    experience = experience,
                                    pincode = pincode,
                                    description = description,
                                    timestamp = System.currentTimeMillis()
                                )
                                val success = FirebaseRepository.postService(newService)
                                isPosting = false
                                if (success) {
                                    Toast.makeText(context.applicationContext, "Service Posted!", Toast.LENGTH_SHORT).show()
                                    onServicePosted()
                                } else {
                                    Toast.makeText(context.applicationContext, "Failed to post service", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = !isPosting
                    ) {
                        Text(if (isPosting) "Posting..." else "Post Service", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(), enabled = isMapScrollEnabled)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            
            // 1. SERVICE DETAILS
            SectionGroup(stringResource(R.string.sg_service_details), primaryColor, lightOrangeColor) {
                // Provider Name
                CustomTextField(stringResource(R.string.prov_bus_name), stringResource(R.string.ph_raju), providerName) { providerName = it }

                // Category
                CustomDropdown(stringResource(R.string.srv_category), listOf("Plumber", "Electrician", "Mechanic", "Carpenter", "Painter", "Cleaning"), category) { newCat -> 
                    category = newCat 
                    val rawDesc = predefinedDescriptions[newCat] ?: ""
                    com.example.localconnect.utils.TranslationManager.translate(rawDesc, targetLanguage) { res ->
                        description = res
                    }
                }
                
                // Description
                CustomTextField(stringResource(R.string.short_desc), stringResource(R.string.ph_pipe), description, singleLine = false, lines = 3) { description = it }
            }

            // 2. PRICING
            SectionGroup(stringResource(R.string.sg_pricing), primaryColor, lightOrangeColor) {
                CustomTextField(stringResource(R.string.visit_fees), stringResource(R.string.ph_500), visitingFees, keyboardType = KeyboardType.Number) { visitingFees = it }
                

            }

            // 3. SERVICE LOCATION
            SectionGroup("SERVICE LOCATION", primaryColor, lightOrangeColor) {
                Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                LocationSelectorMap(
                    addressQuery = "$locality $city $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    onMapTouch = { isMapScrollEnabled = !it },
                    contentBelowMap = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                onClick = {
                                    val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    if (hasFineLocation || hasCoarseLocation) {
                                        isFetchingLocation = true
                                        try {
                                            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                                                if (location != null) {
                                                    latitude = location.latitude
                                                    longitude = location.longitude
                                                    try {
                                                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                                        if (!addresses.isNullOrEmpty()) {
                                                            val addr = addresses[0]
                                                            val newCity = addr.locality ?: addr.subAdminArea ?: ""
                                                            val pin = addr.postalCode ?: ""
                                                            val street = addr.thoroughfare ?: addr.subLocality ?: ""
                                                            if (street.isNotBlank()) locality = street
                                                            if (newCity.isNotBlank()) city = newCity
                                                            if (pin.isNotBlank()) pincode = pin
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Location not available. Try opening Google Maps first.", Toast.LENGTH_SHORT).show()
                                                }
                                                isFetchingLocation = false
                                            }.addOnFailureListener {
                                                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                                                isFetchingLocation = false
                                            }
                                        } catch (e: SecurityException) {
                                            isFetchingLocation = false
                                        }
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        )
                                    }
                                }
                            ) {
                                if (isFetchingLocation) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(stringResource(R.string.use_current_location), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    contentBeforeSave = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(stringResource(R.string.city_label), stringResource(R.string.ph_kol), city) { city = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(stringResource(R.string.area_loc), stringResource(R.string.ph_salt), locality) { locality = it }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(stringResource(R.string.pincode_label), stringResource(R.string.ph_pin), pincode, keyboardType = KeyboardType.Number) { pincode = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CustomDropdown(stringResource(R.string.srv_radius), listOf("2 km", "5 km", "10 km", "20 km"), serviceRadius) { serviceRadius = it }
                            }
                        }
                    },
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->
                        latitude = lat
                        longitude = lon
                        if (forceOverwrite) {
                            if (street.isNotBlank()) locality = street
                            if (newCity.isNotBlank()) city = newCity
                            if (pin.isNotBlank()) pincode = pin
                        } else {
                            if (locality.isBlank()) locality = street
                            if (city.isBlank()) city = newCity
                            if (pincode.isBlank()) pincode = pin
                        }
                    }
                )
            }

            // 4. AVAILABILITY
            SectionGroup(stringResource(R.string.sg_availability), primaryColor, lightOrangeColor) {
                Text(stringResource(R.string.available_on), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { day ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp).clickable {
                            if (availableDays.contains(day)) availableDays.remove(day) else availableDays.add(day)
                        }) {
                            Checkbox(
                                checked = availableDays.contains(day),
                                onCheckedChange = { checked -> if (checked) availableDays.add(day) else availableDays.remove(day) },
                                colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                            )
                            Text(day, fontSize = 14.sp)
                        }
                    }
                }
                val timeOptions = listOf("06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdown(stringResource(R.string.from_label), timeOptions, availableFrom) { availableFrom = it }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdown(stringResource(R.string.to_label), timeOptions, availableTo) { availableTo = it }
                    }
                }
            }

            // 5. EXPERIENCE & CONTACT
            SectionGroup(stringResource(R.string.sg_experience), primaryColor, lightOrangeColor) {
                CustomDropdown(stringResource(R.string.experience_label), listOf("1 Year", "2 Years", "3 Years", "5+ Years"), experience) { experience = it }
            }

            // 6. PREVIEW CARD
            SectionGroup("PREVIEW", primaryColor, lightOrangeColor) {
                Surface(
                    color = lightOrangeColor,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Text(category.ifBlank { "Service Category" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937), modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = primaryColor, shape = CircleShape) {
                                Text(category, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("📍 ${locality.ifBlank { "Area" }}, ${city.ifBlank { "City" }}", fontSize = 14.sp, color = Color(0xFF4B5563))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Visiting Fee: ₹${visitingFees.ifBlank { "0" }}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryDarkColor)
                            if (hasFeesPerKm) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ ₹${feesPerKm.ifBlank { "0" }}/km", fontSize = 14.sp, color = primaryDarkColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("⭐ Exp: $experience", fontSize = 12.sp, color = Color.Gray)
                            Text("🕒 ${availableDays.joinToString(", ").takeIf { it.isNotBlank() } ?: "None"}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // 7. DISCLAIMER
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Disclaimer: Posting fake or misleading services will result in an immediate account ban.",
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SectionGroup(title: String, primaryColor: Color, lightOrangeColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 4.dp))
        Divider(color = lightOrangeColor, thickness = 2.dp, modifier = Modifier.padding(bottom = 16.dp))
        content()
    }
}

@Composable
fun CustomTextField(
    label: String,
    placeholder: String,
    value: String,
    singleLine: Boolean = true,
    lines: Int = 1,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("$label *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            singleLine = singleLine,
            minLines = lines,
            maxLines = if(singleLine) 1 else lines,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF97316),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                disabledContainerColor = Color(0xFFF9FAFB),
                disabledBorderColor = Color(0xFFE5E7EB),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var displayOption by remember { mutableStateOf(selectedOption) }
    val targetLanguage = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]?.language ?: "en"
    
    androidx.compose.runtime.LaunchedEffect(selectedOption, targetLanguage) {
        com.example.localconnect.utils.TranslationManager.translate(selectedOption, targetLanguage) { res ->
            displayOption = res
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("$label *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = displayOption,
                onValueChange = { 
                    displayOption = it
                    onOptionSelected(it)
                    expanded = true 
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF97316),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            if (options.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White).heightIn(max = 200.dp)
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { com.example.localconnect.ui.TranslatedText(selectionOption, color = Color.Black) },
                            onClick = {
                                onOptionSelected(selectionOption)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
