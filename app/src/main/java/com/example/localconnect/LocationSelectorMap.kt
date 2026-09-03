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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.offset
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

@Composable
fun LocationSelectorMap(
    addressQuery: String, 
    isAddressFromMap: Boolean = false,
    currentLat: Double?,
    currentLon: Double?,
    onMapTouch: (Boolean) -> Unit = {},
    contentBelowMap: @Composable () -> Unit = {},
    contentBeforeSave: @Composable () -> Unit = {},
    onLocationSelected: (lat: Double, lon: Double, address: String, city: String, state: String, pin: String, forceOverwrite: Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Default to Kharagpur or somewhere in India if null
    val defaultLocation = LatLng(22.3365, 87.3228) 
    
    var markerPosition by remember { mutableStateOf(defaultLocation) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val fetchAddress = { latLng: LatLng, showToast: Boolean ->
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val rawFeature = addr.featureName ?: ""
                        val feature = if (rawFeature.contains("+") || rawFeature.matches(Regex("^[0-9]+$"))) "" else rawFeature
                        val street = listOfNotNull(feature, addr.thoroughfare, addr.subLocality)
                            .filter { it.isNotBlank() }
                            .distinct()
                            .joinToString(", ")
                        val city = addr.locality ?: addr.subAdminArea ?: ""
                        val state = addr.adminArea ?: ""
                        val pin = addr.postalCode ?: ""
                        onLocationSelected(latLng.latitude, latLng.longitude, street, city, state, pin, true)
                        if (showToast) {
                            Toast.makeText(context, "Location Saved!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onLocationSelected(latLng.latitude, latLng.longitude, "", "", "", "", true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onLocationSelected(latLng.latitude, latLng.longitude, "", "", "", "", true)
                }
            }
        }
    }



    
    // React to manual address typing
    LaunchedEffect(addressQuery) {
        if (!isAddressFromMap && addressQuery.isNotBlank() && addressQuery.length > 5) {
            delay(1500) // Debounce
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(addressQuery, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val pt = LatLng(addr.latitude, addr.longitude)
                    withContext(Dispatchers.Main) {
                        markerPosition = pt
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 15f))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    var isUserDrag by remember { mutableStateOf(false) }

    // Update marker position when map is dragged
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
                isUserDrag = true
            }
        } else {
            markerPosition = cameraPositionState.position.target
            if (isUserDrag) {
                fetchAddress(markerPosition, false)
                isUserDrag = false
            }
        }
    }

    Column {
        val requestDisallowInterceptTouchEvent = remember { RequestDisallowInterceptTouchEvent() }
        @OptIn(ExperimentalComposeUiApi::class)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                            val down = event.changes.any { it.pressed }
                            onMapTouch(down)
                        }
                    }
                }
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    markerPosition = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
                    fetchAddress(latLng, false)
                }
            ) {
            }
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Center Map Pin",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .offset(y = (-20).dp)
            )
        }
        contentBelowMap()
        contentBeforeSave()
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                fetchAddress(markerPosition, true)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.save_pin_location), fontWeight = FontWeight.Bold)
        }
    }
}
