package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun VariablesDialog(
  variables: Map<Char, Double>,
  ansValue: Double,
  onStoreVariable: (Char, Double) -> Unit,
  onRecallVariable: (Char) -> Unit,
  onClearAllVariables: () -> Unit,
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
          text = "Memory Registers (STO / RCL)",
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Ans = ${String.format(java.util.Locale.US, "%.4f", ansValue).trimEnd('0').trimEnd('.')}",
            fontFamily = JetBrainsMonoFontFamily,
            color = Color(0xFF00E5FF),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          TextButton(onClick = onClearAllVariables) {
            Icon(Icons.Default.Delete, contentDescription = "Clear All", modifier = Modifier.size(16.dp), tint = CasioAlphaRuby)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear All", color = CasioAlphaRuby, fontSize = 12.sp)
          }
        }

        HorizontalDivider(color = CasioChassisBorder)

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(variables.keys.toList()) { ch ->
            val v = variables[ch] ?: 0.0
            Surface(
              color = CasioChassisDark,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    color = CasioKeyFunction,
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = ch.toString(),
                      fontFamily = JetBrainsMonoFontFamily,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.5.sp,
                      color = if (ch == 'M') Color(0xFF00E5FF) else CasioAlphaRuby,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = String.format(java.util.Locale.US, "%.5f", v).trimEnd('0').trimEnd('.'),
                    fontFamily = JetBrainsMonoFontFamily,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.5.sp
                  )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  // STO Ans
                  TextButton(
                    onClick = { onStoreVariable(ch, ansValue) },
                    contentPadding = PaddingValues(horizontal = 6.dp)
                  ) {
                    Text("STO", fontSize = 11.sp, color = CasioShiftGold, fontWeight = FontWeight.Bold)
                  }
                  // RCL
                  TextButton(
                    onClick = {
                      onRecallVariable(ch)
                      onDismiss()
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp)
                  ) {
                    Text("RCL", fontSize = 11.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                  }
                }
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
