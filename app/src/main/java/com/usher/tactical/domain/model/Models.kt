package com.usher.tactical.domain.model

/**
 * 宿主信息
 */
data class Host(
    val id: String = "usher",
    val displayName: String = "白厄",
    val overallScore: Float = 70f
)

/**
 * 属性条目
 */
data class HostAttribute(
    val id: String,
    val category: String,
    val attrName: String,
    val value: Float,
    val detailsJson: String? = null
)

/**
 * 资源
 */
data class Resource(
    val id: String,
    val type: String,
    val amount: Float
) {
    companion object {
        const val TYPE_POTENTIAL_POINT = "potential_point"
        const val TYPE_SPECIALTY_POINT = "specialty_point"
        const val TYPE_UNIVERSAL_EXP = "universal_exp"
        const val TYPE_HIDDEN_EXP = "hidden_exp"
    }
}

/**
 * 任务
 */
data class Task(
    val id: String,
    val parentId: String? = null,
    val type: String,
    val name: String,
    val requirementJson: String,
    val rewardJson: String,
    val status: String = "active",
    val orderIndex: Float = 0f,
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val subGoals: List<Task> = emptyList()  // 内存中组装的子目标
) {
    companion object {
        const val TYPE_DAILY = "daily"
        const val TYPE_MAIN = "main"
        const val TYPE_MAIN_SUB = "main_sub"
        const val TYPE_SIDE = "side"
        const val STATUS_ACTIVE = "active"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_ARCHIVED = "archived"
    }
}

/**
 * 签到记录
 */
data class TaskCheckIn(
    val id: String,
    val taskId: String,
    val checkinTime: Long,
    val evidence: String? = null
)

/**
 * 锁死状态
 */
data class LockStatus(
    val disqualificationCounter: Int = 0,
    val isLocked: Boolean = false,
    val lastSettlement: Long = System.currentTimeMillis()
)

/**
 * 系统日志
 */
data class SystemLog(
    val id: String,
    val timestamp: Long,
    val level: String,
    val category: String,
    val message: String
)
