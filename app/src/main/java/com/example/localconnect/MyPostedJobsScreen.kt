package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.Job
import com.example.localconnect.ui.theme.OrangePrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostedJobsScreen(
    session: UserSession,
    onBack: () -> Unit,
    onNavigateToPostNew: () -> Unit,
    onJobClick: (String) -> Unit
) {
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var jobToDelete by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        jobs = FirebaseRepository.getMyPostedJobs(session.phone)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.my_posted_jobs), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.ui.graphics.Color.White) },
                actions = {
                    TextButton(onClick = onBack) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.back), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF97316), titleContentColor = androidx.compose.ui.graphics.Color.White, navigationIconContentColor = androidx.compose.ui.graphics.Color.White, actionIconContentColor = androidx.compose.ui.graphics.Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                PaddingValues(16.dp).let {
                    Button(
                        onClick = onNavigateToPostNew,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.post_new_job), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.no_posted_jobs_yet), color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(jobs) { job ->
                    PostedJobCard(job, onClick = { onJobClick(job.id) }, onLongClick = { jobToDelete = job })
                }
            }
        }

        if (jobToDelete != null) {
            AlertDialog(
                onDismissRequest = { jobToDelete = null },
                title = { Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.remove_job)) },
                text = { Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.confirm_remove_job)) },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            val success = FirebaseRepository.deleteJob(jobToDelete!!.id)
                            if (success) {
                                jobs = jobs.filter { it.id != jobToDelete!!.id }
                            }
                            jobToDelete = null
                        }
                    }) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.remove), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { jobToDelete = null }) {
                        Text(androidx.compose.ui.res.stringResource(com.example.localconnect.R.string.cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostedJobCard(job: Job, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = job.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = job.companyName.ifBlank { "Company Name" },
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Active",
                        color = Color(0xFFF97316),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(job.location.ifBlank { job.city }, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.salary.ifBlank { "₹${job.salaryMin} / ${job.salaryType}" },
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF97316),
                    fontSize = 14.sp
                )
                Text(
                    text = "View Applicants →",
                    color = OrangePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
