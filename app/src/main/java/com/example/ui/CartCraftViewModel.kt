package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CartCraftDatabase
import com.example.data.GroceryConstants
import com.example.data.GroceryItem
import com.example.data.GroceryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterTab {
  ALL,
  TO_BUY,
  COMPLETED
}

enum class SortOption(val displayName: String) {
  DEFAULT("Smart"),
  ALPHABETICAL("A-Z"),
  CATEGORY("Category")
}

data class CartCraftUiState(
  val items: List<GroceryItem> = emptyList(),
  val filteredItems: List<GroceryItem> = emptyList(),
  val selectedCategory: String? = null,
  val activeTab: FilterTab = FilterTab.ALL,
  val searchQuery: String = "",
  val sortOption: SortOption = SortOption.DEFAULT,
  val isAddEditSheetOpen: Boolean = false,
  val editingItem: GroceryItem? = null,
  val itemPendingDelete: GroceryItem? = null,
  val showClearCompletedDialog: Boolean = false,
  val showClearAllDialog: Boolean = false,
  val totalCount: Int = 0,
  val completedCount: Int = 0,
  val activeCount: Int = 0,
  val completionProgress: Float = 0f
)

class CartCraftViewModel(
  application: Application,
  private val repository: GroceryRepository = GroceryRepository(
    CartCraftDatabase.getDatabase(application).groceryDao()
  )
) : AndroidViewModel(application) {

  private val _selectedCategory = MutableStateFlow<String?>(null)
  private val _activeTab = MutableStateFlow(FilterTab.ALL)
  private val _searchQuery = MutableStateFlow("")
  private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
  private val _isAddEditSheetOpen = MutableStateFlow(false)
  private val _editingItem = MutableStateFlow<GroceryItem?>(null)
  private val _itemPendingDelete = MutableStateFlow<GroceryItem?>(null)
  private val _showClearCompletedDialog = MutableStateFlow(false)
  private val _showClearAllDialog = MutableStateFlow(false)

  init {
    viewModelScope.launch {
      repository.populateSampleIfEmpty()
    }
  }

  val uiState: StateFlow<CartCraftUiState> = combine(
    repository.allItems,
    _selectedCategory,
    _activeTab,
    _searchQuery,
    _sortOption,
    _isAddEditSheetOpen,
    _editingItem,
    _itemPendingDelete,
    _showClearCompletedDialog,
    _showClearAllDialog
  ) { args: Array<Any?> ->
    @Suppress("UNCHECKED_CAST")
    val items = args[0] as List<GroceryItem>
    val selectedCategory = args[1] as String?
    val activeTab = args[2] as FilterTab
    val searchQuery = args[3] as String
    val sortOption = args[4] as SortOption
    val isAddEditSheetOpen = args[5] as Boolean
    val editingItem = args[6] as GroceryItem?
    val itemPendingDelete = args[7] as GroceryItem?
    val showClearCompletedDialog = args[8] as Boolean
    val showClearAllDialog = args[9] as Boolean

    val total = items.size
    val completed = items.count { it.isChecked }
    val active = total - completed
    val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f

    val filtered = items.filter { item ->
      val matchesCategory = selectedCategory == null || item.category.equals(selectedCategory, ignoreCase = true)
      val matchesTab = when (activeTab) {
        FilterTab.ALL -> true
        FilterTab.TO_BUY -> !item.isChecked
        FilterTab.COMPLETED -> item.isChecked
      }
      val matchesQuery = searchQuery.isBlank() ||
        item.name.contains(searchQuery, ignoreCase = true) ||
        item.category.contains(searchQuery, ignoreCase = true) ||
        item.notes.contains(searchQuery, ignoreCase = true)

      matchesCategory && matchesTab && matchesQuery
    }.let { list ->
      when (sortOption) {
        SortOption.DEFAULT -> list.sortedWith(
          compareBy<GroceryItem> { it.isChecked }.thenByDescending { it.createdAt }
        )
        SortOption.ALPHABETICAL -> list.sortedWith(
          compareBy<GroceryItem> { it.isChecked }.thenBy { it.name.lowercase() }
        )
        SortOption.CATEGORY -> list.sortedWith(
          compareBy<GroceryItem> { it.isChecked }.thenBy { it.category }.thenBy { it.name.lowercase() }
        )
      }
    }

    CartCraftUiState(
      items = items,
      filteredItems = filtered,
      selectedCategory = selectedCategory,
      activeTab = activeTab,
      searchQuery = searchQuery,
      sortOption = sortOption,
      isAddEditSheetOpen = isAddEditSheetOpen,
      editingItem = editingItem,
      itemPendingDelete = itemPendingDelete,
      showClearCompletedDialog = showClearCompletedDialog,
      showClearAllDialog = showClearAllDialog,
      totalCount = total,
      completedCount = completed,
      activeCount = active,
      completionProgress = progress
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = CartCraftUiState()
  )

  fun toggleItemChecked(item: GroceryItem) {
    viewModelScope.launch {
      repository.toggleChecked(item.id, item.isChecked)
    }
  }

  fun saveItem(name: String, quantity: String, category: String, notes: String) {
    val cleanName = name.trim()
    if (cleanName.isBlank()) return

    val cleanQty = if (quantity.trim().isBlank()) "1" else quantity.trim()
    val cleanCategory = if (category.trim().isBlank()) "Produce" else category.trim()

    viewModelScope.launch {
      val currentEditing = _editingItem.value
      if (currentEditing != null) {
        repository.updateItem(
          currentEditing.copy(
            name = cleanName,
            quantity = cleanQty,
            category = cleanCategory,
            notes = notes.trim()
          )
        )
      } else {
        repository.insertItem(
          GroceryItem(
            name = cleanName,
            quantity = cleanQty,
            category = cleanCategory,
            notes = notes.trim()
          )
        )
      }
      closeSheet()
    }
  }

  fun openAddSheet() {
    _editingItem.value = null
    _isAddEditSheetOpen.value = true
  }

  fun openEditSheet(item: GroceryItem) {
    _editingItem.value = item
    _isAddEditSheetOpen.value = true
  }

  fun closeSheet() {
    _isAddEditSheetOpen.value = false
    _editingItem.value = null
  }

  fun requestDeleteItem(item: GroceryItem) {
    _itemPendingDelete.value = item
  }

  fun cancelDeleteItem() {
    _itemPendingDelete.value = null
  }

  fun confirmDeleteItem() {
    val item = _itemPendingDelete.value ?: return
    viewModelScope.launch {
      repository.deleteItem(item)
      _itemPendingDelete.value = null
    }
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun selectCategory(category: String?) {
    _selectedCategory.value = if (_selectedCategory.value == category) null else category
  }

  fun setFilterTab(tab: FilterTab) {
    _activeTab.value = tab
  }

  fun setSortOption(sort: SortOption) {
    _sortOption.value = sort
  }

  fun checkAll() {
    viewModelScope.launch {
      repository.setAllChecked(true)
    }
  }

  fun uncheckAll() {
    viewModelScope.launch {
      repository.setAllChecked(false)
    }
  }

  fun showClearCompletedDialog(show: Boolean) {
    _showClearCompletedDialog.value = show
  }

  fun confirmClearCompleted() {
    viewModelScope.launch {
      repository.deleteCompleted()
      _showClearCompletedDialog.value = false
    }
  }

  fun showClearAllDialog(show: Boolean) {
    _showClearAllDialog.value = show
  }

  fun confirmClearAll() {
    viewModelScope.launch {
      repository.clearAll()
      _showClearAllDialog.value = false
    }
  }

  fun loadSampleGroceries() {
    viewModelScope.launch {
      repository.insertItems(GroceryConstants.SAMPLE_GROCERIES)
    }
  }

  class Factory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(CartCraftViewModel::class.java)) {
        return CartCraftViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
