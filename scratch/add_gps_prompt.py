import re
import os

filepath = r"app/src/main/java/com/example/localconnect/CustomerDetailsScreen.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

new_code = """
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == android.app.Activity.RESULT_OK) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val addr = addresses[0]
                                        val fullAddress = addr.getAddressLine(0) ?: ""
                                        val cityName = addr.locality ?: addr.subAdminArea ?: ""
                                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                            address = fullAddress
                                            city = cityName
                                            latitude = location.latitude
                                            longitude = location.longitude
                                            Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        } else {
                            Toast.makeText(context, "Could not fetch location", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: SecurityException) { e.printStackTrace() }
            }
        } else {
            Toast.makeText(context, "GPS is required for current location", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkGpsAndFetchLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = com.google.android.gms.location.LocationServices.getSettingsClient(context)
        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            try {
                fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                if (!addresses.isNullOrEmpty()) {
                                    val addr = addresses[0]
                                    val fullAddress = addr.getAddressLine(0) ?: ""
                                    val cityName = addr.locality ?: addr.subAdminArea ?: ""
                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        address = fullAddress
                                        city = cityName
                                        latitude = location.latitude
                                        longitude = location.longitude
                                        Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    } else {
                        Toast.makeText(context, "Could not fetch location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) { e.printStackTrace() }
        }.addOnFailureListener { exception ->
            if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                try {
                    settingResultRequest.launch(androidx.activity.result.IntentSenderRequest.Builder(exception.resolution).build())
                } catch (sendEx: Exception) { }
            } else {
                Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                checkGpsAndFetchLocation()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun requestLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGpsAndFetchLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
"""

old_pattern = re.compile(r"    val locationPermissionLauncher = rememberLauncherForActivityResult\(.*?fun requestLocation\(\) \{.*?else \{\s*locationPermissionLauncher\.launch\(Manifest\.permission\.ACCESS_FINE_LOCATION\)\s*\}\s*\}", re.DOTALL)
content = old_pattern.sub(new_code.strip(), content)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated CustomerDetailsScreen.kt successfully.")
