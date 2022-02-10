package ai.heart.classickbeats.data.db

import ai.heart.classickbeats.data.record.cache.*
import ai.heart.classickbeats.data.reminder.cache.ReminderDao
import ai.heart.classickbeats.data.user.cache.UserDao
import ai.heart.classickbeats.data.util.IntStringConverter
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