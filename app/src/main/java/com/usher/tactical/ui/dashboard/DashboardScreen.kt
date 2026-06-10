package com.usher.tactical.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usher.tactical.domain.model.HostAttribute
import com.usher.tactical.ui.components.AttrProgressBar
import com.usher.tactical.ui.components.BreathingRing
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.navigation.Routes
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: androidx.navigation.NavController? = null,
    viewModel: HostViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { /* ViewModel 自动刷新 */ },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === 顶部宿主ID栏 ===
            HostIdBar(
                hostName = state.host?.displayName ?: "白厄",
                overallScore = state.host?.overallScore ?: 70f
            )

            // === 六维状态速览 ===
            TacticalCard(title = "作战单元状态") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.attributes.forEach { attr ->
                        AttrProgressBar(
                            label = attr.attrName,
                            value = attr.value,
                            maxValue = if (attr.category == "枪械战斗") 10f else 100f
                        )
                    }
                }
            }

            // === 首要任务占位 ===
            TacticalCard(title = "首要任务") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "暂无活跃主线任务",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp, color = TextDim
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "▸ 前往任务中心创建主线任务",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 13.sp, color = AccentCyan
                        ),
                        modifier = Modifier.clickable {
                            navController?.navigate(Routes.TASK) {
                                popUpTo(Routes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // === 锁死状态指示（若触发则展示） ===
            if (state.lockStatus?.isLocked == true) {
                TacticalCard(title = "系统状态") {
                    Text(
                        text = "⚠ 系统已锁死 — 失格计数: ${state.lockStatus?.disqualificationCounter}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            color = com.usher.tactical.ui.theme.WarningRed
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HostIdBar(hostName: String, overallScore: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "// HOST: ${hostName.uppercase()}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = AccentCyan
            )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                BreathingRing(ringSize = 48.dp)
                Text(
                    text = overallScore.toInt().toString(),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AccentCyan,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
