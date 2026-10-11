package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.engine.VectorEngine
import com.example.model.AngleUnit
import com.example.model.VectorModel
import com.example.ui.theme.*

@Composable
fun VectorScreen(
  vectors: Map<String, VectorModel>,
  angleUnit: AngleUnit,
  onUpdateVector: (String, VectorModel) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedVectorName by remember { mutableStateOf("VctA") }
  val currentVector = vectors[selectedVectorName] ?: VectorModel(selectedVectorName, 3, 1.0, 2.0, 3.0)

  var dim by remember(currentVector) { mutableIntStateOf(currentVector.dimension) }
  var valX by remember(currentVector) { mutableStateOf(currentVector.x.toString()) }
  var valY by remember(currentVector) { mutableStateOf(currentVector.y.toString()) }
  var valZ by remember(currentVector) { mutableStateOf(currentVector.z.toString()) }

  var operationResultText by remember { mutableStateOf<String?>(null) }
  var operationResultVector by remember { mutableStateOf<VectorModel?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  fun saveCurrent(xStr: String = valX, yStr: String = valY, zStr: String = valZ, d: Int = dim) {
    val x = xStr.toDoubleOrNull() ?: 0.0
    val y = yStr.toDoubleOrNull() ?: 0.0
    val z = if (d == 3) (zStr.toDoubleOrNull() ?: 0.0) else 0.0
    val updated = VectorModel(selectedVectorName, d, x, y, z)
    onUpdateVector(selectedVectorName, updated)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    // 1. Vector Selection Tabs (VctA, VctB, VctC)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("VctA", "VctB", "VctC").forEach { name ->
        val isSelected = selectedVectorName == name
        Button(
          onClick = {
            selectedVectorName = name
            operationResultText = null
            operationResultVector = null
            errorMessage = null
          },
          modifier = Modifier
            .weight(1f)
            .testTag("tab_$name"),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) CasioShiftGold else CasioChassisSurface,
            contentColor = if (isSelected) Color.Black else CasioKeyTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(name, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMonoFontFamily)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Vector Dimension Selector (2D vs 3D)
    Surface(
      color = CasioChassisSurface,
      shape = RoundedCornerShape(10.dp),
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
        Text("Dimension:", color = CasioKeyTextSecondary, fontSize = 13.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = dim == 2,
            onClick = {
              dim = 2
              saveCurrent(d = 2)
            },
            label = { Text("2-Dimensional", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CasioShiftGold,
              selectedLabelColor = Color.Black
            )
          )
          FilterChip(
            selected = dim == 3,
            onClick = {
              dim = 3
              saveCurrent(d = 3)
            },
            label = { Text("3-Dimensional", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CasioShiftGold,
              selectedLabelColor = Color.Black
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Vector Component Inputs
    Surface(
      color = ScreenOledBg,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "$selectedVectorName = ${currentVector.formatString()}",
          fontFamily = JetBrainsMonoFontFamily,
          fontWeight = FontWeight.Bold,
          color = CasioShiftGold,
          fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          VectorInputField(
            label = "X",
            value = valX,
            onValueChange = {
              valX = it
              saveCurrent(xStr = it)
            },
            modifier = Modifier.weight(1f),
            testTag = "input_vct_x"
          )

          VectorInputField(
            label = "Y",
            value = valY,
            onValueChange = {
              valY = it
              saveCurrent(yStr = it)
            },
            modifier = Modifier.weight(1f),
            testTag = "input_vct_y"
          )

          if (dim == 3) {
            VectorInputField(
              label = "Z",
              value = valZ,
              onValueChange = {
                valZ = it
                saveCurrent(zStr = it)
              },
              modifier = Modifier.weight(1f),
              testTag = "input_vct_z"
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Vector Operations Toolbar
    Text(
      text = "Vector Operations",
      style = Typography.titleMedium,
      color = CasioKeyTextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    val vctA = vectors["VctA"] ?: VectorModel("VctA", 3, 1.0, 2.0, 3.0)
    val vctB = vectors["VctB"] ?: VectorModel("VctB", 3, 4.0, 5.0, 6.0)

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      item {
        VectorOpBtn("Dot (VctA • VctB)") {
          errorMessage = null
          val dot = VectorEngine.dotProduct(vctA, vctB)
          operationResultText = "VctA • VctB = ${formatD(dot)}"
          operationResultVector = null
        }
      }
      item {
        VectorOpBtn("Cross (VctA × VctB)") {
          errorMessage = null
          val res = VectorEngine.crossProduct(vctA, vctB)
          res.onSuccess {
            operationResultVector = it
            operationResultText = "VctA × VctB = ${it.formatString()}"
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        VectorOpBtn("| $selectedVectorName | (Magnitude)") {
          errorMessage = null
          val mag = currentVector.magnitude
          operationResultText = "| $selectedVectorName | = ${formatD(mag)}"
          operationResultVector = null
        }
      }
      item {
        VectorOpBtn("Unit Vector ($selectedVectorName)") {
          errorMessage = null
          val res = VectorEngine.unitVector(currentVector)
          res.onSuccess {
            operationResultVector = it
            operationResultText = "Unit Vector of $selectedVectorName = ${it.formatString()}"
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        VectorOpBtn("Angle (VctA, VctB)") {
          errorMessage = null
          val res = VectorEngine.angleBetween(vctA, vctB, angleUnit)
          res.onSuccess {
            val unitStr = if (angleUnit == AngleUnit.DEG) "°" else " rad"
            operationResultText = "Angle θ = ${formatD(it)}$unitStr"
            operationResultVector = null
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        VectorOpBtn("VctA + VctB") {
          errorMessage = null
          val res = VectorEngine.add(vctA, vctB)
          res.onSuccess {
            operationResultVector = it
            operationResultText = "VctA + VctB = ${it.formatString()}"
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        VectorOpBtn("VctA − VctB") {
          errorMessage = null
          val res = VectorEngine.subtract(vctA, vctB)
          res.onSuccess {
            operationResultVector = it
            operationResultText = "VctA − VctB = ${it.formatString()}"
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
    }

    // 5. Result Display Panel
    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        color = CasioAlphaRuby.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CasioAlphaRuby),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = errorMessage ?: "",
          color = CasioAlphaRuby,
          modifier = Modifier.padding(10.dp),
          fontSize = 12.sp,
          fontFamily = JetBrainsMonoFontFamily
        )
      }
    }

    if (operationResultText != null) {
      Spacer(modifier = Modifier.height(12.dp))
      Surface(
        color = CasioChassisSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = operationResultText ?: "",
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF00E5FF),
            fontSize = 16.sp
          )
        }
      }
    }
  }
}

@Composable
private fun VectorInputField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, color = CasioKeyTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
    modifier = modifier.testTag(testTag),
    textStyle = LocalTextStyle.current.copy(
      fontFamily = JetBrainsMonoFontFamily,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      color = CasioKeyTextPrimary
    ),
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = CasioShiftGold,
      unfocusedBorderColor = CasioChassisBorder,
      focusedContainerColor = CasioChassisSurface,
      unfocusedContainerColor = CasioChassisSurface
    ),
    shape = RoundedCornerShape(6.dp)
  )
}

@Composable
private fun VectorOpBtn(label: String, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(
      containerColor = CasioKeyOperation,
      contentColor = CasioKeyTextPrimary
    ),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioChassisBorder)
  ) {
    Text(label, fontFamily = JetBrainsMonoFontFamily, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold)
  }
}

private fun formatD(v: Double): String {
  if (kotlin.math.abs(v - kotlin.math.round(v)) < 1e-9) {
    return kotlin.math.round(v).toLong().toString()
  }
  return String.format(java.util.Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
}
