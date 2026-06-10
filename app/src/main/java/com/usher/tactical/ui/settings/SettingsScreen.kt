package com.usher.tactical.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.usher.tactical.core.engine.LockEngine
import com.usher.tactical.core.security.TOTPGenerator
import com.usher.tactical.domain.repository.HostRepository
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.dashboard.HostViewModel
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite
import com.usher.tactical.ui.theme.WarningRed
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: HostViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }
    var showLockDialog by remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf("") }
    var seedInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("▸ 设置", fontSize = 18.sp, color = TextWhite)
        Spacer(modifier = Modifier.height(4.dp))

        // API Key 配置
        TacticalCard(title = "战术参谋 API") {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = apiKey, onValueChange = { apiKey = it },
                label = { Text("OpenAI / Claude API Key") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(0.3f),
                    focusedLabelColor = AccentCyan, unfocusedLabelColor = TextGray
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 母块种子
        TacticalCard(title = "母块验证种子") {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = seedInput, onValueChange = { seedInput = it },
                label = { Text("共享种子（base32）") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(0.3f),
                    focusedLabelColor = AccentCyan, unfocusedLabelColor = TextGray
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("测试令牌: ${TOTPGenerator.generate()}", fontSize = 13.sp, color = AccentCyan, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }

        // 危险区域
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .border(1.dp, WarningRed.copy(0.5f), RoundedCornerShape(12.dp)).padding(16.dp)
        ) {
            Column {
                Text("底层编译室", fontSize = 14.sp, color = WarningRed)
                Spacer(modifier = Modifier.height(8.dp))

                DangerButton("导出作战数据 (JSON)", onClick = { /* TODO: export JSON */ })
                DangerButton("手动触发系统锁死", onClick = { showLockDialog = true })
                DangerButton("完全重置系统", onClick = { showResetDialog = true })
            }
        }
    }

    // 重置确认弹窗
    if (showResetDialog) {
        var confirmCode by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("完全重置系统", color = WarningRed) },
            text = {
                Column {
                    Text("此操作将清空所有数据且不可撤销。请输入 24 位确认码。", fontSize = 13.sp, color = TextWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmCode, onValueChange = { confirmCode = it },
                        label = { Text("确认码") }, singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (confirmCode.length == 24) {
                        scope.launch {
                            viewModel.resetAll()
                            showResetDialog = false
                        }
                    }
                }) { Text("确认重置", color = WarningRed) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("取消") } }
        )
    }

    if (showLockDialog) {
        AlertDialog(
            onDismissRequest = { showLockDialog = false },
            title = { Text("手动触发系统锁死？", color = WarningRed) },
            text = { Text("此操作将立刻锁死系统，触发惩罚流程。确认继续？", fontSize = 13.sp, color = TextWhite) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { viewModel.triggerLock() }
                    showLockDialog = false
                }) { Text("确认锁死", color = WarningRed) }
            },
            dismissButton = { TextButton(onClick = { showLockDialog = false }) { Text("取消") } }
        )
    }
}

@Composable
private fun DangerButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(0.5.dp, WarningRed.copy(0.3f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick).padding(12.dp)
    ) {
        Text(label, fontSize = 13.sp, color = WarningRed, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}
