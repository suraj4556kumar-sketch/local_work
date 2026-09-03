import os

job_search_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/JobSearchScreen.kt'
posted_jobs_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyPostedJobsScreen.kt'
job_details_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'

# 1. Update JobSearchScreen.kt
with open(job_search_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_search = """            Spacer(modifier = Modifier.height(4.dp))
            TranslatedText(job.companyName, fontSize = 14.sp, color = OrangePrimary, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))"""
replacement_search = """            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TranslatedText(job.companyName, fontSize = 14.sp, color = OrangePrimary, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (job.rating > 0.0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", job.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))"""
content = content.replace(target_search, replacement_search)
with open(job_search_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 2. Update MyPostedJobsScreen.kt
with open(posted_jobs_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_posted = """            TranslatedText(job.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            TranslatedText(job.companyName, fontSize = 14.sp, color = Color(0xFFF97316), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))"""
replacement_posted = """            TranslatedText(job.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TranslatedText(job.companyName, fontSize = 14.sp, color = Color(0xFFF97316), fontWeight = FontWeight.Medium)
                if (job.rating > 0.0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", job.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))"""
content = content.replace(target_posted, replacement_posted)
with open(posted_jobs_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 3. Update JobDetailsScreen.kt
with open(job_details_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_details = """                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFF97316))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.verified_employer), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316))
                                }
                            }"""
replacement_details = """                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFF97316))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.verified_employer), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF97316))
                                }
                                if (job.rating > 0.0) {
                                    Text(" • ", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(String.format("%.1f", job.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                                    }
                                }
                            }"""
content = content.replace(target_details, replacement_details)
with open(job_details_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Added rating UI to job screens.")
