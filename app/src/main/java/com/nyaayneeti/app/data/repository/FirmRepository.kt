package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushInvitation
import com.nyaayneeti.app.data.local.CaseDao
import com.nyaayneeti.app.data.local.FirmDao
import com.nyaayneeti.app.data.local.InvitationDao
import com.nyaayneeti.app.data.local.UserDao
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FirmRepository(
    private val firmDao: FirmDao,
    private val userDao: UserDao,
    private val caseDao: CaseDao,
    private val invitationDao: InvitationDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeFirm(firmId: String): Flow<LawFirmEntity?> = firmDao.observe(firmId)
    fun observeMembers(firmId: String): Flow<List<UserEntity>> = userDao.observeFirmMembers(firmId)

    suspend fun createFirm(name: String, admin: UserEntity, city: String? = null, address: String? = null, gstin: String? = null): Result<LawFirmEntity> {
        val firmId = "firm-${UUID.randomUUID().toString().take(8)}"
        val firm = LawFirmEntity(firmId = firmId, name = name, adminUid = admin.uid, city = city, address = address, gstin = gstin)
        firmDao.upsert(firm)
        cloud?.pushFirm(firm)
        val updatedAdmin = admin.copy(firmId = firmId, firmRole = FirmRole.OWNER, role = UserRole.FIRM_ADMIN)
        userDao.upsert(updatedAdmin)
        cloud?.pushUser(updatedAdmin)
        return Result.success(firm)
    }

    suspend fun inviteMemberByEmail(firmId: String, email: String, firmRole: FirmRole): Result<InvitationEntity> {
        val normalized = email.trim().lowercase()
        if (!normalized.contains("@") || normalized.length < 5) {
            return Result.failure(IllegalArgumentException("Enter a valid email address"))
        }
        val inv = InvitationEntity(id = UUID.randomUUID().toString(), firmId = firmId, email = normalized, firmRole = firmRole)
        invitationDao.upsert(inv)
        cloud?.pushInvitation(inv)
        return Result.success(inv)
    }

    /** Links a user to a firm if a pending invitation exists for their email. */
    suspend fun acceptInvitation(email: String, user: UserEntity): UserEntity? {
        val inv = invitationDao.getPendingForEmail(email.trim().lowercase()) ?: return null
        val updated = user.copy(firmId = inv.firmId, firmRole = inv.firmRole, role = UserRole.FIRM_MEMBER)
        userDao.upsert(updated)
        cloud?.pushUser(updated)
        invitationDao.setStatus(inv.id, InvitationStatus.ACCEPTED)
        cloud?.pushInvitation(inv.copy(status = InvitationStatus.ACCEPTED))
        notifications.notify(updated.uid, NotificationType.SYSTEM, "Joined firm", "Your profile is now linked to your firm.")
        return updated
    }

    suspend fun removeMember(firmId: String, uid: String) {
        userDao.getById(uid)?.let { user ->
            val updated = user.copy(firmId = null, firmRole = null)
            userDao.upsert(updated)
            cloud?.pushUser(updated)
            caseDao.unassignLawyer(uid)
        }
    }

    suspend fun assignRole(firmId: String, uid: String, role: FirmRole) {
        userDao.getById(uid)?.let { user ->
            val updated = user.copy(firmRole = role)
            userDao.upsert(updated)
            cloud?.pushUser(updated)
        }
    }
}
