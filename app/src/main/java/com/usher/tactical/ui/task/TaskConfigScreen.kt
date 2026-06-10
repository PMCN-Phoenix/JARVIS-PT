package com.usher.tactical.ui.task

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.usher.tactical.domain.model.Task
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * 任务配置中心 — 手动创建任务编辑器
 */
@Composable
fun TaskConfigScreen(navController: NavController, viewModel: TaskViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()

    var taskName by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf("daily") }
    var requirementType by remember { mutableStateOf("numeric") }
    var targetValue by remember { mutableStateOf("100") }
    var unit by remember { mutableStateOf("次") }
    var expReward by remember { mutableStateOf(10f) }
    var attrName by remember { mutableStateOf("力量") }
    var attrReward by remember { mutableStateOf(0.05f) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("▸ 任务配置中心", fontSize = 18.sp, color = TextWhite)
        Text("创建新的任务模板", fontSize = 11.sp, color = TextDim)
        Spacer(modifier = Modifier.height(4.dp))

        // 任务名称
        OutlinedTextField(
            value = taskName, onValueChange = { taskName = it },
            label = { Text("任务名称") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(0.3f),
                focusedLabelColor = AccentCyan, unfocusedLabelColor = TextGray
            )
        )

        // 任务类型选择
        TacticalCard(title = "任务类型") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("daily" to "日常", "main" to "主线", "side" to "支线").forEach { (type, label) ->
                    val selected = taskType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) AccentCyan.copy(0.2f) else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { taskType = type }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(label, fontSize = 13.sp, color = if (selected) AccentCyan else TextGray)
                    }
                }
            }
        }

        // 要求类型
        TacticalCard(title = "要求类型") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("numeric" to "数值型", "percentage" to "百分比型", "duration" to "时间型").forEach { (type, label) ->
                    val selected = requirementType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) AccentCyan.copy(0.2f) else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { requirementType = type }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(label, fontSize = 13.sp, color = if (selected) AccentCyan else TextGray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = targetValue, onValueChange = { targetValue = it },
                    label = { Text("目标值") }, modifier = Modifier.fillMaxWidth(0.6f), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(0.3f),
                        focusedLabelColor = AccentCyan, unfocusedLabelColor = TextGray
                    )
                )
                OutlinedTextField(
                    value = unit, onValueChange = { unit = it },
                    label = { Text("单位") }, modifier = Modifier.width(80.dp), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(0.3f),
                        focusedLabelColor = AccentCyan, unfocusedLabelColor = TextGray
                    )
                )
            }
        }

        // 奖励设定
        TacticalCard(title = "奖励设定") {
            Text("通用经验: ${expReward.toInt()}", fontSize = 13.sp, color = TextGray)
            Slider(
                value = expReward, onValueChange = { expReward = it },
                valueRange = 5f..50f, steps = 8,
                colors = SliderDefaults.colors(thumbColor = AccentAmber, activeTrackColor = AccentAmber)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("属性倾向: $attrName +$attrReward", fontSize = 13.sp, color = TextGray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("力量", "速度", "体力", "弹跳", "排球技术").forEach { name ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (attrName == name) AccentCyan.copy(0.2f) else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { attrName = name }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(name, fontSize = 11.sp, color = if (attrName == name) AccentCyan else TextDim)
                    }
                }
            }
        }

        // 创建按钮
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                .background(AccentAmber).clickable {
                    scope.launch {
                        val reqJson = JSONObject().apply {
                            put("type", requirementType)
                            put("target", targetValue.toFloatOrNull() ?: 100f)
                            put("unit", unit)
                        }
                        val rewardJson = JSONArray().apply {
                            put(JSONObject().apply {
                                put("type", "universal_exp"); put("amount", expReward)
                            })
                            put(JSONObject().apply {
                                put("type", "attribute"); put("name", attrName); put("amount", attrReward)
                            })
                        }
                        viewModel.createTask(
                            Task(
                                id = UUID.randomUUID().toString(),
                                type = taskType,
                                name = taskName.ifBlank { "未命名任务" },
                                requirementJson = reqJson.toString(),
                                rewardJson = rewardJson.toString()
                            )
                        )
                        navController.popBackStack()
                    }
                }.padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("▸ 创建任务", fontSize = 16.sp, color = androidx.compose.ui.graphics.Color.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
