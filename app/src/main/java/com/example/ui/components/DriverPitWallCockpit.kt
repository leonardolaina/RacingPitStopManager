package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun DriverPitWallCockpit(
  car: RaceDriverState,
  onEngineModeChange: (EngineMode) -> Unit,
  onDrivingStyleChange: (DrivingStyle) -> Unit,
  onErsBoost: (ErsMode) -> Unit,
  onCallBox: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isPitting = car.isPittingNextLap || car.isInPit

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(if (car.position <= 3) PodiumGold.copy(alpha = 0.5f) else CarbonBorder)
    )
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Top row: Driver info & Live Position
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(car.team.primaryColor),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${car.driver.number}",
              color = CarbonBlack,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "${car.driver.name} ${car.driver.flagEmoji}",
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (car.dnf) "OUT - ${car.dnfReason}" else "Lap ${car.currentLap} • Stop ${car.totalPitStops}",
              color = if (car.dnf) RacingRed else TextSecondary,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Live Position Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (car.position <= 3) PodiumGold else ElectricCyan)
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (car.dnf) "DNF" else "P${car.position}",
            color = CarbonBlack,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Telemetry Gauges Grid (Tires, Fuel, ERS, Condition)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // 1. Tire Gauge
        TelemetryMetricBox(
          modifier = Modifier.weight(1f),
          title = "TIRES (${car.tireCompound.code})",
          value = "${car.tireHealthPct.toInt()}%",
          subtext = "${car.tireAgeLaps} laps old",
          color = when {
            car.tireHealthPct > 55f -> NeonGreen
            car.tireHealthPct > 25f -> WarningAmber
            else -> RacingRed
          },
          badgeColor = car.tireCompound.displayColor
        )

        // 2. Fuel Gauge
        TelemetryMetricBox(
          modifier = Modifier.weight(1f),
          title = "FUEL",
          value = String.format("%.1f L", car.fuelLapsRemaining),
          subtext = if (car.fuelLapsRemaining > 2f) "Optimal" else "CRITICAL",
          color = if (car.fuelLapsRemaining > 2f) TextPrimary else WarningAmber
        )

        // 3. ERS Battery
        TelemetryMetricBox(
          modifier = Modifier.weight(1f),
          title = "ERS",
          value = "${car.ersPercent.toInt()}%",
          subtext = car.ersMode.label,
          color = if (car.ersPercent > 30f) ElectricCyan else TimingPurple
        )

        // 4. Car Condition
        TelemetryMetricBox(
          modifier = Modifier.weight(1f),
          title = "COND",
          value = "${car.carReliabilityPct.toInt()}%",
          subtext = "Health",
          color = if (car.carReliabilityPct > 40f) TextPrimary else RacingRed
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tactical Strategy Controls
      // Row 1: Engine Fuel Modes (LEAN / NORMAL / PUSH / BURN)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ENG:",
          color = TextSecondary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.width(32.dp)
        )
        EngineMode.values().forEach { mode ->
          val isSelected = car.engineMode == mode
          TacticalButton(
            label = mode.label,
            isSelected = isSelected,
            activeColor = if (mode == EngineMode.OVERTAKE) RacingRed else ElectricCyan,
            modifier = Modifier.weight(1f),
            onClick = { onEngineModeChange(mode) }
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Row 2: Driving Styles (CONSERVE / BALANCED / ATTACK)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PACE:",
          color = TextSecondary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.width(32.dp)
        )
        DrivingStyle.values().forEach { style ->
          val isSelected = car.drivingStyle == style
          TacticalButton(
            label = style.label,
            isSelected = isSelected,
            activeColor = when (style) {
              DrivingStyle.CONSERVE -> NeonGreen
              DrivingStyle.BALANCED -> ElectricCyan
              DrivingStyle.ATTACK -> WarningAmber
            },
            modifier = Modifier.weight(1f),
            onClick = { onDrivingStyleChange(style) }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 3: Action Buttons (ERS Deploy Boost + BOX THIS LAP)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // ERS Boost Button
        Button(
          onClick = {
            val nextMode = if (car.ersMode == ErsMode.BOOST) ErsMode.NEUTRAL else ErsMode.BOOST
            onErsBoost(nextMode)
          },
          modifier = Modifier.weight(1f).height(38.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (car.ersMode == ErsMode.BOOST) TimingPurple else CarbonSurfaceElevated
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "ERS Boost",
            tint = if (car.ersMode == ErsMode.BOOST) CarbonBlack else TimingPurple,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (car.ersMode == ErsMode.BOOST) "BOOSTING!" else "ERS BOOST",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (car.ersMode == ErsMode.BOOST) CarbonBlack else TimingPurple,
            fontFamily = FontFamily.Monospace
          )
        }

        // BOX / PIT STOP Button
        Button(
          onClick = onCallBox,
          modifier = Modifier.weight(1f).height(38.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isPitting) RacingRed else CarbonSurfaceElevated
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Build,
            contentDescription = "Pit Box",
            tint = if (isPitting) Color.White else WarningAmber,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (car.isInPit) "IN PIT LANE" else if (car.isPittingNextLap) "BOXING THIS LAP" else "BOX THIS LAP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPitting) Color.White else WarningAmber,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}

@Composable
private fun TelemetryMetricBox(
  title: String,
  value: String,
  subtext: String,
  color: Color,
  badgeColor: Color? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(CarbonSurfaceElevated)
      .border(0.8.dp, CarbonBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 5.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (badgeColor != null) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(badgeColor)
          )
          Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
          text = title,
          color = TextSecondary,
          fontSize = 8.sp,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.Monospace
        )
      }
      Text(
        text = value,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = subtext,
        color = TextMuted,
        fontSize = 8.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
private fun TacticalButton(
  label: String,
  isSelected: Boolean,
  activeColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val bg by animateColorAsState(
    targetValue = if (isSelected) activeColor else CarbonSurfaceElevated,
    label = "tactical_bg"
  )
  val textColor = if (isSelected) CarbonBlack else TextSecondary

  Box(
    modifier = modifier
      .height(26.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(bg)
      .border(
        width = if (isSelected) 0.dp else 0.5.dp,
        color = CarbonBorder,
        shape = RoundedCornerShape(4.dp)
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = textColor,
      fontSize = 9.sp,
      fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
      fontFamily = FontFamily.Monospace
    )
  }
}
