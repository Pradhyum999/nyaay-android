package com.nyaayneeti.app.data.local

import androidx.room.*
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE userA = :uid OR userB = :uid ORDER BY lastMessageAt DESC")
    fun observeFor(uid: String): Flow<List<ConversationEntity>>
    @Query("SELECT * FROM conversations WHERE id = :id") suspend fun get(id: String): ConversationEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(convo: ConversationEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :convoId ORDER BY timestamp ASC") fun observe(convoId: String): Flow<List<MessageEntity>>
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :convoId AND receiverUid = :me AND isRead = 0")
    fun observeUnread(convoId: String, me: String): Flow<Int>
    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :convoId AND receiverUid = :me AND isRead = 0")
    suspend fun markRead(convoId: String, me: String)
    @Insert suspend fun insert(msg: MessageEntity)
}

@Dao
interface ConsultationDao {
    @Query("SELECT * FROM consultations WHERE id = :id") fun observe(id: String): Flow<ConsultationEntity?>
    @Query("SELECT * FROM consultations WHERE id = :id") suspend fun get(id: String): ConsultationEntity?
    @Query("SELECT * FROM consultations WHERE citizenUid = :uid ORDER BY requestedAt DESC") fun observeForCitizen(uid: String): Flow<List<ConsultationEntity>>
    @Query("SELECT * FROM consultations WHERE lawyerUid = :uid ORDER BY requestedAt DESC") fun observeForLawyer(uid: String): Flow<List<ConsultationEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(c: ConsultationEntity)
    @Query("UPDATE consultations SET status = :status WHERE id = :id") suspend fun setStatus(id: String, status: ConsultationStatus)
}

@Dao
interface InternshipDao {
    @Query("SELECT * FROM internships WHERE status = 'OPEN' ORDER BY createdAt DESC") fun observeOpen(): Flow<List<InternshipEntity>>
    @Query("SELECT * FROM internships WHERE firmId = :firmId ORDER BY createdAt DESC") fun observeByFirm(firmId: String): Flow<List<InternshipEntity>>
    @Query("SELECT * FROM internships WHERE id = :id") fun observe(id: String): Flow<InternshipEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(i: InternshipEntity)
}

@Dao
interface InternshipApplicationDao {
    @Query("SELECT * FROM internship_applications WHERE internshipId = :internshipId ORDER BY appliedAt DESC")
    fun observeForInternship(internshipId: String): Flow<List<InternshipApplicationEntity>>
    @Query("SELECT * FROM internship_applications WHERE studentUid = :uid ORDER BY appliedAt DESC")
    fun observeForStudent(uid: String): Flow<List<InternshipApplicationEntity>>
    @Query("SELECT COUNT(*) FROM internship_applications WHERE internshipId = :internshipId AND studentUid = :uid")
    suspend fun countFor(internshipId: String, uid: String): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(a: InternshipApplicationEntity)
    @Query("UPDATE internship_applications SET status = :status WHERE id = :id") suspend fun setStatus(id: String, status: ApplicationStatus)
}
