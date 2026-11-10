package com.nyaayneeti.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nyaayneeti.app.data.model.*

@Database(
    entities = [
        UserEntity::class, LawFirmEntity::class, CaseEntity::class, TimelineEventEntity::class,
        CaseTaskEntity::class, CaseNoteEntity::class, DocumentEntity::class, HearingEntity::class,
        ConversationEntity::class, MessageEntity::class, ConsultationEntity::class,
        InternshipEntity::class, InternshipApplicationEntity::class, PostEntity::class,
        CommentEntity::class, LikeEntity::class, ReviewEntity::class, NotificationEntity::class,
        SubscriptionEntity::class, PaymentEntity::class, InvoiceEntity::class, InquiryEntity::class,
        VerificationRequestEntity::class, ReportEntity::class, BlockEntity::class, InvitationEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NyaayDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun firmDao(): FirmDao
    abstract fun caseDao(): CaseDao
    abstract fun timelineDao(): TimelineDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun documentDao(): DocumentDao
    abstract fun hearingDao(): HearingDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun consultationDao(): ConsultationDao
    abstract fun internshipDao(): InternshipDao
    abstract fun applicationDao(): InternshipApplicationDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun inquiryDao(): InquiryDao
    abstract fun verificationDao(): VerificationDao
    abstract fun reportDao(): ReportDao
    abstract fun blockDao(): BlockDao
    abstract fun invitationDao(): InvitationDao

    companion object {
        @Volatile private var instance: NyaayDatabase? = null

        fun get(context: Context): NyaayDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NyaayDatabase::class.java,
                    "nyaayneeti.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
