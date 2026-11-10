package com.nyaayneeti.app.data.local

import androidx.room.*
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM forum_posts WHERE isRemoved = 0 ORDER BY createdAt DESC") fun observeLatest(): Flow<List<PostEntity>>
    @Query("SELECT * FROM forum_posts WHERE isRemoved = 0 ORDER BY (upvotes - downvotes) DESC, createdAt DESC")
    fun observeTrending(): Flow<List<PostEntity>>
    @Query("SELECT * FROM forum_posts WHERE id = :id") fun observe(id: String): Flow<PostEntity?>
    @Query("SELECT * FROM forum_posts WHERE id = :id") suspend fun get(id: String): PostEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PostEntity)
    @Query("UPDATE forum_posts SET commentCount = commentCount + 1 WHERE id = :id") suspend fun incrementCommentCount(id: String)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM forum_comments WHERE postId = :postId ORDER BY createdAt ASC") fun observe(postId: String): Flow<List<CommentEntity>>
    @Insert suspend fun insert(c: CommentEntity)
    @Query("DELETE FROM forum_comments WHERE id = :id") suspend fun delete(id: String)
}

@Dao
interface LikeDao {
    @Query("SELECT * FROM forum_likes WHERE postId = :postId AND uid = :uid") suspend fun get(postId: String, uid: String): LikeEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(like: LikeEntity)
    @Query("DELETE FROM forum_likes WHERE postId = :postId AND uid = :uid") suspend fun delete(postId: String, uid: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE lawyerUid = :uid ORDER BY createdAt DESC") fun observeForLawyer(uid: String): Flow<List<ReviewEntity>>
    @Insert suspend fun insert(review: ReviewEntity)
    @Query("SELECT AVG(rating) FROM reviews WHERE lawyerUid = :uid") suspend fun avgRating(uid: String): Double?
    @Query("SELECT COUNT(*) FROM reviews WHERE lawyerUid = :uid") suspend fun count(uid: String): Int
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE uid = :uid ORDER BY createdAt DESC") fun observeFor(uid: String): Flow<List<NotificationEntity>>
    @Query("SELECT COUNT(*) FROM notifications WHERE uid = :uid AND isRead = 0") fun observeUnread(uid: String): Flow<Int>
    @Insert suspend fun insert(n: NotificationEntity)
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id") suspend fun markRead(id: String)
    @Query("UPDATE notifications SET isRead = 1 WHERE uid = :uid") suspend fun markAllRead(uid: String)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE ownerUid = :uid ORDER BY endAt DESC LIMIT 1") fun observeLatest(uid: String): Flow<SubscriptionEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(s: SubscriptionEntity)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE payerUid = :uid ORDER BY createdAt DESC") fun observeFor(uid: String): Flow<List<PaymentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PaymentEntity)
}
