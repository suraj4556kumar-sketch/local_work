import os

post_job = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'

with open(post_job, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# find // 3. Location
start_loc = -1
for i, l in enumerate(lines):
    if '// 3. Location' in l:
        start_loc = i
        break

# find // 4. Salary
end_loc = -1
for i, l in enumerate(lines):
    if '// 4. Salary' in l:
        end_loc = i
        break

if start_loc != -1 and end_loc != -1:
    loc_lines = lines[start_loc:end_loc]
    # We want to replace the whole location section. Let's just rewrite it!
    # But wait, there's logic in it. I can just write it from scratch to be safe, pasting the logic verbatim.
    
    # We'll replace lines[start_loc:end_loc] with the new block.
    # What's in loc_lines?
    # It has the LocationSelectorMap, the address fields, and the "Use Current Location" button.
    # Actually, I can just write a manual replacement that has exactly what we need!
    
    new_loc_block = """            // 3. Location
            FormSection(stringResource(R.string.post_job_location)) {
                Text(stringResource(R.string.select_loc_map), fontSize = 14.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                LocationSelectorMap(
                    addressQuery = "$workAddress $city $state $pincode",
                    currentLat = latitude,
                    currentLon = longitude,
                    contentBetweenMapAndButton = {
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
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                            value = workAddress, onValueChange = { workAddress = it },
                            label = { Text(stringResource(R.string.work_address_building)) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = city, onValueChange = { city = it },
                                label = { Text(stringResource(R.string.city_area)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = state, onValueChange = { state = it },
                                label = { Text(stringResource(R.string.state_label)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = pincode, onValueChange = { pincode = it },
                                label = { Text(stringResource(R.string.pincode_star)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                value = landmark, onValueChange = { landmark = it },
                                label = { Text(stringResource(R.string.landmark_opt)) },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            )
                        }
                    },
                    onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->
                        latitude = lat
                        longitude = lon
                        if (forceOverwrite) {
                            if (street.isNotBlank()) workAddress = street
                            if (newCity.isNotBlank()) city = newCity
                            if (newState.isNotBlank()) state = newState
                            if (pin.isNotBlank()) pincode = pin
                        } else {
                            if (workAddress.isBlank()) workAddress = street
                            if (city.isBlank()) city = newCity
                            if (state.isBlank()) state = newState
                            if (pincode.isBlank()) pincode = pin
                        }
                    }
                )
            }

"""

    lines[start_loc:end_loc] = [new_loc_block]
    
    with open(post_job, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    print("Replaced location block.")
else:
    print("Could not find start/end.")
