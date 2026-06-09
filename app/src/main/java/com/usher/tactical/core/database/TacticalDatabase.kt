package com.usher.tactical.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.usher.tactical.core.database.dao.HostAttributeDao
import com.usher.tactical.core.database.dao.HostDao
import com.usher.tactical.core.database.dao.LockStatusDao
import com.usher.tactical.core.database.dao.ResourceDao
import com.usher.tactical.core.database.dao.SystemLogDao
import com.usher.tactical.core.database.dao.TaskCheckInDao
import com.usher.tactical.core.database.dao.TaskDao
import com.usher.tactical.core.database.dao.TaskTemplateDao
import com.usher.tactical.core.database.entity.HostAttributeEntity
import com.usher.tactical.core.database.entity.HostEntity
import com.usher.tactical.core.database.entity.LockStatusEntity
import com.usher.tactical.core.database.entity.ResourceEntity
import com.usher.tactical.core.database.entity.SystemLogEntity
import com.usher.tactical.core.database.entity.TaskCheckInEntity
import com.usher.tactical.core.database.entity.TaskEntity
import com.usher.tactical.core.database.entity.TaskTemplateEntity
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        HostEntity::class,
        HostAttributeEntity::class,
        ResourceEntity::class,
        TaskEntity::class,
        TaskCheckInEntity::class,
        TaskTemplateEntity::class,
        LockStatusEntity::class,
        SystemLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TacticalDatabase : RoomDatabase() {

    abstract fun hostDao(): HostDao
    abstract fun hostAttributeDao(): HostAttributeDao
    abstract fun resourceDao(): ResourceDao
    abstract fun taskDao(): TaskDao
    abstract fun taskCheckInDao(): TaskCheckInDao
    abstract fun taskTemplateDao(): TaskTemplateDao
    abstract fun lockStatusDao(): LockStatusDao
    abstract fun systemLogDao(): SystemLogDao

    companion object {
        private const val DB_NAME = "tactical.db"
        private const val DB_PASSPHRASE = "usher_tactical_default"

        var useEncryption: Boolean = true

        @Volatile
        private var INSTANCE: TacticalDatabase? = null

        fun getInstance(context: Context, passphrase: ByteArray? = null): TacticalDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, passphrase).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, passphrase: ByteArray?): TacticalDatabase {
            val builder = Room.databaseBuilder(
                context.applicationContext,
                TacticalDatabase::class.java,
                DB_NAME
            )

            if (useEncryption) {
                val passphraseBytes = passphrase ?: DB_PASSPHRASE.toByteArray()
                val factory = SupportOpenHelperFactory(passphraseBytes)
                builder.openHelperFactory(factory)
            }

            return builder
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val now = System.currentTimeMillis()

                        // 创建宿主
                        db.execSQL(
                            "INSERT OR IGNORE INTO host (id, displayName, overallScore, createdAt) " +
                            "VALUES ('usher', '白厄', 70.0, $now)"
                        )

                        // 初始化六维属性
                        val attrs = listOf(
                            Triple("体能", "力量", 83.8f),
                            Triple("体能", "速度", 80.2f),
                            Triple("体能", "体力", 82.9f),
                            Triple("体能", "弹跳", 83.7f),
                            Triple("排球技术", "排球技术", 76.5f),
                            Triple("枪械战斗", "枪械", 3f)
                        )
                        attrs.forEachIndexed { i, (cat, name, value) ->
                            db.execSQL(
                                "INSERT OR IGNORE INTO host_attribute (id, hostId, category, attrName, value, updatedAt) " +
                                "VALUES ('attr_$i', 'usher', '$cat', '$name', $value, $now)"
                            )
                        }

                        // 初始化资源
                        val resources = listOf(
                            Triple("res_0", "potential_point", 3f),
                            Triple("res_1", "specialty_point", 3f),
                            Triple("res_2", "universal_exp", 4100f),
                            Triple("res_3", "hidden_exp", 20f)
                        )
                        resources.forEach { (id, type, amount) ->
                            db.execSQL(
                                "INSERT OR IGNORE INTO resource (id, hostId, type, amount, updatedAt) " +
                                "VALUES ('$id', 'usher', '$type', $amount, $now)"
                            )
                        }

                        // 初始化锁死状态
                        db.execSQL(
                            "INSERT OR IGNORE INTO lock_status (hostId, disqualificationCounter, isLocked, lastSettlement) " +
                            "VALUES ('usher', 0, 0, $now)"
                        )

                        // 预置一条系统日志
                        db.execSQL(
                            "INSERT INTO system_log (id, hostId, timestamp, level, category, message) " +
                            "VALUES ('log_init', 'usher', $now, 'info', 'system', '战术系统初始化完成。欢迎回来，宿主白厄。')"
                        )
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }

        fun rekey(context: Context, newPassphrase: ByteArray) {
            if (!useEncryption) return
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            val db = SQLiteDatabase.openDatabase(
                dbPath, DB_PASSPHRASE, null as SQLiteDatabase.CursorFactory?, 0, null as SQLiteDatabaseHook?
            )
            db.rawExecSQL("PRAGMA rekey = ?", String(newPassphrase))
            db.close()
        }
    }
}
