package com.example.data.repository

import androidx.compose.ui.graphics.Color
import com.example.data.model.*
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PodiumGold
import com.example.ui.theme.RacingRed

object MotorsportRepository {

  fun getInitialTeams(): List<Team> = listOf(
    Team("apex", "Apex Grand Prix", "APX", ElectricCyan, Color(0xFF0D1117), budget = 14_500_000L, points = 0, isPlayer = true),
    Team("scuderia", "Scuderia Veloce", "VEL", RacingRed, Color(0xFFFFD700), budget = 35_000_000L, points = 0),
    Team("silver", "Silver Arrow GP", "SLV", Color(0xFFC0C0C0), Color(0xFF00D2BE), budget = 38_000_000L, points = 0),
    Team("bullseye", "Red Bullseye Racing", "RBR", Color(0xFF1E293B), Color(0xFFFFCC00), budget = 40_000_000L, points = 0),
    Team("mclaren", "Papaya Speedworks", "PAP", Color(0xFFFF8000), Color(0xFF000000), budget = 26_000_000L, points = 0),
    Team("aston", "Aston British Racing", "AST", Color(0xFF00594F), Color(0xFFCCFF00), budget = 24_000_000L, points = 0),
    Team("alpine", "Horizon Alpine", "ALP", Color(0xFF0090FF), Color(0xFFFF80BF), budget = 18_000_000L, points = 0),
    Team("sauber", "Kick Veloce", "KCK", Color(0xFF52E252), Color(0xFF111111), budget = 12_000_000L, points = 0),
    Team("haas", "Haas America Racing", "HAS", Color(0xFFECEFF1), Color(0xFFE53935), budget = 11_000_000L, points = 0),
    Team("williams", "Grove Williams", "WIL", Color(0xFF0044AA), Color(0xFF00E5FF), budget = 10_500_000L, points = 0)
  )

  fun getInitialDrivers(): List<Driver> = listOf(
    // Player Drivers (Apex Grand Prix)
    Driver("d1", "Lucas Silva", "SIL", 44, "Brazil", "🇧🇷", pace = 83, overtaking = 84, smoothness = 80, consistency = 81, fitness = 88, morale = 90, marketability = 85, salaryPerRace = 420_000L, contractRacesRemaining = 12, isPlayer = true),
    Driver("d2", "Marcus Vance", "VAN", 16, "UK", "🇬🇧", pace = 79, overtaking = 78, smoothness = 83, consistency = 84, fitness = 82, morale = 85, marketability = 76, salaryPerRace = 340_000L, contractRacesRemaining = 10, isPlayer = true),

    // Rivals
    Driver("d3", "Max Verstappen", "VER", 1, "Netherlands", "🇳🇱", pace = 96, overtaking = 95, smoothness = 90, consistency = 94, fitness = 96, morale = 95, marketability = 98, salaryPerRace = 1_200_000L, contractRacesRemaining = 18),
    Driver("d4", "Sergio Perez", "PER", 11, "Mexico", "🇲🇽", pace = 86, overtaking = 88, smoothness = 92, consistency = 82, fitness = 88, morale = 80, marketability = 89, salaryPerRace = 700_000L, contractRacesRemaining = 8),
    Driver("d5", "Charles Leclerc", "LEC", 16, "Monaco", "🇲🇨", pace = 94, overtaking = 91, smoothness = 86, consistency = 88, fitness = 92, morale = 90, marketability = 94, salaryPerRace = 1_000_000L, contractRacesRemaining = 16),
    Driver("d6", "Carlos Sainz", "SAI", 55, "Spain", "🇪🇸", pace = 90, overtaking = 89, smoothness = 91, consistency = 90, fitness = 90, morale = 88, marketability = 87, salaryPerRace = 850_000L, contractRacesRemaining = 14),
    Driver("d7", "Lewis Hamilton", "HAM", 44, "UK", "🇬🇧", pace = 93, overtaking = 94, smoothness = 93, consistency = 92, fitness = 94, morale = 92, marketability = 99, salaryPerRace = 1_100_000L, contractRacesRemaining = 14),
    Driver("d8", "George Russell", "RUS", 63, "UK", "🇬🇧", pace = 91, overtaking = 89, smoothness = 87, consistency = 89, fitness = 91, morale = 89, marketability = 88, salaryPerRace = 800_000L, contractRacesRemaining = 12),
    Driver("d9", "Lando Norris", "NOR", 4, "UK", "🇬🇧", pace = 92, overtaking = 90, smoothness = 88, consistency = 89, fitness = 90, morale = 94, marketability = 93, salaryPerRace = 900_000L, contractRacesRemaining = 16),
    Driver("d10", "Oscar Piastri", "PIA", 81, "Australia", "🇦🇺", pace = 89, overtaking = 87, smoothness = 89, consistency = 91, fitness = 88, morale = 91, marketability = 84, salaryPerRace = 650_000L, contractRacesRemaining = 15),
    Driver("d11", "Fernando Alonso", "ALO", 14, "Spain", "🇪🇸", pace = 91, overtaking = 96, smoothness = 94, consistency = 93, fitness = 90, morale = 90, marketability = 95, salaryPerRace = 950_000L, contractRacesRemaining = 10),
    Driver("d12", "Lance Stroll", "STR", 18, "Canada", "🇨🇦", pace = 81, overtaking = 82, smoothness = 78, consistency = 77, fitness = 82, morale = 78, marketability = 72, salaryPerRace = 450_000L, contractRacesRemaining = 20),
    Driver("d13", "Pierre Gasly", "GAS", 10, "France", "🇫🇷", pace = 84, overtaking = 85, smoothness = 82, consistency = 83, fitness = 85, morale = 81, marketability = 80, salaryPerRace = 500_000L, contractRacesRemaining = 10),
    Driver("d14", "Esteban Ocon", "OCO", 31, "France", "🇫🇷", pace = 83, overtaking = 86, smoothness = 81, consistency = 82, fitness = 84, morale = 80, marketability = 78, salaryPerRace = 480_000L, contractRacesRemaining = 9),
    Driver("d15", "Valtteri Bottas", "BOT", 77, "Finland", "🇫🇮", pace = 83, overtaking = 80, smoothness = 89, consistency = 88, fitness = 85, morale = 82, marketability = 82, salaryPerRace = 520_000L, contractRacesRemaining = 8),
    Driver("d16", "Zhou Guanyu", "ZHO", 24, "China", "🇨🇳", pace = 78, overtaking = 76, smoothness = 80, consistency = 81, fitness = 81, morale = 80, marketability = 84, salaryPerRace = 320_000L, contractRacesRemaining = 6),
    Driver("d17", "Nico Hulkenberg", "HUL", 27, "Germany", "🇩🇪", pace = 82, overtaking = 83, smoothness = 84, consistency = 87, fitness = 83, morale = 83, marketability = 77, salaryPerRace = 420_000L, contractRacesRemaining = 7),
    Driver("d18", "Kevin Magnussen", "MAG", 20, "Denmark", "🇩🇰", pace = 80, overtaking = 87, smoothness = 75, consistency = 76, fitness = 82, morale = 79, marketability = 75, salaryPerRace = 380_000L, contractRacesRemaining = 8),
    Driver("d19", "Alexander Albon", "ALB", 23, "Thailand", "🇹🇭", pace = 85, overtaking = 86, smoothness = 86, consistency = 87, fitness = 86, morale = 87, marketability = 83, salaryPerRace = 540_000L, contractRacesRemaining = 14),
    Driver("d20", "Logan Sargeant", "SAR", 2, "USA", "🇺🇸", pace = 75, overtaking = 74, smoothness = 72, consistency = 71, fitness = 79, morale = 74, marketability = 70, salaryPerRace = 250_000L, contractRacesRemaining = 5)
  )

  fun getScoutMarketDrivers(): List<Driver> = listOf(
    Driver("scout1", "Oliver Bearman", "BEA", 38, "UK", "🇬🇧", pace = 82, overtaking = 85, smoothness = 81, consistency = 79, fitness = 89, morale = 94, marketability = 88, salaryPerRace = 280_000L, contractRacesRemaining = 24),
    Driver("scout2", "Kimi Antonelli", "ANT", 12, "Italy", "🇮🇹", pace = 85, overtaking = 83, smoothness = 82, consistency = 80, fitness = 90, morale = 96, marketability = 91, salaryPerRace = 350_000L, contractRacesRemaining = 24),
    Driver("scout3", "Felipe Drugovich", "DRU", 21, "Brazil", "🇧🇷", pace = 81, overtaking = 80, smoothness = 85, consistency = 86, fitness = 86, morale = 88, marketability = 82, salaryPerRace = 260_000L, contractRacesRemaining = 20),
    Driver("scout4", "Liam Lawson", "LAW", 40, "New Zealand", "🇳🇿", pace = 83, overtaking = 85, smoothness = 81, consistency = 83, fitness = 88, morale = 90, marketability = 84, salaryPerRace = 310_000L, contractRacesRemaining = 18)
  )

  fun getInitialCarParts(): List<CarPart> = listOf(
    // Motor
    CarPart(PartType.ENGINE, performance = 72, reliability = 82, specLevel = 1),
    CarPart(PartType.GEARBOX, performance = 74, reliability = 85, specLevel = 1),
    // Chassi
    CarPart(PartType.CHASSIS, performance = 71, reliability = 86, specLevel = 1),
    CarPart(PartType.SUSPENSION, performance = 73, reliability = 84, specLevel = 1),
    // Pneus
    CarPart(PartType.TIRES_DYNAMICS, performance = 70, reliability = 85, specLevel = 1),
    CarPart(PartType.BRAKES, performance = 75, reliability = 90, specLevel = 1),
    // Aerodinâmica
    CarPart(PartType.FRONT_WING, performance = 70, reliability = 88, specLevel = 1),
    CarPart(PartType.REAR_WING, performance = 71, reliability = 86, specLevel = 1)
  )

  fun getInitialFacilities(): List<Facility> = listOf(
    Facility("fac_factory", "HQ Factory & Production", level = 2, upgradeCost = 2_800_000L, description = "Speeds up parts manufacturing and increases repair efficiency between race weekends.", benefit = "+25% faster part fabrication & repair speed"),
    Facility("fac_tunnel", "Wind Tunnel Complex", level = 1, upgradeCost = 3_500_000L, description = "State-of-the-art aero testing facility boosting front and rear wing maximum potential.", benefit = "+12 Aerodynamic ceiling for new spec wings"),
    Facility("fac_sim", "Telemetry & Driver Simulator", level = 2, upgradeCost = 2_200_000L, description = "High-fidelity driver rig improving driver lap consistency, tire awareness and race setup.", benefit = "+5 Driver Consistency & lower setup errors"),
    Facility("fac_pit", "Pit Stop Training Center", level = 2, upgradeCost = 1_800_000L, description = "Equips pit mechanics with automated wheel guns and training rigs to shave vital tenths.", benefit = "Reduces pit stop time to 2.4s and lowers error chance"),
    Facility("fac_scout", "Global Talent Scouting Hub", level = 1, upgradeCost = 1_500_000L, description = "Identifies prodigies and provides detailed telemetry data on rival cars.", benefit = "Unlocks detailed scouting profiles on young stars"),
    Facility("fac_commercial", "Commercial & Brand Headquarters", level = 2, upgradeCost = 2_500_000L, description = "Secures lucrative motorsport sponsorships and increases merchandise revenue.", benefit = "+20% sponsor signing and bonus payouts")
  )

  fun getInitialSponsors(): List<Sponsor> = listOf(
    Sponsor("sp1", "Quantum Telecom", slotNumber = 1, fixedPerRace = 450_000L, bonusGoalText = "Finish 6th or higher", targetPosition = 6, bonusPayout = 750_000L, contractRacesLeft = 8, isSigned = true),
    Sponsor("sp2", "AeroVeloce Synthetic Fuel", slotNumber = 2, fixedPerRace = 320_000L, bonusGoalText = "Double points finish (Both cars Top 10)", targetPosition = 10, bonusPayout = 900_000L, contractRacesLeft = 6, isSigned = true),
    Sponsor("sp3", "Chronos Luxury Watches", slotNumber = 3, fixedPerRace = 280_000L, bonusGoalText = "Podium Finish (Top 3)", targetPosition = 3, bonusPayout = 1_600_000L, contractRacesLeft = 4, isSigned = true)
  )

  // Curated tracks with realistic circuit waypoint paths for 2D radar track view!
  fun getCalendarTracks(): List<Track> = listOf(
    Track(
      id = "monza",
      name = "Autodromo Nazionale Monza",
      location = "Monza, Italy",
      flagEmoji = "🇮🇹",
      totalLaps = 16,
      circuitLengthKm = 5.793f,
      downforceReq = "Very Low (Speed Temple)",
      tireWearLevel = "Medium",
      fuelBurnLevel = "Very High",
      baseLapTimeSec = 82.5f,
      pathPoints = generateMonzaWaypoints()
    ),
    Track(
      id = "silverstone",
      name = "Silverstone Grand Prix Circuit",
      location = "Northamptonshire, UK",
      flagEmoji = "🇬🇧",
      totalLaps = 15,
      circuitLengthKm = 5.891f,
      downforceReq = "High (Maggotts-Becketts)",
      tireWearLevel = "High (Fast lateral load)",
      fuelBurnLevel = "Normal",
      baseLapTimeSec = 88.0f,
      pathPoints = generateSilverstoneWaypoints()
    ),
    Track(
      id = "spa",
      name = "Circuit de Spa-Francorchamps",
      location = "Stavelot, Belgium",
      flagEmoji = "🇧🇪",
      totalLaps = 14,
      circuitLengthKm = 7.004f,
      downforceReq = "Medium-High (Eau Rouge)",
      tireWearLevel = "High",
      fuelBurnLevel = "High",
      baseLapTimeSec = 105.0f,
      pathPoints = generateSpaWaypoints()
    ),
    Track(
      id = "interlagos",
      name = "Autódromo de Interlagos",
      location = "São Paulo, Brazil",
      flagEmoji = "🇧🇷",
      totalLaps = 16,
      circuitLengthKm = 4.309f,
      downforceReq = "High (Senna S & Infield)",
      tireWearLevel = "Medium",
      fuelBurnLevel = "Normal",
      baseLapTimeSec = 71.5f,
      pathPoints = generateInterlagosWaypoints()
    ),
    Track(
      id = "suzuka",
      name = "Suzuka International Racing Course",
      location = "Mie Prefecture, Japan",
      flagEmoji = "🇯🇵",
      totalLaps = 15,
      circuitLengthKm = 5.807f,
      downforceReq = "High (Figure-Eight Crossover)",
      tireWearLevel = "Severe (Esses & 130R)",
      fuelBurnLevel = "Normal",
      baseLapTimeSec = 89.5f,
      pathPoints = generateSuzukaWaypoints()
    ),
    Track(
      id = "monaco",
      name = "Circuit de Monaco",
      location = "Monte Carlo, Monaco",
      flagEmoji = "🇲🇨",
      totalLaps = 18,
      circuitLengthKm = 3.337f,
      downforceReq = "Maximum (Street Circuit)",
      tireWearLevel = "Low (Tight low speed)",
      fuelBurnLevel = "Low",
      baseLapTimeSec = 74.0f,
      pathPoints = generateMonacoWaypoints()
    )
  )

  // Smooth normalized circuit waypoints (0.0 to 1.0)
  private fun generateMonzaWaypoints(): List<TrackWaypoint> = listOf(
    TrackWaypoint(0.20f, 0.85f), // Pit Straight
    TrackWaypoint(0.50f, 0.85f),
    TrackWaypoint(0.80f, 0.85f),
    TrackWaypoint(0.88f, 0.82f), // Variante del Rettifilo
    TrackWaypoint(0.88f, 0.68f),
    TrackWaypoint(0.82f, 0.60f), // Curva Grande
    TrackWaypoint(0.72f, 0.40f),
    TrackWaypoint(0.75f, 0.32f), // Variante della Roggia
    TrackWaypoint(0.70f, 0.25f),
    TrackWaypoint(0.55f, 0.22f), // Lesmo 1 & 2
    TrackWaypoint(0.40f, 0.20f),
    TrackWaypoint(0.32f, 0.32f), // Serraglio straight
    TrackWaypoint(0.24f, 0.45f), // Variante Ascari
    TrackWaypoint(0.18f, 0.52f),
    TrackWaypoint(0.15f, 0.65f), // Back straight
    TrackWaypoint(0.12f, 0.78f), // Parabolica
    TrackWaypoint(0.15f, 0.84f)
  )

  private fun generateSilverstoneWaypoints(): List<TrackWaypoint> = listOf(
    TrackWaypoint(0.25f, 0.75f), // Hamilton Straight
    TrackWaypoint(0.45f, 0.75f),
    TrackWaypoint(0.60f, 0.72f), // Abbey & Farm
    TrackWaypoint(0.75f, 0.68f), // Village & The Loop
    TrackWaypoint(0.82f, 0.58f),
    TrackWaypoint(0.78f, 0.48f), // Aintree & Wellington Straight
    TrackWaypoint(0.60f, 0.45f),
    TrackWaypoint(0.48f, 0.42f), // Brooklands & Luffield
    TrackWaypoint(0.40f, 0.35f),
    TrackWaypoint(0.45f, 0.25f), // Woodcote & Copse
    TrackWaypoint(0.65f, 0.20f),
    TrackWaypoint(0.78f, 0.24f), // Maggotts & Becketts
    TrackWaypoint(0.72f, 0.32f), // Chapel
    TrackWaypoint(0.50f, 0.35f), // Hangar Straight
    TrackWaypoint(0.30f, 0.42f), // Stowe Corner
    TrackWaypoint(0.22f, 0.55f), // Vale & Club Corner
    TrackWaypoint(0.20f, 0.68f)
  )

  private fun generateSpaWaypoints(): List<TrackWaypoint> = listOf(
    TrackWaypoint(0.22f, 0.82f), // Pit Straight
    TrackWaypoint(0.35f, 0.80f), // La Source hairpin
    TrackWaypoint(0.28f, 0.70f),
    TrackWaypoint(0.30f, 0.55f), // Eau Rouge & Raidillon
    TrackWaypoint(0.40f, 0.40f), // Kemmel Straight
    TrackWaypoint(0.65f, 0.25f),
    TrackWaypoint(0.78f, 0.22f), // Les Combes
    TrackWaypoint(0.85f, 0.32f), // Malmedy & Bruxelles
    TrackWaypoint(0.82f, 0.45f), // Rivage
    TrackWaypoint(0.70f, 0.55f), // Pouhon (high speed double left)
    TrackWaypoint(0.60f, 0.65f),
    TrackWaypoint(0.52f, 0.72f), // Campus & Stavelot
    TrackWaypoint(0.42f, 0.82f), // Blanchimont
    TrackWaypoint(0.30f, 0.88f), // Bus Stop Chicane
    TrackWaypoint(0.24f, 0.85f)
  )

  private fun generateInterlagosWaypoints(): List<TrackWaypoint> = listOf(
    TrackWaypoint(0.25f, 0.78f), // Reta dos Boxes
    TrackWaypoint(0.55f, 0.80f),
    TrackWaypoint(0.75f, 0.78f), // Senna S
    TrackWaypoint(0.82f, 0.68f),
    TrackWaypoint(0.78f, 0.55f), // Curva do Sol
    TrackWaypoint(0.62f, 0.45f), // Reta Oposta
    TrackWaypoint(0.45f, 0.38f), // Descida do Lago
    TrackWaypoint(0.35f, 0.42f),
    TrackWaypoint(0.30f, 0.52f), // Ferradura & Laranjinha
    TrackWaypoint(0.40f, 0.58f), // Pinheirinho
    TrackWaypoint(0.50f, 0.54f), // Bico de Pato
    TrackWaypoint(0.48f, 0.64f), // Mergulho
    TrackWaypoint(0.38f, 0.70f), // Junção & Subida dos Boxes
    TrackWaypoint(0.28f, 0.75f)
  )

  private fun generateSuzukaWaypoints(): List<TrackWaypoint> = listOf(
    TrackWaypoint(0.22f, 0.80f), // Pit Straight
    TrackWaypoint(0.40f, 0.78f), // First Curve
    TrackWaypoint(0.55f, 0.70f), // S-Curves
    TrackWaypoint(0.62f, 0.58f),
    TrackWaypoint(0.58f, 0.48f), // Dunlop Curve
    TrackWaypoint(0.68f, 0.40f), // Degner 1 & 2
    TrackWaypoint(0.76f, 0.35f), // Under the bridge
    TrackWaypoint(0.85f, 0.38f), // Hairpin
    TrackWaypoint(0.80f, 0.50f),
    TrackWaypoint(0.70f, 0.60f), // 200R
    TrackWaypoint(0.60f, 0.40f), // Spoon Curve
    TrackWaypoint(0.45f, 0.30f), // Over the bridge
    TrackWaypoint(0.32f, 0.45f), // 130R
    TrackWaypoint(0.24f, 0.65f), // Casio Triangle chicane
    TrackWaypoint(0.20f, 0.75f)
  )

  private fun generateMonacoWaypoints(): List<TrackWaypoint> = listOf(
    TrackWaypoint(0.25f, 0.85f), // Boulevard Albert 1er
    TrackWaypoint(0.55f, 0.85f), // Sainte Dévote
    TrackWaypoint(0.72f, 0.75f), // Beau Rivage
    TrackWaypoint(0.82f, 0.60f), // Massenet
    TrackWaypoint(0.85f, 0.45f), // Casino Square
    TrackWaypoint(0.78f, 0.35f), // Mirabeau Haute
    TrackWaypoint(0.70f, 0.28f), // Grand Hotel Hairpin
    TrackWaypoint(0.62f, 0.35f), // Mirabeau Bas
    TrackWaypoint(0.55f, 0.42f), // Portier
    TrackWaypoint(0.45f, 0.48f), // Tunnel
    TrackWaypoint(0.35f, 0.55f), // Nouvelle Chicane
    TrackWaypoint(0.28f, 0.62f), // Tabac
    TrackWaypoint(0.20f, 0.68f), // Louis Chiron (Swimming Pool)
    TrackWaypoint(0.18f, 0.78f), // La Rascasse & Anthony Noghes
    TrackWaypoint(0.22f, 0.84f)
  )
}
