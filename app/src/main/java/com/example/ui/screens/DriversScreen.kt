package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.data.model.Driver
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriversScreen(
  playerDrivers: List<Driver>,
  scoutMarket: List<Driver>,
  teamBudget: Long,
  onSignDriver: (Driver, String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedScoutDriver by remember { mutableStateOf<Driver?>(null) }
  var showReplaceDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = CarbonBlack,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "DRIVERS & SCOUTING MARKET",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = TextPrimary
            )
          }
        },
        actions = {
          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(CarbonSurfaceElevated)
              .border(1.dp, CarbonBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "$${String.format("%,d", teamBudget)}",
              color = NeonGreen,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CarbonSurface)
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Text(
          text = "ACTIVE TEAM DRIVERS (APEX GP)",
          color = ElectricCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      items(playerDrivers.filter { it.isPlayer }, key = { it.id }) { driver ->
        DriverCard(driver = driver, isPlayer = true)
      }

      item {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "SCOUTING & DRIVER MARKET",
          color = PodiumGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "Scouted talents available for negotiation ($1,200,000 signing bonus)",
          color = TextSecondary,
          fontSize = 11.sp
        )
      }

      items(scoutMarket, key = { it.id }) { scout ->
        DriverCard(
          driver = scout,
          isPlayer = false,
          onSignClick = {
            selectedScoutDriver = scout
            showReplaceDialog = true
          }
        )
      }
    }

    if (showReplaceDialog && selectedScoutDriver != null) {
      val scout = selectedScoutDriver!!
      AlertDialog(
        onDismissRequest = { showReplaceDialog = false },
        containerColor = CarbonSurfaceElevated,
        title = {
          Text(
            text = "CONTRACT NEGOTIATION",
            color = ElectricCyan,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        },
        text = {
          Column {
            Text(
              text = "Sign ${scout.name} (${scout.nationality} ${scout.flagEmoji}) to Apex GP?",
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Signing fee: $1,200,000 • Salary: $${String.format("%,d", scout.salaryPerRace)}/race.",
              color = TextSecondary,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Select driver seat to replace:",
              color = TextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            playerDrivers.filter { it.isPlayer }.forEach { currentDriver ->
              Button(
                onClick = {
                  onSignDriver(scout, currentDriver.id)
                  showReplaceDialog = false
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Replace ${currentDriver.name} (#${currentDriver.number})",
                  color = TextPrimary,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp
                )
              }
            }
          }
        },
        confirmButton = {},
        dismissButton = {
          TextButton(onClick = { showReplaceDialog = false }) {
            Text("CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace)
          }
        }
      )
    }
  }
}

@Composable
private fun DriverCard(
  driver: Driver,
  isPlayer: Boolean,
  onSignClick: (() -> Unit)? = null
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(if (isPlayer) ElectricCyan.copy(alpha = 0.5f) else CarbonBorder)
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(if (isPlayer) ElectricCyan else CarbonSurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${driver.number}",
              color = if (isPlayer) CarbonBlack else TextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "${driver.name} ${driver.flagEmoji}",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${driver.nationality} • $${String.format("%,d", driver.salaryPerRace)}/race",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }

        if (!isPlayer && onSignClick != null) {
          Button(
            onClick = onSignClick,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = CarbonBlack, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "SIGN",
              color = CarbonBlack,
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Stats 4-column grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatBadge("PACE", driver.pace, Modifier.weight(1f))
        StatBadge("OVERTAKE", driver.overtaking, Modifier.weight(1f))
        StatBadge("SMOOTH", driver.smoothness, Modifier.weight(1f))
        StatBadge("CONSIST", driver.consistency, Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Morale: ${driver.morale}%",
          color = TextSecondary,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "Contract: ${driver.contractRacesRemaining} races left",
          color = TextSecondary,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
private fun StatBadge(label: String, score: Int, modifier: Modifier = Modifier) {
  val color = if (score >= 90) PodiumGold else if (score >= 80) ElectricCyan else TextPrimary

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(CarbonSurfaceElevated)
      .padding(vertical = 5.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = label,
        color = TextMuted,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = "$score",
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
