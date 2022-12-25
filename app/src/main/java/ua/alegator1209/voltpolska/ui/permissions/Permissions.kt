package ua.alegator1209.voltpolska.ui.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi


object Permissions {
    private val General = listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    private val Api29 = listOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val Api31 = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
    )

    val Required get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> Api31 + Api29 + General
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Api29 + General
        else -> General
    }
}
