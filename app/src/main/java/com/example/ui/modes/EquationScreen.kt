package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EquationSolverEngine
import com.example.model.EquationType
import com.example.ui.theme.*

@Composable
fun EquationScreen(modifier: Modifier = Modifier) {
  var selectedType by remember { mutableStateOf(EquationType.QUADRATIC) }

  // Quadratic coefficients: ax² + bx + c = 0
  var quadA by remember { mutableStateOf("1") }
  var quadB by remember { mutableStateOf("-5") }
  var quadC by remember { mutableStateOf("6") }
  var quadResult by remember { mutableStateOf<EquationSolverEngine.QuadraticResult?>(null) }

  // Simultaneous 2: a1x + b1y = c1; a2x + b2y = c2
  var s2A1 by remember { mutableStateOf("2") }
  var s2B1 by remember { mutableStateOf("3") }
  var s2C1 by remember { mutableStateOf("8") }
  var s2A2 by remember { mutableStateOf("1") }
  var s2B2 by remember { mutableStateOf("-1") }
  var s2C2 by remember { mutableStateOf("-1") }
  var s2Result by remember { mutableStateOf<EquationSolverEngine.Simultaneous2Result?>(null) }

  // Cubic coefficients: ax³ + bx² + cx + d = 0
  var cubicA by remember { mutableStateOf("1") }
  var cubicB by remember { mutableStateOf("-6") }
  var cubicC by remember { mutableStateOf("11") }
  var cubicD by remember { mutableStateOf("-6") }
  var cubicResult by remember { mutableStateOf<EquationSolverEngine.CubicResult?>(null) }

  // Newton solver: f(x) = 0
  var newtonExpr by remember { mutableStateOf("X^3 - 2*X - 5") }
  var newtonGuess by remember { mutableStateOf("2.0") }
  var newtonResult by remember { mutableStateOf<EquationSolverEngine.NewtonResult?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    // 1. Equation Type Selector Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(EquationType.values()) { type ->
        val isSelected = selectedType == type
        FilterChip(
          selected = isSelected,
          onClick = { selectedType = type },
          label = {
            Text(
              text = when (type) {
                EquationType.QUADRATIC -> "ax² + bx + c = 0"
                EquationType.SIMULTANEOUS_2 -> "2 Unknowns (x, y)"
                EquationType.SIMULTANEOUS_3 -> "3 Unknowns (x, y, z)"
                EquationType.CUBIC -> "ax³ + bx² + cx + d = 0"
                EquationType.NEWTON_SOLVER -> "f(x) = 0 Solver"
              },
              fontSize = 11.sp,
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CasioShiftGold,
            selectedLabelColor = Color.Black,
            containerColor = CasioChassisSurface,
            labelColor = CasioKeyTextSecondary
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) CasioShiftGold else CasioChassisBorder
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Equation Form & Inputs
    when (selectedType) {
      EquationType.QUADRATIC -> {
        QuadraticSection(
          a = quadA, b = quadB, c = quadC,
          onAChange = { quadA = it }, onBChange = { quadB = it }, onCChange = { quadC = it },
          onSolve = {
            val aVal = quadA.toDoubleOrNull() ?: 1.0
            val bVal = quadB.toDoubleOrNull() ?: 0.0
            val cVal = quadC.toDoubleOrNull() ?: 0.0
            quadResult = EquationSolverEngine.solveQuadratic(aVal, bVal, cVal)
          },
          result = quadResult
        )
      }
      EquationType.SIMULTANEOUS_2 -> {
        Simultaneous2Section(
          a1 = s2A1, b1 = s2B1, c1 = s2C1,
          a2 = s2A2, b2 = s2B2, c2 = s2C2,
          onA1 = { s2A1 = it }, onB1 = { s2B1 = it }, onC1 = { s2C1 = it },
          onA2 = { s2A2 = it }, onB2 = { s2B2 = it }, onC2 = { s2C2 = it },
          onSolve = {
            s2Result = EquationSolverEngine.solveSimultaneous2(
              s2A1.toDoubleOrNull() ?: 1.0, s2B1.toDoubleOrNull() ?: 1.0, s2C1.toDoubleOrNull() ?: 0.0,
              s2A2.toDoubleOrNull() ?: 1.0, s2B2.toDoubleOrNull() ?: 1.0, s2C2.toDoubleOrNull() ?: 0.0
            )
          },
          result = s2Result
        )
      }
      EquationType.CUBIC -> {
        CubicSection(
          a = cubicA, b = cubicB, c = cubicC, d = cubicD,
          onA = { cubicA = it }, onB = { cubicB = it }, onC = { cubicC = it }, onD = { cubicD = it },
          onSolve = {
            cubicResult = EquationSolverEngine.solveCubic(
              cubicA.toDoubleOrNull() ?: 1.0, cubicB.toDoubleOrNull() ?: 0.0,
              cubicC.toDoubleOrNull() ?: 0.0, cubicD.toDoubleOrNull() ?: 0.0
            )
          },
          result = cubicResult
        )
      }
      EquationType.NEWTON_SOLVER -> {
        NewtonSection(
          expr = newtonExpr, guess = newtonGuess,
          onExprChange = { newtonExpr = it }, onGuessChange = { newtonGuess = it },
          onSolve = {
            val g = newtonGuess.toDoubleOrNull() ?: 1.0
            newtonResult = EquationSolverEngine.solveNewton(newtonExpr, g)
          },
          result = newtonResult
        )
      }
      EquationType.SIMULTANEOUS_3 -> {
        Simultaneous3Section()
      }
    }
  }
}

@Composable
private fun QuadraticSection(
  a: String, b: String, c: String,
  onAChange: (String) -> Unit, onBChange: (String) -> Unit, onCChange: (String) -> Unit,
  onSolve: () -> Unit,
  result: EquationSolverEngine.QuadraticResult?
) {
  Surface(
    color = CasioChassisSurface,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "ax² + bx + c = 0",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = CasioShiftGold
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CoeffField(label = "a", value = a, onValueChange = onAChange, modifier = Modifier.weight(1f))
        CoeffField(label = "b", value = b, onValueChange = onBChange, modifier = Modifier.weight(1f))
        CoeffField(label = "c", value = c, onValueChange = onCChange, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onSolve,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("solve_quadratic_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Solve Quadratic Equation", fontWeight = FontWeight.Bold)
      }

      if (result != null) {
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CasioChassisBorder)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Discriminant (Δ = b² - 4ac) = ${String.format(java.util.Locale.US, "%.4f", result.discriminant)}",
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = 12.sp,
          color = CasioKeyTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        SolutionBox(label = "Root x₁", value = result.root1.toRectangularString())
        Spacer(modifier = Modifier.height(6.dp))
        SolutionBox(label = "Root x₂", value = result.root2.toRectangularString())

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Parabola Vertex (${if (result.isVertexMin) "Minimum" else "Maximum"}): (${String.format(java.util.Locale.US, "%.3f", result.vertexX)}, ${String.format(java.util.Locale.US, "%.3f", result.vertexY)})",
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = 12.sp,
          color = Color(0xFF00E5FF)
        )
      }
    }
  }
}

@Composable
private fun Simultaneous2Section(
  a1: String, b1: String, c1: String,
  a2: String, b2: String, c2: String,
  onA1: (String) -> Unit, onB1: (String) -> Unit, onC1: (String) -> Unit,
  onA2: (String) -> Unit, onB2: (String) -> Unit, onC2: (String) -> Unit,
  onSolve: () -> Unit,
  result: EquationSolverEngine.Simultaneous2Result?
) {
  Surface(
    color = CasioChassisSurface,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "a₁x + b₁y = c₁\na₂x + b₂y = c₂",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CasioShiftGold
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text("Equation 1:", color = CasioKeyTextSecondary, fontSize = 12.sp)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CoeffField(label = "a₁", value = a1, onValueChange = onA1, modifier = Modifier.weight(1f))
        CoeffField(label = "b₁", value = b1, onValueChange = onB1, modifier = Modifier.weight(1f))
        CoeffField(label = "c₁", value = c1, onValueChange = onC1, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text("Equation 2:", color = CasioKeyTextSecondary, fontSize = 12.sp)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CoeffField(label = "a₂", value = a2, onValueChange = onA2, modifier = Modifier.weight(1f))
        CoeffField(label = "b₂", value = b2, onValueChange = onB2, modifier = Modifier.weight(1f))
        CoeffField(label = "c₂", value = c2, onValueChange = onC2, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onSolve,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Solve Simultaneous System", fontWeight = FontWeight.Bold)
      }

      if (result != null) {
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = CasioChassisBorder)
        Spacer(modifier = Modifier.height(10.dp))

        if (result.isInfiniteSolutions) {
          Text("Infinite Solutions (Coincident Lines)", color = CasioShiftGold, fontFamily = JetBrainsMonoFontFamily)
        } else if (result.isNoSolution) {
          Text("No Solution (Parallel Lines)", color = CasioAlphaRuby, fontFamily = JetBrainsMonoFontFamily)
        } else {
          SolutionBox(label = "x", value = String.format(java.util.Locale.US, "%.5f", result.x).trimEnd('0').trimEnd('.'))
          Spacer(modifier = Modifier.height(6.dp))
          SolutionBox(label = "y", value = String.format(java.util.Locale.US, "%.5f", result.y).trimEnd('0').trimEnd('.'))
        }
      }
    }
  }
}

@Composable
private fun CubicSection(
  a: String, b: String, c: String, d: String,
  onA: (String) -> Unit, onB: (String) -> Unit, onC: (String) -> Unit, onD: (String) -> Unit,
  onSolve: () -> Unit,
  result: EquationSolverEngine.CubicResult?
) {
  Surface(
    color = CasioChassisSurface,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "ax³ + bx² + cx + d = 0",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CasioShiftGold
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CoeffField(label = "a", value = a, onValueChange = onA, modifier = Modifier.weight(1f))
        CoeffField(label = "b", value = b, onValueChange = onB, modifier = Modifier.weight(1f))
        CoeffField(label = "c", value = c, onValueChange = onC, modifier = Modifier.weight(1f))
        CoeffField(label = "d", value = d, onValueChange = onD, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onSolve,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Solve Cubic Equation", fontWeight = FontWeight.Bold)
      }

      if (result != null) {
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = CasioChassisBorder)
        Spacer(modifier = Modifier.height(10.dp))

        SolutionBox(label = "Root x₁", value = result.root1.toRectangularString())
        Spacer(modifier = Modifier.height(6.dp))
        SolutionBox(label = "Root x₂", value = result.root2.toRectangularString())
        Spacer(modifier = Modifier.height(6.dp))
        SolutionBox(label = "Root x₃", value = result.root3.toRectangularString())
      }
    }
  }
}

@Composable
private fun NewtonSection(
  expr: String,
  guess: String,
  onExprChange: (String) -> Unit,
  onGuessChange: (String) -> Unit,
  onSolve: () -> Unit,
  result: EquationSolverEngine.NewtonResult?
) {
  Surface(
    color = CasioChassisSurface,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "f(x) = 0 Root Finder",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CasioShiftGold
      )
      Text(
        text = "Solves arbitrary algebraic equations numerically using Newton-Raphson",
        color = CasioKeyTextSecondary,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = expr,
        onValueChange = onExprChange,
        label = { Text("f(X) =", color = CasioKeyTextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = CasioKeyTextPrimary,
          unfocusedTextColor = CasioKeyTextPrimary,
          focusedBorderColor = CasioShiftGold,
          unfocusedBorderColor = CasioChassisBorder
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = guess,
        onValueChange = onGuessChange,
        label = { Text("Initial Guess x₀ =", color = CasioKeyTextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = CasioKeyTextPrimary,
          unfocusedTextColor = CasioKeyTextPrimary,
          focusedBorderColor = CasioShiftGold,
          unfocusedBorderColor = CasioChassisBorder
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
      )

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onSolve,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Find Root Numerically", fontWeight = FontWeight.Bold)
      }

      if (result != null) {
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = CasioChassisBorder)
        Spacer(modifier = Modifier.height(10.dp))

        if (result.converged) {
          SolutionBox(label = "Root x*", value = String.format(java.util.Locale.US, "%.7f", result.root))
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Converged in ${result.iterations} iterations (f(x*) = ${String.format(java.util.Locale.US, "%.2e", result.fxAtRoot)})",
            color = Color(0xFF00E5FF),
            fontSize = 11.sp,
            fontFamily = JetBrainsMonoFontFamily
          )
        } else {
          Text(
            text = "Did not converge within max iterations. Try another initial guess.",
            color = CasioAlphaRuby,
            fontSize = 12.sp,
            fontFamily = JetBrainsMonoFontFamily
          )
        }
      }
    }
  }
}

@Composable
private fun Simultaneous3Section() {
  var a1 by remember { mutableStateOf("1") }
  var b1 by remember { mutableStateOf("2") }
  var c1 by remember { mutableStateOf("3") }
  var d1 by remember { mutableStateOf("14") }

  var a2 by remember { mutableStateOf("2") }
  var b2 by remember { mutableStateOf("-1") }
  var c2 by remember { mutableStateOf("1") }
  var d2 by remember { mutableStateOf("3") }

  var a3 by remember { mutableStateOf("3") }
  var b3 by remember { mutableStateOf("1") }
  var c3 by remember { mutableStateOf("-1") }
  var d3 by remember { mutableStateOf("2") }

  var result by remember { mutableStateOf<EquationSolverEngine.Simultaneous3Result?>(null) }

  Surface(
    color = CasioChassisSurface,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "3 Unknowns Linear System",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        color = CasioShiftGold
      )
      Text(
        text = "aₙx + bₙy + cₙz = dₙ",
        color = CasioKeyTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = JetBrainsMonoFontFamily
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Equation 1
      Text("Eq 1: a₁x + b₁y + c₁z = d₁", fontSize = 11.sp, color = CasioKeyTextSecondary, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        CoeffField(label = "a₁", value = a1, onValueChange = { a1 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "b₁", value = b1, onValueChange = { b1 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "c₁", value = c1, onValueChange = { c1 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "d₁", value = d1, onValueChange = { d1 = it }, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Equation 2
      Text("Eq 2: a₂x + b₂y + c₂z = d₂", fontSize = 11.sp, color = CasioKeyTextSecondary, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        CoeffField(label = "a₂", value = a2, onValueChange = { a2 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "b₂", value = b2, onValueChange = { b2 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "c₂", value = c2, onValueChange = { c2 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "d₂", value = d2, onValueChange = { d2 = it }, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Equation 3
      Text("Eq 3: a₃x + b₃y + c₃z = d₃", fontSize = 11.sp, color = CasioKeyTextSecondary, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        CoeffField(label = "a₃", value = a3, onValueChange = { a3 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "b₃", value = b3, onValueChange = { b3 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "c₃", value = c3, onValueChange = { c3 = it }, modifier = Modifier.weight(1f))
        CoeffField(label = "d₃", value = d3, onValueChange = { d3 = it }, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = {
          result = EquationSolverEngine.solveSimultaneous3(
            a1.toDoubleOrNull() ?: 1.0, b1.toDoubleOrNull() ?: 0.0, c1.toDoubleOrNull() ?: 0.0, d1.toDoubleOrNull() ?: 0.0,
            a2.toDoubleOrNull() ?: 0.0, b2.toDoubleOrNull() ?: 1.0, c2.toDoubleOrNull() ?: 0.0, d2.toDoubleOrNull() ?: 0.0,
            a3.toDoubleOrNull() ?: 0.0, b3.toDoubleOrNull() ?: 0.0, c3.toDoubleOrNull() ?: 1.0, d3.toDoubleOrNull() ?: 0.0
          )
        },
        modifier = Modifier.fillMaxWidth().testTag("solve_simultaneous3_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Solve 3-Variable System", fontWeight = FontWeight.ExtraBold)
      }

      if (result != null) {
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = CasioChassisBorder)
        Spacer(modifier = Modifier.height(10.dp))

        if (result!!.isSolvable) {
          SolutionBox(label = "Variable x", value = String.format(java.util.Locale.US, "%.5f", result!!.x).trimEnd('0').trimEnd('.'))
          Spacer(modifier = Modifier.height(6.dp))
          SolutionBox(label = "Variable y", value = String.format(java.util.Locale.US, "%.5f", result!!.y).trimEnd('0').trimEnd('.'))
          Spacer(modifier = Modifier.height(6.dp))
          SolutionBox(label = "Variable z", value = String.format(java.util.Locale.US, "%.5f", result!!.z).trimEnd('0').trimEnd('.'))
        } else {
          Text(
            text = "No unique solution exists (Matrix determinant is 0).",
            color = CasioAlphaRuby,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}

@Composable
private fun CoeffField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, color = CasioKeyTextSecondary, fontSize = 11.sp) },
    modifier = modifier,
    textStyle = LocalTextStyle.current.copy(
      fontFamily = JetBrainsMonoFontFamily,
      fontSize = 13.sp,
      textAlign = TextAlign.Center,
      color = CasioKeyTextPrimary
    ),
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = CasioShiftGold,
      unfocusedBorderColor = CasioChassisBorder,
      focusedContainerColor = CasioChassisDark,
      unfocusedContainerColor = CasioChassisDark
    ),
    shape = RoundedCornerShape(6.dp)
  )
}

@Composable
private fun SolutionBox(label: String, value: String) {
  Surface(
    color = ScreenOledBg,
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(label, color = CasioShiftGold, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMonoFontFamily)
      Text(value, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontFamily = JetBrainsMonoFontFamily, fontSize = 16.sp)
    }
  }
}
