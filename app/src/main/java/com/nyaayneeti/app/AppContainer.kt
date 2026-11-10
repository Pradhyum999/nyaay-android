package com.nyaayneeti.app

import android.content.Context
import com.nyaayneeti.app.BuildConfig
import com.nyaayneeti.app.data.ai.CaseAIClient
import com.nyaayneeti.app.data.ai.ECourtsClient
import com.nyaayneeti.app.data.ai.NimChatClient
import com.nyaayneeti.app.data.ai.NimIntakeEngine
import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.FirebaseAuthManager
import com.nyaayneeti.app.data.firebase.FirebaseModule
import com.nyaayneeti.app.data.local.NyaayDatabase
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.prefs.SessionStore
import com.nyaayneeti.app.data.repository.*
import java.util.UUID

class AppContainer(context: Context) {
    val database = NyaayDatabase.get(context)
    val sessionStore = SessionStore(context)

    val firebaseEnabled = FirebaseModule.isConfigured(context)
    val firebaseAuthManager = FirebaseAuthManager()
    val cloudSync: CloudSync? = if (firebaseEnabled) CloudSync(FirebaseModule.firestore) else null

    val notifications = NotificationRepository(database.notificationDao(), cloudSync)
    val authRepository = AuthRepository(database.userDao(), sessionStore, firebaseAuthManager, cloudSync, firebaseEnabled)
    val caseRepository = CaseRepository(
        database.caseDao(), database.timelineDao(), database.taskDao(), database.noteDao(),
        database.documentDao(), database.hearingDao(), database.invoiceDao(), notifications, cloudSync
    )
    val chatRepository = ChatRepository(database.conversationDao(), database.messageDao(), notifications, cloudSync)
    val inquiryRepository = InquiryRepository(database.inquiryDao(), caseRepository, chatRepository, notifications, cloudSync)
    val firmRepository = FirmRepository(database.firmDao(), database.userDao(), database.caseDao(), database.invitationDao(), notifications, cloudSync)
    val billingRepository = BillingRepository(database.paymentDao(), database.subscriptionDao(), notifications, cloudSync)
    val consultationRepository = ConsultationRepository(
        database.consultationDao(), database.reviewDao(), database.userDao(), billingRepository, notifications, cloudSync
    )
    val internshipRepository = InternshipRepository(database.internshipDao(), database.applicationDao(), notifications, cloudSync)
    val forumRepository = ForumRepository(database.postDao(), database.commentDao(), database.likeDao(), notifications, cloudSync)
    val intakeEngine: AIIntakeEngine = NimIntakeEngine(apiKey = BuildConfig.NVIDIA_NIM_API_KEY)
    val ecourtsClient = ECourtsClient(apiKey = BuildConfig.ECOURTS_API_KEY, baseUrl = BuildConfig.ECOURTS_BASE_URL)
    val caseAIClient = CaseAIClient(NimChatClient(BuildConfig.NVIDIA_NIM_API_KEY))
    val verificationRepository = VerificationRepository(database.verificationDao(), database.userDao(), notifications)
    val moderationRepository = ModerationRepository(database.reportDao(), database.blockDao())

    suspend fun seedIfEmpty() {
        if (database.userDao().all().isNotEmpty()) return

        val citizen = UserEntity("u-demo-cit", "+919999999999", "Rahul Sharma", role = UserRole.CITIZEN, onboardingCompleted = true, city = "Mumbai", state = "Maharashtra")
        val lawyer = UserEntity("u-demo-law", "+919876543210", "Adv. Priya Mehta", role = UserRole.INDEPENDENT_LAWYER, onboardingCompleted = true, verificationStatus = VerificationStatus.VERIFIED, barCouncilId = "MH/1234/2015", specializations = "[\"Family\",\"Property & RERA\",\"Matrimonial\"]", languages = "[\"English\",\"Hindi\"]", feeMin = 800.0, feeMax = 2000.0, experienceYears = 9, avgRating = 4.6, reviewCount = 12, city = "Mumbai", state = "Maharashtra", chamberLat = 19.0760, chamberLng = 72.8777)
        val admin = UserEntity("u-demo-admin", "+918888888888", "Adv. Arjun Rao", role = UserRole.FIRM_ADMIN, onboardingCompleted = true, verificationStatus = VerificationStatus.VERIFIED, barCouncilId = "MH/2001/2008", specializations = "[\"Criminal\",\"Civil\"]", languages = "[\"English\",\"Hindi\",\"Marathi\"]", feeMin = 1500.0, feeMax = 5000.0, experienceYears = 16, avgRating = 4.8, reviewCount = 40, city = "Pune", state = "Maharashtra", chamberLat = 18.5204, chamberLng = 73.8567)
        val member = UserEntity("u-demo-member", "+917777777777", "Adv. Neha Kulkarni", role = UserRole.FIRM_MEMBER, onboardingCompleted = true, verificationStatus = VerificationStatus.VERIFIED, barCouncilId = "MH/5566/2019", specializations = "[\"Civil\",\"Consumer\"]", languages = "[\"English\",\"Marathi\"]", feeMin = 500.0, feeMax = 1200.0, experienceYears = 5, avgRating = 4.3, reviewCount = 8, city = "Pune", state = "Maharashtra", chamberLat = 18.5204, chamberLng = 73.8567)
        val criminalLawyer = UserEntity("u-demo-criminal", "+915555555555", "Adv. Vikram Singh", role = UserRole.INDEPENDENT_LAWYER, onboardingCompleted = true, verificationStatus = VerificationStatus.VERIFIED, barCouncilId = "DL/7788/2012", specializations = "[\"Criminal\",\"Cyber\"]", languages = "[\"English\",\"Hindi\"]", feeMin = 1000.0, feeMax = 3000.0, experienceYears = 13, avgRating = 4.7, reviewCount = 25, city = "Delhi", state = "Delhi", chamberLat = 28.6139, chamberLng = 77.2090)
        val corporateLawyer = UserEntity("u-demo-corp", "+914444444444", "Adv. Sana Khan", role = UserRole.INDEPENDENT_LAWYER, onboardingCompleted = true, verificationStatus = VerificationStatus.VERIFIED, barCouncilId = "MH/9012/2016", specializations = "[\"Corporate\",\"Tax\"]", languages = "[\"English\"]", feeMin = 2000.0, feeMax = 6000.0, experienceYears = 8, avgRating = 4.5, reviewCount = 15, city = "Mumbai", state = "Maharashtra", chamberLat = 19.0760, chamberLng = 72.8777)
        val student = UserEntity("u-demo-stu", "+916666666666", "Ananya Singh", role = UserRole.STUDENT, onboardingCompleted = true, college = "ILS Law College", yearOfStudy = "3rd Year", city = "Pune", state = "Maharashtra")

        val firm = LawFirmEntity(firmId = "firm-demo", name = "Rao & Associates", adminUid = admin.uid, city = "Pune", state = "Maharashtra")

        database.userDao().upsert(citizen)
        database.userDao().upsert(lawyer)
        database.userDao().upsert(admin.copy(firmId = firm.firmId, firmRole = FirmRole.OWNER))
        database.userDao().upsert(member.copy(firmId = firm.firmId, firmRole = FirmRole.ASSOCIATE))
        database.userDao().upsert(criminalLawyer)
        database.userDao().upsert(corporateLawyer)
        database.userDao().upsert(student)
        database.firmDao().upsert(firm)

        database.internshipDao().upsert(InternshipEntity("int-1", firm.firmId, firm.name, "Litigation Intern", "Assist in drafting, research and court visits for civil matters.", "Pune", 2, "₹5,000/month", "Drafting, Research, Litigation", "Law student (2nd year+)", System.currentTimeMillis() + 7L * 86400000))
        database.internshipDao().upsert(InternshipEntity("int-2", firm.firmId, firm.name, "Legal Research Intern (Remote)", "Work from home on case law research and drafting briefs.", "Remote", 1, "Unpaid", "Research, Writing", "Law student (any year)", System.currentTimeMillis() + 14L * 86400000))

        database.postDao().upsert(PostEntity("p-1", lawyer.uid, lawyer.name, lawyer.role, ForumCategory.LEGAL_NEWS, "New RERA possession timeline ruling", "Supreme Court clarifies the limitation period for delayed possession claims under RERA. Builders can no longer argue delay on technical grounds.", "RERA, Property", 24, 2, 3, createdAt = System.currentTimeMillis() - 3600000))
        database.postDao().upsert(PostEntity("p-2", admin.uid, admin.name, admin.role, ForumCategory.CAREER, "What we look for in interns", "Drafting discipline, research rigour and court etiquette matter more than grades. Show initiative in your cover letter.", "Internships, Career", 18, 0, 1, createdAt = System.currentTimeMillis() - 7200000))
        database.postDao().upsert(PostEntity("p-3", student.uid, student.name, student.role, ForumCategory.ASK_LAWYER, "Can I file a consumer complaint online?", "I paid a builder and possession is delayed by a year. What is the fastest path to a consumer forum complaint?", "Consumer", 9, 1, 2, createdAt = System.currentTimeMillis() - 10800000))
        database.commentDao().insert(CommentEntity(UUID.randomUUID().toString(), "p-1", admin.uid, admin.name, "Excellent summary — very relevant for ongoing RERA matters.", createdAt = System.currentTimeMillis() - 1800000))
        database.commentDao().insert(CommentEntity(UUID.randomUUID().toString(), "p-3", lawyer.uid, lawyer.name, "Yes — you can file on the eDaakhil portal. Gather your allotment letter and payment receipts first.", createdAt = System.currentTimeMillis() - 3600000))

        val convoId = chatRepository.convoId(citizen.uid, lawyer.uid)
        database.conversationDao().upsert(ConversationEntity(convoId, citizen.uid, lawyer.uid, "Thank you! See you at 4 PM.", System.currentTimeMillis() - 600000, lawyer.uid))
        database.messageDao().insert(MessageEntity(UUID.randomUUID().toString(), convoId, citizen.uid, lawyer.uid, "Hi ma'am, I need help with my builder delaying possession.", MessageType.TEXT, true, System.currentTimeMillis() - 3600000))
        database.messageDao().insert(MessageEntity(UUID.randomUUID().toString(), convoId, lawyer.uid, citizen.uid, "Sure, I can help. Let's schedule a consultation.", MessageType.TEXT, true, System.currentTimeMillis() - 1800000))
        database.messageDao().insert(MessageEntity(UUID.randomUUID().toString(), convoId, citizen.uid, lawyer.uid, "Thank you! See you at 4 PM.", MessageType.TEXT, false, System.currentTimeMillis() - 600000))

        database.notificationDao().insert(NotificationEntity(UUID.randomUUID().toString(), citizen.uid, NotificationType.SYSTEM, "Welcome to NYAAYNEETI", "Your legal home is ready. Build your case or chat with an advocate.", isRead = false, createdAt = System.currentTimeMillis() - 300000))
    }
}
