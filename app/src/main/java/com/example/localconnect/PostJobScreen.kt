package com.example.localconnect

import androidx.compose.ui.res.stringResource

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.Job
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    session: UserSession,
    onBack: () -> Unit,
    onJobPosted: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPosting by remember { mutableStateOf(false) }
    var isMapScrollEnabled by remember { mutableStateOf(true) }
    // State Variables
    var employerId by remember { mutableStateOf(session.phone) }
    var employerName by remember { mutableStateOf(session.name.lowercase()) }
    var contactPersonName by remember { mutableStateOf(session.name.lowercase()) }
    var phoneNumber by remember { mutableStateOf(session.phone) }
    var jobTitle by remember { mutableStateOf("") }
    var jobCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf(stringResource(R.string.cat_cook), stringResource(R.string.cat_maid), stringResource(R.string.cat_driver), stringResource(R.string.cat_office_boy), stringResource(R.string.cat_security_guard), stringResource(R.string.cat_delivery), stringResource(R.string.cat_electrician), stringResource(R.string.cat_plumber), stringResource(R.string.cat_carpenter), stringResource(R.string.cat_painter), stringResource(R.string.cat_cleaner), stringResource(R.string.cat_mechanic), stringResource(R.string.cat_welder), stringResource(R.string.cat_mason), stringResource(R.string.cat_technician), stringResource(R.string.cat_beautician), stringResource(R.string.cat_tutor), stringResource(R.string.cat_data_entry), stringResource(R.string.cat_accountant), stringResource(R.string.cat_sales_exec), stringResource(R.string.cat_other))
    var vacancies by remember { mutableStateOf("1") }
    var workAddress by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var isAddressFromMap by remember { mutableStateOf(false) }
    var isPinSaved by remember { mutableStateOf(false) }

    var salaryType by remember { mutableStateOf("Monthly") }
    var salaryMin by remember { mutableStateOf("30000") }
    var salaryMax by remember { mutableStateOf("") }

    val initialExp = androidx.compose.ui.res.stringResource(R.string.exp_1_3)
    var experienceRequired by remember(initialExp) { mutableStateOf(initialExp) }
    var expExpanded by remember { mutableStateOf(false) }
    val expOptions = listOf(stringResource(R.string.exp_fresher), stringResource(R.string.exp_0_1), stringResource(R.string.exp_1_3), stringResource(R.string.exp_3_plus))
    val initialEdu = androidx.compose.ui.res.stringResource(R.string.edu_12th)
    var educationRequired by remember(initialEdu) { mutableStateOf(initialEdu) }
    var eduExpanded by remember { mutableStateOf(false) }
    val eduOptions = listOf(stringResource(R.string.edu_not_required), stringResource(R.string.edu_10th), stringResource(R.string.edu_12th), stringResource(R.string.edu_graduate))
    val initialGender = androidx.compose.ui.res.stringResource(R.string.gender_male)
    var genderPreference by remember(initialGender) { mutableStateOf(initialGender) }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf(stringResource(R.string.gender_any), stringResource(R.string.gender_male), stringResource(R.string.gender_female))
    var minAge by remember { mutableStateOf("") }
    var maxAge by remember { mutableStateOf("") }

    val workingDays = remember { mutableStateListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday") }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("17:00") }
    var weeklyOff by remember { mutableStateOf("Sunday") }
    var joiningDate by remember { mutableStateOf("Immediate") }

    var jobDescription by remember { mutableStateOf("") }

    val responsibilities = remember { mutableStateListOf<String>() }
    var newRespInput by remember { mutableStateOf("") }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    var isFetchingLocation by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
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
                                city = addr.locality ?: addr.subAdminArea ?: ""
                                state = addr.adminArea ?: ""
                                pincode = addr.postalCode ?: ""
                                val street = addr.thoroughfare ?: addr.subLocality ?: ""
                                workAddress = street
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
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

        val defaultJobTemplates = mapOf(
        stringResource(R.string.cat_cook) to Pair(
            stringResource(R.string.tpl_desc_cook),
            listOf(stringResource(R.string.tpl_resp_cook_1), stringResource(R.string.tpl_resp_cook_2), stringResource(R.string.tpl_resp_cook_3), stringResource(R.string.tpl_resp_cook_4))
        ),
        stringResource(R.string.cat_maid) to Pair(
            stringResource(R.string.tpl_desc_maid),
            listOf(stringResource(R.string.tpl_resp_maid_1), stringResource(R.string.tpl_resp_maid_2), stringResource(R.string.tpl_resp_maid_3), stringResource(R.string.tpl_resp_maid_4))
        ),
        stringResource(R.string.cat_driver) to Pair(
            stringResource(R.string.tpl_desc_driver),
            listOf(stringResource(R.string.tpl_resp_driver_1), stringResource(R.string.tpl_resp_driver_2), stringResource(R.string.tpl_resp_driver_3), stringResource(R.string.tpl_resp_driver_4))
        ),
        stringResource(R.string.cat_office_boy) to Pair(
            stringResource(R.string.tpl_desc_office_boy),
            listOf(stringResource(R.string.tpl_resp_office_boy_1), stringResource(R.string.tpl_resp_office_boy_2), stringResource(R.string.tpl_resp_office_boy_3), stringResource(R.string.tpl_resp_office_boy_4))
        ),
        stringResource(R.string.cat_security_guard) to Pair(
            stringResource(R.string.tpl_desc_security_guard),
            listOf(stringResource(R.string.tpl_resp_security_guard_1), stringResource(R.string.tpl_resp_security_guard_2), stringResource(R.string.tpl_resp_security_guard_3), stringResource(R.string.tpl_resp_security_guard_4))
        ),
        stringResource(R.string.cat_delivery) to Pair(
            stringResource(R.string.tpl_desc_delivery),
            listOf(stringResource(R.string.tpl_resp_delivery_1), stringResource(R.string.tpl_resp_delivery_2), stringResource(R.string.tpl_resp_delivery_3), stringResource(R.string.tpl_resp_delivery_4))
        ),
        stringResource(R.string.cat_electrician) to Pair(
            stringResource(R.string.tpl_desc_electrician),
            listOf(stringResource(R.string.tpl_resp_electrician_1), stringResource(R.string.tpl_resp_electrician_2), stringResource(R.string.tpl_resp_electrician_3), stringResource(R.string.tpl_resp_electrician_4))
        ),
        stringResource(R.string.cat_plumber) to Pair(
            stringResource(R.string.tpl_desc_plumber),
            listOf(stringResource(R.string.tpl_resp_plumber_1), stringResource(R.string.tpl_resp_plumber_2), stringResource(R.string.tpl_resp_plumber_3), stringResource(R.string.tpl_resp_plumber_4))
        ),
        stringResource(R.string.cat_carpenter) to Pair(
            stringResource(R.string.tpl_desc_carpenter),
            listOf(stringResource(R.string.tpl_resp_carpenter_1), stringResource(R.string.tpl_resp_carpenter_2), stringResource(R.string.tpl_resp_carpenter_3), stringResource(R.string.tpl_resp_carpenter_4))
        ),
        stringResource(R.string.cat_painter) to Pair(
            stringResource(R.string.tpl_desc_painter),
            listOf(stringResource(R.string.tpl_resp_painter_1), stringResource(R.string.tpl_resp_painter_2), stringResource(R.string.tpl_resp_painter_3), stringResource(R.string.tpl_resp_painter_4))
        ),
        stringResource(R.string.cat_cleaner) to Pair(
            stringResource(R.string.tpl_desc_cleaner),
            listOf(stringResource(R.string.tpl_resp_cleaner_1), stringResource(R.string.tpl_resp_cleaner_2), stringResource(R.string.tpl_resp_cleaner_3), stringResource(R.string.tpl_resp_cleaner_4))
        ),
        stringResource(R.string.cat_mechanic) to Pair(
            stringResource(R.string.tpl_desc_mechanic),
            listOf(stringResource(R.string.tpl_resp_mechanic_1), stringResource(R.string.tpl_resp_mechanic_2), stringResource(R.string.tpl_resp_mechanic_3), stringResource(R.string.tpl_resp_mechanic_4))
        ),
        stringResource(R.string.cat_welder) to Pair(
            stringResource(R.string.tpl_desc_welder),
            listOf(stringResource(R.string.tpl_resp_welder_1), stringResource(R.string.tpl_resp_welder_2), stringResource(R.string.tpl_resp_welder_3), stringResource(R.string.tpl_resp_welder_4))
        ),
        stringResource(R.string.cat_mason) to Pair(
            stringResource(R.string.tpl_desc_mason),
            listOf(stringResource(R.string.tpl_resp_mason_1), stringResource(R.string.tpl_resp_mason_2), stringResource(R.string.tpl_resp_mason_3), stringResource(R.string.tpl_resp_mason_4))
        ),
        stringResource(R.string.cat_technician) to Pair(
            stringResource(R.string.tpl_desc_technician),
            listOf(stringResource(R.string.tpl_resp_technician_1), stringResource(R.string.tpl_resp_technician_2), stringResource(R.string.tpl_resp_technician_3), stringResource(R.string.tpl_resp_technician_4))
        ),
        stringResource(R.string.cat_beautician) to Pair(
            stringResource(R.string.tpl_desc_beautician),
            listOf(stringResource(R.string.tpl_resp_beautician_1), stringResource(R.string.tpl_resp_beautician_2), stringResource(R.string.tpl_resp_beautician_3), stringResource(R.string.tpl_resp_beautician_4))
        ),
        stringResource(R.string.cat_tutor) to Pair(
            stringResource(R.string.tpl_desc_tutor),
            listOf(stringResource(R.string.tpl_resp_tutor_1), stringResource(R.string.tpl_resp_tutor_2), stringResource(R.string.tpl_resp_tutor_3), stringResource(R.string.tpl_resp_tutor_4))
        ),
        stringResource(R.string.cat_data_entry) to Pair(
            stringResource(R.string.tpl_desc_data_entry),
            listOf(stringResource(R.string.tpl_resp_data_entry_1), stringResource(R.string.tpl_resp_data_entry_2), stringResource(R.string.tpl_resp_data_entry_3), stringResource(R.string.tpl_resp_data_entry_4))
        ),
        stringResource(R.string.cat_accountant) to Pair(
            stringResource(R.string.tpl_desc_accountant),
            listOf(stringResource(R.string.tpl_resp_accountant_1), stringResource(R.string.tpl_resp_accountant_2), stringResource(R.string.tpl_resp_accountant_3), stringResource(R.string.tpl_resp_accountant_4))
        ),
        stringResource(R.string.cat_sales_exec) to Pair(
            stringResource(R.string.tpl_desc_sales_exec),
            listOf(stringResource(R.string.tpl_resp_sales_exec_1), stringResource(R.string.tpl_resp_sales_exec_2), stringResource(R.string.tpl_resp_sales_exec_3), stringResource(R.string.tpl_resp_sales_exec_4))
        ),
        stringResource(R.string.cat_other) to Pair("", emptyList())
    )

    androidx.compose.runtime.LaunchedEffect(jobCategory) {
        if (jobCategory in defaultJobTemplates) {
            val template = defaultJobTemplates[jobCategory]!!
            jobDescription = template.first
            responsibilities.clear()
            responsibilities.addAll(template.second)
            if (jobTitle.isBlank() && jobCategory != "Other") {
                jobTitle = jobCategory
            }
        }
    }

    val documentsRequired = remember { mutableStateListOf<String>() }
    var newDocInput by remember { mutableStateOf("") }

    val benefits = remember { mutableStateListOf<String>() }
    val languagesPref = remember { mutableStateListOf("Hindi", "Bengali") }

    var isGenuineConfirmed by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    // Constants
    val orangePrimary = com.example.localconnect.ui.theme.Primary
    val grayBackground = Color(0xFFF3F4F6)
    val grayCard = Color.White
    val textPrimary = Color.Black
    val textSecondary = Color(0xFF222222)
    val borderColor = Color(0xFFD1D5DB)

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            onSurface = Color.Black,
            onSurfaceVariant = Color(0xFF222222)
        )
    ) {
        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.post_job_btn), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(stringResource(R.string.create_job_hire), fontSize = 12.sp, color = Color.White.copy(alpha=0.8f))
                    }
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF97316), titleContentColor = androidx.compose.ui.graphics.Color.White, navigationIconContentColor = androidx.compose.ui.graphics.Color.White, actionIconContentColor = androidx.compose.ui.graphics.Color.White)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = grayCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = {
                            if (!isGenuineConfirmed || !termsAccepted) {
                                Toast.makeText(context.applicationContext, "Please agree to terms and confirmation", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (jobTitle.isBlank() || city.isBlank() || salaryMin.isBlank()) {
                                Toast.makeText(context.applicationContext, "Please fill required fields (Title, City, Min Salary)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isPinSaved) {
                                Toast.makeText(context.applicationContext, "Please confirm your location by clicking 'Save Pin Location'", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isPosting = true
                            coroutineScope.launch {
                                val salaryRange = "₹$salaryMin / $salaryType"
                                val newJob = Job(
                                    employerId = session.phone,
                                    companyName = employerName,
                                    contactPersonName = contactPersonName,
                                    contactPhone = phoneNumber,
                                    title = jobTitle,
                                    jobCategory = jobCategory,
                                    location = "$city" + (if (state.isNotBlank()) ", $state" else "") + (if (pincode.isNotBlank()) " - $pincode" else ""),
                                    workAddress = workAddress,
                                    city = city,
                                    state = state,
                                    pincode = pincode,
                                    landmark = landmark,
                                    latitude = latitude ?: 0.0,
                                    longitude = longitude ?: 0.0,
                                    salary = salaryRange,
                                    salaryType = salaryType,
                                    salaryMin = salaryMin.toIntOrNull() ?: 0,
                                    salaryMax = salaryMax.toIntOrNull() ?: 0,
                                    salaryValue = salaryMin.toIntOrNull() ?: 0,
                                    experienceReq = experienceRequired,
                                    educationReq = educationRequired,
                                    genderPref = genderPreference,
                                    minAge = minAge.toIntOrNull(),
                                    maxAge = maxAge.toIntOrNull(),
                                    workingDays = workingDays.toList(),
                                    startTime = startTime,
                                    endTime = endTime,
                                    weeklyOff = weeklyOff,
                                    joiningDate = joiningDate,
                                    postingDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.ENGLISH).format(java.util.Date()),
                                    description = jobDescription,
                                    responsibilities = responsibilities.toList(),
                                    isCustomDescription = true,
                                    isCustomResponsibilities = true,
                                    documentsRequired = documentsRequired.toList(),
                                    benefits = benefits.toList(),
                                    languagesPref = languagesPref.toList(),
                                    timestamp = System.currentTimeMillis()
                                )
                                val success = FirebaseRepository.postJob(newJob)
                                isPosting = false
                                if (success) {
                                    Toast.makeText(context.applicationContext, "Job Posted!", Toast.LENGTH_SHORT).show()
                                    onJobPosted()
                                } else {
                                    Toast.makeText(context.applicationContext, "Failed to post job", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(2f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = orangePrimary),
                        enabled = !isPosting
                    ) {
                        Text(stringResource(R.string.post_job_btn), color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        containerColor = grayBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState(), enabled = isMapScrollEnabled)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Employer Info
            FormSection(stringResource(R.string.post_job_employer_info)) {
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                    value = employerName, onValueChange = { employerName = it },
                    label = { Text(stringResource(R.string.employer_company_star)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                    value = contactPersonName, onValueChange = { contactPersonName = it },
                    label = { Text(stringResource(R.string.contact_person_star)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = phoneNumber, onValueChange = { phoneNumber = it },
                    label = { Text(stringResource(R.string.phone_number_star)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Text("+91", modifier = Modifier.padding(start = 12.dp)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 2. Job Details
            FormSection(stringResource(R.string.post_job_details)) {
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = jobTitle, onValueChange = { jobTitle = it },
                    label = { Text(stringResource(R.string.job_title_star)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = jobCategory,
                        onValueChange = { 
                            jobCategory = it
                            categoryExpanded = true 
                        },
                        readOnly = false,
                        label = { Text(stringResource(R.string.category_star)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
                        shape = RoundedCornerShape(8.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    val filteredCategories = categories.filter { it.contains(jobCategory, ignoreCase = true) }
                    
                    if (filteredCategories.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(Color.White).heightIn(max = 200.dp)
                        ) {
                            filteredCategories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption, color = textPrimary) },
                                    onClick = {
                                        jobCategory = selectionOption
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Location
            FormSection(stringResource(R.string.post_job_location)) {
                Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                LocationSelectorMap(
                    addressQuery = "$workAddress $city $state $pincode",
                    isAddressFromMap = isAddressFromMap,
                    currentLat = latitude,
                    currentLon = longitude,
                    onMapTouch = { isTouch -> isMapScrollEnabled = !isTouch },
                    contentBelowMap = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = orangePrimary, contentColor = Color.White),
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
                                                    isPinSaved = false
                                                    try {
                                                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                                        if (!addresses.isNullOrEmpty()) {
                                                            val addr = addresses[0]
                                                            city = addr.locality ?: addr.subAdminArea ?: ""
                                                            state = addr.adminArea ?: ""
                                                            pincode = addr.postalCode ?: ""
                                                            val street = addr.thoroughfare ?: addr.subLocality ?: ""
                                                            workAddress = street
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
                                } else {
                                    Icon(Icons.Filled.MyLocation, contentDescription = "Current Location", modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.use_current_location), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    contentBeforeSave = {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                            value = workAddress, onValueChange = { workAddress = it; isAddressFromMap = false; isPinSaved = false },
                            label = { Text(stringResource(R.string.work_address_building)) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = city, onValueChange = { city = it; isAddressFromMap = false; isPinSaved = false },
                                label = { Text(stringResource(R.string.city_area)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = state, onValueChange = { state = it; isAddressFromMap = false; isPinSaved = false },
                                label = { Text(stringResource(R.string.state_label)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = pincode, onValueChange = { pincode = it; isAddressFromMap = false; isPinSaved = false },
                                label = { Text(stringResource(R.string.pincode_star)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = landmark, onValueChange = { landmark = it; isAddressFromMap = false; isPinSaved = false },
                                label = { Text(stringResource(R.string.landmark_opt)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                        }
                    },
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->
                        isAddressFromMap = true
                        latitude = lat
                        longitude = lon
                        if (forceOverwrite) {
                            isPinSaved = true
                            if (street.isNotBlank()) workAddress = street
                            if (newCity.isNotBlank()) city = newCity
                            if (newState.isNotBlank()) state = newState
                            if (pin.isNotBlank()) pincode = pin
                        } else {
                            isPinSaved = false
                            if (workAddress.isBlank()) workAddress = street
                            if (city.isBlank()) city = newCity
                            if (state.isBlank()) state = newState
                            if (pincode.isBlank()) pincode = pin
                        }
                    }
                )
            }

            // 4. Salary
            FormSection(stringResource(R.string.post_job_salary)) {
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = salaryMin, onValueChange = { salaryMin = it },
                    label = { Text(stringResource(R.string.monthly_salary)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
            }

            // 5. Candidate Requirements
            FormSection(stringResource(R.string.post_job_candidate_reqs)) {
                // Experience Dropdown
                ExposedDropdownMenuBox(
                    expanded = expExpanded,
                    onExpandedChange = { expExpanded = !expExpanded }
                ) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = experienceRequired,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.exp_required)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expExpanded,
                        onDismissRequest = { expExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        expOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = textPrimary) },
                                onClick = {
                                    experienceRequired = option
                                    expExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Education Dropdown
                ExposedDropdownMenuBox(
                    expanded = eduExpanded,
                    onExpandedChange = { eduExpanded = !eduExpanded }
                ) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = educationRequired,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.edu_req)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eduExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = eduExpanded,
                        onDismissRequest = { eduExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        eduOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = textPrimary) },
                                onClick = {
                                    educationRequired = option
                                    eduExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Gender Dropdown (Removed age)
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = genderPreference,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.gender_pref)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = textPrimary) },
                                onClick = {
                                    genderPreference = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 7. Description
            FormSection(stringResource(R.string.post_job_description)) {
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = jobDescription, onValueChange = { jobDescription = it },
                    label = { Text(stringResource(R.string.desc_job_role_env)) },
                    modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(8.dp)
                )
            }

            // 8. Skills & Responsibilities
            FormSection(stringResource(R.string.post_job_skills)) {
                Text(stringResource(R.string.key_resp_skills), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                responsibilities.forEach { res ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(Icons.Filled.Check, null, tint=orangePrimary, modifier=Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(res, color = textSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { responsibilities.remove(res) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = newRespInput, onValueChange = { newRespInput = it },
                        placeholder = { Text(stringResource(R.string.add_skill_resp)) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if(newRespInput.isNotBlank()) { responsibilities.add(newRespInput); newRespInput = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) { Text(stringResource(R.string.add_btn), color = textPrimary) }
                }
            }

            // 9. Documents Required
            FormSection(stringResource(R.string.post_job_docs)) {
                Text(stringResource(R.string.docs_required), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                val docAadhaar = stringResource(R.string.doc_aadhaar)
                val docDriving = stringResource(R.string.doc_driving_license)
                val docResume = stringResource(R.string.doc_resume)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    CheckboxOption(docAadhaar, documentsRequired.contains(docAadhaar), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add(docAadhaar) else documentsRequired.remove(docAadhaar) }
                    CheckboxOption(docDriving, documentsRequired.contains(docDriving), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add(docDriving) else documentsRequired.remove(docDriving) }
                    CheckboxOption(docResume, documentsRequired.contains(docResume), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add(docResume) else documentsRequired.remove(docResume) }
                    
                    documentsRequired.forEach { doc ->
                        if (doc !in listOf(docAadhaar, docDriving, docResume)) {
                            CheckboxOption(doc, documentsRequired.contains(doc), modifier = Modifier.fillMaxWidth()) { if(!it) documentsRequired.remove(doc) }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        value = newDocInput, onValueChange = { newDocInput = it },
                        placeholder = { Text(stringResource(R.string.add_other_doc)) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if(newDocInput.isNotBlank() && !documentsRequired.contains(newDocInput)) { documentsRequired.add(newDocInput); newDocInput = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) { Text(stringResource(R.string.add_btn), color = textPrimary) }
                }
            }

            // 10. Perks & Preferences
            FormSection(stringResource(R.string.post_job_perks)) {
                Text(stringResource(R.string.benefits_offered), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    listOf(stringResource(R.string.perk_free_food), stringResource(R.string.perk_accommodation), stringResource(R.string.perk_weekly_off), stringResource(R.string.perk_travel_allowance)).forEach { ben ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = benefits.contains(ben), onCheckedChange = { if(it) benefits.add(ben) else benefits.remove(ben) })
                            com.example.localconnect.ui.TranslatedText(ben, fontSize = 14.sp, color = textSecondary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.pref_lang), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckboxOption("Hindi", languagesPref.contains("Hindi")) { if(it) languagesPref.add("Hindi") else languagesPref.remove("Hindi") }
                    CheckboxOption("Bengali", languagesPref.contains("Bengali")) { if(it) languagesPref.add("Bengali") else languagesPref.remove("Bengali") }
                    CheckboxOption("English", languagesPref.contains("English")) { if(it) languagesPref.add("English") else languagesPref.remove("English") }
                }
            }

            // 12. Verification
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9FAFB),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(checked = isGenuineConfirmed, onCheckedChange = { isGenuineConfirmed = it })
                        Text(stringResource(R.string.confirm_genuine_job), fontSize = 13.sp, color = textSecondary, modifier = Modifier.padding(top = 12.dp))
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
                        Text(stringResource(R.string.agree_fake_suspend), fontSize = 13.sp, color = textSecondary, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Padding for bottom bar
        }
    }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Divider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(bottom = 16.dp))
            content()
        }
    }
}

@Composable
fun PillOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFFFFF7ED) else Color.White,
        border = BorderStroke(1.dp, if (selected) Color(0xFFEA580C) else Color(0xFFE5E7EB)),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        com.example.localconnect.ui.TranslatedText(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color(0xFFC2410C) else Color(0xFF4B5563),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun CheckboxOption(text: String, checked: Boolean, modifier: Modifier = Modifier, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (checked) Color(0xFFFFF7ED) else Color.White,
        border = BorderStroke(1.dp, if (checked) Color(0xFFEA580C) else Color(0xFFE5E7EB)),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onCheckedChange(!checked) }
    ) {
        com.example.localconnect.ui.TranslatedText(
            text = text,
            fontSize = 14.sp,
            color = if (checked) Color(0xFFC2410C) else Color(0xFF4B5563),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
