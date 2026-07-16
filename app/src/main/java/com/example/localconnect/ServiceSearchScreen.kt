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
import androidx.compose.foundation.layout.fillMaxHeight
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSearchScreen(session: UserSession, initialTab: Int = 0) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var dummyServices by remember { mutableStateOf<List<ServiceTask>>(emptyList()) }
    var bookedServiceIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        dummyServices = FirebaseRepository.getServices()
        val myBookings = FirebaseRepository.getMyServiceBookings(session.phone)
        bookedServiceIds = myBookings.map { it.serviceId }.toSet()
    }

    var selectedService by remember { mutableStateOf<ServiceTask?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showAdvancedFilters by remember { mutableStateOf(false) }
    var showSortByBottomSheet by remember { mutableStateOf(false) }
    val default_activeSort = stringResource(R.string.distance)
    var activeSort by remember { mutableStateOf(default_activeSort) }
    
    var showServiceFilterDropdown by remember { mutableStateOf(false) }
    val default_selectedServiceFilter = stringResource(R.string.all_services)
    var selectedServiceFilter by remember { mutableStateOf(default_selectedServiceFilter) }
    
    var maxDistance by remember { mutableDoubleStateOf(50.0) }
    var maxRate by remember { mutableIntStateOf(2000) }
    var minRating by remember { mutableDoubleStateOf(0.0) }

    val allCategories = remember(dummyServices) { dummyServices.map { it.category }.distinct() }
    val allServicesStr = stringResource(R.string.all_services)
    val serviceFilterOptions = remember(allCategories, allServicesStr) { listOf(allServicesStr) + allCategories }
    val suggestions = allCategories.filter { it.contains(searchQuery, ignoreCase = true) }
    
    val sortedServices = when (activeSort) {
        stringResource(R.string.rating_high_to_low) -> dummyServices.sortedByDescending { it.rating }
        stringResource(R.string.salary_high_to_low) -> dummyServices.sortedBy { it.price }
        else -> dummyServices.sortedBy { it.distanceKm }
    }

    val advancedFiltered = sortedServices.filter {
        it.distanceKm <= maxDistance &&
        it.price <= maxRate &&
        it.rating >= minRating &&
        (selectedServiceFilter == stringResource(R.string.all_services) || it.category == selectedServiceFilter)
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
                text = { Text("Find Services", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Bookings", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
        }

        if (selectedTab == 0) {
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
                        placeholder = { Text(stringResource(R.string.search_service_hint)) },
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

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (displayedServices.isEmpty()) {
                item {
                    Text(
                        text = "${stringResource(R.string.no_jobs_found)} \"$searchQuery\"",
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                items(displayedServices) { svc ->
                    val isBooked = bookedServiceIds.contains(svc.id)
                    PostedServiceCard(
                        service = svc,
                        onClick = { if (!isBooked) selectedService = svc },
                        buttonText = if (isBooked) "Booked" else "Book Service",
                        isButtonEnabled = !isBooked
                    )
                }
            }
        }
    }

    // Sort By Bottom Sheet
    if (showSortByBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showSortByBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.sort_by), fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                val sortOptions = listOf(stringResource(R.string.distance), stringResource(R.string.rating_high_to_low), "Price (Low to High)")
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
            modifier = Modifier.fillMaxHeight(),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showAdvancedFilters = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.filters), fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
                
                Text("${stringResource(R.string.max_distance)}: ${maxDistance.toInt()} km", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = maxDistance.toFloat(),
                    onValueChange = { maxDistance = it.toDouble() },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${stringResource(R.string.max_rate)}: ₹$maxRate/hr", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = maxRate.toFloat(),
                    onValueChange = { maxRate = it.toInt() },
                    valueRange = 100f..5000f,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${stringResource(R.string.min_rating)}: ${String.format("%.1f", minRating)} ★", fontWeight = FontWeight.SemiBold)
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
                    Text(stringResource(R.string.apply_filters), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        } else {
            MyServiceBookingsScreen(session = session)
        }
    }
}


