package com.nyaayneeti.app.data.local

import androidx.room.*
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationDao {
    @Query("SELECT * FROM verification_requests ORDER BY submittedAt DESC") fun observeAll(): Flow<List<VerificationRequestEntity>>
    @Query("SELECT * FROM verification_requests WHERE id = :id") suspend fun getById(id: String): VerificationRequestEntity?
    @Query("SELECT * FROM verification_requests WHERE uid = :uid ORDER BY submittedAt DESC LIMIT 1") suspend fun latestFor(uid: String): VerificationRequestEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(v: VerificationRequestEntity)
    @Query("UPDATE verification_requests SET status = :status, rejectionReason = :reason, verifiedBy = :by, verifiedAt = :at WHERE id = :id")
    suspend fun setDecision(id: String, status: VerificationStatus, reason: String?, by: String?, at: Long?)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY createdAt DESC") fun observeAll(): Flow<List<ReportEntity>>
    @Query("SELECT * FROM reports WHERE id = :id") suspend fun get(id: String): ReportEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(r: ReportEntity)
    @Query("UPDATE reports SET status = :status WHERE id = :id") suspend fun setStatus(id: String, status: ReportStatus)
}

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks WHERE blockerUid = :uid") suspend fun blockedUids(uid: String): List<BlockEntity>
    @Query("SELECT * FROM blocks WHERE blockerUid = :blocker AND blockedUid = :blocked") suspend fun find(blocker: String, blocked: String): BlockEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(b: BlockEntity)
    @Query("DELETE FROM blocks WHERE blockerUid = :blocker AND blockedUid = :blocked") suspend fun delete(blocker: String, blocked: String)
}

@Dao
interface InvitationDao {
    @Query("SELECT * FROM firm_invitations WHERE firmId = :firmId ORDER BY createdAt DESC") fun observeForFirm(firmId: String): Flow<List<InvitationEntity>>
    @Query("SELECT * FROM firm_invitations WHERE email = :email AND status = 'PENDING' ORDER BY createdAt DESC LIMIT 1") suspend fun getPendingForEmail(email: String): InvitationEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(inv: InvitationEntity)
    @Query("UPDATE firm_invitations SET status = :status WHERE id = :id") suspend fun setStatus(id: String, status: InvitationStatus)
}
