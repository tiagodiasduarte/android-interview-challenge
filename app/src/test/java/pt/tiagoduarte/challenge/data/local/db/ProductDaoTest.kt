package pt.tiagoduarte.challenge.data.local.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pt.tiagoduarte.challenge.random.nextProductEntity
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `given an empty database when observeAll is called then emits an empty list`() = runTest {
        // Given an empty database

        // When
        val products = dao.observeAll().first()

        // Then
        assertTrue(products.isEmpty())
    }

    @Test
    fun `given inserted products when observeAll is called then emits them ordered by id`() = runTest {
        // Given
        val first = Random.nextProductEntity(id = 1)
        val second = Random.nextProductEntity(id = 2)
        val third = Random.nextProductEntity(id = 3)
        dao.insertAll(listOf(third, first, second))

        // When
        val products = dao.observeAll().first()

        // Then
        assertEquals(listOf(first, second, third), products)
    }

    @Test
    fun `given a product with an existing id when insertAll is called then replaces it`() = runTest {
        // Given
        val original = Random.nextProductEntity(id = 1)
        val updated = Random.nextProductEntity(id = 1)
        dao.insertAll(listOf(original))

        // When
        dao.insertAll(listOf(updated))

        // Then
        assertEquals(listOf(updated), dao.observeAll().first())
    }

    @Test
    fun `given an inserted product when observeById is called with its id then emits the product`() = runTest {
        // Given
        val product = Random.nextProductEntity(id = 1)
        dao.insertAll(listOf(product, Random.nextProductEntity(id = 2)))

        // When
        val result = dao.observeById(product.id).first()

        // Then
        assertEquals(product, result)
    }

    @Test
    fun `given no product with the id when observeById is called then emits null`() = runTest {
        // Given
        dao.insertAll(listOf(Random.nextProductEntity(id = 1)))

        // When
        val result = dao.observeById(2).first()

        // Then
        assertNull(result)
    }

    @Test
    fun `given inserted products when clearAll is called then removes every product`() = runTest {
        // Given
        dao.insertAll(listOf(Random.nextProductEntity(id = 1), Random.nextProductEntity(id = 2)))

        // When
        dao.clearAll()

        // Then
        assertTrue(dao.observeAll().first().isEmpty())
    }
}
