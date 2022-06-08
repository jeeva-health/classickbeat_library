package ai.heart.classickbeats.network.db

import ai.heart.classickbeats.network.record.cache.*
import ai.heart.classickbeats.network.reminder.cache.ReminderDao
import ai.heart.classickbeats.network.user.cache.UserDao
import ai.heart.classickbeats.network.util.IntStringConverter
import ai.heart.classickbeats.model.entity.HistoryEntity
import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.model.entity.TimelineEntityDatabase
import ai.heart.classickbeats.model.entity.UserEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [UserEntity::class, TimelineEntityDatabase::class, TimelineRemoteKey::class,
        HistoryEntity::class, HistoryRemoteKey::class, ReminderEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IntStringConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun historyDao(): HistoryDao

    abstract fun historyRemoteKeyDao(): HistoryRemoteKeyDao

    abstract fun timelineDao(): TimelineDao

    abstract fun timelineRemoteKeyDao(): TimelineRemoteKeyDao

    abstract fun reminderDao(): ReminderDao

    companion object {
        private const val databaseName = "jeeva-db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}