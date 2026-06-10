package com.usher.tactical.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.BorderCyan
import com.usher.tactical.ui.theme.SurfaceCard
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite

/**
 * 赛博朋克通用卡片容器
 * 背景 #14141F，圆角 12dp，青蓝半透明边框
 */
@Composable
fun TacticalCard(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(0.5.dp, BorderCyan, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        if (title != null) {
            Text(
                text = "▸ $title",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextGray
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        content()
    }
}
