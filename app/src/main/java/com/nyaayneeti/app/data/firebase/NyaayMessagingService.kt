package com.nyaayneeti.app.data.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NyaayMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        // A push notification arrived. For now the app relies on Firestore realtime
        // listeners for in-app updates; foreground push display can be wired here.
        message.notification?.let {
            // TODO: post a local notification (see FIREBASE_SETUP.md)
        }
    }

    override fun onNewToken(token: String) {
        // The FCM registration token refreshed. Sync it to your backend / a users/{uid}/fcmToken doc.
    }
}
