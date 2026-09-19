package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Track
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GameUiState

@Composable
fun HomeScreen(
  uiState: GameUiState,
  currentTrack: Track,
  onNavigate: (AppScreen) -> Unit,
  onStartPreRace: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CarbonBlack)
      .verticalScroll(scrollState)
      .padding(bottom = 24.dp)
  ) {
    // Hero Banner with Team Stats
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_hero_f1_car),
        contentDescription = "F1 Car Hero Banner",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      // Gradient scrim
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, CarbonBlack.copy(alpha = 0.85f), CarbonBlack)
            )
          )
      )

      // Team Header Text
      Column(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(ElectricCyan)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "WORLD MOTORSPORT CHAMPIONSHIP",
              color = CarbonBlack,
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ROUND ${uiState.currentRound} / ${uiState.totalRounds}",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = uiState.team.name,
          color = TextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = (-0.5).sp
        )
      }
    }

    // Financial & Championship Quick Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(CarbonSurface)
        .border(1.dp, CarbonBorder, RoundedCornerShape(12.dp))
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "TEAM BUDGET",
          color = TextSecondary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "$${String.format("%,d", uiState.team.budget)}",
          color = NeonGreen,
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "CONSTRUCTORS PTS",
          color = TextSecondary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${uiState.team.points} PTS",
            color = PodiumGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Next Grand Prix Card
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "UPCOMING RACE WEEKEND",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CarbonSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(ElectricCyan, RacingRed))
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${currentTrack.flagEmoji} ${currentTrack.name}",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = currentTrack.location,
                color = TextSecondary,
                fontSize = 12.sp
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CarbonSurface)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${currentTrack.totalLaps} LAPS",
                color = ElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Circuit Specs Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CircuitSpecPill("Length", "${currentTrack.circuitLengthKm}km", Modifier.weight(1f))
            CircuitSpecPill("Downforce", currentTrack.downforceReq.split(" ").first(), Modifier.weight(1f))
            CircuitSpecPill("Tire Wear", currentTrack.tireWearLevel.split(" ").first(), Modifier.weight(1f))
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onStartPreRace,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RacingRed),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ENTER RACE WEEKEND",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Management Hub Grid
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "TEAM HEADQUARTERS",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val avgPerf = uiState.carParts.map { it.performance }.average().toInt()
        HubNavCard(
          title = "Car Development",
          subtitle = "Avg Perf: $avgPerf / 100",
          icon = Icons.Default.DirectionsCar,
          accentColor = ElectricCyan,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.CAR_DEV) }
        )

        HubNavCard(
          title = "HQ Facilities",
          subtitle = "${uiState.facilities.size} Buildings",
          icon = Icons.Default.Apartment,
          accentColor = PodiumGold,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.FACILITIES) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val playerDrivers = uiState.drivers.filter { it.isPlayer }
        HubNavCard(
          title = "Drivers & Staff",
          subtitle = playerDrivers.joinToString(", ") { it.shortCode },
          icon = Icons.Default.People,
          accentColor = NeonGreen,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.DRIVERS_STAFF) }
        )

        HubNavCard(
          title = "Sponsorships",
          subtitle = "${uiState.sponsors.count { it.isSigned }} Active Deals",
          icon = Icons.Default.AttachMoney,
          accentColor = TimingPurple,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.SPONSORS) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      HubNavCard(
        title = "Championship Standings & Calendar",
        subtitle = "Drivers & Constructors rankings",
        icon = Icons.Default.Leaderboard,
        accentColor = Color(0xFF00D2BE),
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigate(AppScreen.STANDINGS) }
      )
    }
  }
}

@Composable
private fun CircuitSpecPill(title: String, value: String, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(CarbonSurface)
      .padding(vertical = 6.dp, horizontal = 8.dp)
  ) {
    Column {
      Text(
        text = title.uppercase(),
        color = TextMuted,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = value,
        color = TextPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun HubNavCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CarbonBorder)
    )
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = subtitle,
          color = TextSecondary,
          fontSize = 11.sp
        )
      }
    }
  }
}
