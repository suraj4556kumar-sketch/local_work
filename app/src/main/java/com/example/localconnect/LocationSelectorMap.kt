package com.example.localconnect

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import java.util.Locale

@Composable
fun LocationSelectorMap(
    addressQuery: String, 
    currentLat: Double?,
    currentLon: Double?,
    onLocationSelected: (lat: Double, lon: Double, address: String, city: String, state: String, pin: String) -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { ctx ->
            MapView(ctx).apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                
                val newMarker = Marker(this).apply {
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                overlays.add(newMarker)
                marker = newMarker

                val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let {
                            newMarker.position = it
                            controller.animateTo(it)
                            Thread {
                                try {
                                    val geocoder = Geocoder(ctx, Locale.getDefault())
                                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val addr = addresses[0]
                                        val street = addr.thoroughfare ?: addr.featureName ?: ""
                                        val city = addr.locality ?: addr.subAdminArea ?: ""
                                        val state = addr.adminArea ?: ""
                                        val pin = addr.postalCode ?: ""
                                        onLocationSelected(it.latitude, it.longitude, street, city, state, pin)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }.start()
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean = false
                })
                overlays.add(0, mapEventsOverlay)
                mapView = this
            }
        },
        update = { view ->
            if (currentLat != null && currentLon != null) {
                val pt = GeoPoint(currentLat, currentLon)
                marker?.position = pt
                view.controller.setCenter(pt)
            } else if (addressQuery.isNotBlank() && addressQuery.length > 5) {
                Thread {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocationName(addressQuery, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val pt = GeoPoint(addresses[0].latitude, addresses[0].longitude)
                            view.post {
                                marker?.position = pt
                                view.controller.setCenter(pt)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }
    )
}
