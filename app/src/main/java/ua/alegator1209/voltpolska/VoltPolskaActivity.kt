package ua.alegator1209.voltpolska

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.*
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import ua.alegator1209.voltpolska.ui.navigation.Navigator
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme
import java.util.*


@AndroidEntryPoint
class VoltPolskaActivity : ComponentActivity() {
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VoltPolskaTheme {
                Navigator()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        } else {
//            Toast.makeText(this, "Bluetooth adapter set up", Toast.LENGTH_SHORT).show()
        }

        if (!bluetoothAdapter.isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestMultiplePermissions.launch(arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ))
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetooth.launch(enableBtIntent)
            }

            return
        }

//        bluetoothAdapter.bondedDevices.map {
//            "${it.name} -- ${it.type} -- ${it.address} -- ${it.bondState}"
//        }.joinToString("\n").also {
//            if (it.isBlank()) Toast.makeText(this, "No paired devices", Toast.LENGTH_SHORT).show()
//            else Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//        }

        val locationManager = getSystemService(LocationManager::class.java)
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_BLUETOOTH)
        }

        val filters = ScanFilter.Builder()
            .setDeviceName("12V100A00460")
            .build()

        val scanner = bluetoothAdapter.bluetoothLeScanner
        scanner.startScan(listOf(filters), ScanSettings.Builder().build(), object : ScanCallback() {
            private val characteristics = mutableListOf<BluetoothGattCharacteristic>()

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val device = result?.device
                println("DEVICE FOUND: ${device?.name}")

                if (device == null) return

                scanner.stopScan(this)
                val gatt = device.connectGatt(this@VoltPolskaActivity, false, object : BluetoothGattCallback() {
                    override fun onConnectionStateChange(
                        gatt: BluetoothGatt?,
                        status: Int,
                        newState: Int
                    ) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            println("CONNECTED TO ${device.name}. DISCOVERING SERVICES...")
                            gatt?.discoverServices()
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            println("DISCONNECTED FROM ${device.name}.")
                        }
                    }

                    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            println("SERVICES DISCOVERED:")
                            gatt?.services?.let { services ->
                                services.forEach { service ->
                                    println("${service.instanceId}, ${service.uuid}, ${service.type}")
                                    characteristics += service.characteristics.filter {
                                        it.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0
                                    }
                                }
                            }

                            println("FOUND ${characteristics.size} CHARACTERISTICS")
                            val characteristic = characteristics.removeFirst()
                            println("READING ${characteristic.uuid}")
                            gatt?.readCharacteristic(characteristic)
                        }
                    }

                    override fun onCharacteristicRead(
                        gatt: BluetoothGatt?,
                        characteristic: BluetoothGattCharacteristic,
                        status: Int
                    ) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            val array = Arrays.toString(characteristic.value)
                            println("\tCHARACTERISTIC: ${characteristic.uuid}")
                            println("\t\t$array")
                            println("\t\t${characteristic.getStringValue(0)}")
                            println("\t\t${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT32,0)}")
                            println("\t\t${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16,0)}")
                            println("\t\t${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8,0)}")
                            println("\t\t${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,0)}")
                            println("\t\t${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0)}")
                            println("\t\t${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0)}")
                            println("\t\t${characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT,0)}")
                            println("\t\t${characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,0)}")
                        } else {
                            println("${characteristic.uuid} = READ FAILURE")
                        }

                        if (characteristics.isNotEmpty()) {
                            val c = characteristics.removeLast()
                            println("READING ${c.uuid}")
                            gatt?.readCharacteristic(c)
                        }
                    }
                })
            }
        })

//        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
//        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_UUID))
//        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))

//        bluetoothAdapter.startDiscovery().also {
//            println("DISCOVERY STARTED: $it")
//        }

//        lifecycleScope.launchWhenResumed {
//            receiver.flow.collect {
//                println("PAIRING ${it.name}")
//                it.createBond()
//                return@collect
//                println("TRYING TO CONNECT TO ${it.name}")
//                bluetoothAdapter.cancelDiscovery()
//                withContext(Dispatchers.IO) {
//                    println("CREATING SOCKET FOR ${it.name}")
//                    it.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")).use { socket ->
//                        socket.connect()
//                        println("DEVICE CONNECTED")
//                    }
//                }
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        enableBluetooth()
    }

    private object receiver : BroadcastReceiver() {
        val flow = MutableSharedFlow<BluetoothDevice>()

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            when (action) {
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    println("ACTION: $action")
                    val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 1209)
                    BluetoothDevice.BOND_BONDED
                    println("STATE: $state")
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    println(device.name)
                    if (device.name == "12V100A00460") runBlocking {
                        flow.emit(device)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
