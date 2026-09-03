import os

models_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/data/Models.kt'
repo_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt'
leave_review_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/LeaveReviewScreen.kt'
service_screen_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyServiceBookingsScreen.kt'
apps_screen_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/MyApplicationsScreen.kt'

# 1. Update Models.kt
with open(models_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_model = """    val reviewType: String = "service", // "service" or "job"
    val rating: Float = 0f,           // 1.0 to 5.0"""
replacement_model = """    val reviewType: String = "service", // "service" or "job"
    val entityId: String = "",        // serviceId or jobId for per-item ratings
    val rating: Float = 0f,           // 1.0 to 5.0"""
content = content.replace(target_model, replacement_model)
with open(models_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 2. Update FirebaseRepository.kt
with open(repo_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_repo_submit = """            // Aggregate rating onto the target and send notification
            if (review.reviewType == "service" && review.referenceId.isNotEmpty()) {
                updateServiceRatingAggregation(review.targetId)"""
replacement_repo_submit = """            // Aggregate rating onto the target and send notification
            if (review.reviewType == "service" && review.referenceId.isNotEmpty()) {
                updateServiceRatingAggregation(review.entityId)"""
content = content.replace(target_repo_submit, replacement_repo_submit)

target_repo_submit_job = """            } else if (review.reviewType == "job" && review.referenceId.isNotEmpty()) {
                updateJobRatingAggregation(review.targetId)"""
replacement_repo_submit_job = """            } else if (review.reviewType == "job" && review.referenceId.isNotEmpty()) {
                updateJobRatingAggregation(review.entityId)"""
content = content.replace(target_repo_submit_job, replacement_repo_submit_job)

target_repo_agg_job = """    /** Recalculates and saves avg rating + completedJobs count for a given employer */
    private suspend fun updateJobRatingAggregation(employerId: String) {
        try {
            val allReviews = reviewsRef
                .whereEqualTo("targetId", employerId)
                .whereEqualTo("reviewType", "job")
                .get().await()
                .toObjects(Review::class.java)

            if (allReviews.isEmpty()) return

            val avgRating = allReviews.map { it.rating }.average()
            val totalReviews = allReviews.size

            val jobs = jobsRef.whereEqualTo("employerId", employerId).get().await()
            for (doc in jobs.documents) {
                doc.reference.update(
                    mapOf(
                        "rating" to avgRating,
                        "completedJobs" to totalReviews
                    )
                ).await()
            }
            
            usersRef.document(employerId).update(
                mapOf(
                    "rating" to avgRating,
                    "completedJobs" to totalReviews
                )
            ).await()
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error aggregating job rating", e)
        }
    }"""
replacement_repo_agg_job = """    /** Recalculates and saves avg rating + completedJobs count for a specific job */
    private suspend fun updateJobRatingAggregation(jobId: String) {
        if (jobId.isEmpty()) return
        try {
            val allReviews = reviewsRef
                .whereEqualTo("entityId", jobId)
                .whereEqualTo("reviewType", "job")
                .get().await()
                .toObjects(Review::class.java)

            if (allReviews.isEmpty()) return

            val avgRating = allReviews.map { it.rating }.average()
            val totalReviews = allReviews.size

            jobsRef.document(jobId).update(
                mapOf(
                    "rating" to avgRating,
                    "completedJobs" to totalReviews
                )
            ).await()
            
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error aggregating job rating", e)
        }
    }"""
content = content.replace(target_repo_agg_job, replacement_repo_agg_job)

target_repo_agg_svc = """    /** Recalculates and saves avg rating + completedJobs count for a given service provider */
    private suspend fun updateServiceRatingAggregation(providerId: String) {
        try {
            val allReviews = reviewsRef
                .whereEqualTo("targetId", providerId)
                .whereEqualTo("reviewType", "service")
                .get().await()
                .toObjects(Review::class.java)

            if (allReviews.isEmpty()) return

            val avgRating = allReviews.map { it.rating }.average()
            val totalReviews = allReviews.size

            // Update all ServiceTask documents belonging to this provider
            val tasks = servicesRef.whereEqualTo("providerId", providerId).get().await()
            for (doc in tasks.documents) {
                doc.reference.update(
                    mapOf(
                        "rating" to avgRating,
                        "completedJobs" to totalReviews
                    )
                ).await()
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error aggregating rating", e)
        }
    }"""
replacement_repo_agg_svc = """    /** Recalculates and saves avg rating + completedJobs count for a specific service */
    private suspend fun updateServiceRatingAggregation(serviceId: String) {
        if (serviceId.isEmpty()) return
        try {
            val allReviews = reviewsRef
                .whereEqualTo("entityId", serviceId)
                .whereEqualTo("reviewType", "service")
                .get().await()
                .toObjects(Review::class.java)

            if (allReviews.isEmpty()) return

            val avgRating = allReviews.map { it.rating }.average()
            val totalReviews = allReviews.size

            servicesRef.document(serviceId).update(
                mapOf(
                    "rating" to avgRating,
                    "completedJobs" to totalReviews
                )
            ).await()
            
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error aggregating rating", e)
        }
    }"""
content = content.replace(target_repo_agg_svc, replacement_repo_agg_svc)
with open(repo_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 3. Update LeaveReviewScreen.kt
with open(leave_review_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_leave_review = """    targetId: String,
    targetName: String,"""
replacement_leave_review = """    targetId: String,
    targetName: String,
    entityId: String = "",
"""
content = content.replace(target_leave_review, replacement_leave_review)

target_leave_review2 = """                                targetName = targetName,
                                referenceId = referenceId,
                                reviewType = reviewType,"""
replacement_leave_review2 = """                                targetName = targetName,
                                referenceId = referenceId,
                                reviewType = reviewType,
                                entityId = entityId,"""
content = content.replace(target_leave_review2, replacement_leave_review2)
with open(leave_review_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 4. Update MyServiceBookingsScreen.kt
with open(service_screen_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_service_screen = """            targetId = request.providerId,
            targetName = providerUser?.name ?: serviceTask?.providerName ?: request.providerId,"""
replacement_service_screen = """            targetId = request.providerId,
            targetName = providerUser?.name ?: serviceTask?.providerName ?: request.providerId,
            entityId = request.serviceId,"""
content = content.replace(target_service_screen, replacement_service_screen)
with open(service_screen_path, 'w', encoding='utf-8') as f:
    f.write(content)

# 5. Update MyApplicationsScreen.kt
with open(apps_screen_path, 'r', encoding='utf-8') as f:
    content = f.read()
target_apps_screen = """            targetId = appToReview!!.employerId,
            targetName = appToReview!!.companyName,"""
replacement_apps_screen = """            targetId = appToReview!!.employerId,
            targetName = appToReview!!.companyName,
            entityId = appToReview!!.jobId,"""
content = content.replace(target_apps_screen, replacement_apps_screen)
with open(apps_screen_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patch applied to make ratings per-service.")
