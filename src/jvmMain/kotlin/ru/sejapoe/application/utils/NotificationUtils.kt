package ru.sejapoe.application.utils

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage

fun AndroidNotification.notify(tokens: List<String>) {
    if (tokens.isEmpty()) return
    FirebaseMessaging.getInstance().sendMulticast(
        MulticastMessage.builder().setAndroidConfig(
            AndroidConfig.builder().setNotification(
                this
            ).build()
        ).addAllTokens(tokens).build()
    )
}