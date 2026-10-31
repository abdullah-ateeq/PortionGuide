package com.example.portionguide.data.repository

import com.example.portionguide.data.database.CalculationDao
import com.example.portionguide.data.database.CalculationEntity
import com.example.portionguide.domain.model.PortionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PortionRepository {
    fun getAllCalculations(precision: Int = 2): Flow<List<PortionResult>>
    fun getRecentCalculations(limit: Int = 5, precision: Int = 2): Flow<List<PortionResult>>
    suspend fun getCalculationById(id: Long, precision: Int = 2): PortionResult?
    suspend fun saveCalculation(result: PortionResult): Long
    suspend fun deleteCalculation(id: Long)
    suspend fun clearAllCalculations()
}

class PortionRepositoryImpl(
    private val calculationDao: CalculationDao
) : PortionRepository {

    override fun getAllCalculations(precision: Int): Flow<List<PortionResult>> {
        return calculationDao.getAllCalculations().map { entities ->
            entities.map { it.toDomain(precision) }
        }
    }

    override fun getRecentCalculations(limit: Int, precision: Int): Flow<List<PortionResult>> {
        return calculationDao.getRecentCalculations(limit).map { entities ->
            entities.map { it.toDomain(precision) }
        }
    }

    override suspend fun getCalculationById(id: Long, precision: Int): PortionResult? {
        return calculationDao.getCalculationById(id)?.toDomain(precision)
    }

    override suspend fun saveCalculation(result: PortionResult): Long {
        val entity = CalculationEntity.fromDomain(result)
        return calculationDao.insertCalculation(entity)
    }

    override suspend fun deleteCalculation(id: Long) {
        calculationDao.deleteById(id)
    }

    override suspend fun clearAllCalculations() {
        calculationDao.deleteAll()
    }
}
