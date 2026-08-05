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
    val categories = listOf("Cook", "Maid", "Driver", "Office Boy", "Security Guard", "Delivery", "Electrician", "Plumber", "Carpenter", "Painter", "Cleaner", "Mechanic", "Welder", "Mason", "Technician", "Beautician", "Tutor", "Data Entry", "Accountant", "Sales Executive", "Other")
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

    var experienceRequired by remember { mutableStateOf("1 - 3 Years") }
    var expExpanded by remember { mutableStateOf(false) }
    val expOptions = listOf("Fresher / No Experience", "0 - 1 Year", "1 - 3 Years", "3+ Years")
    var educationRequired by remember { mutableStateOf("12th Pass") }
    var eduExpanded by remember { mutableStateOf(false) }
    val eduOptions = listOf("Not Required", "10th Pass", "12th Pass", "Graduate")
    var genderPreference by remember { mutableStateOf("Male") }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Any", "Male Only", "Female Only")
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
        "Cook" to Pair(
            "Looking for an experienced cook to prepare delicious and healthy meals. Must maintain hygiene and manage the kitchen efficiently.",
            listOf("Prepare daily meals as per requirements", "Maintain kitchen cleanliness and hygiene", "Manage groceries and kitchen supplies", "Ensure timely preparation of food")
        ),
        "Maid" to Pair(
            "Seeking a reliable and hardworking maid for household chores including cleaning, dusting, and washing. Must be punctual and trustworthy.",
            listOf("Daily sweeping, mopping, and dusting", "Washing dishes and cleaning the kitchen", "Washing and folding clothes", "General household maintenance")
        ),
        "Driver" to Pair(
            "Looking for a responsible and experienced driver. Must prioritize safety, follow traffic rules strictly, and maintain the vehicle in good condition.",
            listOf("Drive passengers safely to destinations", "Perform basic maintenance and daily inspections", "Maintain a clean vehicle interior and exterior", "Follow all traffic and safety regulations")
        ),
        "Office Boy" to Pair(
            "Seeking an active and honest office boy to assist with daily administrative tasks, file management, and serving tea/coffee to staff.",
            listOf("Serve tea/water to staff and guests", "File management and basic paperwork", "Keep the office premises clean", "Assist in local market runs for office supplies")
        ),
        "Security Guard" to Pair(
            "Looking for a vigilant and physically fit security guard to protect premises, monitor entrances, and ensure the safety of staff and property.",
            listOf("Monitor and authorize entrance of vehicles/people", "Patrol premises regularly", "Report any suspicious activities", "Maintain visitor logbook")
        ),
        "Delivery" to Pair(
            "Seeking an energetic delivery partner for timely and safe delivery of packages. Must have a valid driving license and good area knowledge.",
            listOf("Deliver packages to customers on time", "Use navigation apps to find optimal routes", "Collect payments if required", "Maintain delivery logs and records")
        ),
        "Electrician" to Pair(
            "Looking for a skilled electrician to install, maintain, and repair electrical wiring and equipment. Must follow safety protocols.",
            listOf("Install and repair electrical wiring and fixtures", "Troubleshoot electrical issues", "Ensure all work complies with safety codes", "Perform regular maintenance checks")
        ),
        "Plumber" to Pair(
            "Looking for an experienced plumber to install and repair pipes, fixtures, and plumbing equipment. Must be able to read blueprints and diagnose issues.",
            listOf("Install and repair pipes and fixtures", "Clear obstructions from sink drains and toilets", "Troubleshoot plumbing issues", "Ensure compliance with building codes")
        ),
        "Carpenter" to Pair(
            "Seeking a skilled carpenter to construct, install, and repair wooden structures and furniture. Must have good attention to detail.",
            listOf("Read blueprints and design layouts", "Cut, shape, and smooth wooden materials", "Install cabinets, doors, and framework", "Repair damaged furniture")
        ),
        "Painter" to Pair(
            "Looking for a professional painter for interior and exterior painting projects. Must prepare surfaces, mix colors, and apply paint evenly.",
            listOf("Prepare walls and surfaces for painting", "Mix and match paint colors", "Apply paint, varnish, and other finishes", "Clean up work areas and equipment")
        ),
        "Cleaner" to Pair(
            "Seeking a dedicated cleaner for residential or commercial properties. Must maintain a high standard of cleanliness.",
            listOf("Sweep, mop, and vacuum floors", "Clean and sanitize restrooms", "Empty trash bins and replace liners", "Dust furniture and equipment")
        ),
        "Mechanic" to Pair(
            "Looking for a skilled mechanic to inspect, maintain, and repair vehicles or machinery. Must have strong diagnostic skills.",
            listOf("Inspect vehicle engines and mechanical parts", "Diagnose and troubleshoot issues", "Perform routine maintenance like oil changes", "Repair or replace defective parts")
        ),
        "Welder" to Pair(
            "Seeking an experienced welder to join metal parts together using various welding techniques. Must follow safety standards.",
            listOf("Read and interpret blueprints", "Operate welding equipment safely", "Weld metal components accurately", "Inspect welds for durability and safety")
        ),
        "Mason" to Pair(
            "Looking for a skilled mason for bricklaying and concrete work. Must have physical strength and precision.",
            listOf("Lay bricks, stones, and concrete blocks", "Mix cement and mortar", "Shape and cut materials as required", "Ensure structures are level and secure")
        ),
        "Technician" to Pair(
            "Seeking a reliable technician to install, troubleshoot, and repair equipment. Must have strong technical and problem-solving skills.",
            listOf("Install and configure new equipment", "Perform routine maintenance checks", "Troubleshoot hardware or software issues", "Provide technical support to users")
        ),
        "Beautician" to Pair(
            "Looking for a professional beautician to provide various beauty and grooming services to clients.",
            listOf("Provide haircuts, styling, and coloring", "Perform facials and skincare treatments", "Offer manicure and pedicure services", "Maintain cleanliness of salon equipment")
        ),
        "Tutor" to Pair(
            "Seeking a knowledgeable tutor to help students understand subjects and improve academic performance.",
            listOf("Prepare lesson plans and study materials", "Assess student progress and identify areas for improvement", "Provide one-on-one or group instruction", "Encourage and motivate students")
        ),
        "Data Entry" to Pair(
            "Looking for a detail-oriented data entry operator to input and update records in the database accurately.",
            listOf("Enter customer and account data accurately", "Verify data for accuracy and completeness", "Maintain confidentiality of information", "Generate reports as needed")
        ),
        "Accountant" to Pair(
            "Seeking a qualified accountant to manage financial records, prepare reports, and ensure compliance with regulations.",
            listOf("Maintain and reconcile financial records", "Prepare tax returns and ensure compliance", "Analyze financial statements", "Manage payroll and invoices")
        ),
        "Sales Executive" to Pair(
            "Looking for an enthusiastic sales executive to drive business growth by identifying new opportunities and building client relationships.",
            listOf("Identify and reach out to potential clients", "Present and promote products/services", "Negotiate contracts and close sales", "Maintain customer relationships and follow up")
        ),
        "Other" to Pair(
            "",
            listOf()
        )
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
                                    postingDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
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
            FormSection("1. Employer Info") {
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
            FormSection("2. Job Details") {
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
            FormSection("4. Salary") {
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = salaryMin, onValueChange = { salaryMin = it },
                    label = { Text(stringResource(R.string.monthly_salary)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
            }

            // 5. Candidate Requirements
            FormSection("5. Candidate Requirements") {
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
            FormSection("7. Description") {
                OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    value = jobDescription, onValueChange = { jobDescription = it },
                    label = { Text(stringResource(R.string.desc_job_role_env)) },
                    modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(8.dp)
                )
            }

            // 8. Skills & Responsibilities
            FormSection("8. Skills & Responsibilities") {
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
            FormSection("9. Documents Required") {
                Text(stringResource(R.string.docs_required), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    CheckboxOption("Aadhaar Card", documentsRequired.contains("Aadhaar Card"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("Aadhaar Card") else documentsRequired.remove("Aadhaar Card") }
                    CheckboxOption("Driving License", documentsRequired.contains("Driving License"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("Driving License") else documentsRequired.remove("Driving License") }
                    CheckboxOption("CV / Resume", documentsRequired.contains("CV / Resume"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("CV / Resume") else documentsRequired.remove("CV / Resume") }
                    
                    documentsRequired.forEach { doc ->
                        if (doc !in listOf("Aadhaar Card", "Driving License", "CV / Resume")) {
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
            FormSection("10. Perks & Preferences") {
                Text(stringResource(R.string.benefits_offered), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    listOf("Free Food", "Accommodation", "Weekly Off", "Travel Allowance").forEach { ben ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = benefits.contains(ben), onCheckedChange = { if(it) benefits.add(ben) else benefits.remove(ben) })
                            Text(ben, fontSize = 14.sp, color = textSecondary)
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

            // 13. Live Preview
            Text(stringResource(R.string.live_preview), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textSecondary, modifier = Modifier.padding(start = 4.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(orangePrimary))
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(jobTitle.ifBlank { "Job Title" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                Text(employerName.ifBlank { "Company Name" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                            }
                            Surface(color = Color(0xFFFFF7ED), shape = RoundedCornerShape(4.dp)) {
                                Text(stringResource(R.string.verified_caps), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(city.ifBlank { "Location" }, fontSize = 14.sp, color = textSecondary)
                        Text(if (salaryMin.isNotBlank()) "₹$salaryMin / $salaryType" else "Salary", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(experienceRequired, fontSize = 13.sp, color = textSecondary)
                            if (benefits.isNotEmpty()) {
                                Text(benefits.first(), fontSize = 13.sp, color = textSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (jobCategory.isNotBlank()) {
                                Surface(color = grayBackground, shape = RoundedCornerShape(4.dp)) {
                                    Text(jobCategory, fontSize = 12.sp, color = textSecondary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
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
        Text(
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
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (checked) Color(0xFFC2410C) else Color(0xFF4B5563),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
