package com.example.data.backup

import android.content.Context
import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context, private val database: AppDatabase) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun createBackupJson(): String? = withContext(Dispatchers.IO) {
        try {
            val modulList = database.modulAjarDao().getAllModulDirect()
            val protaList = database.protaDao().getAllProtaDirect()
            val promesList = database.promesDao().getAllPromesDirect()
            val atpList = database.atpDao().getAllAtpDirect()

            val backup = DatabaseBackup(modulList, protaList, promesList, atpList)
            moshi.adapter(DatabaseBackup::class.java).toJson(backup)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun restoreFromBackupFile(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val backup = moshi.adapter(DatabaseBackup::class.java).fromJson(jsonString)
            if (backup != null) {
                database.withTransaction {
                    backup.modulAjarList.forEach { database.modulAjarDao().insertModul(it) }
                    backup.protaList.forEach { database.protaDao().insertProta(it) }
                    backup.promesList.forEach { database.promesDao().insertPromes(it) }
                    backup.atpList.forEach { database.atpDao().insertAtp(it) }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
