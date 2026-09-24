package pt.tiagoduarte.challenge.data.local.db

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import androidx.room.RoomRawQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pt.tiagoduarte.challenge.random.nextProductEntity
import pt.tiagoduarte.challenge.rules.DatabaseTestRule
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    @get:Rule
    val databaseRule = DatabaseTestRule(AppDatabase::class)

    private val dao: ProductDao get() = databaseRule.database.productDao()

    @Test
    fun `given a product with an existing id when insertAll is called then replaces it`() = runTest {
        // Given
        val original = Random.nextProductEntity(id = 1)
        val updated = Random.nextProductEntity(id = 1)
        dao.insertAll(listOf(original))

        // When
        dao.insertAll(listOf(updated))

        // Then
        assertEquals(listOf(updated), savedProducts())
    }

    @Test
    fun `given inserted products when the paging source loads the first page then returns them ordered by id`() =
        runTest {
            // Given
            val first = Random.nextProductEntity(id = 1)
            val second = Random.nextProductEntity(id = 2)
            val third = Random.nextProductEntity(id = 3)
            dao.insertAll(listOf(third, first, second))
            val pager = TestPager(
                PagingConfig(pageSize = 2, initialLoadSize = 2, enablePlaceholders = false),
                dao.pagingSource(RoomRawQuery("SELECT * FROM products ORDER BY id")),
            )

            // When
            val page = pager.refresh() as PagingSource.LoadResult.Page

            // Then
            assertEquals(listOf(first, second), page.data)
        }

    @Test
    fun `given an empty database when observeHasProducts is called then emits false`() = runTest {
        // Given an empty database

        // When
        val hasProducts = dao.observeHasProducts().first()

        // Then
        assertFalse(hasProducts)
    }

    @Test
    fun `given an inserted product when observeHasProducts is called then emits true`() = runTest {
        // Given
        dao.insertAll(listOf(Random.nextProductEntity(id = 1)))

        // When
        val hasProducts = dao.observeHasProducts().first()

        // Then
        assertTrue(hasProducts)
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
    fun `given saved products when replaceAll is called then only the new products remain`() = runTest {
        // Given
        dao.insertAll(listOf(Random.nextProductEntity(id = 1), Random.nextProductEntity(id = 2)))
        val newProducts = listOf(Random.nextProductEntity(id = 2), Random.nextProductEntity(id = 3))

        // When
        dao.replaceAll(newProducts)

        // Then
        assertEquals(newProducts, savedProducts())
    }

    @Test
    fun `given inserted products when clearAll is called then removes every product`() = runTest {
        // Given
        dao.insertAll(listOf(Random.nextProductEntity(id = 1), Random.nextProductEntity(id = 2)))

        // When
        dao.clearAll()

        // Then
        assertTrue(savedProducts().isEmpty())
    }

    private suspend fun savedProducts(): List<ProductEntity> {
        val pager = TestPager(
            PagingConfig(pageSize = 100, enablePlaceholders = false),
            dao.pagingSource(RoomRawQuery("SELECT * FROM products ORDER BY id")),
        )
        return (pager.refresh() as PagingSource.LoadResult.Page).data
    }
}
