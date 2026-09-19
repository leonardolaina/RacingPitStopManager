package com.example

import com.example.data.model.*
import com.example.data.repository.MotorsportRepository
import com.example.domain.engine.RaceSimulationEngine
import com.example.ui.viewmodel.MotorsportViewModel
import org.junit.Assert.*
import org.junit.Test

class MotorsportSimulationTest {

  @Test
  fun testRaceSimulationStepAdvancesCars() {
    val track = MotorsportRepository.getCalendarTracks().first()
    val teams = MotorsportRepository.getInitialTeams()
    val drivers = MotorsportRepository.getInitialDrivers()
    val parts = MotorsportRepository.getInitialCarParts()

    val engine = RaceSimulationEngine(
      track = track,
      initialDrivers = drivers,
      initialTeams = teams,
      playerCarParts = parts,
      pitTrainingLevel = 2
    )

    assertEquals(20, engine.carStates.size)

    val initialProgress = engine.carStates.first().trackProgress
    // Advance 5 seconds of race simulation
    for (i in 0 until 10) {
      engine.update(0.5f)
    }

    val newProgress = engine.carStates.first().trackProgress
    assertTrue("Car progress should advance after simulation update", newProgress > initialProgress || engine.carStates.first().currentLap > 1)
  }

  @Test
  fun testEngineModesAlterTacticalPerformance() {
    val track = MotorsportRepository.getCalendarTracks().first()
    val teams = MotorsportRepository.getInitialTeams()
    val drivers = MotorsportRepository.getInitialDrivers()
    val parts = MotorsportRepository.getInitialCarParts()

    val engine = RaceSimulationEngine(
      track = track,
      initialDrivers = drivers,
      initialTeams = teams,
      playerCarParts = parts,
      pitTrainingLevel = 2
    )

    val playerCar = engine.carStates.first { it.isPlayer }

    // Test switching engine mode to OVERTAKE
    engine.setDriverEngineMode(playerCar.driver.id, EngineMode.OVERTAKE)
    assertEquals(EngineMode.OVERTAKE, playerCar.engineMode)

    // Test switching driving style to ATTACK
    engine.setDriverStyle(playerCar.driver.id, DrivingStyle.ATTACK)
    assertEquals(DrivingStyle.ATTACK, playerCar.drivingStyle)

    // Test ERS Boost activation
    engine.triggerDriverErs(playerCar.driver.id, ErsMode.BOOST)
    assertEquals(ErsMode.BOOST, playerCar.ersMode)
  }

  @Test
  fun testPitStopCallSchedulesNextCompound() {
    val track = MotorsportRepository.getCalendarTracks().first()
    val teams = MotorsportRepository.getInitialTeams()
    val drivers = MotorsportRepository.getInitialDrivers()
    val parts = MotorsportRepository.getInitialCarParts()

    val engine = RaceSimulationEngine(
      track = track,
      initialDrivers = drivers,
      initialTeams = teams,
      playerCarParts = parts,
      pitTrainingLevel = 3
    )

    val playerCar = engine.carStates.first { it.isPlayer }
    engine.callPitStop(playerCar.driver.id, TireCompound.HARD)

    assertTrue("Car should have pit scheduled next lap", playerCar.isPittingNextLap)
    assertEquals(TireCompound.HARD, playerCar.nextPitCompound)
  }

  @Test
  fun testCarPartPerformanceUpgrade() {
    val viewModel = MotorsportViewModel()
    val initialBudget = viewModel.uiState.value.team.budget
    val enginePartBefore = viewModel.uiState.value.carParts.first { it.type == PartType.ENGINE }
    val initialPerf = enginePartBefore.performance
    val expectedCost = PartType.ENGINE.baseResearchCost

    viewModel.upgradePartPerformance(PartType.ENGINE)

    val enginePartAfter = viewModel.uiState.value.carParts.first { it.type == PartType.ENGINE }
    val newBudget = viewModel.uiState.value.team.budget

    assertEquals(initialPerf + PartType.ENGINE.perfGain, enginePartAfter.performance)
    assertEquals(2, enginePartAfter.specLevel)
    assertEquals(initialBudget - expectedCost, newBudget)
  }

  @Test
  fun testAllCategoriesPresentInCarDevelopment() {
    val viewModel = MotorsportViewModel()
    val parts = viewModel.uiState.value.carParts
    assertEquals(8, parts.size)

    val categories = parts.map { it.type.category }.toSet()
    assertTrue(categories.contains(com.example.data.model.ComponentCategory.ENGINE))
    assertTrue(categories.contains(com.example.data.model.ComponentCategory.CHASSIS))
    assertTrue(categories.contains(com.example.data.model.ComponentCategory.TIRES))
    assertTrue(categories.contains(com.example.data.model.ComponentCategory.AERODYNAMICS))
  }

  @Test
  fun testFacilityUpgrade() {
    val viewModel = MotorsportViewModel()
    val initialBudget = viewModel.uiState.value.team.budget
    val facilityBefore = viewModel.uiState.value.facilities.first { it.id == "fac_factory" }
    val initialLevel = facilityBefore.level
    val upgradeCost = facilityBefore.upgradeCost

    viewModel.upgradeFacility("fac_factory")

    val facilityAfter = viewModel.uiState.value.facilities.first { it.id == "fac_factory" }
    val newBudget = viewModel.uiState.value.team.budget

    assertEquals(initialLevel + 1, facilityAfter.level)
    assertEquals(initialBudget - upgradeCost, newBudget)
  }

  @Test
  fun testDriverScoutSigning() {
    val viewModel = MotorsportViewModel()
    val initialBudget = viewModel.uiState.value.team.budget
    val scoutDriver = viewModel.uiState.value.scoutMarket.first()
    val replacedDriver = viewModel.uiState.value.drivers.first { it.isPlayer }

    viewModel.signScoutDriver(scoutDriver, replacedDriver.id)

    val updatedDrivers = viewModel.uiState.value.drivers
    val signedInTeam = updatedDrivers.find { it.name == scoutDriver.name }
    val oldDriverGone = updatedDrivers.none { it.name == replacedDriver.name }

    assertNotNull("New driver should be present in active roster", signedInTeam)
    assertTrue("New driver should be marked as player driver", signedInTeam!!.isPlayer)
    assertTrue("Old driver should be removed from active seat", oldDriverGone)
    assertEquals(initialBudget - 1_200_000L, viewModel.uiState.value.team.budget)
  }
}
