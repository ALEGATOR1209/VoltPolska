package ua.alegator1209.voltpolska

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import ua.alegator1209.voltpolska.ui.navigation.Navigator
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme


@AndroidEntryPoint
class VoltPolskaActivity : ComponentActivity() {
    private val ENABLE_LOCATION_REQUEST_CODE = 111
    private var bluetoothEnabled by mutableStateOf(false)
    private var locationEnabled by mutableStateOf(false)

    private val isBluetoothEnabled: Boolean
        get() = getSystemService(BluetoothManager::class.java).adapter.isEnabled

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_OFF -> bluetoothEnabled = false
                        BluetoothAdapter.STATE_ON -> bluetoothEnabled = true
                    }
                }

                LocationManager.PROVIDERS_CHANGED_ACTION -> {
                    val locationManager = getSystemService<LocationManager>()
                    val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
                    val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

                    locationEnabled = isGpsEnabled || isNetworkEnabled
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(broadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        registerReceiver(broadcastReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        bluetoothEnabled = isBluetoothEnabled
        checkLocation(false)
        setContent {
            VoltPolskaTheme {
                Navigator(
                    isBluetoothEnabled = bluetoothEnabled,
                    isLocationEnabled = locationEnabled,
                    onEnableBluetooth = this::enableBluetooth,
                    onEnableLocation = { checkLocation(true) }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        } else {
            getSystemService(BluetoothManager::class.java).adapter.enable()
        }
    }

    private fun checkLocation(enableIfDisabled: Boolean) {
        val locationRequest = LocationRequest.Builder(10_000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener { response ->
                locationEnabled = response.locationSettingsStates?.isLocationUsable ?: false
            }.addOnFailureListener { exception ->
                if (exception is ResolvableApiException && enableIfDisabled) {
                    try {
                        exception.startResolutionForResult(this, ENABLE_LOCATION_REQUEST_CODE)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
    }
}
