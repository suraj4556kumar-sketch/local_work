package com.example.localconnect

import androidx.compose.ui.res.stringResource

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import com.example.localconnect.ui.theme.Primary
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

@Composable
fun LocationSelectorMap(
    addressQuery: String, 
    currentLat: Double?,
    currentLon: Double?,
    onMapTouch: (Boolean) -> Unit = {},
    onLocationSelected: (lat: Double, lon: Double, address: String, city: String, state: String, pin: String, forceOverwrite: Boolean) -> Unit
) {
    val context = LocalContext.current
    
    // Default to Kharagpur or somewhere in India if null
    val defaultLocation = LatLng(22.3365, 87.3228) 
    
    var markerPosition by remember { 
        mutableStateOf(
            if (currentLat != null && currentLon != null && (currentLat != 0.0 || currentLon != 0.0)) LatLng(currentLat, currentLon) 
            else defaultLocation
        ) 
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }

    // React to addressQuery changes
    LaunchedEffect(addressQuery) {
        if (addressQuery.isNotBlank() && addressQuery.length > 5) {
            kotlinx.coroutines.delay(1000) // Debounce typing
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocationName(addressQuery, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val pt = LatLng(addresses[0].latitude, addresses[0].longitude)
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            markerPosition = pt
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 15f))
                            onLocationSelected(pt.latitude, pt.longitude, "", "", "", "", false)
                        }
                    } else {
                        val pt = when {
                            addressQuery.lowercase().contains("bhiwadi") -> LatLng(28.21, 76.84)
                            addressQuery.lowercase().contains("delhi") -> LatLng(28.70, 77.10)
                            addressQuery.lowercase().contains("mumbai") -> LatLng(19.07, 72.87)
                            addressQuery.lowercase().contains("noida") -> LatLng(28.53, 77.39)
                            else -> null
                        }
                        if (pt != null) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                markerPosition = pt
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 15f))
                                onLocationSelected(pt.latitude, pt.longitude, "", "", "", "", false)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                        val pt = when {
                            addressQuery.lowercase().contains("bhiwadi") -> LatLng(28.21, 76.84)
                            addressQuery.lowercase().contains("delhi") -> LatLng(28.70, 77.10)
                            addressQuery.lowercase().contains("mumbai") -> LatLng(19.07, 72.87)
                            addressQuery.lowercase().contains("noida") -> LatLng(28.53, 77.39)
                            else -> null
                        }
                        if (pt != null) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                markerPosition = pt
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 15f))
                                onLocationSelected(pt.latitude, pt.longitude, "", "", "", "", false)
                            }
                        }
                }
            }
        }
    }

    // React to currentLat/currentLon changes
    LaunchedEffect(currentLat, currentLon) {
        if (currentLat != null && currentLon != null && (currentLat != 0.0 || currentLon != 0.0)) {
            val pt = LatLng(currentLat, currentLon)
            markerPosition = pt
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 15f))
        }
    }

    // Update marker position when map is dragged
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            markerPosition = cameraPositionState.position.target
        }
    }

    Column {
        val requestDisallowInterceptTouchEvent = remember { RequestDisallowInterceptTouchEvent() }
        @OptIn(ExperimentalComposeUiApi::class)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .pointerInteropFilter(requestDisallowInterceptTouchEvent = requestDisallowInterceptTouchEvent) { false }
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    markerPosition = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
                }
            ) {
                Marker(
                    state = MarkerState(position = markerPosition),
                    title = stringResource(R.string.selected_loc),
                    draggable = true
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val latLng = markerPosition
                Thread {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val street = addr.thoroughfare ?: addr.featureName ?: ""
                            val city = addr.locality ?: addr.subAdminArea ?: ""
                            val state = addr.adminArea ?: ""
                            val pin = addr.postalCode ?: ""
                            onLocationSelected(latLng.latitude, latLng.longitude, street, city, state, pin, true)
                        Toast.makeText(context, "Location Saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            onLocationSelected(latLng.latitude, latLng.longitude, "", "", "", "", true)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onLocationSelected(latLng.latitude, latLng.longitude, "", "", "", "", true)
                    }
                }.start()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Save Pin Location", fontWeight = FontWeight.Bold)
        }
    }
}
