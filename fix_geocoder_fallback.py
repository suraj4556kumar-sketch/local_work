import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_code = """                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            withContext(Dispatchers.Main) {
                                                android.widget.Toast.makeText(context, "Emulator Geocoder failed", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }"""

new_code = """                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            // Fallback for emulator so the user can see the red pin for testing
                                            val pt = LatLng(19.0760, 72.8777) // Mumbai default
                                            markerPosition = pt
                                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 14f))
                                            withContext(Dispatchers.Main) {
                                                android.widget.Toast.makeText(context, "Using Mumbai fallback for Emulator", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }"""

content = content.replace(old_code, new_code)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated Geocoder block with Mumbai fallback")
