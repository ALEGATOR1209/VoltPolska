package ua.alegator1209.voltpolska.ui.screens.device

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.common.Battery
import ua.alegator1209.voltpolska.ui.common.BatteryStatus
import ua.alegator1209.voltpolska.ui.common.Units
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme
import ua.alegator1209.voltpolska.utils.bold
import ua.alegator1209.voltpolska.utils.value

@Composable
fun DeviceScreen(
    viewModel: DeviceViewModel,
) {
    DeviceScreenStateless(viewModel.uiState)
}

@Composable
private fun DeviceScreenStateless(
    state: UiState,
) {
    Scaffold(
        topBar = { Header(state.deviceName, state.info) }
    )
    {
        Column(
            modifier = Modifier
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            MainInfo(state.info)
            AdditionalInfo(state.info)
        }
    }
}

@Composable
private fun Header(deviceName: String, batteryInfo: BatteryInfo) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (title, divider) = createRefs()
        val (lhBar, lvBar, rhBar, rvBar, battery) = createRefs()

        Text(
            text = deviceName,
            style = MaterialTheme.typography.h1,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
        )

        Divider(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .height(2.dp)
                .constrainAs(divider) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    end.linkTo(parent.end, 32.dp)
                }
        )

        Box(
            modifier = Modifier
                .height(56.dp)
                .width(2.dp)
                .background(MaterialTheme.colors.secondary)
                .constrainAs(lvBar) {
                    top.linkTo(divider.bottom, (-1).dp)
                    start.linkTo(parent.start, 32.dp)
                }
        )

        Box(
            modifier = Modifier
                .height(2.dp)
                .background(MaterialTheme.colors.secondary)
                .constrainAs(lhBar) {
                    top.linkTo(lvBar.bottom, (-2).dp)
                    start.linkTo(lvBar.end, (-2).dp)

                    width = Dimension.percent(0.1f)
                }
        )

        Box(
            modifier = Modifier
                .height(56.dp)
                .width(2.dp)
                .background(MaterialTheme.colors.secondary)
                .constrainAs(rvBar) {
                    top.linkTo(divider.bottom, (-2).dp)
                    end.linkTo(parent.end, 32.dp)
                }
        )

        Box(
            modifier = Modifier
                .height(2.dp)
                .background(MaterialTheme.colors.secondary)
                .constrainAs(rhBar) {
                    top.linkTo(rvBar.bottom, (-2).dp)
                    end.linkTo(rvBar.start, (-2).dp)

                    width = Dimension.percent(0.1f)
                }
        )

        with (batteryInfo) {
            Battery(
                capacity = (remainingCapacity / nominalCapacity).toFloat(),
                status = status,
                modifier = Modifier.constrainAs(battery) {
                    top.linkTo(lhBar.top)
                    bottom.linkTo(lhBar.bottom)

                    start.linkTo(lhBar.end, (-1).dp)
                    end.linkTo(rhBar.start, (-1).dp)

                    width = Dimension.fillToConstraints
                    height = Dimension.value(80.dp)
                }
            )
        }
    }
}

@Composable
private fun MainInfo(batteryInfo: BatteryInfo) = with(batteryInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(buildAnnotatedString {
            bold(stringResource(id = R.string.charge_label) + ": ")
            value(stringResource(id = R.string.format_2_decimals, remainingCapacity))
            append(Units.Capacity)
            append("/")
            value(stringResource(id = R.string.format_2_decimals, nominalCapacity))
            append(Units.Capacity)
            append(" (")

            val capacityPercent = (remainingCapacity / nominalCapacity) * 100.0

            value("${capacityPercent.toInt()}%")
            append(")")
        })

        Text(buildAnnotatedString {
            bold(stringResource(id = R.string.status_label) + ": ")
            value(when (status) {
                BatteryStatus.CHARGING -> stringResource(id = R.string.status_charging)
                BatteryStatus.STAND_BY -> stringResource(id = R.string.status_stand_by)
                BatteryStatus.DISCHARGING -> stringResource(id = R.string.status_discharging)
            })
        })

        Text(buildAnnotatedString {
            bold(stringResource(id = R.string.consumption_label) + ": ")
            value(stringResource(id = R.string.format_2_decimals, consumption))
            append(Units.Consumption)
        })
    }
}

@Composable
private fun AdditionalInfo(batteryInfo: BatteryInfo) = with(batteryInfo) {
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth()
    ) {
        val (row1Label, row1Bar, row1Divider, row1Value) = createRefs()

        Text(
            text = stringResource(id = R.string.voltage_label),
            modifier = Modifier.constrainAs(row1Label) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)

                width = Dimension.percent(0.75f)
            }
        )

        Divider(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .height(2.dp)
                .constrainAs(row1Divider) {
                    top.linkTo(row1Label.bottom, 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.secondary)
                .constrainAs(row1Bar) {
                    start.linkTo(row1Label.end)
                    top.linkTo(row1Label.top)
                    bottom.linkTo(row1Divider.bottom)

                    width = Dimension.value(2.dp)
                    height = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(id = R.string.format_3_decimals, totalVoltage) + Units.Voltage,
            modifier = Modifier.constrainAs(row1Value) {
                start.linkTo(row1Bar.end, 8.dp)
                top.linkTo(row1Label.top)
                bottom.linkTo(row1Divider.top, 4.dp)
            }
        )

        val (row2Label, row2Bar, row2Divider, row2Value) = createRefs()

        Text(
            text = stringResource(id = R.string.current_label),
            modifier = Modifier.constrainAs(row2Label) {
                top.linkTo(row1Divider.bottom, 4.dp)
                start.linkTo(parent.start)

                width = Dimension.percent(0.75f)
            }
        )

        Divider(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .height(2.dp)
                .constrainAs(row2Divider) {
                    top.linkTo(row2Label.bottom, 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.secondary)
                .constrainAs(row2Bar) {
                    start.linkTo(row2Label.end)
                    top.linkTo(row1Divider.top)
                    bottom.linkTo(row2Divider.bottom)

                    width = Dimension.value(2.dp)
                    height = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(id = R.string.format_3_decimals, electricCurrent) + Units.Current,
            modifier = Modifier.constrainAs(row2Value) {
                start.linkTo(row2Bar.end, 8.dp)
                top.linkTo(row2Label.top)
                bottom.linkTo(row2Divider.top, 4.dp)
            }
        )
    }
}

@Preview
@Composable
private fun DeviceScreenPreview() {
    VoltPolskaTheme {
        DeviceScreenStateless(
            state = UiState(
                deviceName = "12V100A1235"
            )
        )
    }
}
