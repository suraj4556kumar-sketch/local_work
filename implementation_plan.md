# Implementation Plan: Notifications for Application Actions

## 1. Goal Description
The objective is to overhaul the notification system so that when an application's status changes (e.g., Shortlisted, Accepted, Rejected), appropriate notifications are dispatched to both the applicant and the employer. Furthermore, tapping on these notifications will navigate the user to the relevant screen ("My Applications" for applicants, "Applicant List" for employers).

## 2. Proposed Changes

### Database Layer (pp/src/main/java/com/example/localconnect/data/FirebaseRepository.kt)
- **[MODIFY] pplyForJob**: Fix the existing bug where updating an application incorrectly triggers a "New Job Application" notification to the employer. Only trigger this if pplication.id.isEmpty() (i.e., it's a brand new application).
- **[NEW] updateApplicationStatus**: Add a new function specifically for updating the status of an application. This function will:
  - Update the status field in Firestore.
  - Create a notification for the **Applicant**: e.g., "Your application for <Job Title> has been <Status>".
  - Create a confirmation notification for the **Employer**: e.g., "You marked <Applicant Name> as <Status> for <Job Title>".
  - Populate the 	ype field (e.g., "application_status_applicant", "application_status_employer") and the eferenceId (e.g., jobId or pplicationId) in the Notification model to enable smart routing.

### UI Actions (pp/src/main/java/com/example/localconnect/ApplicantListScreen.kt)
- **[MODIFY] Status Updates**: Update the status change callbacks to call FirebaseRepository.updateApplicationStatus(...) instead of pplyForJob(...).

### Notification UI & Navigation (pp/src/main/java/com/example/localconnect/HomeScreen.kt)
- **[MODIFY] NotificationItem**: Make the notification card clickable. It will invoke an onClick lambda passing the Notification object.
- **[MODIFY] NotificationsBottomSheet**: Accept a 
avController and handle the notification click routing:
  - If type is "new_application" or "application_status_employer", route the employer to their posted jobs or directly to the applicant list if possible.
  - If type is "application_status_applicant", route the applicant to the MyApplicationsScreen.

## 3. Open Questions for the User
> [!IMPORTANT]
> - Do you want the employer's notification ("You marked John as Accepted") to route them to the specific job's ApplicantListScreen or just the MyPostedJobsScreen? If the former, I need to ensure the jobId is passed correctly in the navigation route.
> - Similarly, when an applicant gets a status update notification, should it route to the general MyApplicationsScreen, or a specific details page for that application?

## 4. Verification Plan
- Run gradlew assembleDebug to ensure compilation success.
- Temporarily mock the session ID in HomeScreen to test both the Employer and Applicant notification views.
- Click on notifications to verify navigation routing behavior works without crashing.
