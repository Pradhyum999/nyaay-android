package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushVerification
import com.nyaayneeti.app.data.local.UserDao
import com.nyaayneeti.app.data.local.VerificationDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class VerificationRepository(
    private val verificationDao: VerificationDao,
    private val userDao: UserDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeAll() = verificationDao.observeAll()

    suspend fun submit(user: UserEntity, documentType: String, maskedIdNumber: String): Result<Unit> {
        val existing = verificationDao.latestFor(user.uid)
        if (existing?.status == VerificationStatus.PENDING) {
            return Result.failure(IllegalArgumentException("A verification request is already pending review"))
        }
        val request = VerificationRequestEntity(
            id = UUID.randomUUID().toString(),
            uid = user.uid, role = user.role, name = user.name,
            documentType = documentType, maskedIdNumber = maskedIdNumber,
            status = VerificationStatus.PENDING
        )
        verificationDao.upsert(request)
        cloud?.pushVerification(request)
        val updatedUser = user.copy(verificationStatus = VerificationStatus.PENDING)
        userDao.upsert(updatedUser)
        cloud?.pushUser(updatedUser)
        return Result.success(Unit)
    }

    suspend fun approve(request: VerificationRequestEntity, adminName: String) {
        val updatedRequest = request.copy(status = VerificationStatus.VERIFIED, verifiedBy = adminName, verifiedAt = System.currentTimeMillis())
        verificationDao.upsert(updatedRequest)
        cloud?.pushVerification(updatedRequest)
        userDao.getById(request.uid)?.let { u ->
            val updatedUser = u.copy(verificationStatus = VerificationStatus.VERIFIED)
            userDao.upsert(updatedUser)
            cloud?.pushUser(updatedUser)
            notifications.notify(u.uid, NotificationType.VERIFICATION, "Verification approved", "Your advocate profile is now verified.", "verification")
        }
    }

    suspend fun reject(request: VerificationRequestEntity, reason: String, adminName: String) {
        val updatedRequest = request.copy(status = VerificationStatus.REJECTED, rejectionReason = reason, verifiedBy = adminName, verifiedAt = System.currentTimeMillis())
        verificationDao.upsert(updatedRequest)
        cloud?.pushVerification(updatedRequest)
        userDao.getById(request.uid)?.let { u ->
            val updatedUser = u.copy(verificationStatus = VerificationStatus.REJECTED)
            userDao.upsert(updatedUser)
            cloud?.pushUser(updatedUser)
            notifications.notify(u.uid, NotificationType.VERIFICATION, "Verification rejected", reason.ifBlank { "Your verification was rejected." }, "verification")
        }
    }
}
