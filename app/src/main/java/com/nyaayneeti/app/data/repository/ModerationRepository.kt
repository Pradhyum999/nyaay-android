package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.local.BlockDao
import com.nyaayneeti.app.data.local.ReportDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class ModerationRepository(
    private val reportDao: ReportDao,
    private val blockDao: BlockDao
) {
    fun observeReports() = reportDao.observeAll()

    suspend fun report(reporter: UserEntity, target: UserEntity, reason: String) {
        reportDao.upsert(
            ReportEntity(
                id = UUID.randomUUID().toString(),
                reporterUid = reporter.uid,
                targetUid = target.uid,
                targetName = target.name,
                reason = reason
            )
        )
    }

    suspend fun resolveReport(id: String, status: ReportStatus) = reportDao.setStatus(id, status)

    suspend fun block(blocker: UserEntity, blocked: UserEntity) {
        blockDao.upsert(BlockEntity(id = "${blocker.uid}:${blocked.uid}", blockerUid = blocker.uid, blockedUid = blocked.uid))
    }

    suspend fun unblock(blocker: String, blocked: String) = blockDao.delete(blocker, blocked)

    suspend fun isBlocked(blocker: String, blocked: String): Boolean = blockDao.find(blocker, blocked) != null

    suspend fun blockedUids(blocker: String): List<String> = blockDao.blockedUids(blocker).map { it.blockedUid }
}
