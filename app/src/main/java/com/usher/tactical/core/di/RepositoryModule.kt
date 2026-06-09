package com.usher.tactical.core.di

import com.usher.tactical.data.repository.HostRepositoryImpl
import com.usher.tactical.data.repository.TaskRepositoryImpl
import com.usher.tactical.domain.repository.HostRepository
import com.usher.tactical.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHostRepository(impl: HostRepositoryImpl): HostRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
