package com.example.ui.modes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.MathEvaluator
import com.example.model.AngleUnit
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GraphingScreen(
  initialExpression: String,
  onExpressionChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var expression by remember { mutableStateOf(initialExpression) }
  var xMin by remember { mutableDoubleStateOf(-10.0) }
  var xMax by remember { mutableDoubleStateOf(10.0) }
  var yMin by remember { mutableDoubleStateOf(-6.0) }
  var yMax by remember { mutableDoubleStateOf(6.0) }
  var traceX by remember { mutableStateOf<Double?>(null) }
  var showTable by remember { mutableStateOf(false) }

  val presets = listOf("sin(X)", "cos(X)", "X^2 - 4", "X^3 - 3*X", "1/(X)", "sqrt(abs(X))")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .padding(12.dp)
  ) {
    // Top Function Expression Input Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = expression,
        onValueChange = {
          expression = it
          onExpressionChange(it)
        },
        label = { Text("f(x) =", color = CasioKeyTextSecondary) },
        modifier = Modifier
          .weight(1f)
          .testTag("graph_function_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = CasioKeyTextPrimary,
          unfocusedTextColor = CasioKeyTextPrimary,
          focusedBorderColor = CasioShiftGold,
          unfocusedBorderColor = CasioChassisBorder,
          focusedContainerColor = CasioChassisSurface,
          unfocusedContainerColor = CasioChassisSurface
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
      )

      IconButton(
        onClick = { showTable = !showTable },
        modifier = Modifier
          .background(CasioChassisSurface, RoundedCornerShape(8.dp))
          .border(1.dp, CasioChassisBorder, RoundedCornerShape(8.dp))
          .testTag("toggle_graph_table_btn")
      ) {
        Icon(
          imageVector = if (showTable) Icons.Default.Timeline else Icons.Default.TableChart,
          contentDescription = "Toggle Table / Graph",
          tint = CasioShiftGold
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Quick presets chip row
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(presets) { preset ->
        SuggestionChip(
          onClick = {
            expression = preset
            onExpressionChange(preset)
          },
          label = { Text(preset, fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily) },
          colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = CasioChassisSurface,
            labelColor = CasioKeyTextSecondary
          ),
          border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = CasioChassisBorder
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (showTable) {
      // Table of values view
      GraphTableView(
        expression = expression,
        xMin = xMin,
        xMax = xMax,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      )
    } else {
      // 2D Interactive Function Canvas View
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(ScreenOledBg)
          .border(1.5.dp, CasioChassisBorder, RoundedCornerShape(12.dp))
          .testTag("graph_canvas_container")
      ) {
        GraphCanvas(
          expression = expression,
          xMin = xMin,
          xMax = xMax,
          yMin = yMin,
          yMax = yMax,
          traceX = traceX,
          onTraceChange = { traceX = it },
          modifier = Modifier.fillMaxSize()
        )

        // Float zoom overlay buttons at bottom right
        Column(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          FloatingActionButton(
            onClick = {
              val dx = (xMax - xMin) * 0.2
              val dy = (yMax - yMin) * 0.2
              xMin += dx; xMax -= dx
              yMin += dy; yMax -= dy
            },
            modifier = Modifier.size(36.dp),
            containerColor = CasioKeyFunction,
            contentColor = CasioKeyTextPrimary
          ) {
            Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
          }
          FloatingActionButton(
            onClick = {
              val dx = (xMax - xMin) * 0.25
              val dy = (yMax - yMin) * 0.25
              xMin -= dx; xMax += dx
              yMin -= dy; yMax += dy
            },
            modifier = Modifier.size(36.dp),
            containerColor = CasioKeyFunction,
            contentColor = CasioKeyTextPrimary
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
          }
          FloatingActionButton(
            onClick = {
              xMin = -10.0; xMax = 10.0
              yMin = -6.0; yMax = 6.0
              traceX = null
            },
            modifier = Modifier.size(36.dp),
            containerColor = CasioKeyFunction,
            contentColor = CasioKeyTextPrimary
          ) {
            Icon(Icons.Default.RestartAlt, contentDescription = "Reset Zoom", modifier = Modifier.size(18.dp))
          }
        }

        // Trace info badge at top-left
        if (traceX != null) {
          val traceY = evaluateForX(expression, traceX!!)
          Surface(
            color = CasioChassisSurface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold),
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(8.dp)
          ) {
            Text(
              text = "X: ${String.format(java.util.Locale.US, "%.2f", traceX)} | Y: ${String.format(java.util.Locale.US, "%.2f", traceY)}",
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 12.5.sp,
              color = CasioShiftGold,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}

@Composable
private fun GraphCanvas(
  expression: String,
  xMin: Double,
  xMax: Double,
  yMin: Double,
  yMax: Double,
  traceX: Double?,
  onTraceChange: (Double) -> Unit,
  modifier: Modifier = Modifier
) {
  Canvas(
    modifier = modifier
      .pointerInput(xMin, xMax) {
        detectDragGestures { change, _ ->
          val canvasWidth = size.width
          val px = change.position.x.coerceIn(0f, canvasWidth.toFloat())
          val dataX = xMin + (px / canvasWidth) * (xMax - xMin)
          onTraceChange(dataX)
        }
      }
  ) {
    val width = size.width
    val height = size.height

    fun toScreenX(x: Double): Float = ((x - xMin) / (xMax - xMin) * width).toFloat()
    fun toScreenY(y: Double): Float = (height - (y - yMin) / (yMax - yMin) * height).toFloat()

    // 1. Grid lines
    val gridColor = Color(0xFF1E232F)
    val axisColor = Color(0xFF4A5568)

    // Vertical grid
    val xStep = calculateStep(xMax - xMin)
    var currX = (xMin / xStep).toInt() * xStep
    while (currX <= xMax) {
      val sx = toScreenX(currX)
      drawLine(gridColor, Offset(sx, 0f), Offset(sx, height), strokeWidth = 1f)
      currX += xStep
    }

    // Horizontal grid
    val yStep = calculateStep(yMax - yMin)
    var currY = (yMin / yStep).toInt() * yStep
    while (currY <= yMax) {
      val sy = toScreenY(currY)
      drawLine(gridColor, Offset(0f, sy), Offset(width, sy), strokeWidth = 1f)
      currY += yStep
    }

    // 2. Axes
    val originX = toScreenX(0.0)
    val originY = toScreenY(0.0)

    if (originX in 0f..width) {
      drawLine(axisColor, Offset(originX, 0f), Offset(originX, height), strokeWidth = 2f)
    }
    if (originY in 0f..height) {
      drawLine(axisColor, Offset(0f, originY), Offset(width, originY), strokeWidth = 2f)
    }

    // 3. Curve Path
    val path = Path()
    var started = false
    val samples = 300
    val dx = (xMax - xMin) / samples

    for (i in 0..samples) {
      val xVal = xMin + i * dx
      val yVal = evaluateForX(expression, xVal)

      if (!yVal.isNaN() && !yVal.isInfinite() && yVal in (yMin * 3)..(yMax * 3)) {
        val sx = toScreenX(xVal)
        val sy = toScreenY(yVal)
        if (!started) {
          path.moveTo(sx, sy)
          started = true
        } else {
          path.lineTo(sx, sy)
        }
      } else {
        started = false
      }
    }

    // Draw glowing function line
    drawPath(
      path = path,
      color = Color(0xFF00E5FF),
      style = Stroke(width = 3.5f)
    )

    // 4. Trace crosshair
    if (traceX != null) {
      val traceY = evaluateForX(expression, traceX)
      if (!traceY.isNaN() && !traceY.isInfinite()) {
        val sx = toScreenX(traceX)
        val sy = toScreenY(traceY)
        // Vertical dashed line
        drawLine(CasioShiftGold, Offset(sx, 0f), Offset(sx, height), strokeWidth = 1.5f)
        // Dot
        drawCircle(CasioShiftGold, radius = 6f, center = Offset(sx, sy))
        drawCircle(Color.White, radius = 3f, center = Offset(sx, sy))
      }
    }
  }
}

@Composable
private fun GraphTableView(
  expression: String,
  xMin: Double,
  xMax: Double,
  modifier: Modifier = Modifier
) {
  val step = 1.0
  val rows = remember(expression, xMin, xMax) {
    val list = mutableListOf<Pair<Double, Double>>()
    var curr = xMin.roundToInt().toDouble()
    val end = xMax.roundToInt().toDouble()
    while (curr <= end) {
      val y = evaluateForX(expression, curr)
      list.add(Pair(curr, y))
      curr += step
    }
    list
  }

  Surface(
    modifier = modifier,
    color = CasioChassisSurface,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CasioChassisDark, RoundedCornerShape(6.dp))
          .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("X", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = CasioShiftGold)
        Text("f(X)", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
      }

      HorizontalDivider(color = CasioChassisBorder, modifier = Modifier.padding(vertical = 4.dp))

      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(rows) { (x, y) ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = String.format(java.util.Locale.US, "%.1f", x),
              fontFamily = JetBrainsMonoFontFamily,
              color = CasioKeyTextPrimary
            )
            Text(
              text = if (y.isNaN()) "Undefined" else String.format(java.util.Locale.US, "%.4f", y),
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Medium,
              color = if (y.isNaN()) CasioAlphaRuby else Color(0xFF00E5FF)
            )
          }
        }
      }
    }
  }
}

private fun calculateStep(range: Double): Double {
  return when {
    range <= 5.0 -> 0.5
    range <= 15.0 -> 1.0
    range <= 30.0 -> 2.0
    range <= 60.0 -> 5.0
    else -> 10.0
  }
}

private fun evaluateForX(expr: String, x: Double): Double {
  val res = MathEvaluator.evaluate(
    expression = expr,
    angleUnit = AngleUnit.RAD,
    variables = mapOf('X' to x, 'x' to x)
  )
  return res.decimalValue
}
