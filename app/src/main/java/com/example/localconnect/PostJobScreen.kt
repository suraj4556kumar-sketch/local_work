package com.example.localconnect

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

    // State Variables
    var employerId by remember { mutableStateOf(session.phone) }
    var employerName by remember { mutableStateOf(session.name) }
    var contactPersonName by remember { mutableStateOf(session.name) }
    var phoneNumber by remember { mutableStateOf(session.phone) }
    var jobTitle by remember { mutableStateOf("school bus driver") }
    var jobCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Cook", "Maid", "Driver", "Office Boy", "Security Guard", "Delivery", "Electrician", "Other")
    var vacancies by remember { mutableStateOf("1") }
    var workAddress by remember { mutableStateOf("suraj public school") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var city by remember { mutableStateOf("kharagpur") }
    var state by remember { mutableStateOf("west bengal") }
    var pincode by remember { mutableStateOf("721302") }
    var landmark by remember { mutableStateOf("") }

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

    var jobDescription by remember { mutableStateOf("Looking for a responsible and experienced school bus driver to safely transport students to and from Suraj Public School. The ideal candidate must prioritize student safety, follow traffic rules strictly, and maintain the vehicle in good condition.") }

    val responsibilities = remember { mutableStateListOf("Drive the school bus safely on assigned routes", "Ensure the safety of all students during transit", "Perform basic maintenance and daily inspections of the bus", "Maintain discipline among students while in the bus") }
    var newRespInput by remember { mutableStateOf("") }

    val documentsRequired = remember { mutableStateListOf<String>() }

    val benefits = remember { mutableStateListOf<String>() }
    val languagesPref = remember { mutableStateListOf("Hindi", "Bengali") }

    var isGenuineConfirmed by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    // Constants
    val orangePrimary = Color(0xFFEA580C)
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
                        Text("Post Job", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Create a job post to hire workers", fontSize = 12.sp, color = Color.White.copy(alpha=0.8f))
                    }
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Back", color = Color.White, fontWeight = FontWeight.Bold)
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
                    OutlinedButton(
                        onClick = { 
                            Toast.makeText(context, "Draft Saved", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text("Save Draft", color = textSecondary, fontWeight = FontWeight.SemiBold, maxLines = 1, fontSize = 14.sp)
                    }
                    Button(
                        onClick = {
                            if (!isGenuineConfirmed || !termsAccepted) {
                                Toast.makeText(context, "Please agree to terms and confirmation", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (jobTitle.isBlank() || city.isBlank() || salaryMin.isBlank()) {
                                Toast.makeText(context, "Please fill required fields (Title, City, Min Salary)", Toast.LENGTH_SHORT).show()
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
                                    description = jobDescription,
                                    responsibilities = responsibilities.toList(),
                                    documentsRequired = documentsRequired.toList(),
                                    requirements = emptyList(),
                                    benefits = benefits.toList(),
                                    languagesPref = languagesPref.toList()
                                )
                                val success = FirebaseRepository.postJob(newJob)
                                isPosting = false
                                if (success) {
                                    Toast.makeText(context, "Job Posted!", Toast.LENGTH_SHORT).show()
                                    onJobPosted()
                                } else {
                                    Toast.makeText(context, "Failed to post job", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(2f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = orangePrimary),
                        enabled = !isPosting
                    ) {
                        Text("Post Job", color = Color.White, fontWeight = FontWeight.Bold)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Employer Info
            FormSection("1. Employer Info") {
                OutlinedTextField(
                    value = employerName, onValueChange = { employerName = it },
                    label = { Text("Employer / Company Name *") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = contactPersonName, onValueChange = { contactPersonName = it },
                    label = { Text("Contact Person Name *") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phoneNumber, onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Text("+91", modifier = Modifier.padding(start = 12.dp)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 2. Job Details
            FormSection("2. Job Details") {
                OutlinedTextField(
                    value = jobTitle, onValueChange = { jobTitle = it },
                    label = { Text("Job Title *") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = jobCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(8.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        categories.forEach { selectionOption ->
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

            // 3. Location
            FormSection("3. Location") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Location Details", fontSize = 14.sp, color = textSecondary)
                    TextButton(onClick = { 
                        city = "Kharagpur" // Simulated location
                        state = "West Bengal"
                        Toast.makeText(context, "Location fetched!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use Current Location")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = workAddress, onValueChange = { workAddress = it },
                    label = { Text("Work Address / Building") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = city, onValueChange = { city = it },
                        label = { Text("City / Area *") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = state, onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pincode, onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = landmark, onValueChange = { landmark = it },
                    label = { Text("Landmark (Optional)") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )
            }

            // 4. Salary
            FormSection("4. Salary") {
                OutlinedTextField(
                    value = salaryMin, onValueChange = { salaryMin = it },
                    label = { Text("Monthly Salary (₹)") },
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
                    OutlinedTextField(
                        value = experienceRequired,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Experience Required") },
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
                    OutlinedTextField(
                        value = educationRequired,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Education Required") },
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
                    OutlinedTextField(
                        value = genderPreference,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender Preference") },
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
                OutlinedTextField(
                    value = jobDescription, onValueChange = { jobDescription = it },
                    label = { Text("Describe the job role, environment...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(8.dp)
                )
            }

            // 8. Skills & Responsibilities
            FormSection("8. Skills & Responsibilities") {
                Text("Key Responsibilities & Skills", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
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
                    OutlinedTextField(
                        value = newRespInput, onValueChange = { newRespInput = it },
                        placeholder = { Text("Add skill or responsibility") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if(newRespInput.isNotBlank()) { responsibilities.add(newRespInput); newRespInput = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) { Text("Add", color = textPrimary) }
                }
            }

            // 9. Documents Required
            FormSection("9. Documents Required") {
                Text("Documents Required", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    CheckboxOption("Aadhaar Card", documentsRequired.contains("Aadhaar Card"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("Aadhaar Card") else documentsRequired.remove("Aadhaar Card") }
                    CheckboxOption("Driving License", documentsRequired.contains("Driving License"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("Driving License") else documentsRequired.remove("Driving License") }
                    CheckboxOption("Police Verification", documentsRequired.contains("Police Verification"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("Police Verification") else documentsRequired.remove("Police Verification") }
                    CheckboxOption("CV / Resume", documentsRequired.contains("CV / Resume"), modifier = Modifier.fillMaxWidth()) { if(it) documentsRequired.add("CV / Resume") else documentsRequired.remove("CV / Resume") }
                }
            }

            // 10. Perks & Preferences
            FormSection("10. Perks & Preferences") {
                Text("Benefits Offered", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
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
                Text("Preferred Language", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textSecondary)
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
                        Text("I confirm this is a genuine job posting representing an actual vacancy. *", fontSize = 13.sp, color = textSecondary, modifier = Modifier.padding(top = 12.dp))
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
                        Text("I agree that fake or misleading job posts may result in account suspension. *", fontSize = 13.sp, color = textSecondary, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }

            // 13. Live Preview
            Text("Live Preview", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textSecondary, modifier = Modifier.padding(start = 4.dp))
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
                                Text("VERIFIED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
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
