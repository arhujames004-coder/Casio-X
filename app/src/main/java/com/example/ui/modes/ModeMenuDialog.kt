package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleUnit
import com.example.model.CalculatorMode
import com.example.model.ScreenDisplayTheme
import com.example.ui.theme.*

@Composable
fun ModeMenuDialog(
  currentMode: CalculatorMode,
  currentTheme: ScreenDisplayTheme,
  currentAngleUnit: AngleUnit,
  soundHapticsEnabled: Boolean,
  onSelectMode: (CalculatorMode) -> Unit,
  onSelectTheme: (ScreenDisplayTheme) -> Unit,
  onSelectAngleUnit: (AngleUnit) -> Unit,
  onToggleSoundHaptics: () -> Unit,
  onOpenVariables: () -> Unit,
  onOpenCalculus: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MODE / SETUP",
          fontFamily = JetBrainsMonoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = CasioShiftGold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CasioKeyTextSecondary)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Quick Tool shortcuts
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              onDismiss()
              onOpenCalculus()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
          ) {
            Text("∫dx / d/dx", fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = {
              onDismiss()
              onOpenVariables()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CasioShiftGold)
          ) {
            Text("STO / RCL", fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold)
          }
        }

        Text(
          text = "Select Calculator Mode:",
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = CasioKeyTextSecondary
        )

        // Mode Grid (Casio ClassWiz style numbered modes)
        val modes = CalculatorMode.values()
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          modes.forEachIndexed { index, mode ->
            val isSelected = currentMode == mode
            Surface(
              onClick = {
                onSelectMode(mode)
                onDismiss()
              },
              color = if (isSelected) CasioShiftGold else CasioChassisDark,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CasioShiftGold else CasioChassisBorder),
              modifier = Modifier.fillMaxWidth().testTag("mode_option_${mode.label}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${index + 1}:",
                  fontFamily = JetBrainsMonoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (isSelected) Color.Black else CasioShiftGold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = mode.title,
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isSelected) Color.Black else CasioKeyTextPrimary
                  )
                  Text(
                    text = mode.description,
                    fontSize = 10.sp,
                    color = if (isSelected) Color(0xFF262626) else CasioKeyTextSecondary
                  )
                }
              }
            }
          }
        }

        HorizontalDivider(color = CasioChassisBorder)

        // LCD Display Theme Selector
        Text(
          text = "LCD Display Theme:",
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = CasioKeyTextSecondary
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          ScreenDisplayTheme.values().forEach { theme ->
            val isSelected = currentTheme == theme
            FilterChip(
              selected = isSelected,
              onClick = { onSelectTheme(theme) },
              label = { Text(theme.displayName, fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CasioShiftGold,
                selectedLabelColor = Color.Black,
                containerColor = CasioChassisDark,
                labelColor = CasioKeyTextSecondary
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }

        HorizontalDivider(color = CasioChassisBorder)

        // Angle Unit Selector
        Text(
          text = "Default Angle Unit:",
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = CasioKeyTextSecondary
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          AngleUnit.values().forEach { unit ->
            val isSelected = currentAngleUnit == unit
            FilterChip(
              selected = isSelected,
              onClick = { onSelectAngleUnit(unit) },
              label = { Text("${unit.label} [${unit.badge}]", fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CasioKeyEquals,
                selectedLabelColor = Color.White,
                containerColor = CasioChassisDark,
                labelColor = CasioKeyTextSecondary
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }

        HorizontalDivider(color = CasioChassisBorder)

        // Sound / Haptic Feedback Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Tactile Haptic Feedback",
            fontFamily = PlusJakartaSansFontFamily,
            fontSize = 13.sp,
            color = CasioKeyTextPrimary
          )
          Switch(
            checked = soundHapticsEnabled,
            onCheckedChange = { onToggleSoundHaptics() },
            colors = SwitchDefaults.colors(checkedThumbColor = CasioShiftGold)
          )
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
