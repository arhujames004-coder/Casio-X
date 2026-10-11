package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BaseNEngine
import com.example.model.BaseNNumberSystem
import com.example.ui.theme.*

@Composable
fun BaseNScreen(modifier: Modifier = Modifier) {
  var selectedBase by remember { mutableStateOf(BaseNNumberSystem.DEC) }
  var currentInput by remember { mutableStateOf("42") }
  var storedValue by remember { mutableStateOf<Long?>(null) }
  var pendingOp by remember { mutableStateOf<String?>(null) }

  val currentValue: Long = remember(currentInput, selectedBase) {
    BaseNEngine.parse(currentInput, selectedBase) ?: 0L
  }

  fun appendChar(ch: Char) {
    currentInput = if (currentInput == "0") ch.toString() else currentInput + ch
  }

  fun applyOp(op: String) {
    if (op == "NOT") {
      val res = BaseNEngine.bitwiseNot(currentValue)
      currentInput = BaseNEngine.format(res, selectedBase).replace(" ", "")
      return
    }
    if (op == "NEG") {
      val res = BaseNEngine.negate(currentValue)
      currentInput = BaseNEngine.format(res, selectedBase).replace(" ", "")
      return
    }
    storedValue = currentValue
    pendingOp = op
    currentInput = "0"
  }

  fun computeEquals() {
    val a = storedValue ?: return
    val b = currentValue
    val res = when (pendingOp) {
      "AND" -> BaseNEngine.bitwiseAnd(a, b)
      "OR" -> BaseNEngine.bitwiseOr(a, b)
      "XOR" -> BaseNEngine.bitwiseXor(a, b)
      "NAND" -> BaseNEngine.bitwiseNand(a, b)
      "NOR" -> BaseNEngine.bitwiseNor(a, b)
      "XNOR" -> BaseNEngine.bitwiseXnor(a, b)
      "SHL" -> BaseNEngine.shiftLeft(a, b.toInt().coerceIn(0, 63))
      "SHR" -> BaseNEngine.shiftRight(a, b.toInt().coerceIn(0, 63))
      "+" -> a + b
      "-" -> a - b
      "*" -> a * b
      "/" -> if (b != 0L) a / b else 0L
      else -> b
    }
    currentInput = BaseNEngine.format(res, selectedBase).replace(" ", "")
    storedValue = null
    pendingOp = null
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    // 1. Radix Switcher Tabs (DEC, HEX, BIN, OCT)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      BaseNNumberSystem.values().forEach { base ->
        val isSelected = selectedBase == base
        Button(
          onClick = { selectedBase = base },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) CasioShiftGold else CasioChassisSurface,
            contentColor = if (isSelected) Color.Black else CasioKeyTextPrimary
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text(base.label, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMonoFontFamily)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Real-time multi-base breakdown display
    Surface(
      color = ScreenOledBg,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        BaseRow("DEC", BaseNEngine.format(currentValue, BaseNNumberSystem.DEC), selectedBase == BaseNNumberSystem.DEC)
        BaseRow("HEX", BaseNEngine.format(currentValue, BaseNNumberSystem.HEX), selectedBase == BaseNNumberSystem.HEX)
        BaseRow("BIN", BaseNEngine.format(currentValue, BaseNNumberSystem.BIN), selectedBase == BaseNNumberSystem.BIN)
        BaseRow("OCT", BaseNEngine.format(currentValue, BaseNNumberSystem.OCT), selectedBase == BaseNNumberSystem.OCT)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Bitwise Operations Toolbar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf("AND", "OR", "XOR", "NOT", "SHL").forEach { op ->
        Button(
          onClick = { applyOp(op) },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation),
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text(op, fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily)
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf("NAND", "NOR", "XNOR", "NEG", "SHR").forEach { op ->
        Button(
          onClick = { applyOp(op) },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation),
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text(op, fontSize = 10.sp, fontFamily = JetBrainsMonoFontFamily)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 4. Hex Keypad Buttons A-F
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf('A', 'B', 'C', 'D', 'E', 'F').forEach { ch ->
        val enabled = selectedBase == BaseNNumberSystem.HEX
        Button(
          onClick = { appendChar(ch) },
          enabled = enabled,
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(
            containerColor = CasioKeyFunction,
            disabledContainerColor = CasioKeyFunction.copy(alpha = 0.3f),
            contentColor = CasioShiftGold
          ),
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text(ch.toString(), fontWeight = FontWeight.Bold, fontFamily = JetBrainsMonoFontFamily)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 5. Numeric Keypad Block
    // 7, 8, 9, DEL, AC
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      KeypadNumBtn("7", selectedBase.radix > 7) { appendChar('7') }
      KeypadNumBtn("8", selectedBase.radix > 8) { appendChar('8') }
      KeypadNumBtn("9", selectedBase.radix > 9) { appendChar('9') }
      Button(
        onClick = {
          currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyActionDel),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("DEL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = {
          currentInput = "0"
          storedValue = null
          pendingOp = null
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyActionClear),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("AC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 4, 5, 6, *, /
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      KeypadNumBtn("4", selectedBase.radix > 4) { appendChar('4') }
      KeypadNumBtn("5", selectedBase.radix > 5) { appendChar('5') }
      KeypadNumBtn("6", selectedBase.radix > 6) { appendChar('6') }
      Button(onClick = { applyOp("*") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation), shape = RoundedCornerShape(6.dp)) { Text("×", fontSize = 14.sp) }
      Button(onClick = { applyOp("/") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation), shape = RoundedCornerShape(6.dp)) { Text("÷", fontSize = 14.sp) }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 1, 2, 3, +, -
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      KeypadNumBtn("1", selectedBase.radix > 1) { appendChar('1') }
      KeypadNumBtn("2", selectedBase.radix > 2) { appendChar('2') }
      KeypadNumBtn("3", selectedBase.radix > 3) { appendChar('3') }
      Button(onClick = { applyOp("+") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation), shape = RoundedCornerShape(6.dp)) { Text("+", fontSize = 14.sp) }
      Button(onClick = { applyOp("-") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CasioKeyOperation), shape = RoundedCornerShape(6.dp)) { Text("−", fontSize = 14.sp) }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 0, =
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      KeypadNumBtn("0", true, modifier = Modifier.weight(3f)) { appendChar('0') }
      Button(
        onClick = { computeEquals() },
        modifier = Modifier.weight(2f),
        colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("=", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun BaseRow(label: String, value: String, isCurrent: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontFamily = JetBrainsMonoFontFamily,
      fontWeight = FontWeight.ExtraBold,
      color = if (isCurrent) CasioShiftGold else CasioKeyTextSecondary,
      fontSize = 13.sp
    )
    Text(
      text = value,
      fontFamily = JetBrainsMonoFontFamily,
      fontWeight = FontWeight.ExtraBold,
      color = if (isCurrent) Color(0xFF00E5FF) else CasioKeyTextPrimary,
      fontSize = 14.5.sp
    )
  }
}

@Composable
private fun RowScope.KeypadNumBtn(
  label: String,
  enabled: Boolean,
  modifier: Modifier = Modifier.weight(1f),
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(
      containerColor = CasioKeyNumber,
      disabledContainerColor = CasioKeyNumber.copy(alpha = 0.3f),
      contentColor = CasioKeyTextPrimary
    ),
    shape = RoundedCornerShape(6.dp)
  ) {
    Text(label, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, fontFamily = JetBrainsMonoFontFamily)
  }
}
