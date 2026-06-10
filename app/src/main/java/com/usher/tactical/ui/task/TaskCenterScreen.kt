package com.usher.tactical.ui.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.usher.tactical.domain.model.Task
import com.usher.tactical.ui.components.CheckInBox
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.SurfaceCard
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextWhite

private val TASK_TABS = listOf("主线", "支线", "日常")

@Composable
fun TaskCenterScreen(
    navController: NavController? = null,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(2) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题栏 + 齿轮图标
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 标签页
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                TASK_TABS.forEachIndexed { index, label ->
                    val isSelected = index == selectedTab
                    val color by animateColorAsState(
                        targetValue = if (isSelected) AccentCyan else TextDim,
                        animationSpec = tween(200)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedTab = index }
                    ) {
                        Text(text = label, fontSize = 16.sp, color = color)
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(Modifier.width(24.dp).height(2.dp).clip(RoundedCornerShape(1.dp)).background(AccentCyan))
                        }
                    }
                }
            }
            // 齿轮图标 → 配置中心
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "配置",
                tint = TextDim,
                modifier = Modifier.size(22.dp).clickable { navController?.navigate("task_config") }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedTab) {
            0 -> MainQuestTab(state.mainTasks)
            1 -> SideQuestTab(state.sideTasks)
            2 -> DailyTaskTab(state.dailyTasks, state.completedTaskIds, viewModel::checkIn)
        }
    }
}

// ===== 主线任务 =====
@Composable
private fun MainQuestTab(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        TacticalCard(title = "主线任务") {
            Text("暂无主线任务", fontSize = 13.sp, color = TextDim, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        return
    }
    tasks.forEach { task ->
        TacticalCard(title = task.name) {
            // 进度条
            val subTasks = task.subGoals.ifEmpty {
                // 从数据库中的 main_sub 任务解析
                tasks.filter { it.parentId == task.id && it.type == Task.TYPE_MAIN_SUB }
            }
            val total = subTasks.size.coerceAtLeast(1)
            val done = subTasks.count { it.status == Task.STATUS_COMPLETED }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("进度 $done/$total", fontSize = 13.sp, color = TextDim)
                if (done == total && total > 0) {
                    Text("可提交领取奖励", fontSize = 13.sp, color = AccentAmber)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(TextDim.copy(0.3f))) {
                Box(
                    Modifier.fillMaxWidth((done.toFloat() / total).coerceIn(0f, 1f)).height(4.dp)
                        .clip(RoundedCornerShape(2.dp)).background(AccentAmber)
                )
            }
        }
    }
}

@Composable
private fun SideQuestTab(tasks: List<Task>) {
    TacticalCard(title = "支线任务") {
        if (tasks.isEmpty()) Text("暂无支线任务", fontSize = 13.sp, color = TextDim, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun DailyTaskTab(
    tasks: List<Task>,
    completedIds: Set<String>,
    onCheckIn: (taskId: String, evidence: String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tasks.forEach { task ->
            val isDone = task.status == Task.STATUS_COMPLETED || task.id in completedIds
            TacticalCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.name, fontSize = 14.sp, color = if (isDone) AccentAmber else TextWhite)
                        Spacer(modifier = Modifier.height(4.dp))
                        CheckInBox(
                            requirementJson = task.requirementJson,
                            isChecked = isDone,
                            onCheckIn = { evidence -> onCheckIn(task.id, evidence) }
                        )
                    }
                }
            }
        }
        if (tasks.isEmpty()) {
            TacticalCard(title = "日常任务") {
                Text("暂无日常任务", fontSize = 13.sp, color = TextDim, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
