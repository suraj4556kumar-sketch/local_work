import os

job_search_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/JobSearchScreen.kt'
posted_jobs_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyPostedJobsScreen.kt'
job_details_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'

# 1. Update JobSearchScreen.kt
with open(job_search_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_search = """                if (job.rating > 0.0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", job.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                }"""
replacement_search = """                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (job.rating > 0.0) String.format("%.1f", job.rating) else "New", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))"""
content = content.replace(target_search, replacement_search)
with open(job_search_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 2. Update MyPostedJobsScreen.kt
with open(posted_jobs_path, 'r', encoding='utf-8') as f:
    content = f.read()
content = content.replace(target_search, replacement_search)
with open(posted_jobs_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 3. Update JobDetailsScreen.kt
with open(job_details_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_details = """                                if (job.rating > 0.0) {
                                    Text(" • ", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(String.format("%.1f", job.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                                    }
                                }"""
replacement_details = """                                Text(" • ", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (job.rating > 0.0) String.format("%.1f", job.rating) else "New", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                                }"""
content = content.replace(target_details, replacement_details)
with open(job_details_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Always showing rating stars on job screens.")
