package ua.alegator1209.voltpolska.ui.screens.device

import ua.alegator1209.voltpolska.ui.common.BatteryStatus

data class UiState(
  val deviceName: String = "",
  val isLoading: Boolean = false,
  val info: BatteryInfo = BatteryInfo(),
)

data class BatteryInfo(
  val status: BatteryStatus = BatteryStatus.STAND_BY,
  val nominalCapacity: Double = 0.0,
  val remainingCapacity: Double = 0.0,
  val electricCurrent: Double = 0.0,
  val totalVoltage: Double = 0.0,
  val consumption: Double = 0.0,
)
