import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyApplicationsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update signature
target_sig = """@Composable
fun ApplicationJobCard(app: JobApplication, onClick: (JobApplication) -> Unit, onStatusChange: (String) -> Unit, onLongClick: (JobApplication) -> Unit = {}) {"""
replacement_sig = """@Composable
fun ApplicationJobCard(app: JobApplication, onClick: (JobApplication) -> Unit, onStatusChange: (String) -> Unit, onLongClick: (JobApplication) -> Unit = {}, onRateEmployer: () -> Unit = {}) {"""
content = content.replace(target_sig, replacement_sig)

# 2. Update caller
target_caller = """                            onStatusChange = { newStatus ->
                                val index = myApplications.indexOfFirst { it.id == app.id }
                                if (index != -1) {
                                    val updated = myApplications[index].copy(status = newStatus)
                                    val mutable = myApplications.toMutableList()
                                    mutable[index] = updated
                                    myApplications = mutable
                                    coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                                }
                            }
                        )"""
replacement_caller = """                            onStatusChange = { newStatus ->
                                val index = myApplications.indexOfFirst { it.id == app.id }
                                if (index != -1) {
                                    val updated = myApplications[index].copy(status = newStatus)
                                    val mutable = myApplications.toMutableList()
                                    mutable[index] = updated
                                    myApplications = mutable
                                    coroutineScope.launch { FirebaseRepository.applyForJob(updated) }
                                }
                            },
                            onRateEmployer = { appToReview = app }
                        )"""
content = content.replace(target_caller, replacement_caller)

# 3. Add button in Card
target_card_end = """                    Button(
                        onClick = { onStatusChange("Offer Rejected") },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                    ) {
                        Text(stringResource(R.string.reject_offer), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}"""
replacement_card_end = """                    Button(
                        onClick = { onStatusChange("Offer Rejected") },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                    ) {
                        Text(stringResource(R.string.reject_offer), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            if (app.status == "Offer Accepted" && !app.isReviewed) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onRateEmployer() },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rate Employer", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}"""
content = content.replace(target_card_end, replacement_card_end)

# 4. Remove button from Bottom Sheet
target_bs = """                    if (!selectedApp!!.isReviewed) {
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
                    }"""
content = content.replace(target_bs, "")

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Moved Rate Employer button to ApplicationJobCard.")
