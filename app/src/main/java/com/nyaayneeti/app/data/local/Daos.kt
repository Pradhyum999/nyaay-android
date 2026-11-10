package com.nyaayneeti.app.data.local

import androidx.room.*
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid") suspend fun getById(uid: String): UserEntity?
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1") suspend fun getByPhone(phone: String): UserEntity?
    @Query("SELECT * FROM users WHERE uid = :uid") fun observe(uid: String): Flow<UserEntity?>
    @Query("SELECT * FROM users WHERE role IN ('INDEPENDENT_LAWYER','FIRM_ADMIN','FIRM_MEMBER') AND verificationStatus = 'VERIFIED'")
    fun observeVerifiedLawyers(): Flow<List<UserEntity>>
    @Query("SELECT * FROM users WHERE firmId = :firmId") fun observeFirmMembers(firmId: String): Flow<List<UserEntity>>
    @Query("SELECT * FROM users") suspend fun all(): List<UserEntity>
    @Query("SELECT * FROM users") fun observeAll(): Flow<List<UserEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(user: UserEntity)
    @Update suspend fun update(user: UserEntity)
}

@Dao
interface FirmDao {
    @Query("SELECT * FROM firms WHERE firmId = :firmId") fun observe(firmId: String): Flow<LawFirmEntity?>
    @Query("SELECT * FROM firms WHERE firmId = :firmId") suspend fun get(firmId: String): LawFirmEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(firm: LawFirmEntity)
}

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases WHERE id = :id") fun observe(id: String): Flow<CaseEntity?>
    @Query("SELECT * FROM cases WHERE id = :id") suspend fun get(id: String): CaseEntity?
    @Query("SELECT * FROM cases WHERE clientUid = :uid ORDER BY updatedAt DESC") fun observeByClient(uid: String): Flow<List<CaseEntity>>
    @Query("SELECT * FROM cases WHERE lawyerUid = :uid ORDER BY updatedAt DESC") fun observeByLawyer(uid: String): Flow<List<CaseEntity>>
    @Query("SELECT * FROM cases WHERE firmId = :firmId ORDER BY updatedAt DESC") fun observeByFirm(firmId: String): Flow<List<CaseEntity>>
    @Query("SELECT * FROM cases WHERE firmId = :firmId AND lawyerUid = :uid ORDER BY updatedAt DESC")
    fun observeByMember(firmId: String, uid: String): Flow<List<CaseEntity>>
    @Query("SELECT * FROM cases WHERE clientUid = :uid AND lawyerUid IS NULL AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun observeUnassignedByClient(uid: String): Flow<List<CaseEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(entity: CaseEntity)
    @Query("UPDATE cases SET lawyerUid = NULL, lawyerName = NULL WHERE lawyerUid = :uid") suspend fun unassignLawyer(uid: String)
    @Query("SELECT COUNT(*) FROM cases WHERE caseNumber = :n AND isDeleted = 0") suspend fun countByCaseNumber(n: String): Int
    @Query("UPDATE cases SET isDeleted = 1 WHERE id = :id") suspend fun softDelete(id: String)
    @Query("SELECT COUNT(*) FROM cases WHERE lawyerUid = :uid AND status IN ('ACTIVE','PENDING','HEARING_SCHEDULED')")
    suspend fun activeCountForLawyer(uid: String): Int
}

@Dao
interface TimelineDao {
    @Query("SELECT * FROM case_timeline_events WHERE caseId = :caseId ORDER BY createdAt DESC") fun observe(caseId: String): Flow<List<TimelineEventEntity>>
    @Insert suspend fun insert(event: TimelineEventEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM case_tasks WHERE caseId = :caseId ORDER BY createdAt ASC") fun observe(caseId: String): Flow<List<CaseTaskEntity>>
    @Query("SELECT * FROM case_tasks WHERE id = :id") suspend fun get(id: String): CaseTaskEntity?
    @Insert suspend fun insert(task: CaseTaskEntity)
    @Update suspend fun update(task: CaseTaskEntity)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM case_notes WHERE caseId = :caseId ORDER BY createdAt DESC") fun observe(caseId: String): Flow<List<CaseNoteEntity>>
    @Insert suspend fun insert(note: CaseNoteEntity)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE caseId = :caseId ORDER BY uploadedAt DESC") fun observe(caseId: String): Flow<List<DocumentEntity>>
    @Insert suspend fun insert(doc: DocumentEntity)
    @Query("UPDATE documents SET status = :status WHERE id = :id") suspend fun setStatus(id: String, status: DocumentStatus)
}

@Dao
interface HearingDao {
    @Query("SELECT * FROM hearings WHERE hearingDate = :day ORDER BY itemNumber ASC") fun observeByDay(day: Long): Flow<List<HearingEntity>>
    @Query("SELECT * FROM hearings WHERE caseId = :caseId ORDER BY hearingDate DESC") fun observeByCase(caseId: String): Flow<List<HearingEntity>>
    @Query("SELECT * FROM hearings ORDER BY hearingDate ASC") fun observeAll(): Flow<List<HearingEntity>>
    @Query("SELECT COUNT(*) FROM hearings WHERE caseId = :caseId AND hearingDate = :d AND hearingTime = :t") suspend fun countDuplicate(caseId: String, d: Long, t: String): Int
    @Insert suspend fun insert(hearing: HearingEntity)
    @Query("UPDATE hearings SET status = :status, orderNotes = :order WHERE id = :id") suspend fun logOrder(id: String, status: HearingStatus, order: String)
}
