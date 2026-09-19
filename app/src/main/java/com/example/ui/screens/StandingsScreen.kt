package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChampionshipStanding
import com.example.data.model.Track
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingsScreen(
  constructorStandings: List<ChampionshipStanding>,
  driverStandings: List<ChampionshipStanding>,
  calendar: List<Track>,
  currentRound: Int,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("CONSTRUCTORS", "DRIVERS", "CALENDAR")

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = CarbonBlack,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "CHAMPIONSHIP STANDINGS",
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
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Tab Row
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = CarbonSurface,
        contentColor = ElectricCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = ElectricCyan
          )
        }
      ) {
        tabs.forEachIndexed { idx, title ->
          Tab(
            selected = selectedTab == idx,
            onClick = { selectedTab = idx },
            text = {
              Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          )
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        when (selectedTab) {
          0 -> {
            items(constructorStandings, key = { it.name }) { team ->
              StandingRow(standing = team)
            }
          }
          1 -> {
            items(driverStandings, key = { it.name }) { driver ->
              StandingRow(standing = driver)
            }
          }
          2 -> {
            items(calendar) { track ->
              val index = calendar.indexOf(track) + 1
              val isCompleted = index < currentRound
              val isCurrent = index == currentRound

              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = if (isCurrent) CarbonSurfaceElevated else CarbonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(if (isCurrent) ElectricCyan else CarbonBorder)
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "R$index",
                      color = if (isCurrent) ElectricCyan else TextSecondary,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      modifier = Modifier.width(32.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text(
                        text = "${track.flagEmoji} ${track.name}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Text(
                        text = "${track.totalLaps} Laps • ${track.circuitLengthKm}km • ${track.downforceReq}",
                        color = TextSecondary,
                        fontSize = 11.sp
                      )
                    }
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isCompleted) NeonGreen.copy(alpha = 0.15f) else if (isCurrent) ElectricCyan else CarbonBorder.copy(alpha = 0.4f))
                      .padding(horizontal = 6.dp, vertical = 3.dp)
                  ) {
                    Text(
                      text = if (isCompleted) "DONE" else if (isCurrent) "NEXT UP" else "SCHEDULED",
                      color = if (isCompleted) NeonGreen else if (isCurrent) CarbonBlack else TextSecondary,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StandingRow(standing: ChampionshipStanding) {
  val isTop3 = standing.rank <= 3
  val rankColor = when (standing.rank) {
    1 -> PodiumGold
    2 -> Color(0xFFC0C0C0)
    3 -> Color(0xFFCD7F32)
    else -> TextSecondary
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(if (standing.teamName.contains("Apex")) ElectricCyan.copy(alpha = 0.5f) else CarbonBorder)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${standing.rank}",
        color = rankColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.width(28.dp)
      )

      Box(
        modifier = Modifier
          .size(width = 3.dp, height = 18.dp)
          .clip(RoundedCornerShape(1.dp))
          .background(standing.teamColor)
      )
      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = standing.name,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = standing.teamName,
          color = TextSecondary,
          fontSize = 10.sp
        )
      }

      Text(
        text = "${standing.points} PTS",
        color = if (isTop3) PodiumGold else TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
