package com.usher.tactical.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.SurfaceCard
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite

/**
 * 属性横条进度条
 * 属性名 | 横条(6dp高，青蓝渐变填充) | 数值(等宽字体)
 */
@Composable
fun AttrProgressBar(
    label: String,
    value: Float,
    maxValue: Float = 100f,
    barColor: Color = AccentCyan,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 属性名
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 11.sp,
                color = TextGray,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.width(56.dp)
        )

        // 进度条
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF1A1A28))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.5f))
                        )
                    )
            )
        }

        // 数值
        Text(
            text = formatValue(value),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = TextWhite,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.width(52.dp),
            textAlign = TextAlign.End
        )
    }
}

private fun formatValue(value: Float): String {
    return if (value == value.toLong().toFloat() && value >= 10f) {
        "Lv.${value.toInt()}"
    } else {
        String.format("%.1f", value)
    }
}
