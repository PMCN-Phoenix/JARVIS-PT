package com.usher.tactical.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.usher.tactical.ui.theme.TextDim

@Composable
fun TaskCenterScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("▸ 任务中心", style = com.usher.tactical.ui.theme.TacticalTypography.titleLarge)
        Text("阶段三中实现", style = com.usher.tactical.ui.theme.TacticalTypography.bodyLarge)
    }
}
