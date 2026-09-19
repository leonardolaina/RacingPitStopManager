package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RaceDriverState
import com.example.ui.theme.*

@Composable
fun TimingTower(
  carStates: List<RaceDriverState>,
  modifier: Modifier = Modifier
) {
  val sortedCars = carStates.sortedBy { it.position }

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(CarbonSurface)
      .border(1.dp, CarbonBorder, RoundedCornerShape(12.dp))
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CarbonSurfaceElevated)
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "POS",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.width(28.dp)
      )
      Text(
        text = "DRIVER",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.weight(1f)
      )
      Text(
        text = "GAP",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.width(48.dp)
      )
      Text(
        text = "TIRE",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.width(44.dp)
      )
      Text(
        text = "PIT",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.width(24.dp)
      )
    }

    // List of drivers
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 280.dp)
    ) {
      items(sortedCars, key = { it.driver.id }) { car ->
        TimingRow(car = car)
      }
    }
  }
}

@Composable
private fun TimingRow(car: RaceDriverState) {
  val isPlayer = car.isPlayer
  val bgColor = if (isPlayer) ElectricCyan.copy(alpha = 0.12f) else Color.Transparent

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Position badge
    val posColor = when (car.position) {
      1 -> PodiumGold
      2 -> Color(0xFFC0C0C0)
      3 -> Color(0xFFCD7F32)
      in 4..10 -> NeonGreen
      else -> TextMuted
    }

    Text(
      text = if (car.dnf) "OUT" else "${car.position}",
      color = if (car.dnf) RacingRed else posColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.width(28.dp)
    )

    // Team color strip
    Box(
      modifier = Modifier
        .size(width = 3.dp, height = 14.dp)
        .clip(RoundedCornerShape(1.dp))
        .background(car.team.primaryColor)
    )
    Spacer(modifier = Modifier.width(5.dp))

    // Driver Code / Name
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = car.driver.shortCode,
        color = if (isPlayer) ElectricCyan else TextPrimary,
        fontSize = 12.sp,
        fontWeight = if (isPlayer) FontWeight.ExtraBold else FontWeight.Medium,
        fontFamily = FontFamily.Monospace
      )
      if (car.isFastestLap) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(TimingPurple)
            .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
          Text(
            text = "FL",
            color = CarbonBlack,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
      if (car.isInPit) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(PodiumGold)
            .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
          Text(
            text = "PIT",
            color = CarbonBlack,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    // Gap / Interval
    val gapText = when {
      car.dnf -> "DNF"
      car.position == 1 -> "LEAD"
      else -> "+${String.format("%.1f", car.intervalToLeaderSec)}"
    }
    Text(
      text = gapText,
      color = if (car.position == 1) TextSecondary else TextPrimary,
      fontSize = 10.sp,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.width(48.dp)
    )

    // Tire compound badge & health bar
    Row(
      modifier = Modifier.width(44.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(14.dp)
          .clip(CircleShape)
          .background(car.tireCompound.displayColor),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = car.tireCompound.code,
          color = CarbonBlack,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
      Spacer(modifier = Modifier.width(4.dp))
      val tireWearColor = when {
        car.tireHealthPct > 60f -> NeonGreen
        car.tireHealthPct > 30f -> WarningAmber
        else -> RacingRed
      }
      LinearProgressIndicator(
        progress = { car.tireHealthPct / 100f },
        modifier = Modifier
          .weight(1f)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = tireWearColor,
        trackColor = CarbonBorder
      )
    }

    // Pit stops count
    Text(
      text = "${car.totalPitStops}",
      color = TextSecondary,
      fontSize = 10.sp,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.width(24.dp)
    )
  }
}
