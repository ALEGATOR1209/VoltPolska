package ua.alegator1209.voltpolska.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.location.LocationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import ua.alegator1209.voltpolska.domain.exceptions.ScanException

@SuppressLint("MissingPermission")
class ScanRepository(
  private val bluetoothManager: BluetoothManager,
  private val locationManager: LocationManager,
) {
  private val adapter get() = bluetoothManager.adapter
  private var listener: ScanCallback? = null

  private val isBluetoothEnabled get() = adapter != null && adapter.isEnabled
  private val isGpsEnabled get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

  suspend fun startScan(deviceName: String = ""): Flow<ScanResult> {
    val scanner = adapter?.bluetoothLeScanner

    if (scanner == null || !isBluetoothEnabled) throw ScanException.BluetoothException()
    if (!isGpsEnabled) throw ScanException.LocationException()
    if (adapter?.isDiscovering == true && listener != null) stopScan()

    return callbackFlow {
      listener = getListener().also { listener ->
        val nameFilter = deviceName.takeIf { it.isNotBlank() }?.let {
          ScanFilter.Builder()
            .setDeviceName(deviceName)
            .build()
        }

        if (nameFilter != null) {
          scanner.startScan(listOf(nameFilter), ScanSettings.Builder().build(), listener)
        } else {
          scanner.startScan(listener)
        }
      }
      awaitClose { stopScan() }
    }
  }

  fun stopScan() {
    if (listener != null) {
      val scanner = adapter.bluetoothLeScanner
      scanner.stopScan(listener)
      listener = null
    }
  }

  private fun ProducerScope<ScanResult>.getListener() = object : ScanCallback() {
    override fun onScanFailed(errorCode: Int) {
      super.onScanFailed(errorCode)
      println("Scan failure: $errorCode")
      cancel("Scan failure", ScanException.ScanFailureException(errorCode))
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
      super.onScanResult(callbackType, result)
      val device = result?.device ?: return
      println("Scan device found: ${device.name}/${device.address}")

      trySendBlocking(result).onFailure {
        println("Failed to deliver scan result: ${device.name}, ${device.address}")
        it?.printStackTrace()
      }
    }
  }
}
