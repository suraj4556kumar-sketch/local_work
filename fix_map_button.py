import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add imports
imports = """
import android.location.Geocoder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
"""
if 'import com.google.maps.android.compose.GoogleMap' not in content:
    content = content.replace('import androidx.compose.ui.Modifier', imports + '\nimport androidx.compose.ui.Modifier')


# 2. Replace the Mock Map Box
mock_map_target = """                        // Mock Map
                        Box(modifier = Modifier.fillMaxSize().background(Color(0x1A000000)), contentAlignment = Alignment.Center) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.9f),
                                shadowElevation = 2.dp
                            ) {"""

inline_map_replacement = """                        // Inline Map with Clickable Intent
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            var markerPosition by remember { mutableStateOf<LatLng?>(null) }
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 5f) // Default India
                            }
                            val mapAddress = job.workAddress.ifBlank { job.location }
                            
                            LaunchedEffect(mapAddress) {
                                if (mapAddress.isNotBlank()) {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses = geocoder.getFromLocationName(mapAddress, 1)
                                            if (!addresses.isNullOrEmpty()) {
                                                val pt = LatLng(addresses[0].latitude, addresses[0].longitude)
                                                markerPosition = pt
                                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 14f))
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                            
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = com.google.maps.android.compose.MapUiSettings(
                                    zoomControlsEnabled = false, 
                                    scrollGesturesEnabled = false, 
                                    zoomGesturesEnabled = false,
                                    rotationGesturesEnabled = false,
                                    tiltGesturesEnabled = false
                                )
                            ) {
                                markerPosition?.let { pt ->
                                    Marker(
                                        state = MarkerState(position = pt),
                                        title = mapAddress
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.9f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.clickable {
                                    val uri = android.net.Uri.parse("geo:0,0?q=" + android.net.Uri.encode(mapAddress))
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                    intent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        intent.setPackage(null)
                                        try {
                                            context.startActivity(intent)
                                        } catch (e2: Exception) {
                                            android.widget.Toast.makeText(context, "Maps app not found", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            ) {"""

content = content.replace(mock_map_target, inline_map_replacement)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated JobDetailsScreen.kt")
