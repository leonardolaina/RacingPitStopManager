package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.MotorsportManagerTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MotorsportViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MotorsportManagerTheme {
        MotorsportApp()
      }
    }
  }
}

@Composable
fun MotorsportApp(
  viewModel: MotorsportViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.notificationMessage) {
    uiState.notificationMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearNotification()
    }
  }

  // Handle system back button to return to HQ if in a secondary screen
  if (uiState.currentScreen != AppScreen.HQ_HOME && uiState.currentScreen != AppScreen.LIVE_RACE) {
    BackHandler {
      viewModel.navigateTo(AppScreen.HQ_HOME)
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(CarbonBlack),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    containerColor = CarbonBlack
  ) { innerPadding ->
    val contentModifier = Modifier.padding(innerPadding)

    when (uiState.currentScreen) {
      AppScreen.HQ_HOME -> {
        HomeScreen(
          uiState = uiState,
          currentTrack = viewModel.getCurrentTrack(),
          onNavigate = { viewModel.navigateTo(it) },
          onStartPreRace = { viewModel.navigateTo(AppScreen.PRE_RACE) },
          modifier = contentModifier
        )
      }

      AppScreen.CAR_DEV -> {
        CarDevScreen(
          carParts = uiState.carParts,
          teamBudget = uiState.team.budget,
          onUpgradePerformance = { viewModel.upgradePartPerformance(it) },
          onImproveReliability = { viewModel.improvePartReliability(it) },
          onBack = { viewModel.navigateTo(AppScreen.HQ_HOME) },
          modifier = contentModifier
        )
      }

      AppScreen.FACILITIES -> {
        FacilitiesScreen(
          facilities = uiState.facilities,
          teamBudget = uiState.team.budget,
          onUpgradeFacility = { viewModel.upgradeFacility(it) },
          onBack = { viewModel.navigateTo(AppScreen.HQ_HOME) },
          modifier = contentModifier
        )
      }

      AppScreen.DRIVERS_STAFF -> {
        DriversScreen(
          playerDrivers = uiState.drivers,
          scoutMarket = uiState.scoutMarket,
          teamBudget = uiState.team.budget,
          onSignDriver = { scout, replaceId -> viewModel.signScoutDriver(scout, replaceId) },
          onBack = { viewModel.navigateTo(AppScreen.HQ_HOME) },
          modifier = contentModifier
        )
      }

      AppScreen.SPONSORS -> {
        SponsorsScreen(
          sponsors = uiState.sponsors,
          onSignSponsor = { viewModel.signSponsor(it) },
          onBack = { viewModel.navigateTo(AppScreen.HQ_HOME) },
          modifier = contentModifier
        )
      }

      AppScreen.STANDINGS -> {
        StandingsScreen(
          constructorStandings = uiState.constructorStandings,
          driverStandings = uiState.driverStandings,
          calendar = uiState.calendar,
          currentRound = uiState.currentRound,
          onBack = { viewModel.navigateTo(AppScreen.HQ_HOME) },
          modifier = contentModifier
        )
      }

      AppScreen.PRE_RACE -> {
        PreRaceScreen(
          track = viewModel.getCurrentTrack(),
          roundNumber = uiState.currentRound,
          totalRounds = uiState.totalRounds,
          playerDrivers = uiState.drivers.filter { it.isPlayer },
          driver1Compound = uiState.driver1StartCompound,
          driver2Compound = uiState.driver2StartCompound,
          onDriver1CompoundChange = { viewModel.setDriverStartCompound(1, it) },
          onDriver2CompoundChange = { viewModel.setDriverStartCompound(2, it) },
          onStartRace = { viewModel.startRaceWeekend() },
          onBack = { viewModel.navigateTo(AppScreen.HQ_HOME) },
          modifier = contentModifier
        )
      }

      AppScreen.LIVE_RACE -> {
        val engine = viewModel.raceEngine
        if (engine != null) {
          LiveRaceScreen(
            engine = engine,
            simSpeed = uiState.simSpeed,
            pitTrainingLevel = uiState.facilities.find { it.id == "fac_pit" }?.level ?: 2,
            onSpeedChange = { viewModel.setSimSpeed(it) },
            onEngineModeChange = { driverId, mode -> viewModel.setDriverEngineMode(driverId, mode) },
            onDrivingStyleChange = { driverId, style -> viewModel.setDriverStyle(driverId, style) },
            onErsBoost = { driverId, ersMode -> viewModel.triggerDriverErs(driverId, ersMode) },
            onCallPitStop = { driverId, compound -> viewModel.callPitStop(driverId, compound) },
            modifier = contentModifier
          )
        }
      }

      AppScreen.POST_RACE -> {
        PostRaceScreen(
          track = viewModel.getCurrentTrack(),
          results = uiState.latestResults,
          onAdvanceToNextRound = { viewModel.advanceToNextRaceWeekend() },
          modifier = contentModifier
        )
      }
    }
  }
}
