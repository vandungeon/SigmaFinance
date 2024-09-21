package com.example.sigmafinance.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DBType.FundsEventRecurring::class, DBType.DayWithEvents::class],
    version = 1,
    exportSchema = true
)

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