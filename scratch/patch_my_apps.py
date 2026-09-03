import os
import re

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyApplicationsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add appToReview state
target_state = """    var selectedApp by remember { mutableStateOf<JobApplication?>(null) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }"""
replacement_state = """    var selectedApp by remember { mutableStateOf<JobApplication?>(null) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }
    var appToReview by remember { mutableStateOf<JobApplication?>(null) }"""
content = content.replace(target_state, replacement_state)

# 2. Add Rate Employer Button inside Offer Accepted block
target_rate = """                    } else {
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
                }"""
replacement_rate = """                    } else {
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
                    
                    if (!selectedApp!!.isReviewed) {
                        Button(
                            onClick = { 
                                appToReview = selectedApp
                                selectedApp = null 
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                        ) {
                            Text("Rate Employer", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }"""
content = content.replace(target_rate, replacement_rate)

# 3. Add LeaveReviewBottomSheet logic at the bottom of MyApplicationsScreen
target_sheet = """    }
}

@Composable"""
replacement_sheet = """    }
    
    if (appToReview != null) {
        LeaveReviewBottomSheet(
            reviewType = "job",
            targetId = appToReview!!.employerId,
            targetName = appToReview!!.companyName,
            serviceTitle = appToReview!!.jobTitle,
            referenceId = appToReview!!.id,
            reviewerUid = session.phone,
            reviewerName = session.name,
            onDismiss = { appToReview = null },
            onSubmitted = {
                coroutineScope.launch {
                    FirebaseRepository.markJobApplicationReviewed(appToReview!!.id)
                    // Update locally
                    val index = myApplications.indexOfFirst { it.id == appToReview!!.id }
                    if (index != -1) {
                        myApplications[index] = myApplications[index].copy(isReviewed = true)
                    }
                    appToReview = null
                }
            }
        )
    }
}

@Composable"""
content = content.replace(target_sheet, replacement_sheet)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated MyApplicationsScreen.kt with Rating Flow.")
