package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleUnit
import com.example.model.CalculatorMode
import com.example.model.ScreenDisplayTheme
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CasioScreen(
  expression: String,
  cursorPosition: Int,
  resultText: String,
  fractionText: String?,
  isFractionView: Boolean,
  angleUnit: AngleUnit,
  mode: CalculatorMode,
  isShiftActive: Boolean,
  isAlphaActive: Boolean,
  hasMemory: Boolean,
  isError: Boolean,
  screenTheme: ScreenDisplayTheme,
  onCursorPositionChange: (Int) -> Unit,
  onAngleUnitClick: () -> Unit,
  onFractionToggleClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  // Screen Theme Colors with Bold Luminance
  val (screenBg, screenText, screenDim, screenAccent) = when (screenTheme) {
    ScreenDisplayTheme.OLED_DARK -> Quad(ScreenOledBg, ScreenOledText, ScreenOledDimText, ScreenOledAccent)
    ScreenDisplayTheme.LCD_GREEN -> Quad(ScreenGreenLcdBg, ScreenGreenLcdText, ScreenGreenLcdDim, Color(0xFF68D391))
    ScreenDisplayTheme.RETRO_AMBER -> Quad(ScreenAmberLcdBg, ScreenAmberLcdText, ScreenAmberLcdDim, Color(0xFFFFD166))
    ScreenDisplayTheme.CYBER_NAVY -> Quad(ScreenCyberBg, ScreenCyberText, ScreenCyberDim, ScreenCyberAccent)
  }

  // Blinking cursor
  var cursorVisible by remember { mutableStateOf(true) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(450)
      cursorVisible = !cursorVisible
    }
  }

  val exprScrollState = rememberScrollState()
  LaunchedEffect(cursorPosition, expression) {
    if (expression.isNotEmpty() && exprScrollState.maxValue > 0) {
      if (cursorPosition >= expression.length) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
      } else {
        val ratio = cursorPosition.toFloat() / expression.length.coerceAtLeast(1).toFloat()
        val target = (exprScrollState.maxValue * ratio).toInt()
        exprScrollState.animateScrollTo(target)
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(screenBg)
      .border(2.dp, CasioChassisBorder, RoundedCornerShape(12.dp))
      .padding(horizontal = 14.dp, vertical = 10.dp)
      .testTag("casio_screen_container")
  ) {
    // 1. Hardware Branding & Solar Panel Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "CASIO",
          style = Typography.titleMedium.copy(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
          ),
          color = Color(0xFFFFFFFF)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "fx-991EX CLASSWIZ",
          style = Typography.labelMedium.copy(
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold
          ),
          color = Color(0xFFA4B3C6)
        )
      }

      // Simulated Photovoltaic Solar Cell Panel
      SolarCellGraphic(modifier = Modifier.testTag("solar_cell_graphic"))
    }

    // 2. LCD Annunciators / Status Indicators Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // [S] Shift Indicator
        StatusBadge(
          label = "S",
          isActive = isShiftActive,
          activeColor = CasioShiftGold,
          inactiveColor = screenDim
        )
        // [A] Alpha Indicator
        StatusBadge(
          label = "A",
          isActive = isAlphaActive,
          activeColor = CasioAlphaRuby,
          inactiveColor = screenDim
        )
        // [M] Memory Indicator
        StatusBadge(
          label = "M",
          isActive = hasMemory,
          activeColor = screenAccent,
          inactiveColor = screenDim
        )
        // [D] [R] [G] Angle Indicator (Clickable)
        Surface(
          onClick = { onAngleUnitClick() },
          color = CasioChassisSurface,
          shape = RoundedCornerShape(4.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, screenAccent.copy(alpha = 0.5f)),
          modifier = Modifier.testTag("angle_unit_badge")
        ) {
          Text(
            text = "[${angleUnit.badge}]",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = screenAccent,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
        Text(
          text = "MATH",
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = screenDim
        )

        // Casio Authentic Bold Replay & Cursor Arrows
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Text(
            text = "▲▼",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = if (expression.isNotEmpty() || resultText != "0") CasioShiftGold else screenDim.copy(alpha = 0.35f)
          )
          Text(
            text = "◄►",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = if (expression.length > 8) Color(0xFF00E5FF) else screenDim.copy(alpha = 0.35f)
          )
        }
      }

      // Mode Badge (e.g. COMP, GRAPH, MAT, EQN, STAT)
      Surface(
        color = CasioChassisSurface,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioShiftGold.copy(alpha = 0.7f))
      ) {
        Text(
          text = mode.label,
          modifier = Modifier
            .padding(horizontal = 7.dp, vertical = 2.dp)
            .testTag("mode_badge_${mode.label}"),
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold,
          color = CasioShiftGold
        )
      }
    }

    // Divider Line inside LCD
    HorizontalDivider(
      thickness = 1.2.dp,
      color = screenDim.copy(alpha = 0.35f),
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // 3. Expression Input Line (Natural V.P.A.M.)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 42.dp)
        .horizontalScroll(exprScrollState)
        .testTag("expression_display_box")
    ) {
      if (expression.isEmpty()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (cursorVisible) {
            Box(
              modifier = Modifier
                .width(3.dp)
                .height(24.dp)
                .background(screenAccent)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "0",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = screenDim.copy(alpha = 0.6f)
          )
        }
      } else {
        val safePos = cursorPosition.coerceIn(0, expression.length)
        val beforeCursor = expression.substring(0, safePos)
        val afterCursor = expression.substring(safePos)

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = beforeCursor,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = screenText,
            letterSpacing = 0.5.sp
          )
          if (cursorVisible) {
            Box(
              modifier = Modifier
                .width(3.5.dp)
                .height(28.dp)
                .background(screenAccent)
            )
          } else {
            Spacer(modifier = Modifier.width(3.5.dp))
          }
          Text(
            text = afterCursor,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = screenText,
            letterSpacing = 0.5.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 4. Result Line & S<=>D (Fraction/Decimal) Toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // S<=>D toggle button badge if fraction is available
      if (fractionText != null && !isError) {
        Surface(
          onClick = onFractionToggleClick,
          color = CasioChassisSurface,
          shape = RoundedCornerShape(6.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, CasioShiftGold),
          modifier = Modifier.testTag("fraction_toggle_chip")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = "Toggle Fraction/Decimal",
              tint = CasioShiftGold,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = if (isFractionView) "DEC" else "FRAC",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Black,
              color = CasioShiftGold
            )
          }
        }
      } else {
        Spacer(modifier = Modifier.width(1.dp))
      }

      // Result Value Display - Shows ALL characters with auto-scaling & horizontal scrolling
      val resultScrollState = rememberScrollState()
      val resultFontSize = when {
        resultText.length > 25 -> 18.sp
        resultText.length > 20 -> 21.sp
        resultText.length > 15 -> 25.sp
        resultText.length > 11 -> 29.sp
        else -> 34.sp
      }

      Box(
        modifier = Modifier
          .weight(1f, fill = false)
          .horizontalScroll(resultScrollState)
          .clickable {
            clipboardManager.setText(AnnotatedString(resultText))
            Toast.makeText(context, "Copied: $resultText", Toast.LENGTH_SHORT).show()
          }
          .testTag("calculator_result_text"),
        contentAlignment = Alignment.CenterEnd
      ) {
        Text(
          text = resultText,
          fontFamily = JetBrainsMonoFontFamily,
          fontSize = resultFontSize,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.End,
          color = if (isError) CasioAlphaRuby else screenText,
          maxLines = 1,
          softWrap = false
        )
      }
    }
  }
}

@Composable
private fun StatusBadge(
  label: String,
  isActive: Boolean,
  activeColor: Color,
  inactiveColor: Color
) {
  Text(
    text = label,
    fontFamily = JetBrainsMonoFontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight.ExtraBold,
    color = if (isActive) activeColor else inactiveColor.copy(alpha = 0.3f)
  )
}

@Composable
private fun SolarCellGraphic(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .width(72.dp)
      .height(20.dp)
      .clip(RoundedCornerShape(3.dp))
      .background(CasioSolarPanel)
      .border(1.2.dp, CasioSolarGrid, RoundedCornerShape(3.dp))
      .padding(horizontal = 2.dp, vertical = 1.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    repeat(4) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
          .padding(horizontal = 1.dp)
          .background(Color(0xFF38281E))
      )
    }
  }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
val ScreenCyberAccent = Color(0xFF70D6FF)
