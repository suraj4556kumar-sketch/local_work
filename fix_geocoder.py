import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_code = """                                            if (!addresses.isNullOrEmpty()) {
                                                val pt = LatLng(addresses[0].latitude, addresses[0].longitude)
                                                markerPosition = pt
                                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 14f))
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }"""

new_code = """                                            if (!addresses.isNullOrEmpty()) {
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
                                            withContext(Dispatchers.Main) {
                                                android.widget.Toast.makeText(context, "Emulator Geocoder failed", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }"""

content = content.replace(old_code, new_code)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated Geocoder block")
