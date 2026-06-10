package com.usher.tactical.core.engine

import com.usher.tactical.domain.repository.HostRepository
import com.usher.tactical.domain.repository.TaskRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 奖励结算引擎
 * 解析 rewardJson → 更新资源 + 属性 + 写日志
 */
@Singleton
class RewardEngine @Inject constructor(
    private val hostRepository: HostRepository,
    private val taskRepository: TaskRepository
) {

    /**
     * 发放任务奖励
     */
    suspend fun grantReward(taskName: String, rewardJson: String) = coroutineScope {
        val rewards = JSONArray(rewardJson)
        val messages = mutableListOf<String>()

        for (i in 0 until rewards.length()) {
            val reward = rewards.getJSONObject(i)
            val type = reward.getString("type")
            val amount = reward.getDouble("amount").toFloat()

            when (type) {
                "universal_exp" -> {
                    hostRepository.addResourceAmount("universal_exp", amount)
                    messages.add("通用经验+${amount.toInt()}")
                }
                "hidden_exp" -> {
                    hostRepository.addResourceAmount("hidden_exp", amount)
                    messages.add("隐匿经验+${amount.toInt()}")
                }
                "potential_point" -> {
                    hostRepository.addResourceAmount("potential_point", amount)
                    messages.add("潜能点+${amount.toInt()}")
                }
                "specialty_point" -> {
                    hostRepository.addResourceAmount("specialty_point", amount)
                    messages.add("专长点+${amount.toInt()}")
                }
                "attribute" -> {
                    val attrName = reward.getString("name")
                    // 更新对应属性
                    async {
                        hostRepository.addAttributeValue(attrName, amount)
                    }
                    messages.add("$attrName+$amount")
                }
            }
        }

        // 日志：奖励发放
        hostRepository.insertSystemLog(
            "info", "reward",
            "任务[$taskName]完成。${messages.joinToString("，")}"
        )
    }

    /**
     * 每日结算：检查所有日常任务是否完成
     * 全部完成 → 计数器归零；有未完成 → 计数器+1；若>=3 → 锁死
     */
    suspend fun dailySettlement() {
        val dailyTasks = taskRepository.getActiveDailyTasks()
        val allDone = dailyTasks.all { it.status == "completed" }

        val lockStatus = hostRepository.getLockStatus() ?: return

        val newCounter = if (allDone) 0 else lockStatus.disqualificationCounter + 1
        hostRepository.updateLockCounter(newCounter)

        if (allDone) {
            hostRepository.insertSystemLog("info", "system", "今日日常任务全部完成。失格计数器归零。")
        } else {
            val undone = dailyTasks.filter { it.status != "completed" }.map { it.name }
            hostRepository.insertSystemLog(
                "warning", "system",
                "今日有${undone.size}项日常未完成：${undone.joinToString("、")}。失格计数+1 (当前$newCounter)"
            )
            if (newCounter >= 3) {
                hostRepository.setLocked(true)
                hostRepository.insertSystemLog(
                    "error", "penalty",
                    "失格计数已达$newCounter。系统锁死已触发。"
                )
            }
        }
    }

    companion object {
        fun newId(): String = UUID.randomUUID().toString()
    }
}
