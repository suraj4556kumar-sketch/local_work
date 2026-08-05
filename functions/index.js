const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotification = functions.firestore
    .document("notifications/{notificationId}")
    .onCreate(async (snap, context) => {
        const notification = snap.data();
        
        const userId = notification.userId;
        const title = notification.title || "New Notification";
        const message = notification.message || "";

        try {
            const userDoc = await admin.firestore().collection("users").doc(userId).get();
            if (!userDoc.exists) {
                console.log(`User ${userId} not found`);
                return null;
            }

            const userData = userDoc.data();
            const fcmToken = userData.fcmToken;

            if (!fcmToken) {
                console.log(`User ${userId} has no FCM token`);
                return null;
            }

            const payload = {
                notification: {
                    title: title,
                    body: message,
                },
                token: fcmToken
            };

            const response = await admin.messaging().send(payload);
            console.log("Successfully sent message:", response);
            return response;
        } catch (error) {
            console.error("Error sending notification:", error);
            return null;
        }
    });
