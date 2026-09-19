package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamRadioMessage
import com.example.ui.theme.*

@Composable
fun RadioBanner(
  latestMessage: TeamRadioMessage?,
  modifier: Modifier = Modifier
) {
  if (latestMessage == null) return

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(CarbonSurfaceElevated)
      .border(
        width = 1.dp,
        color = latestMessage.color.copy(alpha = 0.6f),
        shape = RoundedCornerShape(8.dp)
      )
      .padding(horizontal = 10.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Headphones,
      contentDescription = "Team Radio",
      tint = latestMessage.color,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = "L${latestMessage.lap} [${latestMessage.driverName}]:",
      color = latestMessage.color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
    Spacer(modifier = Modifier.width(6.dp))

    Text(
      text = latestMessage.message,
      color = TextPrimary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      modifier = Modifier.weight(1f)
    )
  }
}
