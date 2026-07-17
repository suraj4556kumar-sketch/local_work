package com.example.localconnect

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.localconnect.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.localconnect.ui.theme.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private val popularCities = listOf("Delhi", "Mumbai", "Kolkata", "Bangalore", "Chennai", "Hyderabad", "Pune", "Jaipur")

@Composable
fun LocationSetupScreen(
    session: UserSession,
    onBack: () -> Unit,
    onContinue: (location: String) -> Unit
) {
    var city by remember { mutableStateOf("") }
    val canContinue = city.isNotBlank()
    val context = androidx.compose.ui.platform.LocalContext.current
    var isFetchingLocation by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Permission granted, let them click again
        } else {
            android.widget.Toast.makeText(context, "Location permission is required.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun requestLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isFetchingLocation = true
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    scope.launch(Dispatchers.IO) {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val fetchedCity = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea ?: "Unknown"
                                withContext(Dispatchers.Main) {
                                    city = fetchedCity
                                    isFetchingLocation = false
                                }
                            } else {
                                withContext(Dispatchers.Main) { isFetchingLocation = false }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) { isFetchingLocation = false }
                        }
                    }
                } else {
                    android.widget.Toast.makeText(context, "Ensure GPS is on", android.widget.Toast.LENGTH_SHORT).show()
                    isFetchingLocation = false
                }
            }.addOnFailureListener {
                isFetchingLocation = false
            }
        } else {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8F5), Color(0xFFFFF3EE), Color(0xFFFFECE5))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Top bar ----
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom=8.dp, top=8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StepIndicator(current = 2, total = 2)
                Text(
                    text = "Back",
                    fontSize = 16.sp,
                    color = com.example.localconnect.ui.theme.OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBack() }.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Icon ----
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(36.dp), tint = com.example.localconnect.ui.theme.Primary) }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.location_title), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(6.dp))
            Text(stringResource(R.string.location_subtitle), fontSize = 14.sp, color = Color(0xFF9E9E9E))

            Spacer(modifier = Modifier.height(36.dp))

            // ---- City text field ----
            OnboardingTextField(
                value = city,
                onValueChange = { city = it },
                placeholder = stringResource(R.string.city_placeholder)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Use current location button ----
            Button(
                onClick = { requestLocation() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary)
            ) {
                if (isFetchingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = OrangePrimary, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.use_current_location), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---- Popular cities ----
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.popular_cities),
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(popularCities) { c ->
                        CityChip(label = c, isSelected = city == c, onClick = { city = c })
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    isSaving = true
                    val finalCity = city.trim()
                    val finalSession = session.copy(location = finalCity)
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    val userData = hashMapOf(
                        "phone" to finalSession.phone,
                        "name" to finalSession.name,
                        "dob" to finalSession.dob,
                        "gender" to finalSession.gender,
                        "location" to finalSession.location,
                        "language" to finalSession.language.code
                    )
                    db.collection("users").document(finalSession.firebaseUid).set(userData)
                        .addOnSuccessListener {
                            isSaving = false
                            onContinue(finalCity)
                        }
                        .addOnFailureListener { e ->
                            isSaving = false
                            android.widget.Toast.makeText(context, "Error saving profile: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                        }
                },
                enabled = canContinue && !isSaving,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFFB0B0B0)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (canContinue && !isSaving) 4.dp else 0.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.continue_btn), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CityChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(if (isSelected) OrangePrimary else Color.White, tween(200), label = "city_bg")
    val textColor by animateColorAsState(if (isSelected) Color.White else Color(0xFF555555), tween(200), label = "city_text")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(label, fontSize = 13.sp, color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationPreview() {
    com.example.localconnect.ui.theme.LocalConnectTheme {
        LocationSetupScreen(session = UserSession(name = "Sumit"), onBack = {}, onContinue = {})
    }
}
