package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.*
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
  val mode: CalculatorMode = CalculatorMode.COMP,
  val angleUnit: AngleUnit = AngleUnit.DEG,
  val screenTheme: ScreenDisplayTheme = ScreenDisplayTheme.OLED_DARK,
  val expression: String = "",
  val cursorPosition: Int = 0,
  val resultText: String = "0",
  val fractionText: String? = null,
  val isFractionView: Boolean = false,
  val isShiftActive: Boolean = false,
  val isAlphaActive: Boolean = false,
  val isHypActive: Boolean = false,
  val hasMemoryStored: Boolean = false,
  val engShiftStep: Int = 0,
  val ansValue: Double = 0.0,
  val lastEvaluatedValue: Double = 0.0,
  val isError: Boolean = false,
  val errorMessage: String? = null,
  val history: List<CalculationHistory> = emptyList(),
  val historyIndex: Int = -1,
  val variables: Map<Char, Double> = mapOf(
    'A' to 0.0, 'B' to 0.0, 'C' to 0.0,
    'D' to 0.0, 'E' to 0.0, 'F' to 0.0,
    'X' to 0.0, 'Y' to 0.0, 'M' to 0.0
  ),
  val matrices: Map<String, MatrixModel> = mapOf(
    "MatA" to MatrixEngine.createZero("MatA", 3, 3),
    "MatB" to MatrixEngine.createZero("MatB", 3, 3),
    "MatC" to MatrixEngine.createZero("MatC", 3, 3),
    "MatAns" to MatrixEngine.createZero("MatAns", 3, 3)
  ),
  val vectors: Map<String, VectorModel> = mapOf(
    "VctA" to VectorModel("VctA", 3, 1.0, 2.0, 3.0),
    "VctB" to VectorModel("VctB", 3, 4.0, 5.0, 6.0),
    "VctC" to VectorModel("VctC", 3, 0.0, 1.0, 0.0),
    "VctAns" to VectorModel("VctAns", 3, 0.0, 0.0, 0.0)
  ),
  val soundHapticsEnabled: Boolean = true,
  val graphExpression: String = "sin(X)",
  val graphXMin: Double = -10.0,
  val graphXMax: Double = 10.0,
  val graphYMin: Double = -6.0,
  val graphYMax: Double = 6.0,
  val graphTraceX: Double? = null,
  val graphTableStep: Double = 1.0,
  // Stat data
  val statOneVarData: List<Pair<Double, Double>> = listOf(
    Pair(12.0, 1.0), Pair(15.0, 2.0), Pair(18.0, 3.0),
    Pair(20.0, 2.0), Pair(24.0, 1.0)
  ),
  val statTwoVarData: List<Pair<Double, Double>> = listOf(
    Pair(1.0, 2.1), Pair(2.0, 3.9), Pair(3.0, 6.2),
    Pair(4.0, 8.1), Pair(5.0, 10.2)
  ),
  val showCalculusDialog: Boolean = false,
  val showVariablesDialog: Boolean = false
)

class CasioCalculatorViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(CalculatorUiState())
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  // Central Key Press Handler
  fun onKeyClick(keyId: String) {
    val state = _uiState.value

    when (keyId) {
      "SHIFT" -> {
        _uiState.update { it.copy(isShiftActive = !it.isShiftActive) }
        return
      }
      "ALPHA" -> {
        _uiState.update { it.copy(isAlphaActive = !it.isAlphaActive) }
        return
      }
      "HYP" -> {
        _uiState.update { it.copy(isHypActive = !it.isHypActive) }
        return
      }
      "AC" -> {
        clearAll()
        return
      }
      "DEL" -> {
        deleteAtCursor()
        return
      }
      "=" -> {
        executeCalculate()
        return
      }
      "SD" -> {
        toggleFractionDecimal()
        return
      }
      "ENG" -> {
        cycleEngineering()
        return
      }
      "M_PLUS" -> {
        addToMemory(1.0)
        return
      }
      "M_MINUS" -> {
        addToMemory(-1.0)
        return
      }
      "CURSOR_LEFT" -> {
        moveCursor(-1)
        return
      }
      "CURSOR_RIGHT" -> {
        moveCursor(1)
        return
      }
      "CURSOR_UP" -> {
        navigateHistory(-1)
        return
      }
      "CURSOR_DOWN" -> {
        navigateHistory(1)
        return
      }
      "MODE_TOGGLE" -> {
        cycleMode()
        return
      }
      "ANGLE_CYCLE" -> {
        cycleAngleUnit()
        return
      }
    }

    // Handle SHIFT numeric shortcuts like a real Casio fx-991
    if (state.isShiftActive) {
      when (keyId) {
        "1" -> {
          _uiState.update { it.copy(isShiftActive = false, mode = CalculatorMode.STAT) }
          return
        }
        "2" -> {
          _uiState.update { it.copy(isShiftActive = false, mode = CalculatorMode.CMPLX) }
          return
        }
        "3" -> {
          _uiState.update { it.copy(isShiftActive = false, mode = CalculatorMode.BASE_N) }
          return
        }
        "4" -> {
          _uiState.update { it.copy(isShiftActive = false, mode = CalculatorMode.MATRIX) }
          return
        }
        "5" -> {
          _uiState.update { it.copy(isShiftActive = false, mode = CalculatorMode.VECTOR) }
          return
        }
        "6" -> {
          _uiState.update { it.copy(isShiftActive = false, showCalculusDialog = true) }
          return
        }
        "7", "8" -> {
          _uiState.update { it.copy(isShiftActive = false, mode = CalculatorMode.CONV) }
          return
        }
        "9" -> {
          _uiState.update { it.copy(isShiftActive = false) }
          clearAll()
          return
        }
        "STO" -> {
          _uiState.update { it.copy(isShiftActive = false, showVariablesDialog = true) }
          return
        }
      }
    }

    // Direct character insertion from character bar or keys
    if (keyId in listOf("A", "B", "C", "D", "E", "F", "X", "Y", "M", "r", "θ", "π", "e", ":", ",")) {
      insertToken(keyId)
      _uiState.update { it.copy(isShiftActive = false, isAlphaActive = false) }
      return
    }

    // Determine token based on SHIFT or ALPHA modifiers
    val tokenToInsert = resolveToken(keyId, state.isShiftActive, state.isAlphaActive, state.isHypActive)

    if (tokenToInsert.isNotEmpty()) {
      insertToken(tokenToInsert)
      // Reset SHIFT and ALPHA after use like a real Casio
      _uiState.update { it.copy(isShiftActive = false, isAlphaActive = false) }
    }
  }

  private fun resolveToken(keyId: String, isShift: Boolean, isAlpha: Boolean, isHyp: Boolean): String {
    if (isShift) {
      return when (keyId) {
        "x_inv" -> "!"
        "sqrt" -> "∛("
        "x2" -> "³"
        "power" -> "^(1/"
        "log" -> "10^("
        "ln" -> "e^("
        "neg" -> "°"
        "hyp" -> "asinh("
        "sin" -> if (isHyp) "sinh⁻¹(" else "sin⁻¹("
        "cos" -> if (isHyp) "cosh⁻¹(" else "cos⁻¹("
        "tan" -> if (isHyp) "tanh⁻¹(" else "tan⁻¹("
        "ncr" -> "nPr("
        "left_paren" -> "Pol("
        "right_paren" -> "Rec("
        "add" -> "Pol("
        "sub" -> "Rec("
        "mul" -> "gcd("
        "div" -> "lcm("
        "M_PLUS" -> "M-"
        "pow10" -> "π"
        "Ans" -> "%"
        "dot" -> "Ran#"
        "0" -> "Rnd("
        "=" -> "≈"
        "DEL" -> "" // INS mode toggle
        "AC" -> "" // OFF
        "STO" -> "RCL"
        "ENG" -> ""
        "SD" -> ""
        else -> keyId
      }
    }

    if (isAlpha) {
      return when (keyId) {
        "x_inv" -> "A"
        "sqrt" -> "B"
        "x2" -> "C"
        "power" -> "D"
        "log" -> "E"
        "ln" -> "F"
        "neg" -> "X"
        "hyp" -> "Y"
        "sin" -> "M"
        "cos" -> "r"
        "tan" -> "θ"
        "pow10" -> "e"
        "left_paren" -> ":"
        "right_paren" -> ","
        "calc", "=" -> "="
        "Ans" -> "Ans"
        else -> ""
      }
    }

    // Normal or Hyperbolic Key
    return when (keyId) {
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> keyId
      "dot" -> "."
      "add" -> "+"
      "sub" -> "−"
      "mul" -> "×"
      "div" -> "÷"
      "pow10" -> "×10^"
      "Ans" -> "Ans"
      "left_paren" -> "("
      "right_paren" -> ")"
      "x2" -> "²"
      "power" -> "^"
      "sqrt" -> "√("
      "log" -> "log("
      "ln" -> "ln("
      "sin" -> if (isHyp) "sinh(" else "sin("
      "cos" -> if (isHyp) "cosh(" else "cos("
      "tan" -> if (isHyp) "tanh(" else "tan("
      "frac" -> "/"
      "x_inv" -> "⁻¹"
      "neg" -> "(-)"
      "fact" -> "!"
      "ncr" -> "nCr("
      "abs" -> "abs("
      "pi" -> "π"
      "e" -> "e"
      else -> keyId
    }
  }

  fun insertToken(token: String) {
    _uiState.update { current ->
      val expr = current.expression
      val pos = current.cursorPosition.coerceIn(0, expr.length)
      val newExpr = expr.substring(0, pos) + token + expr.substring(pos)
      val newPos = pos + token.length
      current.copy(
        expression = newExpr,
        cursorPosition = newPos,
        isError = false,
        errorMessage = null
      )
    }
  }

  fun dismissDialogs() {
    _uiState.update { it.copy(showCalculusDialog = false, showVariablesDialog = false) }
  }

  fun clearAll() {
    _uiState.update {
      it.copy(
        expression = "",
        cursorPosition = 0,
        resultText = "0",
        fractionText = null,
        isFractionView = false,
        isShiftActive = false,
        isAlphaActive = false,
        isHypActive = false,
        isError = false,
        errorMessage = null,
        historyIndex = -1
      )
    }
  }

  fun deleteAtCursor() {
    _uiState.update { current ->
      val expr = current.expression
      val pos = current.cursorPosition
      if (pos > 0 && expr.isNotEmpty()) {
        val newExpr = expr.substring(0, pos - 1) + expr.substring(pos)
        current.copy(
          expression = newExpr,
          cursorPosition = pos - 1,
          isError = false
        )
      } else {
        current
      }
    }
  }

  fun moveCursor(delta: Int) {
    _uiState.update { current ->
      val newPos = (current.cursorPosition + delta).coerceIn(0, current.expression.length)
      current.copy(cursorPosition = newPos)
    }
  }

  fun setCursorPosition(newPos: Int) {
    _uiState.update { current ->
      current.copy(cursorPosition = newPos.coerceIn(0, current.expression.length))
    }
  }

  fun executeCalculate() {
    val state = _uiState.value
    if (state.expression.isBlank()) return

    val eval = MathEvaluator.evaluate(
      expression = state.expression,
      angleUnit = state.angleUnit,
      ansValue = state.ansValue,
      variables = state.variables
    )

    if (eval.isError) {
      _uiState.update {
        it.copy(
          isError = true,
          errorMessage = eval.errorMessage,
          resultText = eval.errorMessage ?: "Math ERROR"
        )
      }
    } else {
      val newHistory = CalculationHistory(
        expression = state.expression,
        result = eval.formattedDecimal,
        angleUnit = state.angleUnit,
        mode = state.mode
      )
      val updatedHistory = listOf(newHistory) + state.history.take(49)

      _uiState.update {
        it.copy(
          resultText = eval.formattedDecimal,
          fractionText = eval.fractionRepresentation,
          isFractionView = false,
          ansValue = eval.decimalValue,
          lastEvaluatedValue = eval.decimalValue,
          history = updatedHistory,
          historyIndex = -1,
          isError = false,
          errorMessage = null,
          engShiftStep = 0
        )
      }
    }
  }

  fun toggleFractionDecimal() {
    _uiState.update { current ->
      val frac = current.fractionText
      if (frac != null) {
        val nextView = !current.isFractionView
        current.copy(
          isFractionView = nextView,
          resultText = if (nextView) frac else MathEvaluator.formatDecimal(current.lastEvaluatedValue)
        )
      } else {
        current
      }
    }
  }

  fun cycleEngineering() {
    _uiState.update { current ->
      val nextStep = if (current.engShiftStep >= 2) 0 else current.engShiftStep + 1
      val eng = MathEvaluator.toEngineering(current.lastEvaluatedValue, nextStep)
      current.copy(
        engShiftStep = nextStep,
        resultText = eng
      )
    }
  }

  fun addToMemory(sign: Double) {
    val state = _uiState.value
    val eval = if (state.expression.isNotBlank()) {
      MathEvaluator.evaluate(state.expression, state.angleUnit, state.ansValue, state.variables).decimalValue
    } else state.lastEvaluatedValue

    if (!eval.isNaN() && !eval.isInfinite()) {
      val currentM = state.variables['M'] ?: 0.0
      val newM = currentM + (sign * eval)
      val newVars = state.variables.toMutableMap()
      newVars['M'] = newM
      _uiState.update {
        it.copy(
          variables = newVars,
          hasMemoryStored = kotlin.math.abs(newM) > 1e-12
        )
      }
    }
  }

  fun storeVariable(variable: Char, value: Double) {
    val newVars = _uiState.value.variables.toMutableMap()
    newVars[variable] = value
    _uiState.update {
      it.copy(
        variables = newVars,
        hasMemoryStored = kotlin.math.abs(newVars['M'] ?: 0.0) > 1e-12
      )
    }
  }

  fun navigateHistory(delta: Int) {
    val state = _uiState.value
    if (state.history.isEmpty()) return

    val nextIndex = (state.historyIndex + delta).coerceIn(0, state.history.size - 1)
    val item = state.history[nextIndex]
    _uiState.update {
      it.copy(
        historyIndex = nextIndex,
        expression = item.expression,
        cursorPosition = item.expression.length,
        resultText = item.result
      )
    }
  }

  fun recallHistoryItem(item: CalculationHistory) {
    _uiState.update {
      it.copy(
        expression = item.expression,
        cursorPosition = item.expression.length,
        resultText = item.result,
        historyIndex = -1
      )
    }
  }

  fun setMode(mode: CalculatorMode) {
    _uiState.update { it.copy(mode = mode) }
  }

  fun cycleMode() {
    val modes = CalculatorMode.values()
    val nextOrdinal = (_uiState.value.mode.ordinal + 1) % modes.size
    _uiState.update { it.copy(mode = modes[nextOrdinal]) }
  }

  fun cycleAngleUnit() {
    val units = AngleUnit.values()
    val nextOrdinal = (_uiState.value.angleUnit.ordinal + 1) % units.size
    _uiState.update { it.copy(angleUnit = units[nextOrdinal]) }
  }

  fun setAngleUnit(unit: AngleUnit) {
    _uiState.update { it.copy(angleUnit = unit) }
  }

  fun setScreenTheme(theme: ScreenDisplayTheme) {
    _uiState.update { it.copy(screenTheme = theme) }
  }

  // Matrix actions
  fun updateMatrix(name: String, rows: Int, cols: Int, elements: List<Double>) {
    val newMats = _uiState.value.matrices.toMutableMap()
    newMats[name] = MatrixModel(name, rows, cols, elements)
    _uiState.update { it.copy(matrices = newMats) }
  }

  // Graph actions
  fun setGraphExpression(expr: String) {
    _uiState.update { it.copy(graphExpression = expr) }
  }

  fun setGraphRange(xMin: Double, xMax: Double, yMin: Double, yMax: Double) {
    _uiState.update {
      it.copy(
        graphXMin = xMin,
        graphXMax = xMax,
        graphYMin = yMin,
        graphYMax = yMax
      )
    }
  }

  fun setGraphTrace(x: Double?) {
    _uiState.update { it.copy(graphTraceX = x) }
  }

  // Vector actions
  fun updateVector(name: String, model: VectorModel) {
    val newVcts = _uiState.value.vectors.toMutableMap()
    newVcts[name] = model
    _uiState.update { it.copy(vectors = newVcts) }
  }

  fun clearAllVariables() {
    val cleared = _uiState.value.variables.keys.associateWith { 0.0 }
    _uiState.update { it.copy(variables = cleared, hasMemoryStored = false) }
  }

  fun toggleSoundHaptics() {
    _uiState.update { it.copy(soundHapticsEnabled = !it.soundHapticsEnabled) }
  }

  // Statistics actions
  fun updateOneVarData(list: List<Pair<Double, Double>>) {
    _uiState.update { it.copy(statOneVarData = list) }
  }

  fun updateTwoVarData(list: List<Pair<Double, Double>>) {
    _uiState.update { it.copy(statTwoVarData = list) }
  }
}
