package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.local.InternshipApplicationDao
import com.nyaayneeti.app.data.local.InternshipDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class InternshipRepository(
    private val internshipDao: InternshipDao,
    private val applicationDao: InternshipApplicationDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeOpen() = internshipDao.observeOpen()
    fun observeByFirm(firmId: String) = internshipDao.observeByFirm(firmId)
    fun observe(id: String) = internshipDao.observe(id)
    fun observeApplications(internshipId: String) = applicationDao.observeForInternship(internshipId)
    fun observeMyApplications(studentUid: String) = applicationDao.observeForStudent(studentUid)

    suspend fun postInternship(firmId: String, firmName: String, title: String, description: String, location: String, duration: Int, stipend: String, skills: String, eligibility: String, deadline: Long?) {
        val entity = InternshipEntity(
            id = UUID.randomUUID().toString(),
            firmId = firmId,
            firmName = firmName,
            title = title,
            description = description,
            location = location,
            durationMonths = duration,
            stipend = stipend,
            skills = skills,
            eligibility = eligibility,
            deadline = deadline
        )
        internshipDao.upsert(entity)
        cloud?.pushInternship(entity)
    }

    suspend fun apply(internship: InternshipEntity, student: UserEntity, coverLetter: String, resumeUri: String?): Result<Unit> {
        val count = applicationDao.countFor(internship.id, student.uid)
        if (count > 0) return Result.failure(IllegalArgumentException("You already applied to this internship"))
        val entity = InternshipApplicationEntity(
            id = UUID.randomUUID().toString(),
            internshipId = internship.id,
            studentUid = student.uid,
            studentName = student.name,
            coverLetter = coverLetter,
            resumeUri = resumeUri
        )
        applicationDao.upsert(entity)
        cloud?.pushApplication(entity)
        return Result.success(Unit)
    }

    suspend fun setApplicationStatus(internshipId: String, appId: String, status: ApplicationStatus, studentUid: String) {
        applicationDao.setStatus(appId, status)
        notifications.notify(studentUid, NotificationType.INTERNSHIP, "Application update", "Your internship application is now ${status.name}.", "internship/$internshipId")
    }
}
