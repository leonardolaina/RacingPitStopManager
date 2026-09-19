package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.MotorsportRepository
import com.example.domain.engine.RaceSimulationEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
  HQ_HOME,
  CAR_DEV,
  FACILITIES,
  DRIVERS_STAFF,
  SPONSORS,
  STANDINGS,
  PRE_RACE,
  LIVE_RACE,
  POST_RACE
}

data class GameUiState(
  val currentScreen: AppScreen = AppScreen.HQ_HOME,
  val currentRound: Int = 1,
  val totalRounds: Int = 6,
  val team: Team = MotorsportRepository.getInitialTeams().first(),
  val rivalTeams: List<Team> = MotorsportRepository.getInitialTeams().drop(1),
  val drivers: List<Driver> = MotorsportRepository.getInitialDrivers(),
  val scoutMarket: List<Driver> = MotorsportRepository.getScoutMarketDrivers(),
  val carParts: List<CarPart> = MotorsportRepository.getInitialCarParts(),
  val facilities: List<Facility> = MotorsportRepository.getInitialFacilities(),
  val sponsors: List<Sponsor> = MotorsportRepository.getInitialSponsors(),
  val calendar: List<Track> = MotorsportRepository.getCalendarTracks(),
  val driverStandings: List<ChampionshipStanding> = emptyList(),
  val constructorStandings: List<ChampionshipStanding> = emptyList(),

  // Pre-race Strategy selections for player drivers
  val driver1StartCompound: TireCompound = TireCompound.SOFT,
  val driver2StartCompound: TireCompound = TireCompound.MEDIUM,

  // Live Race Simulation
  val simSpeed: Float = 1.0f, // 0 = Pause, 1 = 1x, 2 = 2x, 4 = 4x
  val isSimRunning: Boolean = false,
  val latestResults: List<RaceResultEntry> = emptyList(),
  val notificationMessage: String? = null
)

class MotorsportViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  var raceEngine: RaceSimulationEngine? = null
    private set

  private var simJob: Job? = null

  init {
    calculateStandings()
  }

  fun navigateTo(screen: AppScreen) {
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun getCurrentTrack(): Track {
    val round = _uiState.value.currentRound - 1
    val calendar = _uiState.value.calendar
    return calendar[round.coerceIn(0, calendar.size - 1)]
  }

  // Pre-Race strategy setups
  fun setDriverStartCompound(driverIndex: Int, compound: TireCompound) {
    _uiState.update {
      if (driverIndex == 1) it.copy(driver1StartCompound = compound)
      else it.copy(driver2StartCompound = compound)
    }
  }

  // Start Live Race Weekend
  fun startRaceWeekend() {
    val currentTrack = getCurrentTrack()
    val allTeams = listOf(_uiState.value.team) + _uiState.value.rivalTeams
    val pitGym = _uiState.value.facilities.find { it.id == "fac_pit" }?.level ?: 2

    raceEngine = RaceSimulationEngine(
      track = currentTrack,
      initialDrivers = _uiState.value.drivers,
      initialTeams = allTeams,
      playerCarParts = _uiState.value.carParts,
      pitTrainingLevel = pitGym
    )

    // Apply pre-race starting compound selections for player
    val p1 = raceEngine?.carStates?.find { it.driver.id == "d1" }
    val p2 = raceEngine?.carStates?.find { it.driver.id == "d2" }
    p1?.tireCompound = _uiState.value.driver1StartCompound
    p2?.tireCompound = _uiState.value.driver2StartCompound

    _uiState.update {
      it.copy(
        currentScreen = AppScreen.LIVE_RACE,
        isSimRunning = true,
        simSpeed = 1.0f
      )
    }

    startSimulationLoop()
  }

  fun setSimSpeed(speed: Float) {
    _uiState.update { it.copy(simSpeed = speed) }
  }

  private fun startSimulationLoop() {
    simJob?.cancel()
    simJob = viewModelScope.launch {
      while (isActive) {
        val speed = _uiState.value.simSpeed
        if (speed > 0f) {
          val dt = 0.20f * speed
          raceEngine?.update(dt)

          if (raceEngine?.isRaceFinished == true) {
            onRaceFinished()
            break
          }
        }
        delay(200L) // 5 updates per second
      }
    }
  }

  // Tactical race controls
  fun setDriverEngineMode(driverId: String, mode: EngineMode) {
    raceEngine?.setDriverEngineMode(driverId, mode)
  }

  fun setDriverStyle(driverId: String, style: DrivingStyle) {
    raceEngine?.setDriverStyle(driverId, style)
  }

  fun triggerDriverErs(driverId: String, mode: ErsMode) {
    raceEngine?.triggerDriverErs(driverId, mode)
  }

  fun callPitStop(driverId: String, compound: TireCompound) {
    raceEngine?.callPitStop(driverId, compound)
  }

  private fun onRaceFinished() {
    simJob?.cancel()
    val results = raceEngine?.getFinalResults() ?: emptyList()

    // Process championship points and rewards
    var prizeMoney = 2_200_000L
    var sponsorBonus = 0L

    results.forEach { r ->
      if (r.driver.isPlayer) {
        // Base prize money for high finish
        val bonusForPos = (21 - r.position) * 85_000L
        prizeMoney += bonusForPos

        // Check sponsors
        _uiState.value.sponsors.forEach { sp ->
          if (sp.isSigned && r.position <= sp.targetPosition) {
            sponsorBonus += sp.bonusPayout
          }
        }
      }
    }

    // Driver salaries deduction
    val salaryExpense = _uiState.value.drivers.filter { it.isPlayer }.sumOf { it.salaryPerRace }
    val netIncome = prizeMoney + sponsorBonus - salaryExpense

    // Update team budget and points
    val playerPointsEarned = results.filter { it.driver.isPlayer }.sumOf { it.pointsAwarded }

    _uiState.update { current ->
      val updatedTeam = current.team.copy(
        budget = current.team.budget + netIncome,
        points = current.team.points + playerPointsEarned
      )

      // Update rival teams points
      val updatedRivals = current.rivalTeams.map { rival ->
        val rivalPts = results.filter { it.team.id == rival.id }.sumOf { it.pointsAwarded }
        rival.copy(points = rival.points + rivalPts)
      }

      current.copy(
        team = updatedTeam,
        rivalTeams = updatedRivals,
        latestResults = results,
        currentScreen = AppScreen.POST_RACE,
        notificationMessage = "Race Complete! Earned $${formatCurrency(netIncome)}."
      )
    }

    calculateStandings()
  }

  fun advanceToNextRaceWeekend() {
    _uiState.update { current ->
      val nextRound = current.currentRound + 1
      if (nextRound <= current.totalRounds) {
        current.copy(
          currentRound = nextRound,
          currentScreen = AppScreen.HQ_HOME
        )
      } else {
        // Season concluded! Show HQ with championship celebration
        current.copy(
          currentScreen = AppScreen.HQ_HOME,
          notificationMessage = "Season Completed! Check final standings in Standings!"
        )
      }
    }
  }

  // Car R&D actions
  fun upgradePartPerformance(type: PartType) {
    val part = _uiState.value.carParts.find { it.type == type } ?: return
    val cost = type.baseResearchCost + (part.specLevel - 1) * 200_000L
    if (_uiState.value.team.budget < cost) {
      showToast("Orçamento insuficiente para desenvolver ${type.title} ($${formatCurrency(cost)} necessário)")
      return
    }

    _uiState.update { current ->
      val updatedParts = current.carParts.map {
        if (it.type == type) {
          it.copy(
            performance = (it.performance + type.perfGain).coerceAtMost(100),
            specLevel = it.specLevel + 1
          )
        } else it
      }
      current.copy(
        team = current.team.copy(budget = current.team.budget - cost),
        carParts = updatedParts,
        notificationMessage = "${type.title} atualizado para Spec ${part.specLevel + 1}! +${type.perfGain} Desempenho."
      )
    }
  }

  fun improvePartReliability(type: PartType) {
    val part = _uiState.value.carParts.find { it.type == type } ?: return
    val cost = 380_000L
    if (_uiState.value.team.budget < cost) {
      showToast("Orçamento insuficiente para reforçar confiabilidade ($380,000 necessário)")
      return
    }

    _uiState.update { current ->
      val updatedParts = current.carParts.map {
        if (it.type == type) it.copy(reliability = (it.reliability + 8).coerceAtMost(100))
        else it
      }
      current.copy(
        team = current.team.copy(budget = current.team.budget - cost),
        carParts = updatedParts,
        notificationMessage = "${type.title} reforçado para ${part.reliability + 8}% de confiabilidade!"
      )
    }
  }

  // Facility upgrades
  fun upgradeFacility(facilityId: String) {
    val fac = _uiState.value.facilities.find { it.id == facilityId } ?: return
    if (fac.level >= fac.maxLevel) return

    if (_uiState.value.team.budget < fac.upgradeCost) {
      showToast("Insufficient funds for ${fac.name} ($${formatCurrency(fac.upgradeCost)} required)")
      return
    }

    _uiState.update { current ->
      val updatedFacilities = current.facilities.map {
        if (it.id == facilityId) it.copy(
          level = it.level + 1,
          upgradeCost = (it.upgradeCost * 1.6).toLong()
        )
        else it
      }
      current.copy(
        team = current.team.copy(budget = current.team.budget - fac.upgradeCost),
        facilities = updatedFacilities,
        notificationMessage = "${fac.name} upgraded to Level ${fac.level + 1}!"
      )
    }
  }

  // Driver signing from market
  fun signScoutDriver(scoutDriver: Driver, replaceDriverId: String) {
    val signingFee = 1_200_000L
    if (_uiState.value.team.budget < signingFee) {
      showToast("Insufficient budget for signing fee ($1,200,000 required)")
      return
    }

    _uiState.update { current ->
      val updatedDrivers = current.drivers.map { d ->
        if (d.id == replaceDriverId) {
          scoutDriver.copy(id = replaceDriverId, isPlayer = true)
        } else d
      }
      val updatedScout = current.scoutMarket.filter { it.id != scoutDriver.id }

      current.copy(
        team = current.team.copy(budget = current.team.budget - signingFee),
        drivers = updatedDrivers,
        scoutMarket = updatedScout,
        notificationMessage = "Signed ${scoutDriver.name} to Apex Grand Prix!"
      )
    }
  }

  // Sponsors
  fun signSponsor(sponsorId: String) {
    _uiState.update { current ->
      val updated = current.sponsors.map {
        if (it.id == sponsorId) it.copy(isSigned = true)
        else it
      }
      current.copy(sponsors = updated, notificationMessage = "Sponsorship contract active!")
    }
  }

  private fun calculateStandings() {
    val allTeams = listOf(_uiState.value.team) + _uiState.value.rivalTeams
    val constructorStandings = allTeams.sortedByDescending { it.points }.mapIndexed { idx, tm ->
      ChampionshipStanding(
        rank = idx + 1,
        name = tm.name,
        teamName = tm.name,
        teamColor = tm.primaryColor,
        points = tm.points,
        wins = 0,
        podiums = 0
      )
    }

    val driverStandings = _uiState.value.drivers.sortedByDescending { d ->
      if (d.isPlayer) _uiState.value.team.points / 2
      else when (d.id) {
        "d3" -> 35
        "d5" -> 28
        "d7" -> 22
        "d9" -> 18
        "d11" -> 12
        else -> 6
      }
    }.mapIndexed { idx, drv ->
      val team = allTeams.find { it.id == (if (drv.isPlayer) "apex" else "scuderia") } ?: allTeams.first()
      ChampionshipStanding(
        rank = idx + 1,
        name = "${drv.name} #${drv.number}",
        teamName = team.shortName,
        teamColor = team.primaryColor,
        points = if (drv.isPlayer) _uiState.value.team.points / 2 else (25 - idx * 2).coerceAtLeast(0),
        wins = if (idx == 0) 1 else 0,
        podiums = if (idx < 3) 1 else 0
      )
    }

    _uiState.update {
      it.copy(
        constructorStandings = constructorStandings,
        driverStandings = driverStandings
      )
    }
  }

  private fun showToast(msg: String) {
    _uiState.update { it.copy(notificationMessage = msg) }
  }

  fun clearNotification() {
    _uiState.update { it.copy(notificationMessage = null) }
  }

  fun formatCurrency(amount: Long): String {
    return String.format("%,d", amount)
  }
}
