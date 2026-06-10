package com.usher.tactical.ui.log

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.usher.tactical.domain.model.SystemLog
import com.usher.tactical.ui.dashboard.HostViewModel
import com.usher.tactical.ui.theme.AccentAmber
import com.usher.tactical.ui.theme.AccentCyan
import com.usher.tactical.ui.theme.TextDim
import com.usher.tactical.ui.theme.TextWhite
import com.usher.tactical.ui.theme.WarningRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CATEGORIES = listOf("全部" to null, "签到" to "checkin", "奖励" to "reward", "惩罚" to "penalty", "系统" to "system")

@Composable
fun SystemLogScreen(viewModel: HostViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedCat by remember { mutableStateOf<String?>(null) }

    val logs = state.systemLogs
        .filter { selectedCat == null || it.category == selectedCat }
        .take(50)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("▸ 系统日志", fontSize = 18.sp, color = TextWhite)
        Spacer(modifier = Modifier.height(8.dp))

        // 筛选标签
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CATEGORIES.forEach { (label, cat) ->
                val selected = selectedCat == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selected) AccentCyan.copy(0.2f) else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { selectedCat = cat }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(label, fontSize = 11.sp, color = if (selected) AccentCyan else TextDim)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 日志列表
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(logs) { log -> LogItem(log) }
            if (logs.isEmpty()) {
                item { Text("暂无日志", fontSize = 13.sp, color = TextDim, modifier = Modifier.fillMaxWidth().padding(16.dp)) }
            }
        }
    }
}

@Composable
private fun LogItem(log: SystemLog) {
    val levelColor = when (log.level) {
        "error" -> WarningRed
        "warning" -> AccentAmber
        else -> AccentCyan
    }
    val df = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val time = df.format(Date(log.timestamp))

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier.width(3.dp).height(36.dp)
                .clip(RoundedCornerShape(1.5.dp)).background(levelColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(time, fontSize = 11.sp, color = TextDim)
            Text(log.message, fontSize = 13.sp, color = TextWhite)
        }
    }
}
