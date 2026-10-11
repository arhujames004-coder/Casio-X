package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationHistory
import com.example.ui.theme.*

@Composable
fun HistoryDialog(
  history: List<CalculationHistory>,
  onRecallItem: (CalculationHistory) -> Unit,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = CasioShiftGold)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Calculation History",
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = CasioKeyTextPrimary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CasioKeyTextSecondary)
        }
      }
    },
    text = {
      if (history.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No calculations yet.\nExecute computations to see history.",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 13.sp,
            color = CasioKeyTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 380.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(history) { item ->
            Surface(
              onClick = {
                onRecallItem(item)
                onDismiss()
              },
              color = CasioChassisDark,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
              modifier = Modifier.fillMaxWidth().testTag("history_item_${item.id}")
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = item.expression,
                  fontFamily = JetBrainsMonoFontFamily,
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = CasioKeyTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "[${item.angleUnit.badge}]",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = CasioShiftGold
                  )
                  Text(
                    text = "= ${item.result}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00E5FF)
                  )
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
