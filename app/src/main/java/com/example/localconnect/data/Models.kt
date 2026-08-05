package com.example.localconnect.data

import com.google.firebase.Timestamp

data class UserRoles(
    val isServiceSeeker: Boolean = true,
    val isServiceProvider: Boolean = false,
    val isJobSeeker: Boolean = true,
    val isJobProvider: Boolean = false
)

data class User(
    val uid: String = "",
    val phone: String = "",
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val location: String = "",
    val roles: UserRoles = UserRoles(),
    val languageCode: String = "en",
    val createdAt: Timestamp? = null,
    val rating: Double = 0.0,
    val completedJobs: Int = 0
)

data class ServiceTask(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val serviceTitle: String = "",
    val category: String = "",
    val location: String = "",
    val distanceKm: Double = 0.0,
        val price: Int = 0,
    val pricingType: String = "",
    val feesPerKm: Int = 0,
    val city: String = "",
    val locality: String = "",
    val serviceRadius: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val availableDays: List<String> = emptyList(),
    val availableFrom: String = "",
    val availableTo: String = "",
    val experience: String = "",
    val pincode: String = "",
    val description: String = "",
    val timestamp: Long = 0L,
    val rating: Double = 0.0,
    val completedJobs: Int = 0
)

data class ServiceRequest(
    val id: String = "",
    val seekerId: String = "",
    val providerId: String = "",
    val serviceId: String = "",
    val serviceTitle: String = "",
    val status: String = "Pending", // Pending, Accepted, Completed, Cancelled
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val problemDescription: String = "",
    val appliedOn: Long = 0L,
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAltPhone: String = "",
    val customerAddress: String = "",
    val customerLandmark: String = "",
    val customerCity: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val instructions: String = "",
    val imageUrl: String = "",
    val isReviewed: Boolean = false // true once the seeker has left a review
)

data class Job(
    val id: String = "",
    val employerId: String = "",
    val companyName: String = "",
    val contactPersonName: String = "",
    val contactPhone: String = "",
    val contactPreference: String = "call",
    val title: String = "",
    val jobCategory: String = "",
    val vacancies: Int = 1,
    val jobType: String = "Full Time",
    val location: String = "",
    val workAddress: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceKm: Double = 0.0,
    val salary: String = "",
    val salaryType: String = "Monthly",
    val salaryMin: Int = 0,
    val salaryMax: Int = 0,
    val salaryValue: Int = 0,
    val experienceReq: String = "No Experience",
    val experienceYears: Int = 0,
    val educationReq: String = "Not Required",
    val genderPref: String = "Any",
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val workingDays: List<String> = emptyList(),
    val startTime: String = "",
    val endTime: String = "",
    val weeklyOff: String = "",
    val joiningDate: String = "",
    val postingDate: String = "",
    val description: String = "",
    val descriptionEn: String = "",
    val descriptionHi: String = "",
    val descriptionBn: String = "",
    val responsibilities: List<String> = emptyList(),
    val responsibilitiesEn: List<String> = emptyList(),
    val responsibilitiesHi: List<String> = emptyList(),
    val responsibilitiesBn: List<String> = emptyList(),
    val isCustomDescription: Boolean = false,
    val isCustomResponsibilities: Boolean = false,
    val documentsRequired: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val languagesPref: List<String> = emptyList(),
    val timestamp: Long = 0L,
    val rating: Double = 0.0,
    val completedJobs: Int = 0
)

data class JobApplication(
    val id: String = "",
    val seekerId: String = "",
    val jobId: String = "",
    val employerId: String = "",
    val jobTitle: String = "",
    val companyName: String = "",
    val location: String = "",
    val salary: String = "",
    val status: String = "Pending", // Pending, Accepted, Rejected
    val appliedOnDate: String = "",
    val appliedOn: Long = 0L,
    val feedback: String = "",
    val isReviewed: Boolean = false, // true once the seeker has left a review for the employer

    // applicant details
    val applicantName: String = "",
    val applicantPhone: String = "",
    val applicantAge: String = "",
    val applicantGender: String = "",
    val applicantExperience: String = "",
    val applicantIntro: String = "",
    val uploadedDocumentUri: String = "",
    val uploadedDocuments: Map<String, String> = emptyMap()
)

data class Notification(
    val id: String = "",
    val userId: String = "", // who receives this
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val type: String = "general",
    val referenceId: String = "" // job_application, offer, etc.
)

data class JobTemplate(
    val category: String = "",
    val description: String = "",
    val responsibilities: List<String> = emptyList()
)

/**
 * A single review submitted by a seeker (service or job).
 * reviewType: "service" or "job"
 */
data class Review(
    val id: String = "",
    val reviewerId: String = "",      // UID of person leaving the review
    val reviewerName: String = "",    // Display name of reviewer
    val targetId: String = "",        // providerId (service) or employerId (job)
    val targetName: String = "",      // Provider / Company name
    val referenceId: String = "",     // serviceRequestId or jobApplicationId
    val reviewType: String = "service", // "service" or "job"
    val entityId: String = "",        // serviceId or jobId for per-item ratings
    val rating: Float = 0f,           // 1.0 to 5.0
    val comment: String = "",
    val serviceTitle: String = "",    // for display context
    val timestamp: Long = 0L
)
