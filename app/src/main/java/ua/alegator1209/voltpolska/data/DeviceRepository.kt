package ua.alegator1209.voltpolska.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Build
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ua.alegator1209.voltpolska.domain.models.DeviceInfo
import java.util.*
import kotlin.experimental.and

private val SERVICE_UUID = UUID.fromString("0000FF00-0000-1000-8000-00805F9B34FB")
private val NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000FF01-0000-1000-8000-00805F9B34FB")
private val WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000FF02-0000-1000-8000-00805F9B34FB")

@SuppressLint("MissingPermission")
class DeviceRepository(
    private val context: Context
) {
    private val deviceListener = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
                gatt?.discoverServices()
            } else {
                runBlocking {
                    deviceConnected.emit(false)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.setCharacteristicNotification(
                gatt.services
                    ?.find { it.uuid == SERVICE_UUID }
                    ?.getCharacteristic(NOTIFY_CHARACTERISTIC_UUID),
                true
            )
            runBlocking {
                deviceConnected.emit(status == BluetoothGatt.GATT_SUCCESS)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            runBlocking {
                deviceResponse.emit(value)
            }
        }

        @Deprecated("Deprecated in Java", ReplaceWith(
            "super.onCharacteristicChanged(gatt, characteristic)",
            "android.bluetooth.BluetoothGattCallback"
        ))
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            runBlocking {
                deviceResponse.emit(characteristic?.value ?: ByteArray(0))
            }
        }
    }

    private val deviceConnected = MutableSharedFlow<Boolean>()
    private val deviceResponse = MutableSharedFlow<ByteArray>()
    private var gatt: BluetoothGatt? = null
    private val writeCharacteristic get() = gatt
        ?.getService(SERVICE_UUID)
        ?.getCharacteristic(WRITE_CHARACTERISTIC_UUID)

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

                val isCorrectDevice = if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt?.services?.any { service ->
                        when {
                            service.uuid != SERVICE_UUID -> false
                            service.characteristics.find { it.uuid == NOTIFY_CHARACTERISTIC_UUID } == null -> false
                            service.characteristics.find { it.uuid == WRITE_CHARACTERISTIC_UUID } == null -> false
                            else -> true
                        }
                    } == true
                } else {
                    false
                }

                gatt?.disconnect()
                gatt?.close()

                result.complete(isCorrectDevice)
            }
        })

        return result.await()
    }

    suspend fun connect(device: BluetoothDevice): Boolean {
        gatt = device.connectGatt(context, true, deviceListener)
        return deviceConnected.first()
    }

    suspend fun getDeviceInfo(): DeviceInfo {
        val gatt = gatt
        val writeCharacteristic = writeCharacteristic

        if (gatt == null || writeCharacteristic == null) error("No gatt")

        val value = ByteArray(4)
        value[0] = 0x7b
        value[1] = 0x01
        value[2] = 0x00
        value[3] = 0x7D

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(writeCharacteristic, value, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
        } else {
            writeCharacteristic.value = value
            gatt.writeCharacteristic(writeCharacteristic)
        }

        val response = deviceResponse.first()

        val RSOC = response.slice(3..4).parseInt()
        val totalVoltage = response.slice(5..6).parseDouble()
        val moswd = response.slice(11..12).parseDouble(10.0)
        val electricCurrent = response.slice(13..14).parseDouble()
        val remainingCapacity = response.slice(15..16).parseDouble()
        val fullCapacity = response.slice(17..18).parseDouble()
//        val nominalCapacity = parseDouble(38..41)
//        val xhrl = parseDouble(42..45)
//        val numberOfCycles = parseInt(46..49)
//        val equilibrumState = parseInt(50..53).toString(2)
//        val equilibriumStateHight = parseInt(54..57).toString(2)
//        val FET = parseInt(58..61) // TODO: decode
//        val protectionStatus = 62..65 // TODO: decode

        return DeviceInfo(
            gatt.device?.name ?: "",
            totalVoltage,
            electricCurrent,
            remainingCapacity,
            fullCapacity,
        )
    }
}

private fun List<Byte>.parseInt(): Int {
    var res = 0

    forEach {
        res = (res shl 8) + (it and 0xFF.toByte())
    }

    return res
}

private fun List<Byte>.parseDouble(magnification: Double = 100.0) = parseInt() / magnification
