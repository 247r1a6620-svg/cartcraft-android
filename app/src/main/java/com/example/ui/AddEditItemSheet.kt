package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.GroceryConstants
import com.example.data.GroceryItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditItemSheet(
  item: GroceryItem?,
  onDismiss: () -> Unit,
  onSave: (name: String, quantity: String, category: String, notes: String) -> Unit,
  sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
  var name by remember(item) { mutableStateOf(item?.name ?: "") }
  var quantity by remember(item) { mutableStateOf(item?.quantity ?: "1") }
  var category by remember(item) { mutableStateOf(item?.category ?: "Produce") }
  var notes by remember(item) { mutableStateOf(item?.notes ?: "") }
  var nameError by remember { mutableStateOf(false) }

  val nameFocusRequester = remember { FocusRequester() }

  LaunchedEffect(item) {
    if (item == null) {
      nameFocusRequester.requestFocus()
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = {
      Spacer(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 8.dp)
          .size(width = 40.dp, height = 4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (item != null) Icons.Default.Edit else Icons.Default.ShoppingBag,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (item != null) "Edit Grocery Item" else "Add Grocery Item",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_sheet_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Item Name Field
      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
          if (nameError && it.isNotBlank()) nameError = false
        },
        label = { Text("Item Name *") },
        placeholder = { Text("e.g. Avocado, Whole Milk, Oat Bread") },
        isError = nameError,
        supportingText = {
          if (nameError) {
            Text(
              text = "Item name is required",
              color = MaterialTheme.colorScheme.error
            )
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .focusRequester(nameFocusRequester)
          .testTag("item_name_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Quantity Row & Steppers
      Text(
        text = "Quantity / Amount",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = quantity,
          onValueChange = { quantity = it },
          placeholder = { Text("e.g. 2, 500g, 1 bag") },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("item_quantity_input"),
          shape = RoundedCornerShape(12.dp)
        )

        // Decrement button
        IconButton(
          onClick = {
            val num = quantity.filter { it.isDigit() }.toIntOrNull() ?: 1
            if (num > 1) {
              val unit = quantity.filter { !it.isDigit() }.trim()
              quantity = if (unit.isNotEmpty()) "${num - 1} $unit" else "${num - 1}"
            }
          },
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .testTag("qty_decrement_button")
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Decrease Quantity")
        }

        // Increment button
        IconButton(
          onClick = {
            val num = quantity.filter { it.isDigit() }.toIntOrNull() ?: 1
            val unit = quantity.filter { !it.isDigit() }.trim()
            quantity = if (unit.isNotEmpty()) "${num + 1} $unit" else "${num + 1}"
          },
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .testTag("qty_increment_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
        }
      }

      // Quick quantity suggestions
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("1", "2", "3", "500g", "1 kg", "1 pack", "1 bunch", "1 loaf").forEach { suggestion ->
          FilterChip(
            selected = quantity == suggestion,
            onClick = { quantity = suggestion },
            label = { Text(suggestion, style = MaterialTheme.typography.bodySmall) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Category Picker
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Category,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Category",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        GroceryConstants.CATEGORIES.forEach { cat ->
          val isSelected = category.equals(cat.name, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { category = cat.name },
            label = {
              Text("${cat.emoji} ${cat.name}", style = MaterialTheme.typography.bodySmall)
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("category_chip_${cat.name.replace(" ", "_")}")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Notes (Optional)
      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Notes & Brand (Optional)")
          }
        },
        placeholder = { Text("e.g. Low sodium, Aisle 3, Preferred brand") },
        maxLines = 3,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("item_notes_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("cancel_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            if (name.isBlank()) {
              nameError = true
            } else {
              onSave(name, quantity, category, notes)
            }
          },
          modifier = Modifier
            .weight(1.5f)
            .height(50.dp)
            .testTag("save_item_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          Icon(
            imageVector = if (item != null) Icons.Default.Edit else Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (item != null) "Update Item" else "Add to Checklist",
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
