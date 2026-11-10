package com.nyaayneeti.app.data.local

import androidx.room.*
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices WHERE caseId = :caseId ORDER BY date DESC") fun observeByCase(caseId: String): Flow<List<InvoiceEntity>>
    @Query("SELECT * FROM invoices WHERE lawyerUid = :uid ORDER BY date DESC") fun observeForLawyer(uid: String): Flow<List<InvoiceEntity>>
    @Query("SELECT * FROM invoices WHERE clientUid = :uid ORDER BY date DESC") fun observeForClient(uid: String): Flow<List<InvoiceEntity>>
    @Query("SELECT * FROM invoices WHERE id = :id") suspend fun get(id: String): InvoiceEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(invoice: InvoiceEntity)
}

@Dao
interface InquiryDao {
    @Query("SELECT * FROM inquiries WHERE lawyerUid = :uid ORDER BY createdAt DESC") fun observeForLawyer(uid: String): Flow<List<InquiryEntity>>
    @Query("SELECT * FROM inquiries WHERE clientUid = :uid ORDER BY createdAt DESC") fun observeForClient(uid: String): Flow<List<InquiryEntity>>
    @Query("SELECT * FROM inquiries WHERE id = :id") suspend fun get(id: String): InquiryEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(inquiry: InquiryEntity)
    @Query("UPDATE inquiries SET status = :status WHERE id = :id") suspend fun setStatus(id: String, status: InquiryStatus)
    @Query("UPDATE inquiries SET caseId = :caseId WHERE id = :id") suspend fun setCaseId(id: String, caseId: String)
}
