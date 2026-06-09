package com.usher.tactical.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.usher.tactical.ui.theme.TextDim

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("// HOST: USHER", style = com.usher.tactical.ui.theme.TacticalTypography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("▸ 作战单元状态", style = com.usher.tactical.ui.theme.TacticalTypography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("综合评分: 70", style = com.usher.tactical.ui.theme.TacticalTypography.bodyLarge)
        Text("阶段二中实现六维属性面板", style = com.usher.tactical.ui.theme.TacticalTypography.labelSmall)
    }
}
