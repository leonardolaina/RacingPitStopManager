package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun TrackMapCanvas(
  track: Track,
  carStates: List<RaceDriverState>,
  weather: WeatherType,
  safetyCar: SafetyCarStatus,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "car_pulse")
  val playerPulse by infiniteTransition.animateFloat(
    initialValue = 4f,
    targetValue = 9f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val textMeasurer = rememberTextMeasurer()

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(240.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(CarbonSurface)
  ) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
      val w = size.width
      val h = size.height
      val waypoints = track.pathPoints

      if (waypoints.isNotEmpty()) {
        // Draw track outline (Tarmac ribbon)
        val trackPath = Path().apply {
          val first = waypoints.first()
          moveTo(first.x * w, first.y * h)
          for (i in 1 until waypoints.size) {
            val pt = waypoints[i]
            lineTo(pt.x * w, pt.y * h)
          }
          close()
        }

        // Base tarmac layer (dark wide ribbon)
        drawPath(
          path = trackPath,
          color = Color(0xFF262C36),
          style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Kerb / border accents
        drawPath(
          path = trackPath,
          color = Color(0xFF384252),
          style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Center racing line (subtle neon/cyan guide)
        drawPath(
          path = trackPath,
          color = Color(0xFF4C596D),
          style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Start / Finish line (Checkered bar)
        val sPt = waypoints.first()
        drawCircle(
          color = Color.White,
          radius = 5f,
          center = Offset(sPt.x * w, sPt.y * h)
        )

        // Draw cars on track
        carStates.filter { !it.dnf }.forEach { car ->
          val pos = getCoordinatesAlongTrack(waypoints, car.trackProgress, w, h)

          if (car.isPlayer) {
            // Player aura ring
            drawCircle(
              color = car.team.primaryColor.copy(alpha = 0.45f),
              radius = playerPulse + 6f,
              center = pos
            )
            // Player car core
            drawCircle(
              color = car.team.primaryColor,
              radius = 6.5f,
              center = pos
            )
            drawCircle(
              color = Color.White,
              radius = 3.5f,
              center = pos
            )

            // Driver number label
            drawText(
              textMeasurer = textMeasurer,
              text = "#${car.driver.number}",
              topLeft = Offset(pos.x - 12f, pos.y - 20f),
              style = TextStyle(
                color = ElectricCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            )
          } else {
            // Rival car dot
            drawCircle(
              color = car.team.primaryColor,
              radius = 4.5f,
              center = pos
            )
          }
        }

        // If Safety Car active, show banner overlay inside Canvas
        if (safetyCar != SafetyCarStatus.NONE) {
          drawRect(
            color = WarningAmber.copy(alpha = 0.15f),
            size = size
          )
        }
      }
    }

    // Top status badges inside the track map
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(CarbonSurfaceElevated)
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = "${track.flagEmoji} ${track.name.uppercase()}",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (weather == WeatherType.DRY) CarbonSurfaceElevated else ElectricCyan.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = "${weather.iconRes} ${weather.displayName.uppercase()}",
            color = if (weather == WeatherType.DRY) TextSecondary else ElectricCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        if (safetyCar != SafetyCarStatus.NONE) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(WarningAmber)
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = safetyCar.label,
              color = CarbonBlack,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}

// Interpolate normalized position (0.0 to 1.0) along waypoints spline
private fun getCoordinatesAlongTrack(
  waypoints: List<TrackWaypoint>,
  progress: Float,
  width: Float,
  height: Float
): Offset {
  if (waypoints.isEmpty()) return Offset.Zero
  val clamped = ((progress % 1.0f) + 1.0f) % 1.0f
  val n = waypoints.size
  val totalSegments = n.toFloat()
  val rawIndex = clamped * totalSegments
  val indexA = rawIndex.toInt() % n
  val indexB = (indexA + 1) % n
  val t = rawIndex - rawIndex.toInt()

  val ptA = waypoints[indexA]
  val ptB = waypoints[indexB]

  val x = (ptA.x + (ptB.x - ptA.x) * t) * width
  val y = (ptA.y + (ptB.y - ptA.y) * t) * height

  return Offset(x, y)
}
