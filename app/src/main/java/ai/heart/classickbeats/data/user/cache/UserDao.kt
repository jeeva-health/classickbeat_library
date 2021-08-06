package ai.heart.classickbeats.data.user.cache

import ai.heart.classickbeats.model.entity.UserEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(user: UserEntity)

    @Query("SELECT * FROM user WHERE id = :userId")
    fun load(userId: Int): Flow<UserEntity>

    @Query("SELECT * FROM user LIMIT 1")
    fun load(): Flow<UserEntity>
}