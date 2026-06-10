package com.usher.tactical.core.engine

import com.usher.tactical.core.security.TOTPGenerator
import com.usher.tactical.domain.repository.HostRepository
import com.usher.tactical.domain.repository.TaskRepository
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 锁死与惩罚引擎
 * - 每日结算触发检查
 * - 锁死触发 + 解锁流程
 * - 惩罚执行（扣资源、降属性）
 */
@Singleton
class LockEngine @Inject constructor(
    private val hostRepository: HostRepository,
    private val taskRepository: TaskRepository
) {

    /**
     * 每日结算（由 RewardEngine.dailySettlement 调用后触发锁死检查）
     */
    suspend fun checkLockTrigger() {
        val lockStatus = hostRepository.getLockStatus() ?: return
        if (lockStatus.disqualificationCounter >= 3 && !lockStatus.isLocked) {
            hostRepository.setLocked(true)
            hostRepository.insertSystemLog(
                "error", "penalty",
                "连续${lockStatus.disqualificationCounter}天未完成日常任务。系统已锁死。"
            )
        }
    }

    /**
     * 生成动态令牌并返回（格式 A7X-29K）
     */
    fun generateToken(): String {
        val code = TOTPGenerator.generate()
        return "${code.substring(0, 3)}-${code.substring(3, 6)}"
    }

    /**
     * 验证令牌
     */
    fun verifyToken(token: String): Boolean {
        val clean = token.replace("-", "").replace(" ", "")
        return TOTPGenerator.verify(clean)
    }

    /**
     * 执行惩罚（编译通过后调用）
     * - 扣除通用经验20%
     * - 清空所有潜能点
     * - 全属性随机降低1%-3%
     */
    suspend fun executePenalty(): List<String> {
        val messages = mutableListOf<String>()

        // 扣除20%通用经验
        val expAmount = hostRepository.getResourceByType("universal_exp")?.amount ?: 0f
        val expDeduct = (expAmount * 0.2f).coerceAtLeast(0f)
        if (expDeduct > 0f) {
            hostRepository.addResourceAmount("universal_exp", -expDeduct)
            messages.add("通用经验 -${expDeduct.toInt()}")
        }

        // 清空潜能点
        val ppAmount = hostRepository.getResourceByType("potential_point")?.amount ?: 0f
        if (ppAmount > 0f) {
            hostRepository.addResourceAmount("potential_point", -ppAmount)
            messages.add("潜能点 -${ppAmount.toInt()}")
        }

        // 全属性降低1%-3%
        val attrs = hostRepository.getAttributesByCategory("体能")
        for (attr in attrs) {
            val decayPercent = (Random.nextFloat() * 0.02f + 0.01f) // 1%-3%
            val decay = (attr.value * decayPercent).coerceAtLeast(0.01f)
            hostRepository.addAttributeValue(attr.attrName, -decay)
            messages.add("${attr.attrName} ${String.format("%.2f", -decay)}")
        }

        // 解锁
        hostRepository.setLocked(false)
        hostRepository.updateLockCounter(0)
        hostRepository.insertSystemLog(
            "info", "system",
            "重新编译完成。惩罚已执行：${messages.joinToString("，")}。系统已解锁。"
        )

        return messages
    }
}
