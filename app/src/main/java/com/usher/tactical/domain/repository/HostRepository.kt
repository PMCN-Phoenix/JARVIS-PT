package com.usher.tactical.domain.repository

import com.usher.tactical.domain.model.Host
import com.usher.tactical.domain.model.HostAttribute
import com.usher.tactical.domain.model.LockStatus
import com.usher.tactical.domain.model.Resource
import com.usher.tactical.domain.model.SystemLog
import kotlinx.coroutines.flow.Flow

interface HostRepository {
    fun observeHost(): Flow<Host?>
    fun observeAttributes(): Flow<List<HostAttribute>>
    fun observeAttributesByCategory(category: String): Flow<List<HostAttribute>>
    fun observeResources(): Flow<List<Resource>>
    fun observeLockStatus(): Flow<LockStatus?>
    fun observeSystemLogs(): Flow<List<SystemLog>>
    fun observeSystemLogsByCategory(category: String): Flow<List<SystemLog>>

    suspend fun updateAttribute(attr: HostAttribute)
    suspend fun addAttributeValue(attrName: String, delta: Float)
    suspend fun addResourceAmount(type: String, delta: Float)
    suspend fun updateLockCounter(counter: Int)
    suspend fun setLocked(locked: Boolean)
    suspend fun getLockStatus(): LockStatus?
    suspend fun getResourceByType(type: String): Resource?
    suspend fun getAttributesByCategory(category: String): List<HostAttribute>
    suspend fun insertSystemLog(level: String, category: String, message: String)
}
