package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Driver
import com.example.data.model.TireCompound
import com.example.data.model.Track
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreRaceScreen(
  track: Track,
  roundNumber: Int,
  totalRounds: Int,
  playerDrivers: List<Driver>,
  driver1Compound: TireCompound,
  driver2Compound: TireCompound,
  onDriver1CompoundChange: (TireCompound) -> Unit,
  onDriver2CompoundChange: (TireCompound) -> Unit,
  onStartRace: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  val d1 = playerDrivers.getOrNull(0)
  val d2 = playerDrivers.getOrNull(1)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = CarbonBlack,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "RACE STRATEGY BRIEFING",
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CarbonSurface)
      )
    },
    bottomBar = {
      Surface(
        color = CarbonSurface,
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CarbonBorder))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Button(
            onClick = onStartRace,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RacingRed),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "COMMENCE GRAND PRIX",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(scrollState)
        .padding(bottom = 16.dp)
    ) {
      // Pit Wall Header Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_pit_wall),
          contentDescription = "Pit Wall Strategy",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, CarbonBlack.copy(alpha = 0.8f), CarbonBlack)
              )
            )
        )
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Text(
            text = "ROUND $roundNumber OF $totalRounds",
            color = ElectricCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "${track.flagEmoji} ${track.name}",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "${track.totalLaps} Laps • ${track.circuitLengthKm}km per lap",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }

      // Track Characteristics Matrix
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = CarbonSurface)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "CIRCUIT CHARACTERISTICS",
              color = TextSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              SpecItem("Downforce", track.downforceReq)
              SpecItem("Tire Wear", track.tireWearLevel)
              SpecItem("Fuel Burn", track.fuelBurnLevel)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Driver 1 Starting Strategy
        if (d1 != null) {
          DriverStrategySetupCard(
            driver = d1,
            selectedCompound = driver1Compound,
            onSelectCompound = onDriver1CompoundChange
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Driver 2 Starting Strategy
        if (d2 != null) {
          DriverStrategySetupCard(
            driver = d2,
            selectedCompound = driver2Compound,
            onSelectCompound = onDriver2CompoundChange
          )
        }
      }
    }
  }
}

@Composable
private fun SpecItem(title: String, value: String) {
  Column {
    Text(text = title, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
  }
}

@Composable
private fun DriverStrategySetupCard(
  driver: Driver,
  selectedCompound: TireCompound,
  onSelectCompound: (TireCompound) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CarbonBorder)
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
              .size(24.dp)
              .clip(CircleShape)
              .background(ElectricCyan),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${driver.number}",
              color = CarbonBlack,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${driver.name} ${driver.flagEmoji}",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(selectedCompound.displayColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = selectedCompound.fullName.uppercase(),
            color = CarbonBlack,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "STARTING TIRE COMPOUND",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(TireCompound.SOFT, TireCompound.MEDIUM, TireCompound.HARD).forEach { compound ->
          val isSelected = selectedCompound == compound
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) CarbonSurfaceElevated else CarbonBlack)
              .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) compound.displayColor else CarbonBorder,
                shape = RoundedCornerShape(8.dp)
              )
              .clickable { onSelectCompound(compound) }
              .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .background(compound.displayColor),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = compound.code,
                  color = CarbonBlack,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = compound.fullName.split(" ").first(),
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }
  }
}
