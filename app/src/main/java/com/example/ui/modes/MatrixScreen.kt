package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.MatrixEngine
import com.example.model.MatrixModel
import com.example.ui.theme.*

@Composable
fun MatrixScreen(
  matrices: Map<String, MatrixModel>,
  onUpdateMatrix: (String, Int, Int, List<Double>) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedMatrixName by remember { mutableStateOf("MatA") }
  val currentMatrix = matrices[selectedMatrixName] ?: MatrixEngine.createZero(selectedMatrixName, 3, 3)

  var rows by remember(currentMatrix) { mutableIntStateOf(currentMatrix.rows) }
  var cols by remember(currentMatrix) { mutableIntStateOf(currentMatrix.cols) }
  var elements by remember(currentMatrix) { mutableStateOf(currentMatrix.elements.toMutableList()) }

  var operationResultText by remember { mutableStateOf<String?>(null) }
  var operationResultMatrix by remember { mutableStateOf<MatrixModel?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    // 1. Matrix Selection Tabs (MatA, MatB, MatC, MatAns)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("MatA", "MatB", "MatC").forEach { name ->
        val isSelected = selectedMatrixName == name
        Button(
          onClick = {
            selectedMatrixName = name
            operationResultText = null
            operationResultMatrix = null
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

    // 2. Dimensions Selector (Rows x Cols)
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
        Text("Dimensions:", color = CasioKeyTextSecondary, fontSize = 13.sp)

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Rows dropdown/buttons
          DimensionPill("R: $rows") {
            val nextR = if (rows >= 4) 1 else rows + 1
            rows = nextR
            val newElements = List(nextR * cols) { idx ->
              if (idx < elements.size) elements[idx] else 0.0
            }
            elements = newElements.toMutableList()
            onUpdateMatrix(selectedMatrixName, nextR, cols, newElements)
          }

          Text("×", color = CasioShiftGold, fontWeight = FontWeight.Bold)

          // Cols dropdown/buttons
          DimensionPill("C: $cols") {
            val nextC = if (cols >= 4) 1 else cols + 1
            cols = nextC
            val newElements = List(rows * nextC) { idx ->
              if (idx < elements.size) elements[idx] else 0.0
            }
            elements = newElements.toMutableList()
            onUpdateMatrix(selectedMatrixName, rows, nextC, newElements)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Matrix Interactive Grid Editor
    Surface(
      color = ScreenOledBg,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "$selectedMatrixName [$rows × $cols]",
          fontFamily = JetBrainsMonoFontFamily,
          fontWeight = FontWeight.Bold,
          color = CasioShiftGold,
          fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Grid cells
        for (r in 0 until rows) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            for (c in 0 until cols) {
              val index = r * cols + c
              val cellVal = if (index < elements.size) elements[index] else 0.0

              MatrixCellInput(
                value = cellVal,
                onValueChange = { newVal ->
                  if (index < elements.size) {
                    elements[index] = newVal
                    onUpdateMatrix(selectedMatrixName, rows, cols, elements.toList())
                  }
                },
                modifier = Modifier.weight(1f),
                testTag = "matrix_cell_${r}_$c"
              )
            }
          }
          if (r < rows - 1) Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset action buttons (Identity, Zero)
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              val zero = List(rows * cols) { 0.0 }
              elements = zero.toMutableList()
              onUpdateMatrix(selectedMatrixName, rows, cols, zero)
            },
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CasioKeyTextSecondary)
          ) {
            Text("Clear (0)", fontSize = 11.sp)
          }

          if (rows == cols) {
            OutlinedButton(
              onClick = {
                val ident = MatrixEngine.createIdentity(selectedMatrixName, rows).elements
                elements = ident.toMutableList()
                onUpdateMatrix(selectedMatrixName, rows, cols, ident)
              },
              shape = RoundedCornerShape(6.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = CasioShiftGold)
            ) {
              Text("Identity (I)", fontSize = 11.sp)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Matrix Operations Toolbar
    Text(
      text = "Matrix Operations",
      style = Typography.titleMedium,
      color = CasioKeyTextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      item {
        MatrixOpButton("det($selectedMatrixName)") {
          errorMessage = null
          val res = MatrixEngine.determinant(currentMatrix)
          res.onSuccess {
            operationResultText = "det($selectedMatrixName) = ${formatVal(it)}"
            operationResultMatrix = null
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        MatrixOpButton("${selectedMatrixName}⁻¹ (Inverse)") {
          errorMessage = null
          val res = MatrixEngine.inverse(currentMatrix)
          res.onSuccess {
            operationResultMatrix = it
            operationResultText = "Inverse of $selectedMatrixName:"
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        MatrixOpButton("${selectedMatrixName}ᵀ (Transpose)") {
          errorMessage = null
          val res = MatrixEngine.transpose(currentMatrix)
          operationResultMatrix = res
          operationResultText = "Transpose of $selectedMatrixName:"
        }
      }
      item {
        MatrixOpButton("tr($selectedMatrixName) (Trace)") {
          errorMessage = null
          val res = MatrixEngine.trace(currentMatrix)
          res.onSuccess {
            operationResultText = "tr($selectedMatrixName) = ${formatVal(it)}"
            operationResultMatrix = null
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        MatrixOpButton("MatA × MatB") {
          errorMessage = null
          val a = matrices["MatA"] ?: return@MatrixOpButton
          val b = matrices["MatB"] ?: return@MatrixOpButton
          val res = MatrixEngine.multiply(a, b)
          res.onSuccess {
            operationResultMatrix = it
            operationResultText = "MatA × MatB:"
          }.onFailure {
            errorMessage = it.message
          }
        }
      }
      item {
        MatrixOpButton("MatA + MatB") {
          errorMessage = null
          val a = matrices["MatA"] ?: return@MatrixOpButton
          val b = matrices["MatB"] ?: return@MatrixOpButton
          val res = MatrixEngine.add(a, b)
          res.onSuccess {
            operationResultMatrix = it
            operationResultText = "MatA + MatB:"
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
            fontWeight = FontWeight.Bold,
            color = CasioShiftGold,
            fontSize = 14.sp
          )

          val matRes = operationResultMatrix
          if (matRes != null) {
            Spacer(modifier = Modifier.height(8.dp))
            MatrixResultDisplay(matRes)
          }
        }
      }
    }
  }
}

@Composable
private fun DimensionPill(label: String, onClick: () -> Unit) {
  Surface(
    onClick = onClick,
    color = CasioKeyFunction,
    shape = RoundedCornerShape(6.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder)
  ) {
    Text(
      text = label,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      fontFamily = JetBrainsMonoFontFamily,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = CasioKeyTextPrimary
    )
  }
}

@Composable
private fun MatrixCellInput(
  value: Double,
  onValueChange: (Double) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String
) {
  var text by remember(value) {
    mutableStateOf(if (value == 0.0) "0" else formatVal(value))
  }

  OutlinedTextField(
    value = text,
    onValueChange = {
      text = it
      it.toDoubleOrNull()?.let(onValueChange)
    },
    modifier = modifier.testTag(testTag),
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
      focusedContainerColor = CasioChassisSurface,
      unfocusedContainerColor = CasioChassisSurface
    ),
    shape = RoundedCornerShape(6.dp)
  )
}

@Composable
private fun MatrixOpButton(label: String, onClick: () -> Unit) {
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

@Composable
private fun MatrixResultDisplay(matrix: MatrixModel) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(4.dp)
  ) {
    for (r in 0 until matrix.rows) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        for (c in 0 until matrix.cols) {
          Text(
            text = formatVal(matrix[r, c]),
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 14.sp,
            color = Color(0xFF00E5FF),
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
      if (r < matrix.rows - 1) Spacer(modifier = Modifier.height(4.dp))
    }
  }
}

private fun formatVal(v: Double): String {
  if (kotlin.math.abs(v - kotlin.math.round(v)) < 1e-9) {
    return kotlin.math.round(v).toLong().toString()
  }
  return String.format(java.util.Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
}
