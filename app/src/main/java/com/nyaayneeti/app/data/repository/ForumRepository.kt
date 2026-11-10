package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushComment
import com.nyaayneeti.app.data.firebase.pushPost
import com.nyaayneeti.app.data.local.CommentDao
import com.nyaayneeti.app.data.local.LikeDao
import com.nyaayneeti.app.data.local.PostDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class ForumRepository(
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val likeDao: LikeDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeTrending() = postDao.observeTrending()
    fun observeLatest() = postDao.observeLatest()
    fun observePost(id: String) = postDao.observe(id)
    fun observeComments(postId: String) = commentDao.observe(postId)

    suspend fun createPost(author: UserEntity, category: ForumCategory, title: String, body: String, tags: String) {
        val post = PostEntity(
            id = UUID.randomUUID().toString(),
            authorUid = author.uid,
            authorName = author.name,
            authorRole = author.role,
            category = category,
            title = title,
            body = body,
            tags = tags
        )
        postDao.upsert(post)
        cloud?.pushPost(post)
    }

    suspend fun addComment(postId: String, author: UserEntity, body: String, parentId: String? = null) {
        val comment = CommentEntity(
            id = UUID.randomUUID().toString(), postId = postId,
            authorUid = author.uid, authorName = author.name, body = body, parentId = parentId
        )
        commentDao.insert(comment)
        cloud?.pushComment(comment)
        postDao.incrementCommentCount(postId)
    }

    suspend fun deleteComment(id: String) = commentDao.delete(id)

    suspend fun toggleVote(postId: String, user: UserEntity, value: Int) {
        val existing = likeDao.get(postId, user.uid)
        if (existing?.value == value) {
            likeDao.delete(postId, user.uid)
            adjustVote(postId, -value)
        } else if (existing != null) {
            likeDao.upsert(LikeEntity(id = "$postId:${user.uid}", postId = postId, uid = user.uid, value = value))
            adjustVote(postId, value - existing.value)
        } else {
            likeDao.upsert(LikeEntity(id = "$postId:${user.uid}", postId = postId, uid = user.uid, value = value))
            adjustVote(postId, value)
        }
    }

    suspend fun myVote(postId: String, uid: String): Int = likeDao.get(postId, uid)?.value ?: 0

    private suspend fun adjustVote(postId: String, delta: Int) {
        postDao.get(postId)?.let { post ->
            val updated = post.copy(
                upvotes = (post.upvotes + (if (delta > 0) delta else 0)).coerceAtLeast(0),
                downvotes = (post.downvotes + (if (delta < 0) -delta else 0)).coerceAtLeast(0)
            )
            postDao.upsert(updated)
            cloud?.pushPost(updated)
        }
    }
}
