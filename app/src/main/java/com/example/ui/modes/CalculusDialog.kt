package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CalculusEngine
import com.example.model.AngleUnit
import com.example.ui.theme.*

@Composable
fun CalculusDialog(
  angleUnit: AngleUnit,
  onInsertResult: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var calcType by remember { mutableIntStateOf(0) } // 0: Integral, 1: Derivative, 2: Summation

  var expr by remember { mutableStateOf("X^2") }
  var lowerLimit by remember { mutableStateOf("0") }
  var upperLimit by remember { mutableStateOf("3") }
  var derivX by remember { mutableStateOf("2") }

  var computedResult by remember { mutableStateOf<Double?>(null) }
  var isCalculating by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Calculus & Summation (∫dx, d/dx, Σ)",
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = CasioShiftGold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CasioKeyTextSecondary)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Tab selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          FilterChip(
            selected = calcType == 0,
            onClick = {
              calcType = 0
              computedResult = null
            },
            label = { Text("∫dx Integral", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = calcType == 1,
            onClick = {
              calcType = 1
              computedResult = null
            },
            label = { Text("d/dx Deriv", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = calcType == 2,
            onClick = {
              calcType = 2
              computedResult = null
            },
            label = { Text("Σ Sum", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = expr,
          onValueChange = { expr = it },
          label = { Text("f(X) =", color = CasioKeyTextSecondary) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CasioShiftGold,
            unfocusedBorderColor = CasioChassisBorder
          ),
          singleLine = true
        )

        if (calcType == 0 || calcType == 2) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = lowerLimit,
              onValueChange = { lowerLimit = it },
              label = { Text("Lower limit (a)", fontSize = 11.sp, color = CasioKeyTextSecondary) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true
            )
            OutlinedTextField(
              value = upperLimit,
              onValueChange = { upperLimit = it },
              label = { Text("Upper limit (b)", fontSize = 11.sp, color = CasioKeyTextSecondary) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true
            )
          }
        } else {
          OutlinedTextField(
            value = derivX,
            onValueChange = { derivX = it },
            label = { Text("Evaluate at x =", color = CasioKeyTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }

        Button(
          onClick = {
            isCalculating = true
            try {
              computedResult = when (calcType) {
                0 -> {
                  val a = lowerLimit.toDoubleOrNull() ?: 0.0
                  val b = upperLimit.toDoubleOrNull() ?: 1.0
                  CalculusEngine.definiteIntegral(expr, a, b, angleUnit = angleUnit)
                }
                1 -> {
                  val xVal = derivX.toDoubleOrNull() ?: 0.0
                  CalculusEngine.derivative(expr, xVal, angleUnit = angleUnit)
                }
                else -> {
                  val a = lowerLimit.toLongOrNull() ?: 1L
                  val b = upperLimit.toLongOrNull() ?: 10L
                  CalculusEngine.summation(expr, a, b, angleUnit = angleUnit)
                }
              }
            } finally {
              isCalculating = false
            }
          },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Compute", fontWeight = FontWeight.Bold)
        }

        if (computedResult != null) {
          Surface(
            color = ScreenOledBg,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Result:", color = CasioKeyTextSecondary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
              Text(
                text = String.format(java.util.Locale.US, "%.6f", computedResult).trimEnd('0').trimEnd('.'),
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color(0xFF00E5FF)
              )
              Spacer(modifier = Modifier.height(6.dp))
              TextButton(
                onClick = {
                  onInsertResult(String.format(java.util.Locale.US, "%.6f", computedResult).trimEnd('0').trimEnd('.'))
                  onDismiss()
                }
              ) {
                Text("Insert into Main Calculator", color = CasioShiftGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Done", color = CasioShiftGold, fontWeight = FontWeight.Bold)
      }
    },
    containerColor = CasioChassisSurface,
    shape = RoundedCornerShape(14.dp)
  )
}
