package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun PitStopDialog(
  driver: Driver,
  currentCompound: TireCompound,
  pitTrainingLevel: Int,
  onDismiss: () -> Unit,
  onConfirmPit: (TireCompound) -> Unit
) {
  var selectedCompound by remember { mutableStateOf(if (currentCompound == TireCompound.SOFT) TireCompound.MEDIUM else TireCompound.HARD) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp)),
      colors = CardDefaults.cardColors(containerColor = CarbonSurfaceElevated),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan)
      )
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "PIT STRATEGY CALL",
              color = ElectricCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "${driver.name} #${driver.number}",
              color = TextPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(WarningAmber.copy(alpha = 0.2f))
              .border(1.dp, WarningAmber, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "BOX NEXT LAP",
              color = WarningAmber,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "SELECT TIRE COMPOUND",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Compound options
        TireCompound.values().forEach { compound ->
          val isSelected = selectedCompound == compound
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) CarbonSurface else CarbonBorder.copy(alpha = 0.2f))
              .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) compound.displayColor else CarbonBorder,
                shape = RoundedCornerShape(8.dp)
              )
              .clickable { selectedCompound = compound }
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(compound.displayColor),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = compound.code,
                color = CarbonBlack,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = compound.fullName,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Wear: ~${compound.wearRatePerLap}%/lap • Pace: ${if (compound.basePaceDeltaSec <= 0) "" else "+"}${compound.basePaceDeltaSec}s",
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }

            RadioButton(
              selected = isSelected,
              onClick = { selectedCompound = compound },
              colors = RadioButtonDefaults.colors(
                selectedColor = compound.displayColor,
                unselectedColor = TextMuted
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pit duration estimate
        val estDuration = 2.2f + (5 - pitTrainingLevel) * 0.35f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CarbonSurface)
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "ESTIMATED PIT STOP",
                color = TextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "Pit Crew Facility: Lv. $pitTrainingLevel",
                color = TextMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = "~${String.format("%.1f", estDuration)}s",
              color = ElectricCyan,
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmation buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
          ) {
            Text("CANCEL", fontFamily = FontFamily.Monospace)
          }

          Button(
            onClick = { onConfirmPit(selectedCompound) },
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RacingRed)
          ) {
            Text("CONFIRM BOX", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }
  }
}
