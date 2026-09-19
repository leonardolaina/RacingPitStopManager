package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.domain.engine.RaceSimulationEngine
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LiveRaceScreen(
  engine: RaceSimulationEngine,
  simSpeed: Float,
  pitTrainingLevel: Int,
  onSpeedChange: (Float) -> Unit,
  onEngineModeChange: (String, EngineMode) -> Unit,
  onDrivingStyleChange: (String, DrivingStyle) -> Unit,
  onErsBoost: (String, ErsMode) -> Unit,
  onCallPitStop: (String, TireCompound) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var pittingDriver by remember { mutableStateOf<RaceDriverState?>(null) }

  val playerCars = engine.carStates.filter { it.isPlayer }
  val latestRadio = engine.radioMessages.firstOrNull()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CarbonBlack)
      .verticalScroll(scrollState)
      .padding(bottom = 24.dp)
  ) {
    // Top Pit Wall Header & Telemetry HUD
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CarbonSurface)
        .border(1.dp, CarbonBorder)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Lap Counter
      Column {
        Text(
          text = "LAP",
          color = TextSecondary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "${engine.currentLeaderLap} / ${engine.track.totalLaps}",
          color = ElectricCyan,
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace
        )
      }

      // Track & Weather Condition
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(CarbonSurfaceElevated)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = engine.weather.iconRes,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = engine.weather.displayName.uppercase(),
            color = TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Simulation Speed Controls (Pause, 1x, 2x, 4x)
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SpeedButton(
          label = "❚❚",
          isSelected = simSpeed == 0.0f,
          onClick = { onSpeedChange(0.0f) }
        )
        SpeedButton(
          label = "1x",
          isSelected = simSpeed == 1.0f,
          onClick = { onSpeedChange(1.0f) }
        )
        SpeedButton(
          label = "2x",
          isSelected = simSpeed == 2.0f,
          onClick = { onSpeedChange(2.0f) }
        )
        SpeedButton(
          label = "4x",
          isSelected = simSpeed == 4.0f,
          onClick = { onSpeedChange(4.0f) }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2D Track Map Canvas Radar
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
      TrackMapCanvas(
        track = engine.track,
        carStates = engine.carStates,
        weather = engine.weather,
        safetyCar = engine.safetyCar
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Team Radio Banner
      RadioBanner(latestMessage = latestRadio)

      Spacer(modifier = Modifier.height(10.dp))

      // Dual Player Driver Pit Wall Cockpits
      Text(
        text = "APEX GP PIT WALL CONTROLS",
        color = ElectricCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(6.dp))

      playerCars.forEach { car ->
        DriverPitWallCockpit(
          car = car,
          onEngineModeChange = { onEngineModeChange(car.driver.id, it) },
          onDrivingStyleChange = { onDrivingStyleChange(car.driver.id, it) },
          onErsBoost = { onErsBoost(car.driver.id, it) },
          onCallBox = { pittingDriver = car },
          modifier = Modifier.padding(vertical = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Timing Tower (Live 20-Car Leaderboard)
      Text(
        text = "LIVE TIMING TOWER",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(6.dp))

      TimingTower(carStates = engine.carStates)
    }

    // Pit Stop Strategy Dialog
    if (pittingDriver != null) {
      val car = pittingDriver!!
      PitStopDialog(
        driver = car.driver,
        currentCompound = car.tireCompound,
        pitTrainingLevel = pitTrainingLevel,
        onDismiss = { pittingDriver = null },
        onConfirmPit = { nextCompound ->
          onCallPitStop(car.driver.id, nextCompound)
          pittingDriver = null
        }
      )
    }
  }
}

@Composable
private fun SpeedButton(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(28.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(if (isSelected) ElectricCyan else CarbonSurfaceElevated)
      .border(0.5.dp, if (isSelected) ElectricCyan else CarbonBorder, RoundedCornerShape(4.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = if (isSelected) CarbonBlack else TextPrimary,
      fontSize = 11.sp,
      fontWeight = FontWeight.ExtraBold,
      fontFamily = FontFamily.Monospace
    )
  }
}
