package com.example.sigmafinance.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.room.TypeConverters
import com.example.sigmafinance.database.ListConverter
import java.time.LocalDate

// Add the LocalDateConverter class here or in a separate file
class LocalDateConverter {
    private val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }
}
class ListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(value: List<DBType.FundsEvent>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<DBType.FundsEvent> {
        val listType = object : TypeToken<List<DBType.FundsEvent>>() {}.type
        return gson.fromJson(value, listType)
    }
}
@Database(
    entities = [DBType.FundsEventRecurring::class, DBType.FundsEvent::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class)
abstract class Days_and_events_db : RoomDatabase() {

    abstract fun dao(): DataAccessObjects

    companion object {
        @Volatile
        private var INSTANCE: Days_and_events_db? = null

        fun getDatabase(context: Context): Days_and_events_db {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Days_and_events_db::class.java,
                    "Days_and_events_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
