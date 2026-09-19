package com.example.domain.engine

import androidx.compose.ui.graphics.Color
import com.example.data.model.*
import com.example.ui.theme.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class RaceSimulationEngine(
  val track: Track,
  initialDrivers: List<Driver>,
  initialTeams: List<Team>,
  val playerCarParts: List<CarPart>,
  val pitTrainingLevel: Int = 2
) {
  var weather: WeatherType = WeatherType.DRY
  var trackWetnessPct: Float = 0.0f // 0% dry to 100% heavy rain
  var safetyCar: SafetyCarStatus = SafetyCarStatus.NONE
  var safetyCarLapsRemaining: Int = 0
  var currentLeaderLap: Int = 1
  var isRaceFinished: Boolean = false

  var fastestLapDriver: Driver? = null
  var fastestLapTimeSec: Float = 999.0f

  val carStates: MutableList<RaceDriverState> = mutableListOf()
  val radioMessages: MutableList<TeamRadioMessage> = mutableListOf()

  private var messageIdCounter: Long = 100L

  init {
    val teamMap = initialTeams.associateBy { it.id }

    // Initialize the 20 cars on the starting grid
    // Sort starting grid by driver pace + car base performance + slight randomness
    val sortedGrid = initialDrivers.sortedByDescending { d ->
      val carPerf = if (d.isPlayer) {
        playerCarParts.map { it.performance }.average().toFloat()
      } else {
        when (d.teamId()) {
          "bullseye" -> 93f
          "scuderia" -> 90f
          "silver" -> 88f
          "mclaren" -> 89f
          "aston" -> 84f
          "alpine" -> 78f
          "sauber" -> 73f
          "haas" -> 74f
          else -> 72f
        }
      }
      d.pace * 0.6f + carPerf * 0.4f + Random.nextFloat() * 2.0f
    }

    sortedGrid.forEachIndexed { index, driver ->
      val team = teamMap[driver.teamId()] ?: initialTeams.first()
      val startCompound = if (index % 2 == 0) TireCompound.SOFT else TireCompound.MEDIUM

      val state = RaceDriverState(
        driver = driver,
        team = team,
        currentLap = 1,
        trackProgress = (index * -0.015f), // grid stagger behind start line
        position = index + 1,
        intervalToLeaderSec = index * 0.8f,
        gapToAheadSec = if (index == 0) 0.0f else 0.8f,
        tireCompound = startCompound,
        tireHealthPct = 100.0f,
        tireAgeLaps = 0,
        fuelLapsRemaining = track.totalLaps + 2.5f,
        ersPercent = 100.0f,
        engineMode = EngineMode.NORMAL,
        drivingStyle = DrivingStyle.BALANCED,
        ersMode = ErsMode.NEUTRAL,
        carReliabilityPct = if (driver.isPlayer) {
          playerCarParts.map { it.reliability }.average().toFloat()
        } else {
          85f + Random.nextFloat() * 10f
        }
      )
      carStates.add(state)
    }

    addRadio(
      lap = 1,
      driverName = "Race Control",
      msg = "LIGHTS OUT AND AWAY WE GO! Grand Prix is underway!",
      isAlert = true,
      color = NeonGreen
    )
  }

  private fun Driver.teamId(): String = when (id) {
    "d1", "d2" -> "apex"
    "d3", "d4" -> "bullseye"
    "d5", "d6" -> "scuderia"
    "d7", "d8" -> "silver"
    "d9", "d10" -> "mclaren"
    "d11", "d12" -> "aston"
    "d13", "d14" -> "alpine"
    "d15", "d16" -> "sauber"
    "d17", "d18" -> "haas"
    else -> "williams"
  }

  // Update simulation by delta time (seconds)
  fun update(dtSec: Float) {
    if (isRaceFinished) return

    // Weather evolution check (dynamic weather transitions)
    updateWeather()

    // Safety car countdown
    if (safetyCar != SafetyCarStatus.NONE) {
      safetyCarLapsRemaining--
      if (safetyCarLapsRemaining <= 0) {
        val prev = safetyCar
        safetyCar = SafetyCarStatus.NONE
        addRadio(currentLeaderLap, "Race Control", "$prev IN THIS LAP! GREEN FLAG!", isAlert = true, color = NeonGreen)
      }
    }

    val baseLap = track.baseLapTimeSec

    // Advance each car along the track
    carStates.forEach { car ->
      if (car.dnf) return@forEach

      if (car.isInPit) {
        // Pit stop countdown
        car.pitTimerSec -= dtSec
        if (car.pitTimerSec <= 0f) {
          car.isInPit = false
          car.tireCompound = car.nextPitCompound
          car.tireHealthPct = 100.0f
          car.tireAgeLaps = 0
          car.fuelLapsRemaining += 10.0f
          car.totalPitStops++

          if (car.isPlayer) {
            addRadio(
              car.currentLap,
              car.driver.name,
              "Pit stop done in ${(2.2f + (5 - pitTrainingLevel) * 0.4f).format(1)}s! Fresh ${car.tireCompound.fullName} fitted.",
              isAlert = false,
              color = ElectricCyan
            )
          }
        }
        return@forEach
      }

      // Calculate car speed on track
      val speedFactor = calculateCarSpeed(car, baseLap)
      val progressIncrement = (dtSec / baseLap) * speedFactor

      val oldProgress = car.trackProgress
      car.trackProgress += progressIncrement

      // Consume fuel & wear tires
      val lapDelta = progressIncrement
      car.fuelLapsRemaining = max(0.0f, car.fuelLapsRemaining - (lapDelta * car.engineMode.fuelBurnMultiplier))

      // Tire wear (factoring in driver smoothness, tire compound, suspension & tires R&D)
      val tirePartBonus = if (car.isPlayer) {
        val tiresPart = playerCarParts.find { it.type == PartType.TIRES_DYNAMICS }?.performance ?: 70
        val suspensionPart = playerCarParts.find { it.type == PartType.SUSPENSION }?.performance ?: 70
        // High tire R&D + suspension preserves rubber: up to 20% wear reduction
        1.0f - (((tiresPart + suspensionPart) / 2f - 70f) * 0.005f).coerceIn(-0.1f, 0.20f)
      } else {
        1.0f
      }
      val tireWearRate = car.tireCompound.wearRatePerLap * car.drivingStyle.tireWearMultiplier * ((100f - car.driver.smoothness * 0.35f) / 65f) * tirePartBonus
      car.tireHealthPct = max(0.0f, car.tireHealthPct - (lapDelta * tireWearRate))

      // ERS Recharge / Drain
      if (car.ersMode == ErsMode.BOOST) {
        car.ersPercent = max(0.0f, car.ersPercent - dtSec * 12.0f)
        if (car.ersPercent <= 0f) {
          car.ersMode = ErsMode.NEUTRAL
        }
      } else if (car.ersMode == ErsMode.HARVEST) {
        car.ersPercent = min(100.0f, car.ersPercent + dtSec * 6.0f)
      } else {
        car.ersPercent = min(100.0f, car.ersPercent + dtSec * 1.5f)
      }

      // Car reliability wear
      car.carReliabilityPct = max(10.0f, car.carReliabilityPct - (dtSec * 0.04f * car.engineMode.engineWearMultiplier))

      // Mechanical failure risk if reliability < 35%
      if (car.carReliabilityPct < 35f && Random.nextFloat() < 0.0008f * dtSec) {
        car.dnf = true
        car.dnfReason = "Engine Failure"
        addRadio(car.currentLap, car.driver.name, "NO POWER! Engine has blown, pulling off track!", isAlert = true, color = RacingRed)
        triggerSafetyCar()
        return@forEach
      }

      // Severe tire puncture risk if tire health < 15%
      if (car.tireHealthPct < 15f && Random.nextFloat() < 0.003f * dtSec) {
        car.dnf = true
        car.dnfReason = "Tire Delamination"
        addRadio(car.currentLap, car.driver.name, "PUNCTURE! Right-rear has failed! Heavy crash!", isAlert = true, color = RacingRed)
        triggerSafetyCar()
        return@forEach
      }

      // Completed a lap?
      if (oldProgress < 1.0f && car.trackProgress >= 1.0f) {
        car.currentLap++
        car.trackProgress -= 1.0f
        car.tireAgeLaps++

        // Lap time recording
        val calculatedLapTime = baseLap * (1.0f / speedFactor) + (Random.nextFloat() - 0.5f) * 0.6f
        car.lastLapTimeSec = calculatedLapTime

        if (calculatedLapTime < car.bestLapTimeSec || car.bestLapTimeSec == 0.0f) {
          car.bestLapTimeSec = calculatedLapTime
        }

        if (calculatedLapTime < fastestLapTimeSec) {
          fastestLapTimeSec = calculatedLapTime
          fastestLapDriver = car.driver
          carStates.forEach { it.isFastestLap = (it.driver.id == car.driver.id) }

          if (car.isPlayer) {
            addRadio(car.currentLap, car.driver.name, "PURPLE SECTOR! Fastest lap of the race: ${formatLapTime(fastestLapTimeSec)}!", isAlert = false, color = TimingPurple)
          }
        }

        // Leader lap tracking
        if (car.position == 1 && car.currentLap > currentLeaderLap) {
          currentLeaderLap = car.currentLap
          if (currentLeaderLap > track.totalLaps) {
            isRaceFinished = true
            addRadio(currentLeaderLap, "Race Control", "CHEQUERED FLAG! ${car.driver.name} wins the ${track.name}!", isAlert = true, color = PodiumGold)
          } else if (currentLeaderLap == track.totalLaps) {
            addRadio(currentLeaderLap, "Pit Wall", "FINAL LAP! Keep your head down, bring it home!", isAlert = true, color = ElectricCyan)
          }
        }

        // Pit in check
        if (car.isPittingNextLap) {
          car.isPittingNextLap = false
          car.isInPit = true
          val pitStopDuration = 2.2f + (5 - pitTrainingLevel) * 0.35f + Random.nextFloat() * 0.4f
          car.pitTimerSec = pitStopDuration
        }

        // Radio checks for player
        if (car.isPlayer) {
          if (car.tireHealthPct < 30f && car.tireHealthPct > 20f) {
            addRadio(car.currentLap, car.driver.name, "Tires are going off! Grip is falling off a cliff!", isAlert = true, color = WarningAmber)
          }
          if (car.fuelLapsRemaining < 1.8f && car.fuelLapsRemaining > 1.0f) {
            addRadio(car.currentLap, "Pit Wall", "Fuel warning: 1 lap of fuel remaining! Switch to LEAN mode or box!", isAlert = true, color = WarningAmber)
          }
        }
      }
    }

    // Sort leaderboard positions based on (lap * 10.0 + trackProgress)
    updatePositions()
  }

  private fun calculateCarSpeed(car: RaceDriverState, baseLap: Float): Float {
    if (safetyCar == SafetyCarStatus.SAFETY_CAR) return 0.60f
    if (safetyCar == SafetyCarStatus.VIRTUAL_SAFETY_CAR) return 0.72f

    var speed = 1.0f

    // Driver pace contribution (50-99 scale, 80 is baseline 1.0)
    val driverBonus = (car.driver.pace - 80) * 0.0035f
    speed += driverBonus

    // Car parts contribution
    val carPerfScore = if (car.isPlayer) {
      playerCarParts.map { it.performance }.average().toFloat()
    } else {
      80f
    }
    speed += (carPerfScore - 75f) * 0.004f

    // Tire compound baseline advantage
    val tirePaceDelta = car.tireCompound.basePaceDeltaSec
    speed -= (tirePaceDelta / baseLap)

    // Tire wear degradation
    if (car.tireHealthPct < 40f) {
      val degFactor = (40f - car.tireHealthPct) / 40f
      speed -= degFactor * 0.09f // severe pace drop
    }

    // Weather mismatch penalty
    val isRaining = (weather == WeatherType.LIGHT_RAIN || weather == WeatherType.HEAVY_RAIN)
    val onSlicks = (car.tireCompound == TireCompound.SOFT || car.tireCompound == TireCompound.MEDIUM || car.tireCompound == TireCompound.HARD)
    if (isRaining && onSlicks) {
      speed -= (trackWetnessPct / 100f) * 0.28f // extreme aquaplaning penalty
    } else if (!isRaining && (car.tireCompound == TireCompound.WET || car.tireCompound == TireCompound.INTERMEDIATE)) {
      speed -= 0.08f // wet tires overheating on dry track
    }

    // Engine Mode
    speed -= (car.engineMode.paceDeltaSec / baseLap)

    // Driving Style
    speed -= (car.drivingStyle.paceDeltaSec / baseLap)

    // ERS Mode
    if (car.ersMode == ErsMode.BOOST && car.ersPercent > 5f) {
      speed += 0.065f
    }

    // Out of fuel penalty
    if (car.fuelLapsRemaining <= 0.0f) {
      speed *= 0.5f // crawling in limp home mode
    }

    return max(0.4f, speed)
  }

  private fun updatePositions() {
    val activeCars = carStates.filter { !it.dnf }
      .sortedByDescending { it.currentLap * 100.0f + it.trackProgress }

    val leader = activeCars.firstOrNull()
    val baseLap = track.baseLapTimeSec

    activeCars.forEachIndexed { idx, car ->
      val oldPos = car.position
      val newPos = idx + 1
      car.position = newPos

      if (leader != null) {
        val totalDeltaLaps = (leader.currentLap + leader.trackProgress) - (car.currentLap + car.trackProgress)
        car.intervalToLeaderSec = max(0.0f, totalDeltaLaps * baseLap)
      }

      if (idx > 0) {
        val carAhead = activeCars[idx - 1]
        val lapDeltaAhead = (carAhead.currentLap + carAhead.trackProgress) - (car.currentLap + car.trackProgress)
        car.gapToAheadSec = max(0.0f, lapDeltaAhead * baseLap)
      } else {
        car.gapToAheadSec = 0.0f
      }

      // Check for player overtake notifications
      if (car.isPlayer && newPos < oldPos && oldPos - newPos == 1) {
        addRadio(car.currentLap, car.driver.name, "Great move into turn 1! P${newPos} secured!", isAlert = false, color = NeonGreen)
      }
    }

    // DNFs go to bottom
    val dnfCars = carStates.filter { it.dnf }
    dnfCars.forEachIndexed { idx, car ->
      car.position = activeCars.size + idx + 1
    }
  }

  private fun updateWeather() {
    // Gradual weather shift chances (10% chance every 2-3 laps)
    if (Random.nextFloat() < 0.001f) {
      val next = when (weather) {
        WeatherType.DRY -> if (Random.nextFloat() < 0.35f) WeatherType.OVERCAST else WeatherType.DRY
        WeatherType.OVERCAST -> if (Random.nextFloat() < 0.4f) WeatherType.LIGHT_RAIN else WeatherType.DRY
        WeatherType.LIGHT_RAIN -> if (Random.nextFloat() < 0.3f) WeatherType.HEAVY_RAIN else WeatherType.OVERCAST
        WeatherType.HEAVY_RAIN -> WeatherType.LIGHT_RAIN
      }
      if (next != weather) {
        weather = next
        addRadio(currentLeaderLap, "Race Control", "WEATHER UPDATE: Conditions shifting to ${weather.displayName}!", isAlert = true, color = ElectricCyan)
      }
    }

    // Adjust track wetness
    when (weather) {
      WeatherType.DRY -> trackWetnessPct = max(0.0f, trackWetnessPct - 0.1f)
      WeatherType.OVERCAST -> trackWetnessPct = max(0.0f, trackWetnessPct - 0.05f)
      WeatherType.LIGHT_RAIN -> trackWetnessPct = min(60.0f, trackWetnessPct + 0.15f)
      WeatherType.HEAVY_RAIN -> trackWetnessPct = min(100.0f, trackWetnessPct + 0.3f)
    }
  }

  private fun triggerSafetyCar() {
    if (safetyCar == SafetyCarStatus.NONE) {
      safetyCar = if (Random.nextBoolean()) SafetyCarStatus.SAFETY_CAR else SafetyCarStatus.VIRTUAL_SAFETY_CAR
      safetyCarLapsRemaining = Random.nextInt(2, 4)
      addRadio(
        currentLeaderLap,
        "Race Control",
        "YELLOW FLAG: Incident on track! ${safetyCar.label}!",
        isAlert = true,
        color = WarningAmber
      )
    }
  }

  private fun addRadio(lap: Int, driverName: String, msg: String, isAlert: Boolean, color: Color) {
    radioMessages.add(
      0,
      TeamRadioMessage(
        id = ++messageIdCounter,
        lap = lap,
        driverName = driverName,
        message = msg,
        isAlert = isAlert,
        color = color
      )
    )
    if (radioMessages.size > 20) {
      radioMessages.removeLast()
    }
  }

  fun callPitStop(driverId: String, nextCompound: TireCompound) {
    val car = carStates.find { it.driver.id == driverId } ?: return
    car.isPittingNextLap = true
    car.nextPitCompound = nextCompound
    addRadio(
      car.currentLap,
      "Pit Wall",
      "BOX, BOX, BOX! Pitting this lap for ${nextCompound.fullName} tires.",
      isAlert = true,
      color = ElectricCyan
    )
  }

  fun setDriverEngineMode(driverId: String, mode: EngineMode) {
    val car = carStates.find { it.driver.id == driverId } ?: return
    car.engineMode = mode
  }

  fun setDriverStyle(driverId: String, style: DrivingStyle) {
    val car = carStates.find { it.driver.id == driverId } ?: return
    car.drivingStyle = style
  }

  fun triggerDriverErs(driverId: String, mode: ErsMode) {
    val car = carStates.find { it.driver.id == driverId } ?: return
    car.ersMode = mode
    if (mode == ErsMode.BOOST) {
      addRadio(car.currentLap, car.driver.name, "Deploying battery overtake boost!", isAlert = false, color = TimingPurple)
    }
  }

  fun getFinalResults(): List<RaceResultEntry> {
    val pointsMap = mapOf(
      1 to 25, 2 to 18, 3 to 15, 4 to 12, 5 to 10,
      6 to 8, 7 to 6, 8 to 4, 9 to 2, 10 to 1
    )

    return carStates.sortedBy { it.position }.map { car ->
      val pts = (pointsMap[car.position] ?: 0) + (if (car.isFastestLap && car.position <= 10) 1 else 0)
      val gapStr = if (car.position == 1) "WINNER" else if (car.dnf) "DNF (${car.dnfReason})" else "+${car.intervalToLeaderSec.format(2)}s"

      RaceResultEntry(
        position = car.position,
        driver = car.driver,
        team = car.team,
        totalTimeText = if (car.dnf) "DNF" else formatLapTime(car.bestLapTimeSec),
        intervalText = gapStr,
        bestLapSec = car.bestLapTimeSec,
        isFastestLap = car.isFastestLap,
        pointsAwarded = pts,
        pitStops = car.totalPitStops
      )
    }
  }

  private fun Float.format(digits: Int) = String.format("%.${digits}f", this)

  private fun formatLapTime(sec: Float): String {
    if (sec <= 0f || sec > 300f) return "--:--.---"
    val minutes = (sec / 60).toInt()
    val remainderSec = sec % 60
    return String.format("%d:%06.3f", minutes, remainderSec)
  }
}
