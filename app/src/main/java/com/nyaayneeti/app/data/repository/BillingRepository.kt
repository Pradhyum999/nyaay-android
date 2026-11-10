package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushPayment
import com.nyaayneeti.app.data.firebase.pushSubscription
import com.nyaayneeti.app.data.local.PaymentDao
import com.nyaayneeti.app.data.local.SubscriptionDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class BillingRepository(
    private val paymentDao: PaymentDao,
    private val subscriptionDao: SubscriptionDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observePayments(uid: String) = paymentDao.observeFor(uid)
    fun observeSubscription(uid: String) = subscriptionDao.observeLatest(uid)

    suspend fun subscribe(owner: UserEntity, tier: SubscriptionTier, firmId: String? = null) {
        val now = System.currentTimeMillis()
        val end = now + 30L * 24 * 60 * 60 * 1000
        val sub = SubscriptionEntity(id = UUID.randomUUID().toString(), ownerUid = owner.uid, firmId = firmId, tier = tier, endAt = end)
        subscriptionDao.upsert(sub)
        cloud?.pushSubscription(sub)
        recordPayment(owner.uid, null, tierPrice(tier), "subscription", owner.uid)
        notifications.notify(owner.uid, NotificationType.PAYMENT, "Subscription active", "Your ${tier.name} plan is now active.")
    }

    suspend fun recordPayment(payerUid: String, payeeUid: String?, amount: Double, purpose: String, refId: String) {
        val payment = PaymentEntity(
            id = UUID.randomUUID().toString(), payerUid = payerUid, payeeUid = payeeUid,
            amount = amount, purpose = purpose, refId = refId, status = PaymentStatus.PAID
        )
        paymentDao.upsert(payment)
        cloud?.pushPayment(payment)
    }

    companion object {
        fun tierPrice(tier: SubscriptionTier): Double = when (tier) {
            SubscriptionTier.FREE -> 0.0
            SubscriptionTier.PROFESSIONAL -> 499.0
            SubscriptionTier.FIRM -> 1499.0
            SubscriptionTier.PREMIUM -> 2999.0
        }

        fun tierFeatures(tier: SubscriptionTier): String = when (tier) {
            SubscriptionTier.FREE -> "3 active cases · 1 firm seat · 50 MB storage"
            SubscriptionTier.PROFESSIONAL -> "25 active cases · analytics · 1 GB storage"
            SubscriptionTier.FIRM -> "Unlimited members · firm analytics · 10 GB storage"
            SubscriptionTier.PREMIUM -> "Unlimited everything · priority support"
        }
    }
}
