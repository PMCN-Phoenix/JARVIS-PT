package com.usher.tactical.core.di

import android.content.Context
import com.usher.tactical.core.database.TacticalDatabase
import com.usher.tactical.core.database.dao.HostAttributeDao
import com.usher.tactical.core.database.dao.HostDao
import com.usher.tactical.core.database.dao.LockStatusDao
import com.usher.tactical.core.database.dao.ResourceDao
import com.usher.tactical.core.database.dao.SystemLogDao
import com.usher.tactical.core.database.dao.TaskCheckInDao
import com.usher.tactical.core.database.dao.TaskDao
import com.usher.tactical.core.database.dao.TaskTemplateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TacticalDatabase {
        return TacticalDatabase.getInstance(context)
    }

    @Provides fun provideHostDao(db: TacticalDatabase): HostDao = db.hostDao()
    @Provides fun provideHostAttributeDao(db: TacticalDatabase): HostAttributeDao = db.hostAttributeDao()
    @Provides fun provideResourceDao(db: TacticalDatabase): ResourceDao = db.resourceDao()
    @Provides fun provideTaskDao(db: TacticalDatabase): TaskDao = db.taskDao()
    @Provides fun provideTaskCheckInDao(db: TacticalDatabase): TaskCheckInDao = db.taskCheckInDao()
    @Provides fun provideTaskTemplateDao(db: TacticalDatabase): TaskTemplateDao = db.taskTemplateDao()
    @Provides fun provideLockStatusDao(db: TacticalDatabase): LockStatusDao = db.lockStatusDao()
    @Provides fun provideSystemLogDao(db: TacticalDatabase): SystemLogDao = db.systemLogDao()
}
