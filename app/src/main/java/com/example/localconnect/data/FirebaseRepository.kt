package com.example.localconnect.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

object FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    private val usersRef = db.collection("users")
    private val jobsRef = db.collection("jobs")
    private val servicesRef = db.collection("services")
    private val jobAppsRef = db.collection("job_applications")
    private val serviceBookingsRef = db.collection("service_bookings")
    private val notificationsRef = db.collection("notifications")

    // --- Users ---
    suspend fun saveUser(user: User): Boolean {
        return try {
            usersRef.document(user.uid).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error saving user", e)
            false
        }
    }

    suspend fun getUser(uid: String): User? {
        return try {
            val snapshot = usersRef.document(uid).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting user", e)
            null
        }
    }

    // --- Jobs ---
    suspend fun getJob(jobId: String): Job? {
        return try {
            val snapshot = jobsRef.document(jobId).get().await()
            snapshot.toObject(Job::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting job", e)
            null
        }
    }

    suspend fun postJob(job: Job): Boolean {
        return try {
            val docRef = if (job.id.isEmpty()) jobsRef.document() else jobsRef.document(job.id)
            val newJob = job.copy(id = docRef.id, timestamp = System.currentTimeMillis())
            docRef.set(newJob).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error posting job", e)
            false
        }
    }

    
    suspend fun deleteJob(jobId: String): Boolean {
        return try {
            jobsRef.document(jobId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error deleting job", e)
            false
        }
    }

    suspend fun getMyPostedJobs(employerId: String): List<Job> {
        return try {
            val snapshot = jobsRef.whereEqualTo("employerId", employerId).get().await()
            val jobs = snapshot.toObjects(Job::class.java)
            jobs.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting my posted jobs", e)
            emptyList()
        }
    }

    fun getJobsFlow(): kotlinx.coroutines.flow.Flow<List<Job>> = kotlinx.coroutines.flow.callbackFlow {
        val listener = jobsRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val jobs = snapshot.toObjects(Job::class.java)
                    trySend(jobs).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getJobs(): List<Job> {
        return try {
            val snapshot = jobsRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            snapshot.toObjects(Job::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting jobs", e)
            emptyList()
        }
    }

    // --- Services ---
    suspend fun getMyPostedServices(providerId: String): List<ServiceTask> {
        return try {
            val snapshot = servicesRef.whereEqualTo("providerId", providerId).get().await()
            val services = snapshot.toObjects(ServiceTask::class.java)
            services.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting my posted services", e)
            emptyList()
        }
    }

    suspend fun postService(service: ServiceTask): Boolean {
        return try {
            val docRef = if (service.id.isEmpty()) servicesRef.document() else servicesRef.document(service.id)
            val newService = service.copy(id = docRef.id, timestamp = System.currentTimeMillis())
            docRef.set(newService).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error posting service", e)
            false
        }
    }

    suspend fun getServices(): List<ServiceTask> {
        return try {
            val snapshot = servicesRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            snapshot.toObjects(ServiceTask::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting services", e)
            emptyList()
        }
    }

    // --- Job Applications ---
    suspend fun uploadDocument(uri: android.net.Uri, context: android.content.Context): String {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: ""
            
            val inputStream = contentResolver.openInputStream(uri)
                ?: throw Exception("Could not open file stream from device.")
            
            var bytes = inputStream.readBytes()
            inputStream.close()
            
            if (bytes.isEmpty()) {
                throw Exception("Selected file is empty or unreadable (0 bytes).")
            }
            
            // If it's an image, scale it down and compress it
            if (mimeType.startsWith("image/")) {
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) {
                    // Max dimension 800
                    val maxDim = 800f
                    val ratio = java.lang.Math.min(maxDim / bitmap.width, maxDim / bitmap.height)
                    val scaledBitmap = if (ratio < 1f) {
                        android.graphics.Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
                    } else {
                        bitmap
                    }
                    
                    val outputStream = java.io.ByteArrayOutputStream()
                    scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, outputStream)
                    bytes = outputStream.toByteArray()
                    
                    val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                    return@withContext "data:image/jpeg;name=doc.jpg;base64,$base64"
                }
            }
            
            // If not an image or bitmap decoding failed, check size
            if (bytes.size > 800 * 1024) { // 800 KB limit for Firestore
                throw Exception("File is too large (>${bytes.size / 1024} KB). Please select a file under 800 KB.")
            }
            
            var ext = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (ext.isNullOrEmpty()) {
                ext = if (mimeType.contains("pdf")) "pdf" else if (mimeType.contains("word")) "docx" else "bin"
            }
            val finalMime = if (mimeType.isNotEmpty()) mimeType else "application/octet-stream"
            val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            return@withContext "data:$finalMime;name=doc.$ext;base64,$base64"
        }
    }

    suspend fun applyForJob(application: JobApplication): Boolean {
        return try {
            val docRef = if (application.id.isEmpty()) jobAppsRef.document() else jobAppsRef.document(application.id)
            val newApp = application.copy(id = docRef.id, appliedOn = System.currentTimeMillis())
            docRef.set(newApp).await()
            
            // Notify employer
            createNotification(Notification(
                userId = newApp.employerId,
                title = "New Job Application",
                message = "${newApp.applicantName} applied for a job.",
                timestamp = System.currentTimeMillis(),
                type = "job_application"
            ))
            
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error applying for job", e)
            false
        }
    }

    
    suspend fun getJobApplicationsForJob(jobId: String): List<JobApplication> {
        return try {
            val snapshot = jobAppsRef.whereEqualTo("jobId", jobId).get().await()
            val apps = snapshot.toObjects(JobApplication::class.java)
            apps.sortedByDescending { it.appliedOn }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting applications for job", e)
            emptyList()
        }
    }

    suspend fun getMyJobApplications(seekerId: String): List<JobApplication> {
        return try {
            val snapshot = jobAppsRef.whereEqualTo("seekerId", seekerId).get().await()
            snapshot.toObjects(JobApplication::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting applications", e)
            emptyList()
        }
    }

    suspend fun deleteJobApplication(applicationId: String): Boolean {
        return try {
            jobAppsRef.document(applicationId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error deleting application", e)
            false
        }
    }

    // --- Service Bookings ---
    suspend fun bookService(request: ServiceRequest): Boolean {
        return try {
            val docRef = if (request.id.isEmpty()) serviceBookingsRef.document() else serviceBookingsRef.document(request.id)
            val newRequest = request.copy(id = docRef.id, appliedOn = System.currentTimeMillis())
            docRef.set(newRequest).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error booking service", e)
            false
        }
    }

    suspend fun getMyServiceBookings(seekerId: String): List<ServiceRequest> {
        return try {
            val snapshot = serviceBookingsRef.whereEqualTo("seekerId", seekerId).get().await()
            snapshot.toObjects(ServiceRequest::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting bookings", e)
            emptyList()
        }
    }

    // Notifications
    suspend fun createNotification(notification: Notification) {
        try {
            val id = notificationsRef.document().id
            notificationsRef.document(id).set(notification.copy(id = id)).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error creating notification", e)
        }
    }

    fun getNotificationsFlow(userId: String): kotlinx.coroutines.flow.Flow<List<Notification>> = kotlinx.coroutines.flow.callbackFlow {
        val subscription = notificationsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val notifications = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
                        .sortedByDescending { it.timestamp }
                    trySend(notifications)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun markNotificationsAsRead(userId: String) {
        try {
            val unread = notificationsRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            db.runBatch { batch ->
                for (doc in unread.documents) {
                    batch.update(doc.reference, "isRead", true)
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error marking notifications read", e)
        }
    }
}
