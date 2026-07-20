import os

repo_path = 'app/src/main/java/com/example/localconnect/data/FirebaseRepository.kt'
with open(repo_path, 'r', encoding='utf-8') as f: content = f.read()

# Update applyForJob (Task 1)
old_apply = '''            createNotification(Notification(
                userId = newApp.employerId,
                title = "New Job Application",
                message = "${newApp.applicantName} applied for a job.",
                timestamp = System.currentTimeMillis(),
                type = "job_application"
            ))'''
new_apply = '''            createNotification(Notification(
                userId = newApp.employerId,
                title = "New Job Application",
                message = "${newApp.applicantName} applied for your job: ${newApp.jobTitle}",
                timestamp = System.currentTimeMillis(),
                type = "job_application",
                referenceId = newApp.jobId
            ))'''
content = content.replace(old_apply, new_apply)

# Update bookService (Task 4)
old_book = '''    fun bookService(request: ServiceRequest): Boolean {
        return try {
            val docRef = if (request.id.isEmpty()) serviceBookingsRef.document() else serviceBookingsRef.document(request.id)
            val newRequest = request.copy(id = docRef.id, appliedOn = System.currentTimeMillis())
            docRef.set(newRequest).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error booking service", e)
            false
        }
    }'''
new_book = '''    suspend fun bookService(request: ServiceRequest): Boolean {
        return try {
            val docRef = if (request.id.isEmpty()) serviceBookingsRef.document() else serviceBookingsRef.document(request.id)
            val newRequest = request.copy(id = docRef.id, appliedOn = System.currentTimeMillis())
            docRef.set(newRequest).await()
            
            // Task 4: Notification to provider
            createNotification(Notification(
                userId = request.providerId,
                title = "New Service Booking",
                message = "${request.customerName} booked your service: ${request.serviceTitle}",
                timestamp = System.currentTimeMillis(),
                type = "service_request",
                referenceId = request.serviceId
            ))
            
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error booking service", e)
            false
        }
    }'''
content = content.replace(old_book, new_book)

# Update updateServiceRequestStatus (Task 5)
old_status = '''    suspend fun updateServiceRequestStatus(request: ServiceRequest, newStatus: String): Boolean {
        return try {
            serviceBookingsRef.document(request.id).update("status", newStatus).await()
            
            createNotification(Notification(
                userId = request.seekerId,
                title = "Service Request $newStatus",
                message = "Your request for ${request.serviceTitle.ifBlank { "a service" }} has been $newStatus.",
                timestamp = System.currentTimeMillis(),
                type = "service_status_update",
                referenceId = request.serviceId
            ))
            
            true'''
new_status = '''    suspend fun updateServiceRequestStatus(request: ServiceRequest, newStatus: String): Boolean {
        return try {
            serviceBookingsRef.document(request.id).update("status", newStatus).await()
            
            val message = when(newStatus) {
                "Accepted" -> "Your booking for ${request.serviceTitle} has been confirmed by the provider!"
                "Rejected" -> "Your booking for ${request.serviceTitle} was declined by the provider."
                "Completed" -> "Your service ${request.serviceTitle} has been marked as completed."
                else -> "Your booking for ${request.serviceTitle} is now $newStatus."
            }
            
            createNotification(Notification(
                userId = request.seekerId,
                title = "Booking Update",
                message = message,
                timestamp = System.currentTimeMillis(),
                type = "service_status_update",
                referenceId = request.serviceId
            ))
            
            true'''
content = content.replace(old_status, new_status)

# Update updateApplicationStatus (Task 2 & 3 support)
old_app_status = '''    suspend fun updateApplicationStatus(application: JobApplication, status: String): Boolean {
        return try {
            jobAppsRef.document(application.id).update("status", status).await()
            
            val notificationMessage = when (status) {
                "Accepted", "Hired" -> "Congratulations! You have been hired for ${application.jobTitle}!"
                "Rejected" -> "Your application for ${application.jobTitle} was not selected this time."
                "Shortlisted" -> "Great news! You have been shortlisted for ${application.jobTitle}."
                else -> "Your application for ${application.jobTitle} has been marked as $status."
            }

            createNotification(Notification(
                userId = application.seekerId,
                title = "Application Update",
                message = notificationMessage,
                timestamp = System.currentTimeMillis(),
                type = "status_update",
                referenceId = application.jobId
            ))
            
            true'''

new_app_status = '''    suspend fun updateApplicationStatus(application: JobApplication, status: String): Boolean {
        return try {
            jobAppsRef.document(application.id).update("status", status).await()
            
            val recipientId = if (status == "Offer Accepted" || status == "Offer Rejected") application.employerId else application.seekerId
            
            val notificationMessage = when (status) {
                "Selected", "Shortlisted" -> "You are selected for the job ${application.jobTitle} at ${application.companyName}. Please accept it from your side."
                "Rejected" -> "Your application for ${application.jobTitle} was unfortunately not selected."
                "Offer Accepted" -> "${application.applicantName} has accepted your job offer for ${application.jobTitle}!"
                "Offer Rejected" -> "${application.applicantName} declined the job offer for ${application.jobTitle}."
                else -> "Your application for ${application.jobTitle} has been marked as $status."
            }
            
            val notificationType = if (status == "Offer Accepted" || status == "Offer Rejected") "job_application_update" else "status_update"

            createNotification(Notification(
                userId = recipientId,
                title = "Application Update",
                message = notificationMessage,
                timestamp = System.currentTimeMillis(),
                type = notificationType,
                referenceId = application.jobId
            ))
            
            true'''
content = content.replace(old_app_status, new_app_status)

with open(repo_path, 'w', encoding='utf-8') as f: f.write(content)
print("FirebaseRepository updated successfully")
