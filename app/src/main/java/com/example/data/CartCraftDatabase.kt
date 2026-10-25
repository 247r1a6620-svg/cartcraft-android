package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GroceryItem::class], version = 1, exportSchema = false)
abstract class CartCraftDatabase : RoomDatabase() {

  abstract fun groceryDao(): GroceryDao

  companion object {
    @Volatile
    private var INSTANCE: CartCraftDatabase? = null

    fun getDatabase(context: Context): CartCraftDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CartCraftDatabase::class.java,
          "cartcraft_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
