package ai.heart.classickbeats.data.user.cache

import ai.heart.classickbeats.model.entity.UserEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(user: UserEntity)

    suspend fun insertWithTimestamp(user: UserEntity) {
        insert(user.apply {
            createdAt = System.currentTimeMillis()
            modifiedAt = System.currentTimeMillis()
        })
    }

    @Update
    abstract suspend fun update(user: UserEntity)

    suspend fun updateWithTimestamp(user: UserEntity) {
        update(user.apply {
            modifiedAt = System.currentTimeMillis()
        })
    }

    @Query("SELECT * FROM user WHERE id = :userId")
    abstract fun load(userId: Int): Flow<UserEntity>

    @Query("SELECT * FROM user")
    abstract fun load(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM user WHERE modified_at >= (:currentTime - :freshTimeout)")
    abstract suspend fun hasUser(
        freshTimeout: Long,
        currentTime: Long = System.currentTimeMillis()
    ): Int
}