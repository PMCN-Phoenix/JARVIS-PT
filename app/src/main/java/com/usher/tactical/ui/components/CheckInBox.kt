package com.usher.tactical.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.SurfaceCard
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextGray
import com.usher.tactical.ui.theme.TextWhite
import org.json.JSONObject

/**
 * 签到框组件
 * 根据 requirementJson 中 type 显示不同交互模式：
 * - numeric: 输入实际值 → [签到]
 * - duration: 进度 + [+累加] 按钮 → [签到]
 * - percentage: 前值/后值输入 → [签到]
 */
@Composable
fun CheckInBox(
    requirementJson: String,
    isChecked: Boolean,
    evidence: String? = null,
    onCheckIn: (value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val req = remember(requirementJson) { JSONObject(requirementJson) }
    val type = req.optString("type", "numeric")
    val target = req.optDouble("target", 100.0).toFloat()
    val unit = req.optString("unit", "")

    val scaleAnim by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isChecked) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    when (type) {
        "numeric" -> NumericCheckIn(target, unit, isChecked, onCheckIn, modifier.scale(scaleAnim))
        "duration" -> DurationCheckIn(target, unit, isChecked, onCheckIn, modifier.scale(scaleAnim))
        "percentage" -> PercentageCheckIn(isChecked, onCheckIn, modifier.scale(scaleAnim))
    }
}

// ===== 数值型签到 =====
@Composable
private fun NumericCheckIn(
    target: Float, unit: String,
    isChecked: Boolean, onCheckIn: (String) -> Unit,
    modifier: Modifier
) {
    var inputValue by remember { mutableStateOf("") }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isChecked) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                modifier = Modifier.width(80.dp),
                placeholder = { Text("0", fontSize = 11.sp, color = TextDim) },
                suffix = { Text(unit, fontSize = 10.sp, color = TextGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(alpha = 0.3f)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            CheckInButton("签到") { onCheckIn(inputValue) }
        } else {
            CheckedBadge()
        }
    }
}

// ===== 时间型签到 =====
@Composable
private fun DurationCheckIn(
    target: Float, unit: String,
    isChecked: Boolean, onCheckIn: (String) -> Unit,
    modifier: Modifier
) {
    var accumulated by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isChecked) {
            Text(
                text = "${String.format("%.1f", accumulated)}/$target$unit",
                fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = AccentCyan
            )
            Spacer(modifier = Modifier.width(8.dp))
            CheckInButton("+30分钟") {
                accumulated += 30f
                if (accumulated >= target) onCheckIn("${String.format("%.1f", accumulated)}$unit")
            }
            Spacer(modifier = Modifier.width(4.dp))
            if (accumulated > 0f && accumulated < target) {
                CheckInButton("签到") { onCheckIn("${String.format("%.1f", accumulated)}$unit") }
            }
        } else {
            CheckedBadge()
        }
    }
}

// ===== 百分比型签到 =====
@Composable
private fun PercentageCheckIn(
    isChecked: Boolean, onCheckIn: (String) -> Unit,
    modifier: Modifier
) {
    var beforeValue by remember { mutableStateOf("") }
    var afterValue by remember { mutableStateOf("") }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isChecked) {
            OutlinedTextField(
                value = beforeValue, onValueChange = { beforeValue = it },
                modifier = Modifier.width(64.dp),
                placeholder = { Text("前", fontSize = 11.sp, color = TextDim) },
                suffix = { Text("%", fontSize = 10.sp, color = TextGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(alpha = 0.3f)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedTextField(
                value = afterValue, onValueChange = { afterValue = it },
                modifier = Modifier.width(64.dp),
                placeholder = { Text("后", fontSize = 11.sp, color = TextDim) },
                suffix = { Text("%", fontSize = 10.sp, color = TextGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan, unfocusedBorderColor = TextDim.copy(alpha = 0.3f)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            CheckInButton("签到") { onCheckIn("前${beforeValue}%→后${afterValue}%") }
        } else {
            CheckedBadge()
        }
    }
}

@Composable
private fun CheckInButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, AccentCyan, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp, color = AccentCyan
        )
    }
}

@Composable
private fun CheckedBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AccentAmber)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text("✓ 已完成", fontSize = 12.sp, color = Color.Black)
    }
}
