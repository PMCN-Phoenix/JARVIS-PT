package com.usher.tactical.ui.backpack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.usher.tactical.domain.model.Resource
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.dashboard.HostViewModel
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite

@Composable
fun BackpackScreen(viewModel: HostViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("▸ 资源背包", fontSize = 18.sp, color = TextWhite)
        Spacer(modifier = Modifier.height(4.dp))

        // 2x2 资源网格
        val resources = state.resources
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResourceGridCard("潜能点", findResource(resources, "potential_point"), AccentAmber, Modifier.fillMaxWidth(0.5f))
            ResourceGridCard("专长点", findResource(resources, "specialty_point"), AccentCyan, Modifier.fillMaxWidth(0.5f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResourceGridCard("通用经验", findResource(resources, "universal_exp"), TextWhite, Modifier.fillMaxWidth(0.5f))
            ResourceGridCard("隐匿经验", findResource(resources, "hidden_exp"), TextDim, Modifier.fillMaxWidth(0.5f))
        }
    }
}

@Composable
private fun ResourceGridCard(label: String, amount: Float, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    TacticalCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (amount == amount.toLong().toFloat() && amount < 100) amount.toInt().toString() else String.format("%.0f", amount),
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                color = color,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, color = TextGray)
        }
    }
}

private fun findResource(resources: List<Resource>?, type: String): Float {
    return resources?.find { it.type == type }?.amount ?: 0f
}
