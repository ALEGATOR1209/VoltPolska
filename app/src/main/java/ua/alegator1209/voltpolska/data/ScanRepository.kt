package ua.alegator1209.voltpolska.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.location.LocationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import ua.alegator1209.voltpolska.domain.exceptions.ScanException

private const val DEFAULT_TIMEOUT = 30_000L

@SuppressLint("MissingPermission")
class ScanRepository(
  private val bluetoothManager: BluetoothManager,
  private val locationManager: LocationManager,
) {
  private val adapter get() = bluetoothManager.adapter
  private var listener: ScanCallback? = null

  val isBluetoothEnabled get() = adapter != null && adapter.isEnabled
  val isGpsEnabled get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

  suspend fun startScan(timeout: Long = DEFAULT_TIMEOUT): Flow<ScanResult> {
    val scanner = adapter?.bluetoothLeScanner

    if (scanner == null || !isBluetoothEnabled) throw ScanException.BluetoothException()
    if (!isGpsEnabled) throw ScanException.LocationException()
    if (adapter?.isDiscovering == true && listener != null) stopScan()

    return callbackFlow {
      listener = getListener().also(scanner::startScan)

      launch(Dispatchers.IO) {
        delay(timeout)
        this@callbackFlow.cancel("Timeout")
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
      cancel("Scan failure", ScanException.ScanFailureException(errorCode))
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
      super.onScanResult(callbackType, result)
      val device = result?.device ?: return

      trySendBlocking(result).onFailure {
        println("Failed to deliver scan result: ${device.name}, ${device.address}")
        it?.printStackTrace()
      }
    }
  }
}
