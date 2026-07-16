import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_block = """                            LaunchedEffect(mapAddress) {
                                if (mapAddress.isNotBlank()) {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses = geocoder.getFromLocationName(mapAddress, 1)
                                            if (!addresses.isNullOrEmpty()) {
                                                val pt = LatLng(addresses[0].latitude, addresses[0].longitude)
                                                markerPosition = pt
                                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 14f))
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    android.widget.Toast.makeText(context, "Location not found on map", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            // Fallback for emulator so the user can see the red pin for testing
                                            val pt = LatLng(19.0760, 72.8777) // Mumbai default
                                            markerPosition = pt
                                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 14f))
                                            withContext(Dispatchers.Main) {
                                                android.widget.Toast.makeText(context, "Using Mumbai fallback for Emulator", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }"""

new_block = """                            LaunchedEffect(mapAddress) {
                                if (mapAddress.isNotBlank()) {
                                    var pt: LatLng? = null
                                    var showNotFound = false
                                    var showEmulatorFallback = false
                                    
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses = geocoder.getFromLocationName(mapAddress, 1)
                                            if (!addresses.isNullOrEmpty()) {
                                                pt = LatLng(addresses[0].latitude, addresses[0].longitude)
                                            } else {
                                                showNotFound = true
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            pt = LatLng(19.0760, 72.8777) // Mumbai default
                                            showEmulatorFallback = true
                                        }
                                    }
                                    
                                    if (pt != null) {
                                        markerPosition = pt
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt!!, 14f))
                                    }
                                    if (showNotFound) {
                                        android.widget.Toast.makeText(context, "Location not found on map", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                    if (showEmulatorFallback) {
                                        android.widget.Toast.makeText(context, "Using Mumbai fallback for Emulator", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }"""

content = content.replace(old_block, new_block)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated LaunchedEffect block")
