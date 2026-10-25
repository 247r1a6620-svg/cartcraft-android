package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CartCraftDatabase
import com.example.data.GroceryDao
import com.example.data.GroceryItem
import com.example.data.GroceryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: CartCraftDatabase
  private lateinit var dao: GroceryDao
  private lateinit var repository: GroceryRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, CartCraftDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.groceryDao()
    repository = GroceryRepository(dao)
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun appNameResourceIsCartCraft() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CartCraft", appName)
  }

  @Test
  fun testInsertAndRetrieveGroceryItem() = runBlocking {
    val item = GroceryItem(
      name = "Organic Honey Crisp Apples",
      quantity = "4 pcs",
      category = "Produce",
      notes = "Crisp and sweet"
    )
    val id = repository.insertItem(item)
    assertTrue(id > 0)

    val items = repository.allItems.first()
    assertEquals(1, items.size)
    assertEquals("Organic Honey Crisp Apples", items[0].name)
    assertEquals("4 pcs", items[0].quantity)
    assertEquals("Produce", items[0].category)
    assertFalse(items[0].isChecked)
  }

  @Test
  fun testToggleItemChecked() = runBlocking {
    val item = GroceryItem(name = "Almond Milk", quantity = "1 carton", category = "Dairy & Eggs")
    val id = repository.insertItem(item)

    repository.toggleChecked(id, currentChecked = false)
    var items = repository.allItems.first()
    assertTrue(items[0].isChecked)

    repository.toggleChecked(id, currentChecked = true)
    items = repository.allItems.first()
    assertFalse(items[0].isChecked)
  }

  @Test
  fun testDeleteCompletedItems() = runBlocking {
    val item1 = GroceryItem(name = "Bananas", isChecked = true)
    val item2 = GroceryItem(name = "Bread", isChecked = false)
    val item3 = GroceryItem(name = "Butter", isChecked = true)

    repository.insertItems(listOf(item1, item2, item3))
    var items = repository.allItems.first()
    assertEquals(3, items.size)

    repository.deleteCompleted()
    items = repository.allItems.first()
    assertEquals(1, items.size)
    assertEquals("Bread", items[0].name)
  }

  @Test
  fun testClearAllItems() = runBlocking {
    val item1 = GroceryItem(name = "Eggs")
    val item2 = GroceryItem(name = "Cheese")

    repository.insertItems(listOf(item1, item2))
    assertEquals(2, repository.allItems.first().size)

    repository.clearAll()
    assertEquals(0, repository.allItems.first().size)
  }
}
