package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushBlock
import com.nyaayneeti.app.data.firebase.pushReport
import com.nyaayneeti.app.data.local.BlockDao
import com.nyaayneeti.app.data.local.ReportDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class ModerationRepository(
    private val reportDao: ReportDao,
    private val blockDao: BlockDao,
    private val cloud: CloudSync? = null
) {
    fun observeReports() = reportDao.observeAll()

    suspend fun report(reporter: UserEntity, target: UserEntity, reason: String) {
        val report = ReportEntity(
            id = UUID.randomUUID().toString(),
            reporterUid = reporter.uid,
            targetUid = target.uid,
            targetName = target.name,
            reason = reason
        )
        reportDao.upsert(report)
        cloud?.pushReport(report)
    }

    suspend fun resolveReport(id: String, status: ReportStatus) {
        reportDao.setStatus(id, status)
        reportDao.get(id)?.let { cloud?.pushReport(it.copy(status = status)) }
    }

    suspend fun block(blocker: UserEntity, blocked: UserEntity) {
        val block = BlockEntity(id = "${blocker.uid}:${blocked.uid}", blockerUid = blocker.uid, blockedUid = blocked.uid)
        blockDao.upsert(block)
        cloud?.pushBlock(block)
    }

    suspend fun unblock(blocker: String, blocked: String) = blockDao.delete(blocker, blocked)

    suspend fun isBlocked(blocker: String, blocked: String): Boolean = blockDao.find(blocker, blocked) != null

    suspend fun blockedUids(blocker: String): List<String> = blockDao.blockedUids(blocker).map { it.blockedUid }
}
