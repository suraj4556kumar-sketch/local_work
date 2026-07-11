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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Job
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.ui.theme.OrangePrimary
import androidx.compose.runtime.collectAsState





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSearchScreen(
    session: UserSession
) {
    var selectedTab by remember { mutableIntStateOf(0) }
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
                text = { Text("Find Jobs", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Applications", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
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
        val dummyJobs = remember(session.language.code) { getDummyJobs(session.language.code) }
    
    val firebaseJobs by FirebaseRepository.getJobsFlow().collectAsState(initial = emptyList())

    val allJobs = remember(dummyJobs, firebaseJobs) { firebaseJobs + dummyJobs }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showAdvancedFilters by remember { mutableStateOf(false) }
    var showSortByBottomSheet by remember { mutableStateOf(false) }
    val default_activeSort = stringResource(R.string.distance)
    var activeSort by remember { mutableStateOf(default_activeSort) }
    
    var showJobFilterDropdown by remember { mutableStateOf(false) }
    val default_selectedJobFilter = stringResource(R.string.all_jobs)
    var selectedJobFilter by remember { mutableStateOf(default_selectedJobFilter) }
    
    var maxDistance by remember { mutableDoubleStateOf(50.0) }
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
        it.distanceKm <= maxDistance &&
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
                .padding(16.dp)
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
                        modifier = Modifier.fillMaxWidth().height(52.dp).menuAnchor(),
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
                            containerColor = Color.White
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
                Spacer(modifier = Modifier.height(16.dp))
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
                            containerColor = Color.White
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

        // Job List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

    if (showAdvancedFilters) {
        ModalBottomSheet(
            onDismissRequest = { showAdvancedFilters = false },
            containerColor = Color.White
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
                    valueRange = 1f..50f,
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
            onDismissRequest = { showSortByBottomSheet = false },
            containerColor = Color.White
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(job.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(job.companyName, fontSize = 14.sp, color = OrangePrimary, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp).padding(top = 2.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(job.location, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(" • ", fontSize = 13.sp, color = Color.Gray)
                        Text("${job.distanceKm} km", fontSize = 13.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(job.postingDate, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(stringResource(R.string.salary), fontSize = 12.sp, color = Color.Gray)
                    Text(job.salary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(stringResource(R.string.experience), fontSize = 12.sp, color = Color.Gray)
                    Text(job.experienceReq, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
