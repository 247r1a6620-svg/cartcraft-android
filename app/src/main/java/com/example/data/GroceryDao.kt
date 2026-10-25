package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

  @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, createdAt DESC")
  fun getAllItems(): Flow<List<GroceryItem>>

  @Query("SELECT * FROM grocery_items WHERE id = :id LIMIT 1")
  suspend fun getItemById(id: Long): GroceryItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: GroceryItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItems(items: List<GroceryItem>): List<Long>

  @Update
  suspend fun updateItem(item: GroceryItem)

  @Delete
  suspend fun deleteItem(item: GroceryItem)

  @Query("DELETE FROM grocery_items WHERE id = :id")
  suspend fun deleteItemById(id: Long)

  @Query("UPDATE grocery_items SET isChecked = :isChecked WHERE id = :id")
  suspend fun updateCheckedStatus(id: Long, isChecked: Boolean)

  @Query("UPDATE grocery_items SET isChecked = :isChecked")
  suspend fun setAllCheckedStatus(isChecked: Boolean)

  @Query("DELETE FROM grocery_items WHERE isChecked = 1")
  suspend fun deleteCompletedItems()

  @Query("DELETE FROM grocery_items")
  suspend fun clearAllItems()

  @Query("SELECT COUNT(*) FROM grocery_items")
  suspend fun getItemCount(): Int
}
