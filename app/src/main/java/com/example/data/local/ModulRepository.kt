package com.example.data.local

import kotlinx.coroutines.flow.Flow

class ModulRepository(private val dao: ModulAjarDao) {
    val allModul: Flow<List<ModulAjarEntity>> = dao.getAllModul()
    val favoriteModul: Flow<List<ModulAjarEntity>> = dao.getFavoriteModul()
    val modulCount: Flow<Int> = dao.getModulCount()

    fun getModulById(id: Long): Flow<ModulAjarEntity?> = dao.getModulById(id)

    suspend fun getModulDirect(id: Long): ModulAjarEntity? = dao.getModulDirect(id)

    fun searchModul(query: String): Flow<List<ModulAjarEntity>> = dao.searchModul(query)

    suspend fun insertModul(modul: ModulAjarEntity): Long = dao.insertModul(modul)

    suspend fun updateModul(modul: ModulAjarEntity) = dao.updateModul(modul)

    suspend fun deleteModul(id: Long) = dao.deleteModulById(id)

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        dao.setFavorite(id, !currentFavorite)
    }
}
