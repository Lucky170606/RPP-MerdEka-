package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ModulAjarEntity::class, ProtaEntity::class, PromesEntity::class, AtpEntity::class, AssessmentEntity::class, P5AssessmentEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modulAjarDao(): ModulAjarDao
    abstract fun protaDao(): ProtaDao
    abstract fun promesDao(): PromesDao
    abstract fun atpDao(): AtpDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun p5AssessmentDao(): P5AssessmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rpp_merdeka_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
