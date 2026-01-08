package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AutomationConfig::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AutomationConfigDatabase : RoomDatabase() {

    abstract fun automationConfigDao(): AutomationConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AutomationConfigDatabase? = null

        fun getDatabase(context: Context): AutomationConfigDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutomationConfigDatabase::class.java,
                    "automation_config_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
