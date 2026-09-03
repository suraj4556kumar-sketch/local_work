import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update submitReview
target_submit = """            // Aggregate rating onto the ServiceTask if this is a service review
            if (review.reviewType == "service" && review.referenceId.isNotEmpty()) {
                updateServiceRatingAggregation(review.targetId)
            }"""
replacement_submit = """            // Aggregate rating onto the target and send notification
            if (review.reviewType == "service" && review.referenceId.isNotEmpty()) {
                updateServiceRatingAggregation(review.targetId)
                createNotification(Notification(
                    userId = review.targetId,
                    title = "New Rating",
                    message = "You received a ${review.rating.toInt()}-star rating for your service.",
                    type = "rating",
                    referenceId = review.referenceId
                ))
            } else if (review.reviewType == "job" && review.referenceId.isNotEmpty()) {
                updateJobRatingAggregation(review.targetId)
                createNotification(Notification(
                    userId = review.targetId,
                    title = "New Rating",
                    message = "You received a ${review.rating.toInt()}-star rating from an applicant.",
                    type = "rating",
                    referenceId = review.referenceId
                ))
            }"""
content = content.replace(target_submit, replacement_submit)

# 2. Add updateJobRatingAggregation
target_agg = """    /** Recalculates and saves avg rating + completedJobs count for a given service provider */"""
replacement_agg = """    /** Recalculates and saves avg rating + completedJobs count for a given employer */
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
    }

    /** Recalculates and saves avg rating + completedJobs count for a given service provider */"""
content = content.replace(target_agg, replacement_agg)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated FirebaseRepository.kt for ratings & notifications.")
