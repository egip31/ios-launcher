package com.egip31.ioslauncher.data

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** Snapshot of values shown by the iOS-style status bar. */
data class SystemStatus(
    val batteryPct: Int = 100,
    val charging: Boolean = false,
    val wifiConnected: Boolean = false,
    /** Cellular signal level 0..4. -1 if no SIM / unknown. */
    val cellularLevel: Int = -1,
)

/**
 * Live system status. Battery comes from [BatteryManager.ACTION_BATTERY_CHANGED] sticky
 * intent + a registered receiver. Wi-Fi state comes from [ConnectivityManager]. Cellular
 * level (0..4) is *best effort*: we only read what's available without dangerous runtime
 * permissions, so on many Android 12+ devices it stays at -1 unless `READ_PHONE_STATE`
 * is granted.
 */
@SuppressLint("MissingPermission")
@Composable
fun rememberSystemStatus(): SystemStatus {
    val context = LocalContextSafe.current
    var status by remember { mutableStateOf(SystemStatus()) }

    DisposableEffect(Unit) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        fun readWifi(): Boolean {
            val net = cm?.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(net) ?: return false
            return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

        fun publish(intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val pct = if (level >= 0 && scale > 0) (level * 100 / scale) else status.batteryPct
            val chargeState = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val charging = chargeState == BatteryManager.BATTERY_STATUS_CHARGING ||
                chargeState == BatteryManager.BATTERY_STATUS_FULL
            status = status.copy(
                batteryPct = pct.coerceIn(0, 100),
                charging = charging,
                wifiConnected = readWifi(),
                cellularLevel = readSignalLevelSafe(tm),
            )
        }

        // Sticky broadcast — gives us the current value immediately on register.
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) = publish(intent)
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
        val sticky = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION", "UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }
        publish(sticky)

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }

    return status
}

@Suppress("DEPRECATION")
private fun readSignalLevelSafe(tm: TelephonyManager?): Int {
    tm ?: return -1
    return runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            tm.signalStrength?.level ?: -1
        } else -1
    }.getOrDefault(-1)
}

/** Tiny shim so the file doesn't depend on a specific compose-ui version's accessor. */
private object LocalContextSafe {
    val current: Context
        @Composable
        get() = androidx.compose.ui.platform.LocalContext.current
}

/** SignalStrength helper kept here for completeness — unused on lower APIs. */
@Suppress("unused")
private fun SignalStrength.bars0to4(): Int = level.coerceIn(0, 4)
