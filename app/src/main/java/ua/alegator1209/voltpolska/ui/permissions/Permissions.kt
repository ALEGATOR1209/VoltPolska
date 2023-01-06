package ua.alegator1209.voltpolska.ui.permissions

import android.Manifest
import android.os.Build


object Permissions {
    val Bluetooth by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            listOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    val Location by lazy {
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
