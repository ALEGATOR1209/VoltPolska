package ua.alegator1209.voltpolska

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import ua.alegator1209.voltpolska.ui.navigation.Navigator
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme


@AndroidEntryPoint
class VoltPolskaActivity : ComponentActivity() {
    private var bluetoothEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        bluetoothEnabled = isBluetoothEnabled
        setContent {
            VoltPolskaTheme {
                Navigator(
                    isBluetoothEnabled = bluetoothEnabled,
                    onEnableBluetooth = this::enableBluetooth,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
    }

    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        } else {
            getSystemService(BluetoothManager::class.java).adapter.enable()
        }
    }

    private val isBluetoothEnabled: Boolean
        get() = getSystemService(BluetoothManager::class.java).adapter.isEnabled

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                    BluetoothAdapter.STATE_OFF -> bluetoothEnabled = false
                    BluetoothAdapter.STATE_ON -> bluetoothEnabled = true
                }
            }
        }
    }
}
