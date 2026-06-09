package com.usher.tactical.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机自启广播接收器（预留，阶段二之后启用）
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: 阶段二 — 调度 WorkManager 每日结算
        }
    }
}
