import os

post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'
with open(post_job_file, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Lowercase session name
content = content.replace(
    'var employerName by remember { mutableStateOf(session.name) }',
    'var employerName by remember { mutableStateOf(session.name.lowercase()) }'
)
content = content.replace(
    'var contactPersonName by remember { mutableStateOf(session.name) }',
    'var contactPersonName by remember { mutableStateOf(session.name.lowercase()) }'
)

# 2. Swap Use Current Location and LocationSelectorMap
block = """                Spacer(modifier = Modifier.height(12.dp))
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
                }"""

if block in content:
    content = content.replace(block + '\n', '')
    
    # Insert right before LocationSelectorMap
    insert_target = '                LocationSelectorMap('
    content = content.replace(insert_target, block + '\n                Spacer(modifier = Modifier.height(12.dp))\n' + insert_target)
else:
    print("Could not find exact block in PostJobScreen.kt")

with open(post_job_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patch applied to PostJobScreen.kt successfully.")
