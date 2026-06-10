package com.usher.tactical.ui.attribute

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
import com.usher.tactical.domain.model.HostAttribute
import com.usher.tactical.ui.components.AttrProgressBar
import com.usher.tactical.ui.components.TacticalCard
import com.usher.tactical.ui.dashboard.HostViewModel
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.BorderCyan
import com.usher.tactical.ui.theme.SurfaceCard
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite

private val TABS = listOf("体能", "排球技术", "枪械战斗")

@Composable
fun AttributeScreen(viewModel: HostViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // === 标签页 ===
        TabRow(selectedIndex = selectedTab, onTabSelected = { selectedTab = it })

        Spacer(modifier = Modifier.height(4.dp))

        // 按选中标签筛选属性
        val category = TABS[selectedTab]
        val categoryAttrs = state.attributes.filter { it.category == category }

        when (category) {
            "体能" -> PhysicalTab(categoryAttrs)
            "排球技术" -> VolleyballTab(categoryAttrs)
            "枪械战斗" -> FirearmTab(categoryAttrs)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TabRow(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TABS.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val color by animateColorAsState(
                targetValue = if (isSelected) AccentCyan else TextDim,
                animationSpec = tween(200)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTabSelected(index) }
            ) {
                Text(
                    text = label,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        color = color
                    )
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(AccentCyan)
                    )
                }
            }
        }
    }
}

// ============ 体能子页 ============
@Composable
private fun PhysicalTab(attrs: List<HostAttribute>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        attrs.forEach { attr ->
            TacticalCard(title = attr.attrName) {
                Text(
                    text = String.format("%.1f", attr.value),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AccentCyan,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 分项微型横条（核心、臂力）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MiniBarInline("核心", attr.value)
                    MiniBarInline("臂力", attr.value)
                }
            }
        }
    }
}

@Composable
private fun MiniBarInline(label: String, value: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = TextGray),
            modifier = Modifier.width(28.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(com.usher.tactical.ui.theme.TextDim.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AccentCyan)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", value),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextWhite
            )
        )
    }
}

// ============ 排球技术子页 ============
@Composable
private fun VolleyballTab(attrs: List<HostAttribute>) {
    TacticalCard(title = "技术指标") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // 基础技术用进度条列表展示
            val skills = listOf(
                "发球" to 81.3f, "垫球" to 75.8f, "传球" to 69f,
                "扣球" to 83.1f, "拦网" to 82.1f, "防守" to 76.4f
            )
            skills.forEach { (name, value) ->
                AttrProgressBar(label = name, value = value, maxValue = 100f)
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    TacticalCard(title = "进阶技能") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SkillRow("跳发球", "73.5", "+0.2")
            SkillRow("快攻战术", "68.0", "→")
            SkillRow("后排进攻", "71.2", "↑")
        }
    }
}

@Composable
private fun SkillRow(name: String, value: String, trend: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextWhite))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                value,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = TextWhite
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(trend, style = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = AccentCyan))
        }
    }
}

// ============ 枪械战斗子页 ============
@Composable
private fun FirearmTab(attrs: List<HostAttribute>) {
    // 枪械属性（数据库中 value 是等级 3.0）
    val firearmAttr = attrs.firstOrNull() ?: return

    TacticalCard(title = "轻型火器专精") {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            // 等级大字
            Text(
                text = "Lv.${firearmAttr.value.toInt()}",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AccentAmber,
                    letterSpacing = 2.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 经验进度条
            AttrProgressBar(label = "EXP", value = 500f, maxValue = 1000f, barColor = AccentAmber)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "500 / 1000",
                style = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = TextDim)
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    TacticalCard(title = "战斗加成") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BonusRow("射击精度", "+9%")
            BonusRow("要害命中", "+1.5%")
            BonusRow("洞察", "+0.3")
            BonusRow("反射", "+0.2")
        }
    }
}

@Composable
private fun BonusRow(name: String, bonus: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextGray))
        Text(
            bonus,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = AccentCyan
            )
        )
    }
}
