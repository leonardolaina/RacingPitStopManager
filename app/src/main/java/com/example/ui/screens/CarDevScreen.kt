package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CarPart
import com.example.data.model.ComponentCategory
import com.example.data.model.PartType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDevScreen(
  carParts: List<CarPart>,
  teamBudget: Long,
  onUpgradePerformance: (PartType) -> Unit,
  onImproveReliability: (PartType) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf<ComponentCategory?>(null) }

  val filteredParts = remember(carParts, selectedCategory) {
    if (selectedCategory == null) carParts
    else carParts.filter { it.type.category == selectedCategory }
  }

  val overallPerf = remember(carParts) {
    if (carParts.isEmpty()) 0 else carParts.map { it.performance }.average().toInt()
  }
  val overallRel = remember(carParts) {
    if (carParts.isEmpty()) 0 else carParts.map { it.reliability }.average().toInt()
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = CarbonBlack,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "DESENVOLVIMENTO DO CARRO",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "P&D / R&D DE COMPONENTES",
              color = ElectricCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Monospace
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Voltar",
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
              .padding(horizontal = 10.dp, vertical = 5.dp)
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
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Car Overview Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = CarbonSurfaceElevated),
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
                Icon(
                  imageVector = Icons.Default.DirectionsCar,
                  contentDescription = null,
                  tint = ElectricCyan,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Monoposto Apex AP-26",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Pacote Geral de Competição",
                    color = TextMuted,
                    fontSize = 10.sp
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(ElectricCyan.copy(alpha = 0.15f))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "8 COMPONENTES",
                  color = ElectricCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // Avg Performance Metric
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(CarbonSurface)
                  .padding(10.dp)
              ) {
                Column {
                  Text(
                    text = "MÉDIA DESEMPENHO",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "$overallPerf / 100",
                    color = ElectricCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }

              // Avg Reliability Metric
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(CarbonSurface)
                  .padding(10.dp)
              ) {
                Column {
                  Text(
                    text = "MÉDIA CONFIABILIDADE",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  val relColor = if (overallRel > 75) NeonGreen else if (overallRel > 50) WarningAmber else RacingRed
                  Text(
                    text = "$overallRel%",
                    color = relColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }
          }
        }
      }

      // Category Filter Chips
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          item {
            FilterChip(
              selected = selectedCategory == null,
              onClick = { selectedCategory = null },
              label = { Text("Todos (${carParts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ElectricCyan,
                selectedLabelColor = CarbonBlack,
                containerColor = CarbonSurface,
                labelColor = TextSecondary
              )
            )
          }

          items(ComponentCategory.values()) { cat ->
            val count = carParts.count { it.type.category == cat }
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text("${cat.title} ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ElectricCyan,
                selectedLabelColor = CarbonBlack,
                containerColor = CarbonSurface,
                labelColor = TextSecondary
              )
            )
          }
        }
      }

      // Component Cards
      items(filteredParts, key = { it.type.name }) { part ->
        val costPerf = part.type.baseResearchCost + (part.specLevel - 1) * 200_000L
        val costRel = 380_000L

        PartCard(
          part = part,
          costPerf = costPerf,
          costRel = costRel,
          canAffordPerf = teamBudget >= costPerf,
          canAffordRel = teamBudget >= costRel,
          onUpgradePerformance = { onUpgradePerformance(part.type) },
          onImproveReliability = { onImproveReliability(part.type) }
        )
      }
    }
  }
}

@Composable
private fun PartCard(
  part: CarPart,
  costPerf: Long,
  costRel: Long,
  canAffordPerf: Boolean,
  canAffordRel: Boolean,
  onUpgradePerformance: () -> Unit,
  onImproveReliability: () -> Unit
) {
  val categoryIcon: ImageVector = when (part.type.category) {
    ComponentCategory.ENGINE -> Icons.Default.Speed
    ComponentCategory.CHASSIS -> Icons.Default.Build
    ComponentCategory.TIRES -> Icons.Default.TrackChanges
    ComponentCategory.AERODYNAMICS -> Icons.Default.Air
  }

  val categoryColor: Color = when (part.type.category) {
    ComponentCategory.ENGINE -> RacingRed
    ComponentCategory.CHASSIS -> WarningAmber
    ComponentCategory.TIRES -> TimingPurple
    ComponentCategory.AERODYNAMICS -> ElectricCyan
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CarbonBorder)
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header with Category Badge and Spec Level
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = categoryIcon,
              contentDescription = null,
              tint = categoryColor,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = part.type.title,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = part.type.category.title,
              color = categoryColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(CarbonSurfaceElevated)
            .border(1.dp, CarbonBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "SPEC ${part.specLevel}",
            color = PodiumGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = part.type.description,
        color = TextSecondary,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Performance Impact Highlight
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(CarbonSurfaceElevated)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Impacto: ${part.type.impactDescription}",
            color = NeonGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Performance Progress Bar
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "DESEMPENHO",
          color = TextSecondary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.width(90.dp)
        )
        LinearProgressIndicator(
          progress = { part.performance / 100f },
          modifier = Modifier
            .weight(1f)
            .height(7.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = ElectricCyan,
          trackColor = CarbonBorder
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "${part.performance}/100",
          color = ElectricCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.width(55.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Reliability Progress Bar
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "CONFIABILIDADE",
          color = TextSecondary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.width(90.dp)
        )
        val relColor = if (part.reliability > 70) NeonGreen else if (part.reliability > 40) WarningAmber else RacingRed
        LinearProgressIndicator(
          progress = { part.reliability / 100f },
          modifier = Modifier
            .weight(1f)
            .height(7.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = relColor,
          trackColor = CarbonBorder
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "${part.reliability}%",
          color = relColor,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.width(55.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons with Costs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Pesquisar / Upgrade Desempenho
        val perfCostStr = if (costPerf >= 1_000_000) "$${costPerf / 1_000_000.0}M" else "$${costPerf / 1000}k"
        Button(
          onClick = onUpgradePerformance,
          enabled = canAffordPerf && part.performance < 100,
          modifier = Modifier.weight(1f).height(40.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = CarbonBlack,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "+${part.type.perfGain} PERF ($perfCostStr)",
            color = CarbonBlack,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }

        // Reforçar Confiabilidade
        OutlinedButton(
          onClick = onImproveReliability,
          enabled = canAffordRel && part.reliability < 100,
          modifier = Modifier.weight(1f).height(40.dp),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 6.dp),
          border = ButtonDefaults.outlinedButtonBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(NeonGreen)
          )
        ) {
          Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "+8% REL ($380k)",
            color = NeonGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}
