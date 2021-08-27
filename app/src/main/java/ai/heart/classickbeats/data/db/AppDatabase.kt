package ai.heart.classickbeats.data.db

import ai.heart.classickbeats.data.record.cache.PPGDao
import ai.heart.classickbeats.data.user.cache.UserDao
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.model.entity.UserEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, PPGEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun ppgDao(): PPGDao

    companion object {
        private const val databaseName = "jeeva-db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}