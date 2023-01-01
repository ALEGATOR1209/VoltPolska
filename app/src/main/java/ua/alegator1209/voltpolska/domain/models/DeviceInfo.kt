package ua.alegator1209.voltpolska.domain.models

data class DeviceInfo(
    val totalVoltage: Double,
    val electricCurrent: Double,
    val remainingCapacity: Double,
    val nominalCapacity: Double,
    val numberOfCycles: Int,
)
