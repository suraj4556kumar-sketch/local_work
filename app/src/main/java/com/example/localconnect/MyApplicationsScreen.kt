package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import com.example.localconnect.ui.theme.*
import com.example.localconnect.data.FirebaseRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.JobApplication



val MY_APPLICATIONS = listOf(
    JobApplication(id = "APP1024", jobTitle = "Driver (Personal)", companyName = "Verma Household", location = "Kharagpur, West Bengal", salary = "₹15,000 / month", appliedOnDate = "Oct 18, 2023", status = "Pending"),
    JobApplication(id = "APP2048", jobTitle = "Delivery Executive", companyName = "Fast Delivery Service", location = "Kolkata, West Bengal", salary = "₹18,000 / month", appliedOnDate = "Oct 10, 2023", status = "Accepted", feedback = "Great driving record"),
    JobApplication(id = "APP3096", jobTitle = "Office Boy", companyName = "ABC Office", location = "Kharagpur, West Bengal", salary = "₹12,000 / month", appliedOnDate = "Sep 25, 2023", status = "Rejected", feedback = "Looking for more experience"),
    JobApplication(id = "APP4128", jobTitle = "Cook", companyName = "Sri Krishna PG", location = "Kharagpur, West Bengal", salary = "₹20,000 / month", appliedOnDate = "Oct 21, 2023", status = "Pending")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(session: UserSession, onJobClick: (com.example.localconnect.data.Job) -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var filter by remember { mutableStateOf("All") }
    var applicationToDelete by remember { mutableStateOf<JobApplication?>(null) }
        var selectedApp by remember { mutableStateOf<JobApplication?>(null) }
    var selectedJob by remember { mutableStateOf<com.example.localconnect.data.Job?>(null) }
    
    LaunchedEffect(selectedApp) {
        if (selectedApp != null) {
            selectedJob = FirebaseRepository.getJob(selectedApp!!.jobId)
        } else {
            selectedJob = null
        }
    }

    var myApplications by remember { mutableStateOf<List<JobApplication>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(session.firebaseUid) {
        myApplications = FirebaseRepository.getMyJobApplications(session.firebaseUid)
        isLoading = false
    }

    val filteredApps = remember(filter, myApplications) {
        when (filter) {
            "All" -> myApplications
            "Pending" -> myApplications.filter { it.status == "Pending" || it.status == "Shortlisted" }
            "Accepted" -> myApplications.filter { it.status == "Selected" || it.status == "Offer Accepted" }
            "Rejected" -> myApplications.filter { it.status == "Rejected" || it.status == "Offer Rejected" }
            else -> myApplications
        }
    }

    val statsTotal = myApplications.size
    val statsPending = myApplications.count { it.status == "Pending" || it.status == "Shortlisted" }
    val statsAccepted = myApplications.count { it.status == "Selected" || it.status == "Offer Accepted" }
    val statsRejected = myApplications.count { it.status == "Rejected" || it.status == "Offer Rejected" }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp).statusBarsPadding()) {
                    Text(stringResource(R.string.my_apps_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(stringResource(R.string.track_apps), fontSize = 14.sp, color = Color(0xFF6B7280))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Summary Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTab("All", statsTotal, filter == "All", { filter = "All" }, Modifier.weight(1f))
                FilterTab("Pending", statsPending, filter == "Pending", { filter = "Pending" }, Modifier.weight(1f))
                FilterTab("Accepted", statsAccepted, filter == "Accepted", { filter = "Accepted" }, Modifier.weight(1f))
                FilterTab("Rejected", statsRejected, filter == "Rejected", { filter = "Rejected" }, Modifier.weight(1f))
            }

            // List
            if (filteredApps.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.no_apps_found), fontWeight = FontWeight.Bold, color = Color(0xFF1F2937), fontSize = 16.sp)
                    Text(stringResource(R.string.no_apps_desc), color = Color(0xFF6B7280), fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredApps) { app ->
                        ApplicationJobCard(
                            app = app, 
                            onClick = { selectedApp = it },
                            onLongClick = { applicationToDelete = it },
                            onStatusChange = { newStatus ->
                                val index = myApplications.indexOfFirst { it.id == app.id }
                                if (index != -1) {
                                    val updated = myApplications[index].copy(status = newStatus)
                                    val mutable = myApplications.toMutableList()
                                    mutable[index] = updated
                                    myApplications = mutable
                                    coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Modal
    if (applicationToDelete != null) {
        AlertDialog(
            onDismissRequest = { applicationToDelete = null },
            title = { Text(stringResource(R.string.remove_application)) },
            text = { Text("Are you sure you want to remove your application for ${applicationToDelete!!.jobTitle}?") },
            confirmButton = {
                Button(
                    onClick = {
                        val app = applicationToDelete!!
                        coroutineScope.launch {
                            val success = FirebaseRepository.deleteJobApplication(app.id)
                            if (success) {
                                myApplications = myApplications.filter { it.id != app.id }
                                Toast.makeText(context, "Application removed", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to remove application", Toast.LENGTH_SHORT).show()
                            }
                        }
                        applicationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(stringResource(R.string.remove), color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { applicationToDelete = null }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    if (selectedApp != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedApp = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.clickable { selectedApp = null }.padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("← ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                    Text(stringResource(R.string.back), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B7280))
                }
                
                Text(selectedApp!!.jobTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text(selectedApp!!.companyName, fontSize = 16.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(bottom = 24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 32.dp)) {
                    Surface(color = Color(0xFFF9FAFB), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.applied_on), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                            Spacer(modifier = Modifier.height(4.dp))
                            val formattedDate = if (selectedApp!!.appliedOnDate.isNotEmpty()) selectedApp!!.appliedOnDate 
                            else if (selectedApp!!.appliedOn > 0L) java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(selectedApp!!.appliedOn))
                            else "Unknown Date"
                            Text(formattedDate, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
                        }
                    }
                    Surface(color = Color(0xFFF9FAFB), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.current_status), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(selectedApp!!.status, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                        }
                    }
                }

                if (selectedApp!!.feedback.isNotEmpty()) {
                    Surface(
                        color = PrimaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryContainer),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.emp_feedback), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnPrimaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("\"${selectedApp!!.feedback}\"", fontSize = 14.sp, color = PrimaryDark)
                        }
                    }
                }

                Divider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(24.dp))

                if (selectedJob != null) {
                    Button(
                        onClick = { onJobClick(selectedJob!!) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E7FF), contentColor = Color(0xFF4338CA))
                    ) {
                        Text(stringResource(R.string.view_job_details), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (selectedApp!!.status == "Selected") {
                    val coroutineScope = rememberCoroutineScope()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val updated = selectedApp!!.copy(status = "Offer Accepted")
                                selectedApp = updated
                                val index = myApplications.indexOfFirst { it.id == updated.id }
                                if (index != -1) {
                                    val mutable = myApplications.toMutableList()
                                    mutable[index] = updated
                                    myApplications = mutable
                                }
                                coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                        ) {
                            Text(stringResource(R.string.accept_offer), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Button(
                            onClick = {
                                val updated = selectedApp!!.copy(status = "Offer Rejected")
                                selectedApp = updated
                                val index = myApplications.indexOfFirst { it.id == updated.id }
                                if (index != -1) {
                                    val mutable = myApplications.toMutableList()
                                    mutable[index] = updated
                                    myApplications = mutable
                                }
                                coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                        ) {
                            Text(stringResource(R.string.reject_offer), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (selectedApp!!.status == "Offer Accepted") {
                    if (selectedJob != null && selectedJob!!.contactPhone.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val callIntent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${selectedJob!!.contactPhone}"))
                                    context.startActivity(callIntent)
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                            ) {
                                Text(stringResource(R.string.call_employer), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Button(
                                onClick = {
                                    var waPhone = selectedJob!!.contactPhone
                                    if (waPhone.length == 10 && !waPhone.startsWith("+")) waPhone = "+91$waPhone"
                                    val waIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://api.whatsapp.com/send?phone=$waPhone"))
                                    try {
                                        context.startActivity(waIntent)
                                    } catch(e: Exception) {
                                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text(stringResource(R.string.whatsapp), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                            enabled = false
                        ) {
                            Text(if (selectedJob == null) "Loading Employer details..." else "Employer phone not available", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Button(
                    onClick = { selectedApp = null },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF1F2937))
                ) {
                    Text(stringResource(R.string.close), fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FilterTab(label: String, value: Int, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val bgColor = if (selected) Primary else Color.White
    val textColor = if (selected) Color.White else Color(0xFF4B5563)
    val borderColor = if (selected) Primary else Color(0xFFE5E7EB)

    Surface(
        onClick = onClick,
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor.copy(alpha = 0.8f))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationJobCard(app: JobApplication, onClick: (JobApplication) -> Unit, onStatusChange: (String) -> Unit, onLongClick: (JobApplication) -> Unit = {}) {
    val statusBgColor = when (app.status) {
        "Accepted" -> Color(0xFFFFF7ED)
        "Rejected" -> Color(0xFFFFF7ED)
        else -> Color(0xFFFEF9C3)
    }
    val statusTextColor = when (app.status) {
        "Accepted" -> Color(0xFFF97316)
        "Rejected" -> Color(0xFFF97316)
        else -> Color(0xFFA16207)
    }
    val statusBorderColor = when (app.status) {
        "Accepted" -> Color(0xFFFFF7ED)
        "Rejected" -> Color(0xFFFFF7ED)
        else -> Color(0xFFFEF08A)
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().combinedClickable(
            onClick = { onClick(app) },
            onLongClick = { onLongClick(app) }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(app.jobTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(app.companyName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6B7280))
                }
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusBorderColor)
                ) {
                    Text(
                        app.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(app.location, fontSize = 14.sp, color = Color(0xFF4B5563))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AttachMoney, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(app.salary, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Primary)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onClick(app) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF374151))
                ) {
                    Text(stringResource(R.string.view_details), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                if (app.status == "Selected") {
                    Button(
                        onClick = { onStatusChange("Offer Accepted") },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White)
                    ) {
                        Text(stringResource(R.string.accept_offer), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { onStatusChange("Offer Rejected") },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                    ) {
                        Text(stringResource(R.string.reject), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
