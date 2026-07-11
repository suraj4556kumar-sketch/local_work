package com.example.localconnect

import androidx.compose.ui.res.stringResource
import com.example.localconnect.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.Job
import com.example.localconnect.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    job: Job,
    session: UserSession,
    onBack: () -> Unit,
    onApply: () -> Unit,
    onSimilarJobClick: (Job) -> Unit
) {
    val scrollState = rememberScrollState()
        var hasApplied by remember { mutableStateOf(false) }
    var checkingApplication by remember { mutableStateOf(true) }

    LaunchedEffect(job.id, session.firebaseUid) {
        val myApps = com.example.localconnect.data.FirebaseRepository.getMyJobApplications(session.firebaseUid)
        hasApplied = myApps.any { it.jobId == job.id }
        checkingApplication = false
    }

    LaunchedEffect(job) {
        scrollState.scrollTo(0)
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            Surface(
                color = OrangePrimary,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.job_details),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = stringResource(R.string.back),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(8.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (checkingApplication) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color.Gray),
                            enabled = false
                        ) {
                            Text(stringResource(R.string.loading), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else if (hasApplied) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.applied), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = onApply,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316), contentColor = Color.White)
                        ) {
                            Text(stringResource(R.string.apply_now), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp)
        ) {
            
            // --- Hero Section ---
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = job.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                lineHeight = 30.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = job.companyName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Text(" • ", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                
                                Row(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF7ED), RoundedCornerShape(4.dp))
                                        .border(1.dp, Color(0xFFFFF7ED), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFF97316))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.verified_employer), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFFEDD5), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Restaurant, contentDescription = null, modifier = Modifier.size(28.dp), tint = com.example.localconnect.ui.theme.Primary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(job.location, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(job.postingDate, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Quick Highlights Grid ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Salary
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Text("₹", fontSize = 20.sp, color = Color(0xFFF97316), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.salary), fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(job.salary, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                    
                    // Experience
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Work, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.experience), fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(job.experienceReq, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Education
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.School, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.education), fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(job.educationReq.ifBlank { "Not Required" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                    
                    // Empty placeholder to keep Education aligned
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Job Description ---
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.job_description), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    // Audio Controls
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(stringResource(R.string.audio_description), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF334155)),
                                    border = borderStroke(Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp), tint = com.example.localconnect.ui.theme.Primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.listen), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF334155)),
                                    border = borderStroke(Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.Headphones, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.hindi), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF334155)),
                                    border = borderStroke(Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.Headphones, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.bengali), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        job.description,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        lineHeight = 22.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    if (job.responsibilities.isNotEmpty()) {
                        Text(stringResource(R.string.skills_responsibilities), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            job.responsibilities.forEach { res ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp).padding(top = 4.dp), tint = com.example.localconnect.ui.theme.Primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(res, fontSize = 14.sp, color = Color(0xFF475569))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Documents Required ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.documents_required), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    val reqs = job.requirements + job.documentsRequired
                    
                    if (reqs.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reqs.forEach { req ->
                                ReqChip(req, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    } else {
                        Text(stringResource(R.string.no_specific_documents_mentioned), fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Benefits ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.benefits), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    if (job.benefits.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            job.benefits.forEach { benefit ->
                                BenefitChip(benefit, Color(0xFFFFF7ED), Color(0xFFF97316), Color(0xFFFFF7ED))
                            }
                        }
                    } else {
                        Text(stringResource(R.string.no_specific_benefits_mentioned), fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Employer Details ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.employer_details), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFF1F5F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(job.companyName.take(2).uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(job.companyName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFB300))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("4.6", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(stringResource(R.string.jobs_posted_15), fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                    
                    // Removed call and chat options
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Job Location ---
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text(stringResource(R.string.job_location), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Box(
                            modifier = Modifier.background(Color(0xFFFFF7ED), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.LocationOn, contentDescription=null, modifier=Modifier.size(10.dp), tint=Color(0xFFF97316)); Text(" ${job.distanceKm} km away", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316)) }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(job.location, fontSize = 14.sp, color = Color(0xFF475569), modifier = Modifier.padding(bottom = 12.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Mock Map
                        Box(modifier = Modifier.fillMaxSize().background(Color(0x1A000000)), contentAlignment = Alignment.Center) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.9f),
                                shadowElevation = 2.dp
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.view_on_map), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Similar Jobs ---
            Column(modifier = Modifier.padding(vertical = 24.dp)) {
                Text(stringResource(R.string.similar_jobs), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val similarJobs = getDummyJobs(session.language.code).filter { it.id != job.id }.take(3)
                    
                    similarJobs.forEach { simJob ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = borderStroke(Color(0xFFF1F5F9)),
                            shadowElevation = 1.dp,
                            modifier = Modifier.width(220.dp).clickable { onSimilarJobClick(simJob) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(simJob.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(simJob.companyName, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(bottom = 12.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                                    Text(simJob.salary, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(stringResource(R.string.view), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for fixed bottom bar
        }
    }
}

@Composable
fun borderStroke(color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(1.dp, color)
}

@Composable
fun ReqChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFF97316))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 12.sp, color = Color(0xFF475569))
        }
    }
}

@Composable
fun BenefitChip(text: String, bgColor: Color, textColor: Color, borderColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(50))
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}
