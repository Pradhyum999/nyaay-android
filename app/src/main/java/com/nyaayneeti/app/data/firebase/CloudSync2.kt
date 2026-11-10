package com.nyaayneeti.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.nyaayneeti.app.data.model.*

/** Part 2 of CloudSync (post, comment, notification, review, subscription, payment). */
fun CloudSync.pushPost(p: PostEntity) {
    db.push("posts", p.id, hashMapOf(
        "id" to p.id, "authorUid" to p.authorUid, "authorName" to p.authorName,
        "authorRole" to p.authorRole.name, "category" to p.category.name, "title" to p.title,
        "body" to p.body, "tags" to p.tags, "upvotes" to p.upvotes, "downvotes" to p.downvotes,
        "commentCount" to p.commentCount, "isRemoved" to p.isRemoved, "createdAt" to p.createdAt
    ))
}

fun CloudSync.pushComment(c: CommentEntity) {
    db.push("posts/${c.postId}/comments", c.id, hashMapOf(
        "id" to c.id, "postId" to c.postId, "authorUid" to c.authorUid,
        "authorName" to c.authorName, "body" to c.body, "parentId" to c.parentId, "createdAt" to c.createdAt
    ))
}

fun CloudSync.pushNotification(n: NotificationEntity) {
    db.push("users/${n.uid}/notifications", n.id, hashMapOf(
        "id" to n.id, "uid" to n.uid, "type" to n.type.name, "title" to n.title,
        "body" to n.body, "deepLink" to n.deepLink, "isRead" to n.isRead, "createdAt" to n.createdAt
    ))
}

fun CloudSync.pushReview(r: ReviewEntity) {
    db.push("reviews", r.id, hashMapOf(
        "id" to r.id, "lawyerUid" to r.lawyerUid, "citizenUid" to r.citizenUid,
        "citizenName" to r.citizenName, "rating" to r.rating, "comment" to r.comment, "createdAt" to r.createdAt
    ))
}

fun CloudSync.pushSubscription(s: SubscriptionEntity) {
    db.push("subscriptions", s.id, hashMapOf(
        "id" to s.id, "ownerUid" to s.ownerUid, "firmId" to s.firmId,
        "tier" to s.tier.name, "startAt" to s.startAt, "endAt" to s.endAt, "status" to s.status.name
    ))
}

fun CloudSync.pushPayment(p: PaymentEntity) {
    db.push("payments", p.id, hashMapOf(
        "id" to p.id, "payerUid" to p.payerUid, "payeeUid" to p.payeeUid,
        "amount" to p.amount, "purpose" to p.purpose, "refId" to p.refId,
        "status" to p.status.name, "createdAt" to p.createdAt
    ))
}

fun CloudSync.pushInvoice(i: InvoiceEntity) {
    db.push("invoices", i.id, hashMapOf(
        "id" to i.id, "caseId" to i.caseId, "lawyerUid" to i.lawyerUid, "clientUid" to i.clientUid,
        "invoiceNumber" to i.invoiceNumber, "caseNumber" to i.caseNumber, "clientName" to i.clientName,
        "date" to i.date, "appearanceFee" to i.appearanceFee, "draftingFee" to i.draftingFee,
        "clerkageMisc" to i.clerkageMisc, "totalAmount" to i.totalAmount, "status" to i.status.name,
        "paidVia" to i.paidVia?.name, "upiRef" to i.upiRef, "notes" to i.notes, "createdAt" to i.createdAt
    ))
}

fun CloudSync.pushInquiry(i: InquiryEntity) {
    db.push("inquiries", i.id, hashMapOf(
        "id" to i.id, "lawyerUid" to i.lawyerUid, "clientUid" to i.clientUid, "clientName" to i.clientName,
        "subject" to i.subject, "message" to i.message, "attachAIBrief" to i.attachAIBrief,
        "status" to i.status.name, "createdAt" to i.createdAt
    ))
}

private fun FirebaseFirestore.push(collection: String, docId: String, data: Map<String, Any?>) {
    collection(collection).document(docId).set(data)
}
