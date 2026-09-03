import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Imports
imports = """import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import androidx.compose.material.icons.filled.MyLocation
import java.util.Locale
"""
content = content.replace('import com.example.localconnect.data.ServiceTask\n', 'import com.example.localconnect.data.ServiceTask\n' + imports)

# 2. Add location variables
target_vars = """    val targetLanguage = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]?.language ?: "en"
    
    val primaryColor = Color(0xFFF97316)"""
replacement_vars = """    val targetLanguage = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]?.language ?: "en"
    
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.entries.any { it.value }
            if (!granted) {
                Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }
    )
    
    val primaryColor = Color(0xFFF97316)"""
content = content.replace(target_vars, replacement_vars)

# 3. Empty radius
content = content.replace('var serviceRadius by remember { mutableStateOf("5 km") }', 'var serviceRadius by remember { mutableStateOf("") }')

# 4. Dropdown item text dark
content = content.replace('text = { com.example.localconnect.ui.TranslatedText(selectionOption) }', 'text = { com.example.localconnect.ui.TranslatedText(selectionOption, color = Color.Black) }')

# 5. Insert contentBelowMap in LocationSelectorMap
target_map = """                    contentBeforeSave = {"""
replacement_map = """                    contentBelowMap = {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
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
                                                            val newCity = addr.locality ?: addr.subAdminArea ?: ""
                                                            val pin = addr.postalCode ?: ""
                                                            val street = addr.thoroughfare ?: addr.subLocality ?: ""
                                                            if (street.isNotBlank()) locality = street
                                                            if (newCity.isNotBlank()) city = newCity
                                                            if (pin.isNotBlank()) pincode = pin
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
                    contentBeforeSave = {"""
content = content.replace(target_map, replacement_map)


with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated PostServiceScreen.kt completely")
