package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.model.AngleUnit
import com.example.model.ComplexNumber
import com.example.ui.theme.*

@Composable
fun ComplexScreen(
  angleUnit: AngleUnit,
  onInsertResult: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var realA by remember { mutableStateOf("3") }
  var imagA by remember { mutableStateOf("4") }

  var realB by remember { mutableStateOf("1") }
  var imagB by remember { mutableStateOf("-2") }

  val zA = remember(realA, imagA) {
    ComplexNumber(realA.toDoubleOrNull() ?: 0.0, imagA.toDoubleOrNull() ?: 0.0)
  }

  val zB = remember(realB, imagB) {
    ComplexNumber(realB.toDoubleOrNull() ?: 0.0, imagB.toDoubleOrNull() ?: 0.0)
  }

  var resultZ by remember { mutableStateOf<ComplexNumber?>(zA) }
  var operationLabel by remember { mutableStateOf("z₁ Current Value") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    // 1. Complex Number A Input
    Surface(
      color = CasioChassisSurface,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Complex Number z₁:", color = CasioShiftGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            text = zA.toPolarString(isDeg = angleUnit == AngleUnit.DEG),
            color = Color(0xFF00E5FF),
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ComplexInputField("Re (a)", realA, onValueChange = { realA = it }, modifier = Modifier.weight(1f))
          Text("+", color = CasioKeyTextSecondary, fontWeight = FontWeight.Bold)
          ComplexInputField("Im (b)", imagA, onValueChange = { imagA = it }, modifier = Modifier.weight(1f))
          Text("i", color = CasioShiftGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Complex Number B Input
    Surface(
      color = CasioChassisSurface,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Complex Number z₂:", color = CasioShiftGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            text = zB.toPolarString(isDeg = angleUnit == AngleUnit.DEG),
            color = Color(0xFF00E5FF),
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ComplexInputField("Re (c)", realB, onValueChange = { realB = it }, modifier = Modifier.weight(1f))
          Text("+", color = CasioKeyTextSecondary, fontWeight = FontWeight.Bold)
          ComplexInputField("Im (d)", imagB, onValueChange = { imagB = it }, modifier = Modifier.weight(1f))
          Text("i", color = CasioShiftGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Binary Operations
    Text("Operations (z₁ and z₂):", color = CasioKeyTextSecondary, fontSize = 12.sp)
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Button(
        onClick = {
          resultZ = zA + zB
          operationLabel = "z₁ + z₂"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("z₁ + z₂", fontSize = 11.sp)
      }

      Button(
        onClick = {
          resultZ = zA - zB
          operationLabel = "z₁ − z₂"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("z₁ − z₂", fontSize = 11.sp)
      }

      Button(
        onClick = {
          resultZ = zA * zB
          operationLabel = "z₁ × z₂"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("z₁ × z₂", fontSize = 11.sp)
      }

      Button(
        onClick = {
          try {
            resultZ = zA / zB
            operationLabel = "z₁ ÷ z₂"
          } catch (e: Exception) {
            operationLabel = "Math ERROR: Divide by Zero"
          }
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("z₁ ÷ z₂", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4. Single Complex Unary Operations on z₁
    Text("Properties of z₁:", color = CasioKeyTextSecondary, fontSize = 12.sp)
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Button(
        onClick = {
          val mag = zA.magnitude
          resultZ = ComplexNumber(mag, 0.0)
          operationLabel = "|z₁| (Modulus)"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyFunction),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("|z₁|", fontSize = 11.sp)
      }

      Button(
        onClick = {
          val arg = if (angleUnit == AngleUnit.DEG) zA.argumentDeg() else zA.argumentRad
          resultZ = ComplexNumber(arg, 0.0)
          operationLabel = "arg(z₁) (Argument)"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyFunction),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("arg(z₁)", fontSize = 11.sp)
      }

      Button(
        onClick = {
          resultZ = zA.conjugate()
          operationLabel = "Conjugate z₁*"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyFunction),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("z₁*", fontSize = 11.sp)
      }

      Button(
        onClick = {
          resultZ = zA * zA
          operationLabel = "z₁² (Square)"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyFunction),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("z₁²", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 5. Result Display Box
    Surface(
      color = ScreenOledBg,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, CasioShiftGold),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(operationLabel, color = CasioShiftGold, fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        val r = resultZ
        if (r != null) {
          Text(
            text = "Rectangular: ${r.toRectangularString()}",
            color = Color(0xFF00E5FF),
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Polar: ${r.toPolarString(isDeg = angleUnit == AngleUnit.DEG)}",
            color = CasioKeyTextPrimary,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )

          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = { onInsertResult(r.toRectangularString()) },
            colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Insert Result into Expression", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun ComplexInputField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, color = CasioKeyTextSecondary, fontSize = 10.sp) },
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
