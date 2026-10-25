package com.example.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val quantity: String = "1",
  val category: String = "Produce",
  val isChecked: Boolean = false,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

data class GroceryCategory(
  val name: String,
  val emoji: String,
  val color: Long // Hex color representation for display
)

object GroceryConstants {
  val CATEGORIES = listOf(
    GroceryCategory("Produce", "🥦", 0xFF2E7D32),
    GroceryCategory("Dairy & Eggs", "🥛", 0xFF0277BD),
    GroceryCategory("Bakery", "🍞", 0xFFB06000),
    GroceryCategory("Meat & Seafood", "🥩", 0xFFC62828),
    GroceryCategory("Pantry", "🥫", 0xFFD84315),
    GroceryCategory("Snacks & Sweets", "🍿", 0xFF6A1B9A),
    GroceryCategory("Beverages", "🧃", 0xFF00838F),
    GroceryCategory("Frozen", "🧊", 0xFF1565C0),
    GroceryCategory("Household", "🧹", 0xFF37474F),
    GroceryCategory("Other", "📦", 0xFF546E7A)
  )

  fun getCategoryEmoji(categoryName: String): String {
    return CATEGORIES.firstOrNull { it.name.equals(categoryName, ignoreCase = true) }?.emoji ?: "🛒"
  }

  fun getCategoryColor(categoryName: String): Color {
    val hex = CATEGORIES.firstOrNull { it.name.equals(categoryName, ignoreCase = true) }?.color ?: 0xFF455A64
    return Color(hex)
  }

  val SAMPLE_GROCERIES = listOf(
    GroceryItem(name = "Organic Bananas", quantity = "1 bunch", category = "Produce", notes = "Slightly green"),
    GroceryItem(name = "Whole Milk", quantity = "1 gallon", category = "Dairy & Eggs"),
    GroceryItem(name = "Fresh Sourdough Bread", quantity = "1 loaf", category = "Bakery"),
    GroceryItem(name = "Chicken Breast", quantity = "2 lbs", category = "Meat & Seafood"),
    GroceryItem(name = "Extra Virgin Olive Oil", quantity = "750 ml", category = "Pantry"),
    GroceryItem(name = "Sparkling Water", quantity = "6-pack", category = "Beverages"),
    GroceryItem(name = "Dark Chocolate Almonds", quantity = "1 bag", category = "Snacks & Sweets")
  )
}
