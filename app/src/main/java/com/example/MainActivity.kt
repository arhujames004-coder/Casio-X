package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CalculatorMode
import com.example.ui.components.CasioKeypad
import com.example.ui.components.CasioScreen
import com.example.ui.modes.*
import com.example.ui.theme.*
import com.example.viewmodel.CasioCalculatorViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: CasioCalculatorViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        CasioCalculatorApp(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasioCalculatorApp(viewModel: CasioCalculatorViewModel) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  var showHistoryDialog by remember { mutableStateOf(false) }
  var showModeDialog by remember { mutableStateOf(false) }
  var showVariablesDialog by remember { mutableStateOf(false) }
  var showCalculusDialog by remember { mutableStateOf(false) }

  // Back handler to return to COMP mode when in other modes
  if (state.mode != CalculatorMode.COMP) {
    BackHandler {
      viewModel.setMode(CalculatorMode.COMP)
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(CasioChassisDark),
    containerColor = CasioChassisDark,
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { showModeDialog = true }
          ) {
            Text(
              text = "CASIO",
              fontFamily = PlusJakartaSansFontFamily,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = CasioChassisHighlight,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "fx-991EX",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = CasioShiftGold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        },
        actions = {
          // Angle Unit Toggle Button
          TextButton(
            onClick = { viewModel.cycleAngleUnit() },
            modifier = Modifier.testTag("app_bar_angle_btn")
          ) {
            Text(
              text = "[${state.angleUnit.badge}]",
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = CasioShiftGold
            )
          }

          // Calculus Dialog Button
          IconButton(
            onClick = { showCalculusDialog = true },
            modifier = Modifier.testTag("app_bar_calculus_btn")
          ) {
            Text(
              text = "∫dx",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF00E5FF)
            )
          }

          // STO / RCL Variables Button
          IconButton(
            onClick = { showVariablesDialog = true },
            modifier = Modifier.testTag("app_bar_vars_btn")
          ) {
            Text(
              text = "STO",
              fontFamily = PlusJakartaSansFontFamily,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (state.hasMemoryStored) Color(0xFF00E5FF) else CasioKeyTextSecondary
            )
          }

          // History Button
          IconButton(
            onClick = { showHistoryDialog = true },
            modifier = Modifier.testTag("app_bar_history_btn")
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = "History",
              tint = CasioKeyTextPrimary
            )
          }

          // Setup / Mode Menu Button
          IconButton(
            onClick = { showModeDialog = true },
            modifier = Modifier.testTag("app_bar_setup_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Mode Setup",
              tint = CasioKeyTextPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = CasioChassisSurface
        ),
        windowInsets = WindowInsets.statusBars
      )
    },
    bottomBar = {
      // Quick Mode Switcher Bar at the bottom
      Surface(
        color = CasioChassisSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        val modes = CalculatorMode.values()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          modes.forEach { m ->
            val isSelected = state.mode == m
            Surface(
              onClick = { viewModel.setMode(m) },
              color = if (isSelected) CasioShiftGold else CasioChassisDark,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isSelected) CasioShiftGold else CasioChassisBorder),
              modifier = Modifier.testTag("nav_mode_${m.label}")
            ) {
              Text(
                text = m.label,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 540.dp)
          .align(Alignment.TopCenter)
      ) {
        when (state.mode) {
          CalculatorMode.COMP -> {
            // Standard Scientific Natural V.P.A.M. Mode
            BoxWithConstraints(
              modifier = Modifier.fillMaxSize()
            ) {
              val availableH = maxHeight
              val isShortScreen = availableH < 530.dp

              Column(
                modifier = if (isShortScreen) {
                  Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                } else {
                  Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                },
                verticalArrangement = if (isShortScreen) Arrangement.Top else Arrangement.SpaceBetween
              ) {
                // LCD Screen
                CasioScreen(
                  expression = state.expression,
                  cursorPosition = state.cursorPosition,
                  resultText = state.resultText,
                  fractionText = state.fractionText,
                  isFractionView = state.isFractionView,
                  angleUnit = state.angleUnit,
                  mode = state.mode,
                  isShiftActive = state.isShiftActive,
                  isAlphaActive = state.isAlphaActive,
                  hasMemory = state.hasMemoryStored,
                  isError = state.isError,
                  screenTheme = state.screenTheme,
                  onCursorPositionChange = { viewModel.setCursorPosition(it) },
                  onAngleUnitClick = { viewModel.cycleAngleUnit() },
                  onFractionToggleClick = { viewModel.toggleFractionDecimal() },
                  modifier = Modifier.padding(bottom = 2.dp)
                )

                if (isShortScreen) {
                  Spacer(modifier = Modifier.height(4.dp))
                }

                // Physical Styled Keypad
                CasioKeypad(
                  isShiftActive = state.isShiftActive,
                  isAlphaActive = state.isAlphaActive,
                  soundHapticsEnabled = state.soundHapticsEnabled,
                  onKeyClick = { viewModel.onKeyClick(it) },
                  modifier = if (isShortScreen) Modifier.fillMaxWidth() else Modifier.weight(1f, fill = false)
                )
              }
            }
          }
        CalculatorMode.CMPLX -> {
          ComplexScreen(
            angleUnit = state.angleUnit,
            onInsertResult = { inserted ->
              viewModel.insertToken(inserted)
              viewModel.setMode(CalculatorMode.COMP)
            },
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.GRAPH -> {
          GraphingScreen(
            initialExpression = state.graphExpression,
            onExpressionChange = { viewModel.setGraphExpression(it) },
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.MATRIX -> {
          MatrixScreen(
            matrices = state.matrices,
            onUpdateMatrix = { name, r, c, list ->
              viewModel.updateMatrix(name, r, c, list)
            },
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.VECTOR -> {
          VectorScreen(
            vectors = state.vectors,
            angleUnit = state.angleUnit,
            onUpdateVector = { name, model ->
              viewModel.updateVector(name, model)
            },
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.EQN -> {
          EquationScreen(
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.STAT -> {
          StatisticsScreen(
            initialOneVar = state.statOneVarData,
            initialTwoVar = state.statTwoVarData,
            onOneVarChange = { viewModel.updateOneVarData(it) },
            onTwoVarChange = { viewModel.updateTwoVarData(it) },
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.BASE_N -> {
          BaseNScreen(
            modifier = Modifier.fillMaxSize()
          )
        }
        CalculatorMode.CONV -> {
          UnitConverterScreen(
            onInsertIntoCalculator = { inserted ->
              viewModel.insertToken(inserted)
              viewModel.setMode(CalculatorMode.COMP)
            },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}

  // Dialogs
  if (showHistoryDialog) {
    HistoryDialog(
      history = state.history,
      onRecallItem = { item -> viewModel.recallHistoryItem(item) },
      onDismiss = { showHistoryDialog = false }
    )
  }

  if (showModeDialog) {
    ModeMenuDialog(
      currentMode = state.mode,
      currentTheme = state.screenTheme,
      currentAngleUnit = state.angleUnit,
      soundHapticsEnabled = state.soundHapticsEnabled,
      onSelectMode = { viewModel.setMode(it) },
      onSelectTheme = { viewModel.setScreenTheme(it) },
      onSelectAngleUnit = { viewModel.setAngleUnit(it) },
      onToggleSoundHaptics = { viewModel.toggleSoundHaptics() },
      onOpenVariables = { showVariablesDialog = true },
      onOpenCalculus = { showCalculusDialog = true },
      onDismiss = { showModeDialog = false }
    )
  }

  if (showVariablesDialog || state.showVariablesDialog) {
    VariablesDialog(
      variables = state.variables,
      ansValue = state.ansValue,
      onStoreVariable = { ch, v -> viewModel.storeVariable(ch, v) },
      onRecallVariable = { ch -> viewModel.insertToken(ch.toString()) },
      onClearAllVariables = { viewModel.clearAllVariables() },
      onDismiss = {
        showVariablesDialog = false
        viewModel.dismissDialogs()
      }
    )
  }

  if (showCalculusDialog || state.showCalculusDialog) {
    CalculusDialog(
      angleUnit = state.angleUnit,
      onInsertResult = { res -> viewModel.insertToken(res) },
      onDismiss = {
        showCalculusDialog = false
        viewModel.dismissDialogs()
      }
    )
  }
}
