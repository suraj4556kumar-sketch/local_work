package com.example.localconnect

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.ServiceTask
import com.example.localconnect.ui.theme.OrangePrimary
import com.example.localconnect.data.FirebaseRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect



fun getDummyServices(langCode: String): List<ServiceTask> {
    return listOf(
        ServiceTask(id = "S1", providerName = "Raju Plumber", serviceTitle = "Pipe & Leakage Repair", category = "Plumbing", location = "Kharagpur", distanceKm = 2.5, price = 300, rating = 4.8, completedJobs = 120, description = "Expert in fixing all kinds of pipe leakages and bathroom fittings."),
        ServiceTask(id = "S2", providerName = "Sham Electrician", serviceTitle = "Wiring & Fan Repair", category = "Electrical", location = "Kharagpur", distanceKm = 4.1, price = 400, rating = 4.5, completedJobs = 85, description = "Quick and reliable electrical repairs including fan, lights, and switchboards."),
        ServiceTask(id = "S3", providerName = "Anita Maid Service", serviceTitle = "Daily House Cleaning", category = "Cleaning", location = "Midnapore", distanceKm = 1.2, price = 150, rating = 4.9, completedJobs = 300, description = "Thorough sweeping, mopping, and utensil washing services."),
        ServiceTask(id = "S4", providerName = "Ramesh Carpenter", serviceTitle = "Furniture Assembly & Repair", category = "Carpentry", location = "Kharagpur", distanceKm = 5.0, price = 500, rating = 4.6, completedJobs = 45, description = "Specialist in wooden furniture repair, door fixing, and custom beds."),
        ServiceTask(id = "S5", providerName = "CoolTech AC Repair", serviceTitle = "AC Servicing & Gas Fill", category = "Appliance Repair", location = "Kharagpur", distanceKm = 3.2, price = 800, rating = 4.7, completedJobs = 210, description = "Complete AC servicing, cleaning, and gas refilling at affordable rates.")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSearchScreen(session: UserSession) {
    val strings = getStrings(session.language.code)
    var dummyServices by remember { mutableStateOf<List<ServiceTask>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        dummyServices = FirebaseRepository.getServices()
        if (dummyServices.isEmpty()) {
            dummyServices = getDummyServices(session.language.code)
        }
    }

    var selectedService by remember { mutableStateOf<ServiceTask?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showAdvancedFilters by remember { mutableStateOf(false) }
    var showSortByBottomSheet by remember { mutableStateOf(false) }
    var activeSort by remember { mutableStateOf(strings.distance) }
    
    var showServiceFilterDropdown by remember { mutableStateOf(false) }
    var selectedServiceFilter by remember { mutableStateOf(strings.allServices) }
    
    var maxDistance by remember { mutableDoubleStateOf(50.0) }
    var maxRate by remember { mutableIntStateOf(2000) }
    var minRating by remember { mutableDoubleStateOf(0.0) }

    val allCategories = remember(dummyServices) { dummyServices.map { it.category }.distinct() }
    val serviceFilterOptions = remember(allCategories, strings.allServices) { listOf(strings.allServices) + allCategories }
    val suggestions = allCategories.filter { it.contains(searchQuery, ignoreCase = true) }
    
    val sortedServices = when (activeSort) {
        strings.ratingHighToLow -> dummyServices.sortedByDescending { it.rating }
        strings.salaryHighToLow -> dummyServices.sortedBy { it.price }
        else -> dummyServices.sortedBy { it.distanceKm }
    }

    val advancedFiltered = sortedServices.filter {
        it.distanceKm <= maxDistance &&
        it.price <= maxRate &&
        it.rating >= minRating &&
        (selectedServiceFilter == strings.allServices || it.category == selectedServiceFilter)
    }

    val displayedServices = if (searchQuery.isBlank()) {
        advancedFiltered
    } else {
        advancedFiltered.filter {
            it.serviceTitle.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.providerName.contains(searchQuery, ignoreCase = true)
        }
    }

    if (selectedService != null) {
        BookServiceScreen(
            session = session,
            service = selectedService!!,
            onBack = { selectedService = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Search & Filters Header
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
                        placeholder = { Text(strings.searchServiceHint) },
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
                        modifier = Modifier.fillMaxWidth().height(52.dp).menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
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
                            suggestions.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A)) },
                                    onClick = {
                                        searchQuery = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = showServiceFilterDropdown,
                        onExpandedChange = { showServiceFilterDropdown = it },
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
                                Text(selectedServiceFilter, color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = com.example.localconnect.ui.theme.Primary)
                            }
                        }
                        
                        ExposedDropdownMenu(
                            expanded = showServiceFilterDropdown,
                            onDismissRequest = { showServiceFilterDropdown = false },
                            containerColor = Color.White
                        ) {
                            serviceFilterOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A)) },
                                    onClick = {
                                        selectedServiceFilter = opt
                                        showServiceFilterDropdown = false
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
                            Text(strings.filters, color = OrangePrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                            Text(strings.sortBy, color = OrangePrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (displayedServices.isEmpty()) {
                item {
                    Text(
                        text = "${strings.noJobsFound} \"$searchQuery\"",
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                items(displayedServices) { svc ->
                    ServiceCard(svc, strings, onClick = { selectedService = svc })
                }
            }
        }
    }

    // Sort By Bottom Sheet
    if (showSortByBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortByBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(strings.sortBy, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                val sortOptions = listOf(strings.distance, strings.ratingHighToLow, "Price (Low to High)")
                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            activeSort = option
                            showSortByBottomSheet = false
                        }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = activeSort == option,
                            onClick = { activeSort = option; showSortByBottomSheet = false },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option, fontSize = 16.sp, fontWeight = if (activeSort == option) FontWeight.Bold else FontWeight.Normal)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Advanced Filters Bottom Sheet
    if (showAdvancedFilters) {
        ModalBottomSheet(
            onDismissRequest = { showAdvancedFilters = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(strings.filters, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
                
                Text("${strings.maxDistance}: ${maxDistance.toInt()} km", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = maxDistance.toFloat(),
                    onValueChange = { maxDistance = it.toDouble() },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${strings.maxRate}: ₹$maxRate/hr", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = maxRate.toFloat(),
                    onValueChange = { maxRate = it.toInt() },
                    valueRange = 100f..5000f,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${strings.minRating}: ${String.format("%.1f", minRating)} ★", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = minRating.toFloat(),
                    onValueChange = { minRating = it.toDouble() },
                    valueRange = 0f..5f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showAdvancedFilters = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.applyFilters, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ServiceCard(svc: ServiceTask, strings: AppStrings, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
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
                Column {
                    Text(svc.serviceTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(svc.providerName, fontSize = 14.sp, color = OrangePrimary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Text(svc.location, fontSize = 13.sp, color = Color.Gray)
                        Text(" • ", fontSize = 13.sp, color = Color.Gray)
                        Text("${svc.distanceKm} km", fontSize = 13.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp).padding(end = 2.dp), tint = Color(0xFFFFB300))
                        Text("${svc.rating}", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(strings.ratePerHour, fontSize = 12.sp, color = Color.Gray)
                    Text("₹${svc.price}/hr", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(strings.serviceCategory, fontSize = 12.sp, color = Color.Gray)
                    Text(svc.category, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
