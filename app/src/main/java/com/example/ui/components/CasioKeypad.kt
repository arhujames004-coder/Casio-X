package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CasioKeypad(
  isShiftActive: Boolean,
  isAlphaActive: Boolean,
  soundHapticsEnabled: Boolean = true,
  onKeyClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current

  val triggerKey: (String) -> Unit = { keyId ->
    if (soundHapticsEnabled) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    onKeyClick(keyId)
  }

  BoxWithConstraints(
    modifier = modifier.fillMaxWidth()
  ) {
    val availableHeight = maxHeight
    val isParentScrolled = availableHeight == Dp.Infinity
    val isConstrainedHeight = (availableHeight < 400.dp && !isParentScrolled) || isParentScrolled
    val isMediumHeight = !isParentScrolled && availableHeight in 400.dp..<490.dp

    val digitKeyHeight: Dp = when {
      isConstrainedHeight -> 34.dp
      isMediumHeight -> 39.dp
      else -> 44.dp
    }
    val funcKeyHeight: Dp = when {
      isConstrainedHeight -> 27.dp
      isMediumHeight -> 31.dp
      else -> 35.dp
    }
    val topClusterHeight: Dp = when {
      isConstrainedHeight -> 52.dp
      isMediumHeight -> 58.dp
      else -> 66.dp
    }
    val legendHeight: Dp = when {
      isConstrainedHeight -> 10.dp
      isMediumHeight -> 11.5.dp
      else -> 13.dp
    }
    val rowSpacing: Dp = when {
      isConstrainedHeight -> 1.5.dp
      isMediumHeight -> 2.5.dp
      else -> 3.5.dp
    }
    val digitFontSize: TextUnit = when {
      isConstrainedHeight -> 17.sp
      isMediumHeight -> 19.sp
      else -> 21.sp
    }
    val funcFontSize: TextUnit = when {
      isConstrainedHeight -> 11.5.sp
      isMediumHeight -> 12.5.sp
      else -> 13.5.sp
    }
    val legendFontSize: TextUnit = when {
      isConstrainedHeight -> 8.5.sp
      isMediumHeight -> 9.5.sp
      else -> 10.5.sp
    }

    val columnModifier = if (isConstrainedHeight && !isParentScrolled) {
      Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 4.dp, vertical = 2.dp)
    } else {
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 2.dp)
    }

    Column(
      modifier = columnModifier,
      verticalArrangement = Arrangement.spacedBy(rowSpacing)
    ) {
      // 1. Top Control & Navigation Cluster
      TopControlCluster(
        isShiftActive = isShiftActive,
        isAlphaActive = isAlphaActive,
        clusterHeight = topClusterHeight,
        onKeyClick = triggerKey
      )

      Spacer(modifier = Modifier.height(1.dp))

      // Active Characters / Symbols Quick Banner
      if (isAlphaActive) {
        Surface(
          color = CasioChassisDark,
          shape = RoundedCornerShape(6.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioAlphaRuby),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
              .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Text(
              text = "ALPHA:",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = CasioAlphaRuby
            )
            listOf("A", "B", "C", "D", "E", "F", "X", "Y", "M", "r", "θ", "π", "e", ":", "=", ",").forEach { ch ->
              Surface(
                onClick = { triggerKey(if (ch == "=") "calc" else ch) },
                color = CasioAlphaRuby.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CasioAlphaRuby)
              ) {
                Text(
                  text = ch,
                  fontFamily = JetBrainsMonoFontFamily,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                )
              }
            }
          }
        }
      }

      if (isShiftActive) {
        Surface(
          color = CasioChassisDark,
          shape = RoundedCornerShape(6.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioShiftGold),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
              .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Text(
              text = "SHIFT:",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = CasioShiftGold
            )
            listOf(
              "x!" to "x_inv", "∛" to "sqrt", "x³" to "x2", "ˣ√" to "power",
              "10ˣ" to "log", "eˣ" to "ln", "°" to "neg", "sinh⁻¹" to "hyp",
              "sin⁻¹" to "sin", "cos⁻¹" to "cos", "tan⁻¹" to "tan", "nPr" to "ncr",
              "Pol" to "left_paren", "Rec" to "right_paren", "π" to "pow10", "%" to "Ans"
            ).forEach { (sym, kId) ->
              Surface(
                onClick = { triggerKey(kId) },
                color = CasioShiftGold.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold)
              ) {
                Text(
                  text = sym,
                  fontFamily = JetBrainsMonoFontFamily,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Black,
                  color = CasioShiftGold,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                )
              }
            }
          }
        }
      }

      // 2. Scientific Function Keys (3 rows)
      // Row 1
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "x⁻¹", shiftLabel = "x!", alphaLabel = "A",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("x_inv") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_x_inv"
        )
        CasioKey(
          label = "√", shiftLabel = "∛", alphaLabel = "B",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("sqrt") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_sqrt"
        )
        CasioKey(
          label = "x²", shiftLabel = "x³", alphaLabel = "C",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("x2") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_x2"
        )
        CasioKey(
          label = "xʸ", shiftLabel = "ˣ√", alphaLabel = "D",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("power") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_power"
        )
        CasioKey(
          label = "log", shiftLabel = "10ˣ", alphaLabel = "E",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("log") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_log"
        )
        CasioKey(
          label = "ln", shiftLabel = "eˣ", alphaLabel = "F",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("ln") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_ln"
        )
      }

      // Row 2
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "(–)", shiftLabel = "° ' \"", alphaLabel = "X",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("neg") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_neg"
        )
        CasioKey(
          label = "hyp", shiftLabel = "sinh⁻¹", alphaLabel = "Y",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("HYP") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_hyp"
        )
        CasioKey(
          label = "sin", shiftLabel = "sin⁻¹", alphaLabel = "M",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("sin") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_sin"
        )
        CasioKey(
          label = "cos", shiftLabel = "cos⁻¹", alphaLabel = "r",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("cos") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_cos"
        )
        CasioKey(
          label = "tan", shiftLabel = "tan⁻¹", alphaLabel = "θ",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("tan") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_tan"
        )
        CasioKey(
          label = "nCr", shiftLabel = "nPr",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("ncr") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_ncr"
        )
      }

      // Row 3
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "STO", shiftLabel = "RCL",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("STO") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_sto"
        )
        CasioKey(
          label = "ENG", shiftLabel = "←ENG",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("ENG") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_eng"
        )
        CasioKey(
          label = "(", shiftLabel = "Pol", alphaLabel = ":",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("left_paren") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_left_paren"
        )
        CasioKey(
          label = ")", shiftLabel = "Rec", alphaLabel = ",",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("right_paren") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_right_paren"
        )
        CasioKey(
          label = "S⇔D", shiftLabel = "a b/c",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("SD") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_sd"
        )
        CasioKey(
          label = "M+", shiftLabel = "M-",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyFunction, onClick = { triggerKey("M_PLUS") },
          keyHeight = funcKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_m_plus"
        )
      }

      Spacer(modifier = Modifier.height(1.dp))

      // 3. Numeric & Arithmetic Main Block (5 columns)
      // Row 4: 7, 8, 9, DEL, AC
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "7", shiftLabel = "CONST",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("7") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_7"
        )
        CasioKey(
          label = "8", shiftLabel = "CONV",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("8") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_8"
        )
        CasioKey(
          label = "9", shiftLabel = "CLR",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("9") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_9"
        )
        CasioKey(
          label = "DEL", shiftLabel = "INS",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyActionDel, labelColor = Color.White,
          onClick = { triggerKey("DEL") },
          keyHeight = digitKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_del"
        )
        CasioKey(
          label = "AC", shiftLabel = "OFF",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyActionClear, labelColor = Color.White,
          onClick = { triggerKey("AC") },
          keyHeight = digitKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_ac"
        )
      }

      // Row 5: 4, 5, 6, ×, ÷
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "4", shiftLabel = "MAT",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("4") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_4"
        )
        CasioKey(
          label = "5", shiftLabel = "VCT",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("5") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_5"
        )
        CasioKey(
          label = "6", shiftLabel = "CALC",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("6") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_6"
        )
        CasioKey(
          label = "×", shiftLabel = "GCD",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyOperation, onClick = { triggerKey("mul") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_mul"
        )
        CasioKey(
          label = "÷", shiftLabel = "LCM",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyOperation, onClick = { triggerKey("div") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_div"
        )
      }

      // Row 6: 1, 2, 3, +, −
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "1", shiftLabel = "STAT",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("1") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_1"
        )
        CasioKey(
          label = "2", shiftLabel = "CMPLX",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("2") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_2"
        )
        CasioKey(
          label = "3", shiftLabel = "BASE",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("3") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_3"
        )
        CasioKey(
          label = "+", shiftLabel = "Pol",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyOperation, onClick = { triggerKey("add") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_add"
        )
        CasioKey(
          label = "−", shiftLabel = "Rec",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyOperation, onClick = { triggerKey("sub") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_sub"
        )
      }

      // Row 7: 0, ., ×10ˣ, Ans, =
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
      ) {
        CasioKey(
          label = "0", shiftLabel = "Rnd",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("0") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_0"
        )
        CasioKey(
          label = ".", shiftLabel = "Ran#",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyNumber, onClick = { triggerKey("dot") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), isDigit = true, testTag = "key_dot"
        )
        CasioKey(
          label = "×10ˣ", shiftLabel = "π", alphaLabel = "e",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyOperation, onClick = { triggerKey("pow10") },
          keyHeight = digitKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_pow10"
        )
        CasioKey(
          label = "Ans", shiftLabel = "%",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyOperation, onClick = { triggerKey("Ans") },
          keyHeight = digitKeyHeight, fontSize = funcFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_ans"
        )
        CasioKey(
          label = "=", shiftLabel = "≈", alphaLabel = "SOLVE",
          isShiftActive = isShiftActive, isAlphaActive = isAlphaActive,
          bg = CasioKeyEquals, labelColor = Color.White,
          onClick = { triggerKey("=") },
          keyHeight = digitKeyHeight, fontSize = digitFontSize,
          legendHeight = legendHeight, legendFontSize = legendFontSize,
          modifier = Modifier.weight(1f), testTag = "key_equals"
        )
      }
    }
  }
}

@Composable
private fun TopControlCluster(
  isShiftActive: Boolean,
  isAlphaActive: Boolean,
  clusterHeight: Dp = 60.dp,
  onKeyClick: (String) -> Unit
) {
  val btnHeight = (clusterHeight - 6.dp) / 2

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(clusterHeight),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Left cluster: SHIFT & ALPHA
    Column(
      modifier = Modifier
        .width(62.dp)
        .fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // SHIFT Key
      Surface(
        onClick = { onKeyClick("SHIFT") },
        modifier = Modifier
          .fillMaxWidth()
          .height(btnHeight)
          .testTag("key_shift"),
        shape = RoundedCornerShape(6.dp),
        color = if (isShiftActive) CasioShiftGold else CasioKeyFunction,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isShiftActive) CasioShiftGold else CasioChassisBorder)
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(
            text = "SHIFT",
            fontFamily = PlusJakartaSansFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isShiftActive) Color.Black else CasioShiftGold
          )
        }
      }

      // ALPHA Key
      Surface(
        onClick = { onKeyClick("ALPHA") },
        modifier = Modifier
          .fillMaxWidth()
          .height(btnHeight)
          .testTag("key_alpha"),
        shape = RoundedCornerShape(6.dp),
        color = if (isAlphaActive) CasioAlphaRuby else CasioKeyFunction,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isAlphaActive) CasioAlphaRuby else CasioChassisBorder)
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(
            text = "ALPHA",
            fontFamily = PlusJakartaSansFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isAlphaActive) Color.White else CasioAlphaRuby
          )
        }
      }
    }

    // Center: Casio Iconic Wide REPLAY Navigation Controller with BIG BOLD ARROWS
    Box(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 6.dp)
        .height(clusterHeight)
        .clip(RoundedCornerShape(clusterHeight / 2))
        .background(CasioKeyDpad)
        .border(2.dp, CasioChassisHighlight, RoundedCornerShape(clusterHeight / 2))
        .testTag("dpad_wheel"),
      contentAlignment = Alignment.Center
    ) {
      // 1. Left Arrow Key (Big & Easy to Tap)
      Box(
        modifier = Modifier
          .align(Alignment.CenterStart)
          .fillMaxHeight()
          .width(52.dp)
          .clickable { onKeyClick("CURSOR_LEFT") }
          .testTag("dpad_left"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "◀",
          fontSize = 26.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      }

      // 2. Right Arrow Key (Big & Easy to Tap)
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .fillMaxHeight()
          .width(52.dp)
          .clickable { onKeyClick("CURSOR_RIGHT") }
          .testTag("dpad_right"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "▶",
          fontSize = 26.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      }

      // 3. Up Arrow Key (Big & Easy to Tap)
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxWidth(0.44f)
          .height(clusterHeight * 0.48f)
          .clickable { onKeyClick("CURSOR_UP") }
          .testTag("dpad_up"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "▲",
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      }

      // 4. Down Arrow Key (Big & Easy to Tap)
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth(0.44f)
          .height(clusterHeight * 0.48f)
          .clickable { onKeyClick("CURSOR_DOWN") }
          .testTag("dpad_down"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "▼",
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      }

      // Center Replay Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(CasioChassisDark)
          .border(1.dp, CasioChassisBorder, RoundedCornerShape(6.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "REPLAY",
          fontFamily = PlusJakartaSansFontFamily,
          fontSize = 8.5.sp,
          fontWeight = FontWeight.ExtraBold,
          color = CasioShiftGold
        )
      }
    }

    // Right cluster: MODE & ON
    Column(
      modifier = Modifier
        .width(62.dp)
        .fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Surface(
        onClick = { onKeyClick("MODE_TOGGLE") },
        modifier = Modifier
          .fillMaxWidth()
          .height(btnHeight)
          .testTag("key_mode"),
        shape = RoundedCornerShape(6.dp),
        color = CasioKeyFunction,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioChassisBorder)
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(
            text = "MODE",
            fontFamily = PlusJakartaSansFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        }
      }

      Surface(
        onClick = { onKeyClick("AC") },
        modifier = Modifier
          .fillMaxWidth()
          .height(btnHeight)
          .testTag("key_on"),
        shape = RoundedCornerShape(6.dp),
        color = CasioKeyFunction,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioChassisBorder)
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(
            text = "ON",
            fontFamily = PlusJakartaSansFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun CasioKey(
  label: String,
  modifier: Modifier = Modifier,
  shiftLabel: String? = null,
  alphaLabel: String? = null,
  isShiftActive: Boolean = false,
  isAlphaActive: Boolean = false,
  bg: Color = CasioKeyFunction,
  labelColor: Color = CasioKeyTextPrimary,
  isDigit: Boolean = false,
  keyHeight: Dp = 42.dp,
  fontSize: TextUnit = 18.sp,
  legendHeight: Dp = 12.dp,
  legendFontSize: TextUnit = 10.sp,
  testTag: String,
  onClick: () -> Unit
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Modifier legends above key - Bold & High Contrast, ALL characters visible
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 1.dp, vertical = 0.5.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (shiftLabel != null) {
        val sSize = if (shiftLabel.length > 4) (legendFontSize.value - 1.5f).coerceAtLeast(7.5f).sp else legendFontSize
        Text(
          text = shiftLabel,
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = sSize,
          fontWeight = FontWeight.Black,
          color = if (isShiftActive) Color(0xFFFFEB3B) else CasioShiftGold,
          maxLines = 1,
          softWrap = false,
          modifier = Modifier.weight(1f, fill = false)
        )
      } else {
        Spacer(modifier = Modifier.width(1.dp))
      }

      if (alphaLabel != null) {
        Text(
          text = alphaLabel,
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = legendFontSize,
          fontWeight = FontWeight.Black,
          color = if (isAlphaActive) Color(0xFFFF5252) else CasioAlphaRuby,
          maxLines = 1,
          softWrap = false,
          modifier = Modifier.weight(1f, fill = false)
        )
      }
    }

    // Physical Key Surface - Bold, Sharp & Tactile
    Surface(
      onClick = onClick,
      modifier = Modifier
        .fillMaxWidth()
        .height(keyHeight)
        .testTag(testTag),
      shape = RoundedCornerShape(7.dp),
      color = bg,
      border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioChassisBorder),
      tonalElevation = 3.dp
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 1.dp)
      ) {
        Text(
          text = label,
          fontFamily = if (isDigit) JetBrainsMonoFontFamily else PlusJakartaSansFontFamily,
          fontSize = fontSize,
          fontWeight = FontWeight.Black,
          color = labelColor,
          textAlign = TextAlign.Center,
          maxLines = 1,
          softWrap = false
        )
      }
    }
  }
}
