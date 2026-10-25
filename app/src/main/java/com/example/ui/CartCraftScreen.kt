package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GroceryConstants
import com.example.data.GroceryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCraftScreen(
  viewModel: CartCraftViewModel,
  onShareApk: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var menuExpanded by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CartCraft",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Grocery Checklist",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.testTag("menu_more_button")
          ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu Options")
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("Check All Items") },
              leadingIcon = { Icon(Icons.Default.DoneAll, contentDescription = null) },
              onClick = {
                viewModel.checkAll()
                menuExpanded = false
              },
              modifier = Modifier.testTag("menu_check_all")
            )
            DropdownMenuItem(
              text = { Text("Uncheck All Items") },
              leadingIcon = { Icon(Icons.Default.RemoveDone, contentDescription = null) },
              onClick = {
                viewModel.uncheckAll()
                menuExpanded = false
              },
              modifier = Modifier.testTag("menu_uncheck_all")
            )
            if (uiState.completedCount > 0) {
              DropdownMenuItem(
                text = { Text("Clear Completed (${uiState.completedCount})") },
                leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null) },
                onClick = {
                  viewModel.showClearCompletedDialog(true)
                  menuExpanded = false
                },
                modifier = Modifier.testTag("menu_clear_completed")
              )
            }
            DropdownMenuItem(
              text = { Text("Load Sample Items") },
              leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
              onClick = {
                viewModel.loadSampleGroceries()
                menuExpanded = false
              },
              modifier = Modifier.testTag("menu_load_samples")
            )
            DropdownMenuItem(
              text = { Text("Share CartCraft APK") },
              leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
              onClick = {
                onShareApk()
                menuExpanded = false
              },
              modifier = Modifier.testTag("menu_share_apk")
            )
            DropdownMenuItem(
              text = { Text("Clear All Items") },
              leadingIcon = {
                Icon(
                  Icons.Default.Delete,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.error
                )
              },
              onClick = {
                viewModel.showClearAllDialog(true)
                menuExpanded = false
              },
              modifier = Modifier.testTag("menu_clear_all")
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { viewModel.openAddSheet() },
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("Add Item", fontWeight = FontWeight.SemiBold) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .navigationBarsPadding()
          .testTag("add_item_fab")
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Progress summary banner
      if (uiState.totalCount > 0) {
        GroceryProgressCard(
          total = uiState.totalCount,
          completed = uiState.completedCount,
          active = uiState.activeCount,
          progress = uiState.completionProgress,
          onClearCompleted = { viewModel.showClearCompletedDialog(true) },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }

      // Search field
      OutlinedTextField(
        value = uiState.searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search items, notes, or categories...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        trailingIcon = {
          if (uiState.searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear search")
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("search_text_field")
      )

      // Tabs: All, To Buy, Completed
      PrimaryTabRow(
        selectedTabIndex = uiState.activeTab.ordinal,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = uiState.activeTab == FilterTab.ALL,
          onClick = { viewModel.setFilterTab(FilterTab.ALL) },
          text = {
            Text("All (${uiState.totalCount})", fontWeight = FontWeight.SemiBold)
          },
          modifier = Modifier.testTag("tab_all")
        )
        Tab(
          selected = uiState.activeTab == FilterTab.TO_BUY,
          onClick = { viewModel.setFilterTab(FilterTab.TO_BUY) },
          text = {
            Text("To Buy (${uiState.activeCount})", fontWeight = FontWeight.SemiBold)
          },
          modifier = Modifier.testTag("tab_to_buy")
        )
        Tab(
          selected = uiState.activeTab == FilterTab.COMPLETED,
          onClick = { viewModel.setFilterTab(FilterTab.COMPLETED) },
          text = {
            Text("Done (${uiState.completedCount})", fontWeight = FontWeight.SemiBold)
          },
          modifier = Modifier.testTag("tab_completed")
        )
      }

      // Categories Horizontal Scroll Filter
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = uiState.selectedCategory == null,
          onClick = { viewModel.selectCategory(null) },
          label = { Text("All Categories") },
          leadingIcon = {
            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("category_filter_all")
        )

        GroceryConstants.CATEGORIES.forEach { category ->
          val isSelected = uiState.selectedCategory.equals(category.name, ignoreCase = true)
          val count = uiState.items.count { it.category.equals(category.name, ignoreCase = true) }
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.selectCategory(category.name) },
            label = {
              Text("${category.emoji} ${category.name}" + if (count > 0) " ($count)" else "")
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("category_filter_${category.name.replace(" ", "_")}")
          )
        }
      }

      // Grocery items list
      if (uiState.filteredItems.isEmpty()) {
        EmptyGroceryList(
          isFilterActive = uiState.searchQuery.isNotEmpty() || uiState.selectedCategory != null || uiState.activeTab != FilterTab.ALL,
          onClearFilters = {
            viewModel.setSearchQuery("")
            viewModel.selectCategory(null)
            viewModel.setFilterTab(FilterTab.ALL)
          },
          onAddSample = { viewModel.loadSampleGroceries() },
          onAddNew = { viewModel.openAddSheet() },
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .testTag("grocery_items_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(
            items = uiState.filteredItems,
            key = { it.id }
          ) { item ->
            GroceryItemCard(
              item = item,
              onToggleChecked = { viewModel.toggleItemChecked(item) },
              onEdit = { viewModel.openEditSheet(item) },
              onDelete = { viewModel.requestDeleteItem(item) },
              modifier = Modifier.testTag("item_card_${item.id}")
            )
          }
        }
      }
    }
  }

  // Add/Edit Bottom Sheet
  if (uiState.isAddEditSheetOpen) {
    AddEditItemSheet(
      item = uiState.editingItem,
      onDismiss = { viewModel.closeSheet() },
      onSave = { name, quantity, category, notes ->
        viewModel.saveItem(name, quantity, category, notes)
      }
    )
  }

  // Confirm Single Item Delete Dialog
  if (uiState.itemPendingDelete != null) {
    val item = uiState.itemPendingDelete!!
    AlertDialog(
      onDismissRequest = { viewModel.cancelDeleteItem() },
      title = { Text("Remove Item?") },
      text = { Text("Are you sure you want to remove \"${item.name}\" from your checklist?") },
      confirmButton = {
        TextButton(
          onClick = { viewModel.confirmDeleteItem() },
          modifier = Modifier.testTag("confirm_delete_button")
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { viewModel.cancelDeleteItem() },
          modifier = Modifier.testTag("cancel_delete_button")
        ) {
          Text("Cancel")
        }
      }
    )
  }

  // Confirm Clear Completed Dialog
  if (uiState.showClearCompletedDialog) {
    AlertDialog(
      onDismissRequest = { viewModel.showClearCompletedDialog(false) },
      title = { Text("Clear Completed Items?") },
      text = { Text("This will permanently remove ${uiState.completedCount} completed items from your checklist.") },
      confirmButton = {
        TextButton(
          onClick = { viewModel.confirmClearCompleted() },
          modifier = Modifier.testTag("confirm_clear_completed_button")
        ) {
          Text("Clear Completed", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.showClearCompletedDialog(false) }) {
          Text("Cancel")
        }
      }
    )
  }

  // Confirm Clear All Dialog
  if (uiState.showClearAllDialog) {
    AlertDialog(
      onDismissRequest = { viewModel.showClearAllDialog(false) },
      title = { Text("Clear Entire Checklist?") },
      text = { Text("This will remove all items from your CartCraft checklist.") },
      confirmButton = {
        TextButton(
          onClick = { viewModel.confirmClearAll() },
          modifier = Modifier.testTag("confirm_clear_all_button")
        ) {
          Text("Clear All", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.showClearAllDialog(false) }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun GroceryProgressCard(
  total: Int,
  completed: Int,
  active: Int,
  progress: Float,
  onClearCompleted: () -> Unit,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress_anim")
  val percent = (animatedProgress * 100).toInt()

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    )
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (percent == 100) "Shopping Complete! 🎉" else "Cart Progress",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = "$completed of $total items checked ($percent%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }

        if (completed > 0) {
          TextButton(
            onClick = onClearCompleted,
            modifier = Modifier.testTag("quick_clear_completed_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear Done", style = MaterialTheme.typography.labelMedium)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
      )
    }
  }
}

@Composable
fun GroceryItemCard(
  item: GroceryItem,
  onToggleChecked: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cardBackground by animateColorAsState(
    targetValue = if (item.isChecked) {
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    } else {
      MaterialTheme.colorScheme.surface
    },
    label = "card_bg_color"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onToggleChecked() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = cardBackground),
    elevation = CardDefaults.cardElevation(
      defaultElevation = if (item.isChecked) 0.dp else 1.5.dp
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Checkbox
      Checkbox(
        checked = item.isChecked,
        onCheckedChange = { onToggleChecked() },
        colors = CheckboxDefaults.colors(
          checkedColor = MaterialTheme.colorScheme.primary,
          checkmarkColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.testTag("checkbox_${item.id}")
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Item Content
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.SemiBold,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
            color = if (item.isChecked) {
              MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
              MaterialTheme.colorScheme.onSurface
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          // Quantity pill
          if (item.quantity.isNotBlank()) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
              Text(
                text = item.quantity,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Category Tag & Notes
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Category badge
          val categoryEmoji = GroceryConstants.getCategoryEmoji(item.category)
          val categoryColor = GroceryConstants.getCategoryColor(item.category)

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(categoryColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "$categoryEmoji ${item.category}",
              style = MaterialTheme.typography.labelSmall,
              color = categoryColor,
              fontWeight = FontWeight.Medium
            )
          }

          if (item.notes.isNotBlank()) {
            Text(
              text = "• ${item.notes}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Action buttons: Edit & Delete
      IconButton(
        onClick = onEdit,
        modifier = Modifier
          .size(36.dp)
          .testTag("edit_item_${item.id}")
      ) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Edit Item",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier
          .size(36.dp)
          .testTag("delete_item_${item.id}")
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Delete Item",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun EmptyGroceryList(
  isFilterActive: Boolean,
  onClearFilters: () -> Unit,
  onAddSample: () -> Unit,
  onAddNew: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(90.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isFilterActive) Icons.Default.Search else Icons.Default.ShoppingCart,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(44.dp)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = if (isFilterActive) "No matching grocery items" else "Your grocery list is empty",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = if (isFilterActive) {
        "Try clearing your search query or category filters."
      } else {
        "Add groceries for your upcoming shopping trip or load a starter checklist."
      },
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    if (isFilterActive) {
      TextButton(
        onClick = onClearFilters,
        modifier = Modifier.testTag("clear_filter_button")
      ) {
        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Clear Filters")
      }
    } else {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TextButton(
          onClick = onAddSample,
          modifier = Modifier.testTag("load_samples_empty_button")
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Load Samples")
        }

        TextButton(
          onClick = onAddNew,
          modifier = Modifier.testTag("add_item_empty_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add First Item")
        }
      }
    }
  }
}
