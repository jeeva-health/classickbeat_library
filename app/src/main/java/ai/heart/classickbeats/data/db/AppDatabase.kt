package ai.heart.classickbeats.data.db

import ai.heart.classickbeats.data.record.cache.*
import ai.heart.classickbeats.data.user.cache.UserDao
import ai.heart.classickbeats.model.HistoryRecordDatabase
import ai.heart.classickbeats.model.entity.TimelineEntity
import ai.heart.classickbeats.model.entity.UserEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, HistoryRecordDatabase::class, HistoryRemoteKey::class, TimelineEntity::class, TimelineRemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun historyDao(): HistoryDao

    abstract fun historyRemoteKeyDao(): HistoryRemoteKeyDao

    abstract fun timelineDao(): TimelineDao

    abstract fun timelineRemoteKeyDao(): TimelineRemoteKeyDao

    companion object {
        private const val databaseName = "jeeva-db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}