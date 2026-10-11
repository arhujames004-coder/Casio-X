package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.UnitConverterEngine
import com.example.model.ScientificConstant
import com.example.ui.theme.*

@Composable
fun UnitConverterScreen(
  onInsertIntoCalculator: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableIntStateOf(0) } // 0: Unit Converter, 1: Scientific Constants

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CasioChassisDark)
      .padding(12.dp)
  ) {
    // Top Tab Selector
    TabRow(
      selectedTabIndex = activeTab,
      containerColor = CasioChassisSurface,
      contentColor = CasioShiftGold,
      divider = {}
    ) {
      Tab(
        selected = activeTab == 0,
        onClick = { activeTab = 0 },
        text = { Text("Unit Converter", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
      Tab(
        selected = activeTab == 1,
        onClick = { activeTab = 1 },
        text = { Text("Physical Constants", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (activeTab == 0) {
      UnitConverterTab(onInsertIntoCalculator = onInsertIntoCalculator)
    } else {
      ScientificConstantsTab(onInsertIntoCalculator = onInsertIntoCalculator)
    }
  }
}

@Composable
private fun UnitConverterTab(onInsertIntoCalculator: (String) -> Unit) {
  val categories = UnitConverterEngine.categories
  var selectedCategory by remember { mutableStateOf(categories[0]) }

  var fromUnit by remember(selectedCategory) { mutableStateOf(selectedCategory.units[0]) }
  var toUnit by remember(selectedCategory) {
    mutableStateOf(if (selectedCategory.units.size > 1) selectedCategory.units[1] else selectedCategory.units[0])
  }

  var inputValue by remember { mutableStateOf("1") }

  val convertedValue = remember(inputValue, fromUnit, toUnit, selectedCategory) {
    val d = inputValue.toDoubleOrNull() ?: 0.0
    UnitConverterEngine.convert(d, fromUnit, toUnit, selectedCategory.id)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    // Categories horizontal chip scroller
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(categories) { cat ->
        val isSelected = selectedCategory.id == cat.id
        FilterChip(
          selected = isSelected,
          onClick = { selectedCategory = cat },
          label = { Text(cat.name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CasioShiftGold,
            selectedLabelColor = Color.Black,
            containerColor = CasioChassisSurface,
            labelColor = CasioKeyTextSecondary
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // From Box
    Surface(
      color = CasioChassisSurface,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text("From:", color = CasioKeyTextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 18.sp, color = CasioKeyTextPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CasioShiftGold,
              unfocusedBorderColor = CasioChassisBorder
            ),
            shape = RoundedCornerShape(8.dp)
          )

          // Unit selector pill
          UnitDropdown(
            current = fromUnit,
            options = selectedCategory.units,
            onSelect = { fromUnit = it }
          )
        }
      }
    }

    // Swap button with BIG bold arrows
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      IconButton(
        onClick = {
          val temp = fromUnit
          fromUnit = toUnit
          toUnit = temp
        },
        modifier = Modifier
          .background(CasioKeyOperation, RoundedCornerShape(50))
          .border(1.2.dp, CasioShiftGold.copy(alpha = 0.5f), RoundedCornerShape(50))
          .size(44.dp)
      ) {
        Icon(Icons.Default.SwapVert, contentDescription = "Swap Units", tint = CasioShiftGold, modifier = Modifier.size(26.dp))
      }
    }

    // To Box (Result)
    Surface(
      color = ScreenOledBg,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, CasioShiftGold),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text("To (Converted Result):", color = CasioKeyTextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = formatConverted(convertedValue),
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF00E5FF),
            modifier = Modifier.weight(1f)
          )

          UnitDropdown(
            current = toUnit,
            options = selectedCategory.units,
            onSelect = { toUnit = it }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Insert into calculator button
    Button(
      onClick = {
        onInsertIntoCalculator(formatConverted(convertedValue))
      },
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(containerColor = CasioKeyEquals),
      shape = RoundedCornerShape(8.dp)
    ) {
      Icon(Icons.Default.Input, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Insert Result into Calculator", fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun ScientificConstantsTab(onInsertIntoCalculator: (String) -> Unit) {
  val constants = UnitConverterEngine.scientificConstants

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(constants) { constant ->
      Surface(
        onClick = { onInsertIntoCalculator(constant.value.toString()) },
        color = CasioChassisSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = constant.symbol,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = CasioShiftGold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = constant.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = CasioKeyTextPrimary
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${constant.valueString} ${constant.unit}",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 12.sp,
              color = Color(0xFF00E5FF)
            )
          }

          Icon(
            imageVector = Icons.Default.Input,
            contentDescription = "Insert into expression",
            tint = CasioKeyTextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun UnitDropdown(
  current: UnitConverterEngine.UnitItem,
  options: List<UnitConverterEngine.UnitItem>,
  onSelect: (UnitConverterEngine.UnitItem) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  Box {
    Surface(
      onClick = { expanded = true },
      color = CasioKeyOperation,
      shape = RoundedCornerShape(8.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, CasioChassisBorder)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(current.symbol, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, color = CasioShiftGold, fontSize = 14.sp)
        Icon(Icons.Default.ArrowDownward, contentDescription = "Dropdown", tint = CasioShiftGold, modifier = Modifier.size(20.dp))
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.background(CasioChassisSurface)
    ) {
      options.forEach { item ->
        DropdownMenuItem(
          text = {
            Text("${item.name} (${item.symbol})", color = CasioKeyTextPrimary, fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp)
          },
          onClick = {
            onSelect(item)
            expanded = false
          }
        )
      }
    }
  }
}

private fun formatConverted(d: Double): String {
  if (d.isNaN()) return "0"
  if (d.isInfinite()) return if (d > 0) "∞" else "-∞"
  if (kotlin.math.abs(d) >= 1e12 || (kotlin.math.abs(d) > 0 && kotlin.math.abs(d) < 1e-6)) {
    return String.format(java.util.Locale.US, "%.6e", d)
  }
  return String.format(java.util.Locale.US, "%.6f", d).trimEnd('0').trimEnd('.')
}
