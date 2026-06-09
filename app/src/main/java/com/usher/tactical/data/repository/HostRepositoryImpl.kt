package com.usher.tactical.data.repository

import com.usher.tactical.core.database.dao.HostAttributeDao
import com.usher.tactical.core.database.dao.HostDao
import com.usher.tactical.core.database.dao.LockStatusDao
import com.usher.tactical.core.database.dao.ResourceDao
import com.usher.tactical.core.database.dao.SystemLogDao
import com.usher.tactical.core.database.entity.HostAttributeEntity
import com.usher.tactical.core.database.entity.SystemLogEntity
import com.usher.tactical.domain.model.Host
import com.usher.tactical.domain.model.HostAttribute
import com.usher.tactical.domain.model.LockStatus
import com.usher.tactical.domain.model.Resource
import com.usher.tactical.domain.model.SystemLog
import com.usher.tactical.domain.repository.HostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class HostRepositoryImpl @Inject constructor(
    private val hostDao: HostDao,
    private val attributeDao: HostAttributeDao,
    private val resourceDao: ResourceDao,
    private val lockStatusDao: LockStatusDao,
    private val systemLogDao: SystemLogDao
) : HostRepository {

    override fun observeHost(): Flow<Host?> = hostDao.observe().map { entity ->
        entity?.let { Host(it.id, it.displayName, it.overallScore) }
    }

    override fun observeAttributes(): Flow<List<HostAttribute>> =
        attributeDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeAttributesByCategory(category: String): Flow<List<HostAttribute>> =
        attributeDao.observeByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun observeResources(): Flow<List<Resource>> =
        resourceDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeLockStatus(): Flow<LockStatus?> =
        lockStatusDao.observe().map { entity ->
            entity?.let { LockStatus(it.disqualificationCounter, it.isLocked, it.lastSettlement) }
        }

    override fun observeSystemLogs(): Flow<List<SystemLog>> =
        systemLogDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeSystemLogsByCategory(category: String): Flow<List<SystemLog>> =
        systemLogDao.observeByCategory(category).map { list -> list.map { it.toDomain() } }

    override suspend fun updateAttribute(attr: HostAttribute) {
        attributeDao.upsert(HostAttributeEntity(
            id = attr.id,
            category = attr.category,
            attrName = attr.attrName,
            value = attr.value,
            detailsJson = attr.detailsJson,
            updatedAt = System.currentTimeMillis()
        ))
    }

    override suspend fun addResourceAmount(type: String, delta: Float) {
        resourceDao.addAmount(type, delta)
    }

    override suspend fun updateLockCounter(counter: Int) {
        lockStatusDao.updateCounter(counter)
    }

    override suspend fun setLocked(locked: Boolean) {
        lockStatusDao.setLocked(locked)
    }

    override suspend fun insertSystemLog(level: String, category: String, message: String) {
        systemLogDao.insert(SystemLogEntity(
            id = UUID.randomUUID().toString(),
            level = level,
            category = category,
            message = message
        ))
    }

    // --- Mappers ---

    private fun HostAttributeEntity.toDomain() = HostAttribute(id, category, attrName, value, detailsJson)

    private fun com.usher.tactical.core.database.entity.ResourceEntity.toDomain() = Resource(id, type, amount)

    private fun SystemLogEntity.toDomain() = SystemLog(id, timestamp, level, category, message)
}
