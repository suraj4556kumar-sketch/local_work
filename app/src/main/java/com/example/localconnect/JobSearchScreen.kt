package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import com.example.localconnect.ui.TranslatedText
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Job
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.ui.theme.OrangePrimary
import androidx.compose.runtime.collectAsState

import android.Manifest
import android.widget.Toast
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import androidx.compose.ui.platform.LocalContext
import kotlin.math.round





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSearchScreen(
    session: UserSession,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var showProfileSetup by remember { mutableStateOf(false) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }

    if (showProfileSetup && selectedJob != null) {
        JobApplicationScreen(
            job = selectedJob!!,
            session = session,
            onBack = { showProfileSetup = false },
            onSubmit = { showProfileSetup = false; selectedJob = null }
        )
    } else if (selectedJob != null) {
        JobDetailsScreen(
            job = selectedJob!!,
            session = session,
            onBack = { selectedJob = null },
            onApply = { showProfileSetup = true },
            onSimilarJobClick = { selectedJob = it }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
        // Tab Row Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = OrangePrimary,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.White
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.find_jobs_tab), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.my_apps_title), fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
        }

        if (selectedTab == 0) {
            JobSearchContent(session = session, onJobClick = { selectedJob = it })
        } else {
            MyApplicationsScreen(session = session, onJobClick = { selectedJob = it })
        }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSearchContent(session: UserSession, onJobClick: (Job) -> Unit) {
    val context = LocalContext.current
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.values.all { it }
            if (granted) {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                        if (location != null) {
                            userLat = location.latitude
                            userLon = location.longitude
                        } else {
                            userLat = 19.0760
                            userLon = 72.8777
                        }
                    }.addOnFailureListener {
                        userLat = 19.0760
                        userLon = 72.8777
                    }
                } catch (e: SecurityException) {
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFineLocation || hasCoarseLocation) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                    if (location != null) {
                        userLat = location.latitude
                        userLon = location.longitude
                    } else {
                        // Fallback to Mumbai for emulator if location is null
                        userLat = 19.0760
                        userLon = 72.8777
                        Toast.makeText(context, "Using fallback location (Mumbai). Please enable GPS.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    userLat = 19.0760
                    userLon = 72.8777
                    Toast.makeText(context, "Failed to get location. Using fallback (Mumbai).", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                userLat = 19.0760
                userLon = 72.8777
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        val distKm = results[0] / 1000.0
        return round(distKm * 10.0) / 10.0
    }

        
    
    val firebaseJobs by FirebaseRepository.getJobsFlow().collectAsState(initial = emptyList<com.example.localconnect.data.Job>())

    
    val allJobs = remember(firebaseJobs, userLat, userLon) { 
        firebaseJobs.map { 
            it.copy(distanceKm = if (userLat != null && userLon != null && it.latitude != 0.0 && it.longitude != 0.0) calculateDistance(userLat!!, userLon!!, it.latitude, it.longitude) else if (it.latitude == 0.0 && it.longitude == 0.0) -1.0 else it.distanceKm)
        } 
    }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showAdvancedFilters by remember { mutableStateOf(false) }
    var showSortByBottomSheet by remember { mutableStateOf(false) }
    val default_activeSort = stringResource(R.string.distance)
    var activeSort by remember { mutableStateOf(default_activeSort) }
    
    var showJobFilterDropdown by remember { mutableStateOf(false) }
    val default_selectedJobFilter = stringResource(R.string.all_jobs)
    var selectedJobFilter by remember { mutableStateOf(default_selectedJobFilter) }
    
    var maxDistance by remember { mutableDoubleStateOf(100.0) }
    var minSalary by remember { mutableIntStateOf(0) }
    var minExperience by remember { mutableIntStateOf(0) }

    val allRoles = remember(allJobs) { allJobs.map { it.title }.distinct() }
    val allJobsStr = stringResource(R.string.all_jobs)
    val jobFilterOptions = remember(allRoles, allJobsStr) { listOf(allJobsStr) + allRoles }
    val suggestions = allRoles.filter { it.contains(searchQuery, ignoreCase = true) }
    
    val sortedJobs = when (activeSort) {
        stringResource(R.string.salary_high_to_low) -> allJobs.sortedByDescending { it.salaryValue }
        stringResource(R.string.experience_low_to_high) -> allJobs.sortedBy { it.experienceYears }
        stringResource(R.string.recent) -> allJobs.sortedByDescending { it.timestamp }
        else -> allJobs.sortedBy { it.distanceKm } // Distance
    }

    val advancedFiltered = sortedJobs.filter {
        (if (maxDistance >= 100.0) true else it.distanceKm in 0.0..maxDistance) &&
        it.salaryValue >= minSalary &&
        it.experienceYears >= minExperience &&
        (selectedJobFilter == stringResource(R.string.all_jobs) || it.title == selectedJobFilter)
    }

    val displayedJobs = if (searchQuery.isBlank()) {
        advancedFiltered
    } else {
        advancedFiltered.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filters
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            expanded = it.isNotEmpty() && suggestions.isNotEmpty()
                        },
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    expanded = false 
                                }) {
                                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                }
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A)),
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = OrangePrimary,
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        )
                    )

                    if (searchQuery.isNotEmpty() && suggestions.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = Color(0xFFFFF3E0)
                        ) {
                            suggestions.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A)) },
                                    onClick = {
                                        searchQuery = role
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = showJobFilterDropdown,
                        onExpandedChange = { showJobFilterDropdown = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(selectedJobFilter, color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = com.example.localconnect.ui.theme.Primary)
                            }
                        }
                        
                        ExposedDropdownMenu(
                            expanded = showJobFilterDropdown,
                            onDismissRequest = { showJobFilterDropdown = false },
                            containerColor = Color(0xFFFFF3E0)
                        ) {
                            jobFilterOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A)) },
                                    onClick = {
                                        selectedJobFilter = opt
                                        showJobFilterDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { showAdvancedFilters = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.filters), color = OrangePrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                    
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { showSortByBottomSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Sort, contentDescription = null, modifier = Modifier.size(16.dp), tint = com.example.localconnect.ui.theme.Primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.sort_by), color = OrangePrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        var isRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()
        val coroutineScope = rememberCoroutineScope()
        
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coroutineScope.launch {
                    delay(1000) // Simulate refresh
                    isRefreshing = false
                }
            },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (displayedJobs.isEmpty()) {
                    item {
                        Text(
                            "${stringResource(R.string.no_jobs_found)} '$searchQuery'.",
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    items(displayedJobs) { job ->
                        JobCard(job, session, onJobClick)
                    }
                }
            }
        }
    }

    if (showAdvancedFilters) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showAdvancedFilters = false },
            containerColor = Color(0xFFFFF3E0)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(stringResource(R.string.filters), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("${stringResource(R.string.max_distance)}: ${maxDistance.toInt()} km", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = maxDistance.toFloat(),
                    onValueChange = { maxDistance = it.toDouble() },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${stringResource(R.string.min_salary)}: ₹$minSalary", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = minSalary.toFloat(),
                    onValueChange = { minSalary = it.toInt() },
                    valueRange = 5000f..50000f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${stringResource(R.string.min_experience)}: $minExperience yrs", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = minExperience.toFloat(),
                    onValueChange = { minExperience = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showAdvancedFilters = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(stringResource(R.string.apply_filters), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showSortByBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showSortByBottomSheet = false },
            containerColor = Color(0xFFFFF3E0)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(stringResource(R.string.sort_by), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(16.dp))

                val sortOptions = listOf(stringResource(R.string.distance), stringResource(R.string.salary_high_to_low), stringResource(R.string.experience_low_to_high), stringResource(R.string.recent))
                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeSort = option
                                showSortByBottomSheet = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = activeSort == option,
                            onClick = { 
                                activeSort = option
                                showSortByBottomSheet = false 
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(option, fontSize = 16.sp, color = if (activeSort == option) OrangePrimary else Color(0xFF1A1A1A))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun JobCard(job: Job, session: UserSession, onJobClick: (Job) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onJobClick(job) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                TranslatedText(job.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (job.timestamp > 0L) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(job.timestamp)), fontSize = 12.sp, color = Color.Gray)
                } else if (job.postingDate.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TranslatedText(job.postingDate, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                TranslatedText(job.companyName, fontSize = 14.sp, color = OrangePrimary, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (job.rating > 0.0) String.format("%.1f", job.rating) else "New", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                
                TranslatedText(job.location, fontSize = 13.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (job.distanceKm >= 0.0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• ", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("${round(job.distanceKm * 10) / 10} km", fontSize = 13.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.salary), fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    TranslatedText(job.salary, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.experience_lbl), fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    TranslatedText(job.experienceReq, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}


fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
