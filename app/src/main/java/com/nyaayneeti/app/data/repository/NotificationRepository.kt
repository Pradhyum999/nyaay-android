package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushNotification
import com.nyaayneeti.app.data.local.NotificationDao
import com.nyaayneeti.app.data.model.NotificationEntity
import com.nyaayneeti.app.data.model.NotificationType
import java.util.UUID

class NotificationRepository(
    private val notificationDao: NotificationDao,
    private val cloud: CloudSync? = null
) {

    fun observeFor(uid: String) = notificationDao.observeFor(uid)
    fun observeUnread(uid: String) = notificationDao.observeUnread(uid)
    suspend fun markRead(id: String) = notificationDao.markRead(id)
    suspend fun markAllRead(uid: String) = notificationDao.markAllRead(uid)

    suspend fun notify(
        uid: String,
        type: NotificationType,
        title: String,
        body: String,
        deepLink: String? = null
    ) {
        val entity = NotificationEntity(
            id = UUID.randomUUID().toString(),
            uid = uid,
            type = type,
            title = title,
            body = body,
            deepLink = deepLink
        )
        notificationDao.insert(entity)
        cloud?.pushNotification(entity)
    }
}
