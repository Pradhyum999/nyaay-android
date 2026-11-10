package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.local.UserDao
import com.nyaayneeti.app.data.local.VerificationDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class VerificationRepository(
    private val verificationDao: VerificationDao,
    private val userDao: UserDao,
    private val notifications: NotificationRepository
) {
    fun observeAll() = verificationDao.observeAll()

    suspend fun submit(user: UserEntity, documentType: String, maskedIdNumber: String): Result<Unit> {
        val existing = verificationDao.latestFor(user.uid)
        if (existing?.status == VerificationStatus.PENDING) {
            return Result.failure(IllegalArgumentException("A verification request is already pending review"))
        }
        verificationDao.upsert(
            VerificationRequestEntity(
                id = UUID.randomUUID().toString(),
                uid = user.uid, role = user.role, name = user.name,
                documentType = documentType, maskedIdNumber = maskedIdNumber,
                status = VerificationStatus.PENDING
            )
        )
        userDao.upsert(user.copy(verificationStatus = VerificationStatus.PENDING))
        return Result.success(Unit)
    }

    suspend fun approve(request: VerificationRequestEntity, adminName: String) {
        verificationDao.setDecision(request.id, VerificationStatus.VERIFIED, null, adminName, System.currentTimeMillis())
        userDao.getById(request.uid)?.let { u ->
            userDao.upsert(u.copy(verificationStatus = VerificationStatus.VERIFIED))
            notifications.notify(u.uid, NotificationType.VERIFICATION, "Verification approved", "Your advocate profile is now verified.", "verification")
        }
    }

    suspend fun reject(request: VerificationRequestEntity, reason: String, adminName: String) {
        verificationDao.setDecision(request.id, VerificationStatus.REJECTED, reason, adminName, System.currentTimeMillis())
        userDao.getById(request.uid)?.let { u ->
            userDao.upsert(u.copy(verificationStatus = VerificationStatus.REJECTED))
            notifications.notify(u.uid, NotificationType.VERIFICATION, "Verification rejected", reason.ifBlank { "Your verification was rejected." }, "verification")
        }
    }
}
