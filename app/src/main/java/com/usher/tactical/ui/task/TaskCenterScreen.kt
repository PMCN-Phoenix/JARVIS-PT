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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.usher.tactical.domain.model.Task
import com.usher.tactical.ui.components.CheckInBox
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextWhite

private val TASK_TABS = listOf("主线", "支线", "日常")

@Composable
fun TaskCenterScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(2) } // 默认选中"日常"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // === 标签页 ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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
                        Box(
                            modifier = Modifier
                                .width(24.dp).height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(AccentCyan)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedTab) {
            0 -> MainQuestTab(state.mainTasks)
            1 -> SideQuestTab(state.sideTasks)
            2 -> DailyTaskTab(state.dailyTasks, state.completedTaskIds, viewModel::checkIn)
        }
    }
}

// ===== 主线任务（占位） =====
@Composable
private fun MainQuestTab(tasks: List<Task>) {
    TacticalCard(title = "主线任务") {
        if (tasks.isEmpty()) {
            PlaceholderText("暂无主线任务")
        }
    }
}

// ===== 支线任务（占位） =====
@Composable
private fun SideQuestTab(tasks: List<Task>) {
    TacticalCard(title = "支线任务") {
        if (tasks.isEmpty()) {
            PlaceholderText("暂无支线任务")
        }
    }
}

// ===== 日常任务 =====
@Composable
private fun DailyTaskTab(
    tasks: List<Task>,
    completedIds: Set<String>,
    onCheckIn: (taskId: String, evidence: String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tasks.forEach { task ->
            val isDone = task.status == Task.STATUS_COMPLETED || task.id in completedIds
            DailyTaskCard(task = task, isDone = isDone, onCheckIn = onCheckIn)
        }

        if (tasks.isEmpty()) {
            TacticalCard(title = "日常任务") {
                PlaceholderText("暂无日常任务")
            }
        }
    }
}

@Composable
private fun DailyTaskCard(
    task: Task,
    isDone: Boolean,
    onCheckIn: (taskId: String, evidence: String) -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isDone) AccentAmber.copy(alpha = 0.1f) else com.usher.tactical.ui.theme.SurfaceCard,
        animationSpec = tween(300)
    )

    TacticalCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧：任务名
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    fontSize = 14.sp,
                    color = if (isDone) AccentAmber else TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 签到组件
                CheckInBox(
                    requirementJson = task.requirementJson,
                    isChecked = isDone,
                    onCheckIn = { evidence -> onCheckIn(task.id, evidence) }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderText(text: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            text = text,
            fontSize = 13.sp, color = TextDim, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
