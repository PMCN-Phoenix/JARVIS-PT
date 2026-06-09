package com.usher.tactical.ui.backpack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.usher.tactical.ui.theme.TextDim

@Composable
fun BackpackScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("▸ 资源背包", style = com.usher.tactical.ui.theme.TacticalTypography.titleLarge)
        Text("阶段六中实现", style = com.usher.tactical.ui.theme.TacticalTypography.bodyLarge)
    }
}
