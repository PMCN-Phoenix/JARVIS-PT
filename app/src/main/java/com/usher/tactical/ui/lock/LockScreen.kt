package com.usher.tactical.ui.lock

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.SuccessGreen
import com.usher.tactical.ui.theme.WarningRed
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 系统锁死覆盖页 — 破碎玻璃效果
 */
@Composable
fun LockScreen(viewModel: LockViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    if (!state.isLocked) return

    Box(modifier = Modifier.fillMaxSize().background(Color(0xCC0A0A0F))) {
        // 破碎玻璃效果
        BrokenGlassOverlay()

        if (!state.showRecompile) {
            // 锁死警告内容
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text("⚠", fontSize = 48.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "警告",
                    fontSize = 18.sp,
                    color = WarningRed,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "宿主白厄连续3天未达最低作战标准。",
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "系统已锁死。",
                    fontSize = 14.sp,
                    color = WarningRed,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                // 申请重新编译按钮
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Transparent)
                        .clickable { viewModel.requestRecompile() }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                        drawRoundRect(
                            color = WarningRed.copy(alpha = 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                        )
                        drawRoundRect(
                            color = WarningRed,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                            style = Stroke(1.dp.toPx())
                        )
                    }
                    Text(
                        "申请重新编译",
                        fontSize = 16.sp,
                        color = WarningRed,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            // 重新编译页
            RecompilePage(state, viewModel)
        }
    }
}

@Composable
private fun BrokenGlassOverlay() {
    val cracks = remember { List(12) { CrackLine.random() } }
    val alpha by animateFloatAsState(targetValue = 0.35f, animationSpec = tween(500))

    Canvas(modifier = Modifier.fillMaxSize()) {
        cracks.forEach { crack -> crack.draw(this, alpha) }
    }
}

private data class CrackLine(
    val startX: Float, val startY: Float,
    val endX: Float, val endY: Float,
    val branches: List<Pair<Float, Float>>
) {
    companion object {
        fun random() = CrackLine(
            startX = Random.nextFloat(),
            startY = Random.nextFloat(),
            endX = Random.nextFloat(),
            endY = Random.nextFloat(),
            branches = (0..Random.nextInt(3)).map { Random.nextFloat() to Random.nextFloat() }
        )
    }

    fun draw(scope: DrawScope, alpha: Float) {
        val w = scope.size.width
        val h = scope.size.height
        val color = Color.White.copy(alpha = alpha * 0.4f)

        scope.drawLine(
            color = color,
            start = Offset(startX * w, startY * h),
            end = Offset(endX * w, endY * h),
            strokeWidth = 1.5f
        )
        val midX = (startX + endX) / 2 * w
        val midY = (startY + endY) / 2 * h
        branches.forEach { (bx, by) ->
            scope.drawLine(
                color = color.copy(alpha = alpha * 0.2f),
                start = Offset(midX, midY),
                end = Offset(midX + bx * 80, midY + by * 80),
                strokeWidth = 0.8f
            )
        }
    }
}

@Composable
private fun RecompilePage(state: LockUiState, viewModel: LockViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        // 倒计时环
        val countdownProgress = state.countdown / 300f
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            Canvas(modifier = Modifier.size(80.dp)) {
                drawArc(
                    color = AccentCyan,
                    startAngle = -90f,
                    sweepAngle = 360f * countdownProgress,
                    useCenter = false,
                    style = Stroke(3.dp.toPx())
                )
            }
            Text(
                text = "${state.countdown}",
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                color = AccentCyan
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 动态令牌
        Text(
            text = state.token,
            fontSize = 36.sp,
            fontFamily = FontFamily.Monospace,
            color = AccentCyan,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("请连接主终端，使用母块进行验证。", fontSize = 14.sp, color = Color(0xFF888888))

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isCompiling) {
            // 编译终端文字
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D0D14)).padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                state.compileLines.forEach { line ->
                    Text(
                        text = line,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SuccessGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}
