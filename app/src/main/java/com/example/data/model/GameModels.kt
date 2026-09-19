package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PodiumGold
import com.example.ui.theme.RacingRed
import com.example.ui.theme.TireHardWhite
import com.example.ui.theme.TireInterGreen
import com.example.ui.theme.TireMediumYellow
import com.example.ui.theme.TireSoftRed
import com.example.ui.theme.TireWetBlue
import com.example.ui.theme.TimingPurple

enum class TireCompound(
  val code: String,
  val fullName: String,
  val displayColor: Color,
  val basePaceDeltaSec: Float, // Soft fastest (-0.6s), Hard slowest (+0.6s)
  val wearRatePerLap: Float,   // Pct per lap
  val optimalWeather: List<WeatherType>
) {
  SOFT("S", "Soft Slick", TireSoftRed, -0.7f, 7.5f, listOf(WeatherType.DRY, WeatherType.OVERCAST)),
  MEDIUM("M", "Medium Slick", TireMediumYellow, 0.0f, 4.5f, listOf(WeatherType.DRY, WeatherType.OVERCAST)),
  HARD("H", "Hard Slick", TireHardWhite, 0.6f, 2.8f, listOf(WeatherType.DRY, WeatherType.OVERCAST)),
  INTERMEDIATE("I", "Intermediate", TireInterGreen, 2.0f, 3.5f, listOf(WeatherType.LIGHT_RAIN)),
  WET("W", "Full Wet", TireWetBlue, 4.5f, 3.0f, listOf(WeatherType.HEAVY_RAIN))
}

enum class EngineMode(
  val label: String,
  val paceDeltaSec: Float,
  val fuelBurnMultiplier: Float,
  val engineWearMultiplier: Float
) {
  LEAN("LEAN", 0.6f, 0.75f, 0.6f),
  NORMAL("NORM", 0.0f, 1.0f, 1.0f),
  PUSH("PUSH", -0.45f, 1.35f, 1.5f),
  OVERTAKE("BURN", -0.85f, 1.8f, 2.2f)
}

enum class DrivingStyle(
  val label: String,
  val paceDeltaSec: Float,
  val tireWearMultiplier: Float,
  val mistakeRiskPct: Float
) {
  CONSERVE("CONSERVE", 0.7f, 0.6f, 0.01f),
  BALANCED("BALANCED", 0.0f, 1.0f, 0.03f),
  ATTACK("ATTACK", -0.6f, 1.7f, 0.08f)
}

enum class ErsMode(
  val label: String,
  val drainRatePct: Float,
  val boostSec: Float
) {
  HARVEST("HARVEST", -8.0f, 0.4f), // Charges +8% per lap segment
  NEUTRAL("NEUTRAL", 0.0f, 0.0f),
  BOOST("OVERTAKE BOOST", 15.0f, -0.6f) // Drains 15% quickly for instant pass
}

enum class ComponentCategory(val title: String) {
  ENGINE("Motor / Power Unit"),
  CHASSIS("Chassis & Suspensão"),
  TIRES("Pneus & Dinâmica"),
  AERODYNAMICS("Aerodinâmica")
}

enum class PartType(
  val title: String,
  val description: String,
  val category: ComponentCategory,
  val baseResearchCost: Long,
  val perfGain: Int,
  val impactDescription: String
) {
  // Motor / Power Unit
  ENGINE(
    "Motor V6 Turbo-Híbrido",
    "Potência pura e velocidade máxima em retas longas",
    ComponentCategory.ENGINE,
    1_100_000L,
    6,
    "+0.4s em retas e velocidade de ponta"
  ),
  GEARBOX(
    "Câmbio de Marchas (Gearbox)",
    "Aceleração e retomada rápida nas saídas de curvas lentas",
    ComponentCategory.ENGINE,
    850_000L,
    5,
    "+0.3s de aceleração em tração"
  ),

  // Chassi & Suspensão
  CHASSIS(
    "Monocoque & Estrutura do Chassi",
    "Rigidez torcional e redução de peso geral do monoposto",
    ComponentCategory.CHASSIS,
    950_000L,
    6,
    "+0.35s de agilidade geral e estabilidade"
  ),
  SUSPENSION(
    "Suspensão Ativa & Barras",
    "Absorção de zebras, geometria e preservação de pneus",
    ComponentCategory.CHASSIS,
    750_000L,
    5,
    "-15% desgaste de pneus e estabilidade em zebras"
  ),

  // Pneus & Dinâmica
  TIRES_DYNAMICS(
    "Compostos & Dutos de Pneus",
    "Gerenciamento térmico, aquecimento e aderência mecânica",
    ComponentCategory.TIRES,
    800_000L,
    5,
    "+10% aderência em curvas e menor degradação"
  ),
  BRAKES(
    "Freios de Carbono",
    "Frenagem tardia em zonas de ultrapassagem e reaceleração",
    ComponentCategory.TIRES,
    700_000L,
    5,
    "+0.25s e maior taxa de ultrapassagens em freadas"
  ),

  // Aerodinâmica
  FRONT_WING(
    "Asa Dianteira & Flaps Aero",
    "Downforce frontal, entrada de curva rápida e fluxo de ar",
    ComponentCategory.AERODYNAMICS,
    900_000L,
    6,
    "+0.4s em curvas de média e alta velocidade"
  ),
  REAR_WING(
    "Asa Traseira & Sistema DRS",
    "Estabilidade traseira, downforce em alta e eficiência do DRS",
    ComponentCategory.AERODYNAMICS,
    950_000L,
    6,
    "+0.35s e ultrapassagens facilitadas com DRS"
  )
}

data class CarPart(
  val type: PartType,
  val performance: Int,     // 0 - 100
  val reliability: Int,     // 0 - 100%
  val maxPerformance: Int = 100,
  val isUpgradingPerformance: Boolean = false,
  val isUpgradingReliability: Boolean = false,
  val specLevel: Int = 1,
  val completedUpgrades: List<String> = emptyList()
)

data class Driver(
  val id: String,
  val name: String,
  val shortCode: String,
  val number: Int,
  val nationality: String,
  val flagEmoji: String,
  val pace: Int,          // 50-99
  val overtaking: Int,    // 50-99
  val smoothness: Int,    // 50-99 (reduces tire wear)
  val consistency: Int,   // 50-99 (fewer errors)
  val fitness: Int,       // 50-99
  val morale: Int,        // 0-100%
  val marketability: Int, // 0-100% (sponsor bonus boost)
  val salaryPerRace: Long,
  val contractRacesRemaining: Int,
  val isPlayer: Boolean = false
)

data class Team(
  val id: String,
  val name: String,
  val shortName: String,
  val primaryColor: Color,
  val secondaryColor: Color,
  var budget: Long,
  var points: Int = 0,
  val isPlayer: Boolean = false
)

data class Facility(
  val id: String,
  val name: String,
  val level: Int,
  val maxLevel: Int = 5,
  val upgradeCost: Long,
  val description: String,
  val benefit: String
)

data class Sponsor(
  val id: String,
  val name: String,
  val slotNumber: Int,
  val fixedPerRace: Long,
  val bonusGoalText: String,
  val targetPosition: Int, // finish position needed to trigger bonus
  val bonusPayout: Long,
  val contractRacesLeft: Int,
  val isSigned: Boolean = false
)

data class TrackWaypoint(
  val x: Float, // 0.0 to 1.0 normalized
  val y: Float
)

data class Track(
  val id: String,
  val name: String,
  val location: String,
  val flagEmoji: String,
  val totalLaps: Int,
  val circuitLengthKm: Float,
  val downforceReq: String, // Low, Medium, High
  val tireWearLevel: String, // Low, Medium, Severe
  val fuelBurnLevel: String, // Low, Normal, High
  val baseLapTimeSec: Float,
  val pathPoints: List<TrackWaypoint>
)

enum class WeatherType(val displayName: String, val iconRes: String) {
  DRY("Dry & Sunny", "☀️"),
  OVERCAST("Overcast", "⛅"),
  LIGHT_RAIN("Light Rain", "🌦️"),
  HEAVY_RAIN("Heavy Rain / Storm", "🌧️")
}

enum class SafetyCarStatus(val label: String) {
  NONE("GREEN FLAG"),
  VIRTUAL_SAFETY_CAR("VIRTUAL SAFETY CAR"),
  SAFETY_CAR("SAFETY CAR DEPLOYED")
}

data class TeamRadioMessage(
  val id: Long,
  val lap: Int,
  val driverName: String,
  val message: String,
  val isAlert: Boolean = false,
  val color: Color = ElectricCyan
)

data class RaceDriverState(
  val driver: Driver,
  val team: Team,
  var currentLap: Int = 1,
  var trackProgress: Float = 0.0f, // 0.0f to 1.0f on current lap
  var position: Int = 1,
  var intervalToLeaderSec: Float = 0.0f,
  var gapToAheadSec: Float = 0.0f,
  var tireCompound: TireCompound = TireCompound.MEDIUM,
  var tireHealthPct: Float = 100.0f,
  var tireAgeLaps: Int = 0,
  var fuelLapsRemaining: Float = 12.0f,
  var ersPercent: Float = 100.0f,
  var engineMode: EngineMode = EngineMode.NORMAL,
  var drivingStyle: DrivingStyle = DrivingStyle.BALANCED,
  var ersMode: ErsMode = ErsMode.NEUTRAL,
  var carReliabilityPct: Float = 98.0f,
  var isPittingNextLap: Boolean = false,
  var nextPitCompound: TireCompound = TireCompound.MEDIUM,
  var isInPit: Boolean = false,
  var pitTimerSec: Float = 0.0f,
  var totalPitStops: Int = 0,
  var lastLapTimeSec: Float = 0.0f,
  var bestLapTimeSec: Float = 0.0f,
  var isFastestLap: Boolean = false,
  var dnf: Boolean = false,
  var dnfReason: String = ""
) {
  val isPlayer: Boolean get() = driver.isPlayer
}

data class RaceResultEntry(
  val position: Int,
  val driver: Driver,
  val team: Team,
  val totalTimeText: String,
  val intervalText: String,
  val bestLapSec: Float,
  val isFastestLap: Boolean,
  val pointsAwarded: Int,
  val pitStops: Int
)

data class ChampionshipStanding(
  val rank: Int,
  val name: String,
  val teamName: String,
  val teamColor: Color,
  val points: Int,
  val wins: Int,
  val podiums: Int
)
