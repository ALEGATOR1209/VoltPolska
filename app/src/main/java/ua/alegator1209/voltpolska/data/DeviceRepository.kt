package ua.alegator1209.voltpolska.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import kotlinx.coroutines.CompletableDeferred
import java.util.UUID

private val SERVICE_UUID = UUID.fromString("0000FF00-0000-1000-8000-00805F9B34FB")
private val NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000FF01-0000-1000-8000-00805F9B34FB")
private val WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000FF02-0000-1000-8000-00805F9B34FB")

@SuppressLint("MissingPermission")
class DeviceRepository(
    private val context: Context
) {
    suspend fun isVoltPolskaDevice(device: BluetoothDevice): Boolean {
        val result = CompletableDeferred<Boolean>()

        device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt?.discoverServices()
                } else {
                    result.complete(false)
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    result.complete(gatt?.services?.any { service ->
                        when {
                            service.uuid != SERVICE_UUID -> false
                            service.characteristics.find { it.uuid == NOTIFY_CHARACTERISTIC_UUID } == null -> false
                            service.characteristics.find { it.uuid == WRITE_CHARACTERISTIC_UUID } == null -> false
                            else -> true
                        }
                    } == true)

                    gatt?.disconnect()
                } else {
                    gatt?.disconnect()
                    result.complete(false)
                }
            }
        })

        return result.await()
    }
}
