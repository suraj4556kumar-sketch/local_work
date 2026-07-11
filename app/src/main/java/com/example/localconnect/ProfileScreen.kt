package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    session: UserSession,
    onUpdateSession: (UserSession) -> Unit,
    onLogout: () -> Unit,
    onNavigateToPostJob: () -> Unit = {},
    onNavigateToPostService: () -> Unit = {}
) {
        var showLanguageSheet by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    val initial = session.name.firstOrNull()?.uppercaseChar()?.toString()
        ?: session.phone.firstOrNull()?.toString() ?: "U"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.profile_tab_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(OrangePrimary, OrangeLight))),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = session.name.ifBlank { "User" },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { showNameDialog = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Name", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileRow(icon = Icons.Filled.Phone, label = stringResource(R.string.phone_label), value = session.phone)
                HorizontalDivider(color = Color(0xFFF0F0F0))
                ProfileRow(icon = Icons.Filled.Cake, label = stringResource(R.string.dob_label), value = session.dob)
                HorizontalDivider(color = Color(0xFFF0F0F0))
                ProfileRow(icon = Icons.Filled.Person, label = stringResource(R.string.gender_label), value = session.gender)
                HorizontalDivider(color = Color(0xFFF0F0F0))
                ProfileRow(icon = Icons.Filled.LocationOn, label = stringResource(R.string.location_label), value = session.location)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageSheet = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.change_language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = session.language.nativeName,
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFF0F0),
                contentColor = Color(0xFFD32F2F)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(stringResource(R.string.logout), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.change_language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                supportedLanguages.forEach { lang ->
                    val isSelected = session.language.code == lang.code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (!isSelected) {
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("users").document(session.firebaseUid)
                                        .update("language", lang.code)
                                    onUpdateSession(session.copy(language = lang))
                                }
                                showLanguageSheet = false
                            }
                            .background(if (isSelected) Color(0xFFFFF3EE) else Color.Transparent)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lang.nativeName,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) OrangeDark else Color(0xFF1A1A1A),
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = com.example.localconnect.ui.theme.Primary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

    if (showNameDialog) {
        var newName by remember { mutableStateOf(session.name) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            containerColor = Color.White,
            title = { Text(stringResource(R.string.change_name), fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.new_name)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        focusedLabelColor = OrangePrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null && newName.isNotBlank()) {
                            FirebaseFirestore.getInstance().collection("users").document(uid)
                                .update("name", newName)
                                .addOnSuccessListener {
                                    onUpdateSession(session.copy(name = newName))
                                    showNameDialog = false
                                }
                        } else {
                            if (newName.isNotBlank()) {
                                onUpdateSession(session.copy(name = newName))
                            }
                            showNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(stringResource(R.string.save), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    }
}

@Composable
private fun ProfileRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = androidx.compose.ui.graphics.Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color(0xFF9E9E9E))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value.ifBlank { "Not specified" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}
