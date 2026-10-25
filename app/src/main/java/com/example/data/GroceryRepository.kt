package com.example.data

import kotlinx.coroutines.flow.Flow

class GroceryRepository(private val groceryDao: GroceryDao) {

  val allItems: Flow<List<GroceryItem>> = groceryDao.getAllItems()

  suspend fun insertItem(item: GroceryItem): Long = groceryDao.insertItem(item)

  suspend fun insertItems(items: List<GroceryItem>) = groceryDao.insertItems(items)

  suspend fun updateItem(item: GroceryItem) = groceryDao.updateItem(item)

  suspend fun deleteItem(item: GroceryItem) = groceryDao.deleteItem(item)

  suspend fun deleteItemById(id: Long) = groceryDao.deleteItemById(id)

  suspend fun toggleChecked(id: Long, currentChecked: Boolean) {
    groceryDao.updateCheckedStatus(id, !currentChecked)
  }

  suspend fun setChecked(id: Long, isChecked: Boolean) {
    groceryDao.updateCheckedStatus(id, isChecked)
  }

  suspend fun setAllChecked(isChecked: Boolean) {
    groceryDao.setAllCheckedStatus(isChecked)
  }

  suspend fun deleteCompleted() {
    groceryDao.deleteCompletedItems()
  }

  suspend fun clearAll() {
    groceryDao.clearAllItems()
  }

  suspend fun populateSampleIfEmpty() {
    if (groceryDao.getItemCount() == 0) {
      groceryDao.insertItems(GroceryConstants.SAMPLE_GROCERIES)
    }
  }
}
