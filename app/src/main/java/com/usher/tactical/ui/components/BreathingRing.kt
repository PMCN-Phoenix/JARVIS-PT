package com.usher.tactical.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.usher.tactical.ui.theme.AccentCyan

/**
 * 呼吸光环动画
 * 青蓝色圆环，透明度 0.4 ↔ 0.05 交替，2秒周期
 */
@Composable
fun BreathingRing(
    modifier: Modifier = Modifier,
    ringSize: Dp = 48.dp,
    strokeWidth: Dp = 2.dp,
    ringColor: Color = AccentCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_alpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(ringSize)) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val padding = strokeWidth.toPx() / 2
            drawArc(
                color = ringColor.copy(alpha = alpha),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(padding, padding),
                size = Size(size.width - padding * 2, size.height - padding * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
