package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RaceResultEntry
import com.example.data.model.Track
import com.example.ui.theme.*

@Composable
fun PostRaceScreen(
  track: Track,
  results: List<RaceResultEntry>,
  onAdvanceToNextRound: () -> Unit,
  modifier: Modifier = Modifier
) {
  val top3 = results.take(3)
  val playerResults = results.filter { it.driver.isPlayer }
  val playerPoints = playerResults.sumOf { it.pointsAwarded }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = CarbonBlack,
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
            onClick = onAdvanceToNextRound,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = CarbonBlack)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "RETURN TO HQ (ADVANCE SEASON)",
              color = CarbonBlack,
              fontSize = 14.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      item {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(PodiumGold)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "CHEQUERED FLAG • OFFICIAL RESULTS",
              color = CarbonBlack,
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${track.flagEmoji} ${track.name}",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }

      // Podium Top 3 Cards
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CarbonSurfaceElevated),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(PodiumGold)
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = PodiumGold, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "GRAND PRIX PODIUM",
                color = PodiumGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              top3.forEachIndexed { index, entry ->
                val placeColor = when (index) {
                  0 -> PodiumGold
                  1 -> Color(0xFFC0C0C0)
                  else -> Color(0xFFCD7F32)
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CarbonSurface)
                    .border(1.dp, placeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = when (index) {
                        0 -> "1ST"
                        1 -> "2ND"
                        else -> "3RD"
                      },
                      color = placeColor,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.ExtraBold,
                      fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = entry.driver.shortCode,
                      color = TextPrimary,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = entry.team.shortName,
                      color = entry.team.primaryColor,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Team Performance Debrief Box
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = CarbonSurface),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan.copy(alpha = 0.5f))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "APEX GP POST-RACE DEBRIEF",
              color = ElectricCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              playerResults.forEach { r ->
                Column {
                  Text(
                    text = "${r.driver.name} (#${r.driver.number})",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Finish: P${r.position} (${r.pointsAwarded} pts)",
                    color = if (r.position <= 10) NeonGreen else TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = CarbonBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Total Points Scored:",
                color = TextSecondary,
                fontSize = 12.sp
              )
              Text(
                text = "+$playerPoints PTS",
                color = PodiumGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // Full Classification List
      item {
        Text(
          text = "FULL CLASSIFICATION (20 CARS)",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      items(results, key = { it.driver.id }) { entry ->
        val isPlayer = entry.driver.isPlayer

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = if (isPlayer) ElectricCyan.copy(alpha = 0.12f) else CarbonSurface),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isPlayer) ElectricCyan else CarbonBorder)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${entry.position}",
              color = if (entry.position <= 3) PodiumGold else if (entry.position <= 10) NeonGreen else TextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.width(28.dp)
            )

            Box(
              modifier = Modifier
                .size(width = 3.dp, height = 16.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(entry.team.primaryColor)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${entry.driver.name} ${entry.driver.flagEmoji}",
                color = if (isPlayer) ElectricCyan else TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (isPlayer) FontWeight.Bold else FontWeight.Medium
              )
              Text(
                text = "${entry.team.name} • ${entry.pitStops} stops",
                color = TextSecondary,
                fontSize = 10.sp
              )
            }

            Text(
              text = entry.intervalText,
              color = TextSecondary,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.padding(end = 12.dp)
            )

            Text(
              text = if (entry.pointsAwarded > 0) "+${entry.pointsAwarded}" else "-",
              color = if (entry.pointsAwarded > 0) PodiumGold else TextMuted,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.width(24.dp)
            )
          }
        }
      }
    }
  }
}
