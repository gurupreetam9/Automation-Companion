package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationConfigDao {

    @Query("SELECT * FROM automation_configs WHERE packageName = :packageName")
    fun getAutomationConfig(packageName: String): Flow<AutomationConfig?>

    @Query("SELECT * FROM automation_configs")
    fun getAllAutomationConfigs(): Flow<List<AutomationConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomationConfig(config: AutomationConfig)
}
