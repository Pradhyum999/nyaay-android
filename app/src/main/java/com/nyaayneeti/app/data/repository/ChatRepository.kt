package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.local.ConversationDao
import com.nyaayneeti.app.data.local.MessageDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class ChatRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeConversations(uid: String) = conversationDao.observeFor(uid)
    fun observeMessages(convoId: String) = messageDao.observe(convoId)
    fun observeUnread(convoId: String, me: String) = messageDao.observeUnread(convoId, me)

    fun convoId(a: String, b: String): String = if (a < b) "$a|$b" else "$b|$a"

    suspend fun sendMessage(sender: UserEntity, receiver: UserEntity, text: String, type: MessageType = MessageType.TEXT) {
        val cid = convoId(sender.uid, receiver.uid)
        val existing = conversationDao.get(cid)
        val convo = ConversationEntity(
            id = cid,
            userA = if (sender.uid < receiver.uid) sender.uid else receiver.uid,
            userB = if (sender.uid < receiver.uid) receiver.uid else sender.uid,
            lastMessage = when (type) {
                MessageType.CALL -> "📞 Voice call"
                MessageType.IMAGE -> "📷 Photo"
                MessageType.TEXT -> text
            },
            lastMessageAt = System.currentTimeMillis(),
            lastSenderUid = sender.uid
        )
        conversationDao.upsert(convo)
        cloud?.pushConversation(convo)
        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = cid,
            senderUid = sender.uid,
            receiverUid = receiver.uid,
            text = text,
            type = type
        )
        messageDao.insert(message)
        cloud?.pushMessage(cid, message)
        if (type == MessageType.TEXT) {
            notifications.notify(receiver.uid, NotificationType.MESSAGE, sender.name, text.take(80), "chat/$cid")
        }
    }

    suspend fun markRead(convoId: String, me: String) = messageDao.markRead(convoId, me)

    /** Create (or open) a case-linked conversation and seed it with the client's initial message. */
    suspend fun openCaseChat(client: UserEntity, lawyer: UserEntity, caseId: String, initialMessage: String?) {
        val cid = convoId(client.uid, lawyer.uid)
        val existing = conversationDao.get(cid)
        val convo = ConversationEntity(
            id = cid,
            userA = if (client.uid < lawyer.uid) client.uid else lawyer.uid,
            userB = if (client.uid < lawyer.uid) lawyer.uid else client.uid,
            lastMessage = initialMessage ?: existing?.lastMessage ?: "",
            lastMessageAt = System.currentTimeMillis(),
            lastSenderUid = client.uid,
            caseId = caseId
        )
        conversationDao.upsert(convo)
        cloud?.pushConversation(convo)
        if (!initialMessage.isNullOrBlank()) {
            val message = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = cid,
                senderUid = client.uid,
                receiverUid = lawyer.uid,
                text = initialMessage,
                type = MessageType.TEXT
            )
            messageDao.insert(message)
            cloud?.pushMessage(cid, message)
        }
        notifications.notify(lawyer.uid, NotificationType.MESSAGE, client.name, "New case conversation started.", "chat/$cid")
    }
}
