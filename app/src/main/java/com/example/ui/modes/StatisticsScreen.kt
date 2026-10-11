package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.engine.StatisticsEngine
import com.example.model.StatOneVarResult
import com.example.model.StatTwoVarResult
import com.example.ui.theme.*

@Composable
fun StatisticsScreen(
  initialOneVar: List<Pair<Double, Double>>,
  initialTwoVar: List<Pair<Double, Double>>,
  onOneVarChange: (List<Pair<Double, Double>>) -> Unit,
  onTwoVarChange: (List<Pair<Double, Double>>) -> Unit,
  modifier: Modifier = Modifier
) {
  var isTwoVar by remember { mutableStateOf(false) }
  var oneVarList by remember { mutableStateOf(initialOneVar.toMutableList()) }
  var twoVarList by remember { mutableStateOf(initialTwoVar.toMutableList()) }

  var newX by remember { mutableStateOf("") }
  var newYorFreq by remember { mutableStateOf("1") }

  val oneVarResult = remember(oneVarList.size, oneVarList.hashCode()) {
    StatisticsEngine.calculateOneVar(oneVarList)
  }

  val twoVarResult = remember(twoVarList.size, twoVarList.hashCode()) {
    StatisticsEngine.calculateTwoVar(twoVarList)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    // Top Tabs: 1-Variable vs 2-Variable (Linear Regression)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { isTwoVar = false },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (!isTwoVar) CasioShiftGold else CasioChassisSurface,
          contentColor = if (!isTwoVar) Color.Black else CasioKeyTextPrimary
        ),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("1-Variable Stats", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      Button(
        onClick = { isTwoVar = true },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isTwoVar) CasioShiftGold else CasioChassisSurface,
          contentColor = if (isTwoVar) Color.Black else CasioKeyTextPrimary
        ),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("2-Var (y = a+bx)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Data Input Row
    Surface(
      color = CasioChassisSurface,
      shape = RoundedCornerShape(10.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        OutlinedTextField(
          value = newX,
          onValueChange = { newX = it },
          label = { Text("x", fontSize = 11.sp, color = CasioKeyTextSecondary) },
          modifier = Modifier.weight(1f),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(6.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CasioShiftGold,
            unfocusedBorderColor = CasioChassisBorder
          )
        )

        OutlinedTextField(
          value = newYorFreq,
          onValueChange = { newYorFreq = it },
          label = { Text(if (isTwoVar) "y" else "freq", fontSize = 11.sp, color = CasioKeyTextSecondary) },
          modifier = Modifier.weight(1f),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(6.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CasioShiftGold,
            unfocusedBorderColor = CasioChassisBorder
          )
        )

        IconButton(
          onClick = {
            val xv = newX.toDoubleOrNull()
            val yv = newYorFreq.toDoubleOrNull() ?: 1.0
            if (xv != null) {
              if (isTwoVar) {
                twoVarList.add(Pair(xv, yv))
                onTwoVarChange(twoVarList.toList())
              } else {
                oneVarList.add(Pair(xv, yv))
                onOneVarChange(oneVarList.toList())
              }
              newX = ""
              newYorFreq = "1"
            }
          },
          modifier = Modifier
            .background(CasioKeyEquals, RoundedCornerShape(6.dp))
            .size(48.dp)
            .testTag("add_stat_row_btn")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Point", tint = Color.White)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Data points chips
    Text(
      text = "Dataset (${if (isTwoVar) twoVarList.size else oneVarList.size} points):",
      color = CasioKeyTextSecondary,
      fontSize = 12.sp,
      fontFamily = JetBrainsMonoFontFamily
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Render list of points
    val currentPoints = if (isTwoVar) twoVarList else oneVarList
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(CasioChassisSurface, RoundedCornerShape(8.dp))
        .border(1.dp, CasioChassisBorder, RoundedCornerShape(8.dp))
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      if (currentPoints.isEmpty()) {
        Text("No points entered. Add data points above.", color = CasioKeyTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
      } else {
        currentPoints.take(8).forEachIndexed { idx, pair ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isTwoVar) "(${pair.first}, ${pair.second})" else "x = ${pair.first} (freq: ${pair.second.toInt()})",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 12.sp,
              color = CasioKeyTextPrimary
            )
            IconButton(
              onClick = {
                if (isTwoVar) {
                  twoVarList.removeAt(idx)
                  onTwoVarChange(twoVarList.toList())
                } else {
                  oneVarList.removeAt(idx)
                  onOneVarChange(oneVarList.toList())
                }
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CasioAlphaRuby, modifier = Modifier.size(16.dp))
            }
          }
        }
        if (currentPoints.size > 8) {
          Text("+ ${currentPoints.size - 8} more points...", color = CasioShiftGold, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Statistical Results Section
    if (!isTwoVar && oneVarResult != null) {
      OneVarResultsView(oneVarResult)
    } else if (isTwoVar && twoVarResult != null) {
      TwoVarResultsView(twoVarResult)
    }
  }
}

@Composable
private fun OneVarResultsView(res: StatOneVarResult) {
  Surface(
    color = ScreenOledBg,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "1-Variable Statistics Analysis",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CasioShiftGold
      )

      Spacer(modifier = Modifier.height(10.dp))

      StatMetricRow("Sample Size (n)", res.count.toString())
      StatMetricRow("Sample Mean (x̄)", formatD(res.mean))
      StatMetricRow("Sum of X (Σx)", formatD(res.sumX))
      StatMetricRow("Sum of X² (Σx²)", formatD(res.sumX2))
      StatMetricRow("Sample Std Dev (s_x)", formatD(res.sampleStdDev))
      StatMetricRow("Pop Std Dev (σ_x)", formatD(res.popStdDev))
      StatMetricRow("Variance (s²)", formatD(res.variance))

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = CasioChassisBorder)
      Spacer(modifier = Modifier.height(10.dp))

      Text("5-Number Summary (Boxplot metrics):", color = Color(0xFF00E5FF), fontSize = 12.sp, fontFamily = JetBrainsMonoFontFamily)
      Spacer(modifier = Modifier.height(6.dp))
      StatMetricRow("Minimum (minX)", formatD(res.min))
      StatMetricRow("First Quartile (Q₁)", formatD(res.q1))
      StatMetricRow("Median (Med)", formatD(res.median))
      StatMetricRow("Third Quartile (Q₃)", formatD(res.q3))
      StatMetricRow("Maximum (maxX)", formatD(res.max))
      StatMetricRow("IQR (Q₃ - Q₁)", formatD(res.q3 - res.q1))
    }
  }
}

@Composable
private fun TwoVarResultsView(res: StatTwoVarResult) {
  Surface(
    color = ScreenOledBg,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "Linear Regression (y = a + bx)",
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CasioShiftGold
      )

      Spacer(modifier = Modifier.height(10.dp))

      StatMetricRow("Slope (b)", formatD(res.slopeA))
      StatMetricRow("Y-Intercept (a)", formatD(res.interceptB))
      StatMetricRow("Regression Equation", "y = ${formatD(res.interceptB)} + ${formatD(res.slopeA)}x")

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = CasioChassisBorder)
      Spacer(modifier = Modifier.height(10.dp))

      StatMetricRow("Correlation Coeff (r)", formatD(res.correlationR))
      StatMetricRow("Coeff of Determination (r²)", formatD(res.rSquared))
      StatMetricRow("Covariance (covXY)", formatD(res.covXY))
      StatMetricRow("Mean of X (x̄)", formatD(res.meanX))
      StatMetricRow("Mean of Y (ȳ)", formatD(res.meanY))
    }
  }
}

@Composable
private fun StatMetricRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, color = CasioKeyTextSecondary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMonoFontFamily)
    Text(value, color = Color(0xFF00E5FF), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, fontFamily = JetBrainsMonoFontFamily)
  }
}

private fun formatD(d: Double): String {
  if (d.isNaN()) return "NaN"
  if (d.isInfinite()) return if (d > 0) "∞" else "-∞"
  if (kotlin.math.abs(d - kotlin.math.round(d)) < 1e-9) {
    return kotlin.math.round(d).toLong().toString()
  }
  return String.format(java.util.Locale.US, "%.5f", d).trimEnd('0').trimEnd('.')
}
