package ru.sejapoe.application.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*

fun Application.configureNotifications() {
    val projectId = System.getenv()["FIREBASE_PROJECT_ID"] ?: return

    val options = GoogleCredentials.getApplicationDefault()
        .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        .let {
            FirebaseOptions.builder()
                .setCredentials(it)
                .setProjectId(projectId)
                .build()
        }

    FirebaseApp.initializeApp(options)
}