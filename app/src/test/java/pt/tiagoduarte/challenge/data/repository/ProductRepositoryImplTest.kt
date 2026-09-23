package pt.tiagoduarte.challenge.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import pt.tiagoduarte.challenge.fakes.FakeAppPreferences
import pt.tiagoduarte.challenge.fakes.FakeProductApi
import pt.tiagoduarte.challenge.fakes.FakeProductDao
import pt.tiagoduarte.challenge.random.nextProductResponse
import pt.tiagoduarte.challenge.mapper.toEntity
import kotlin.random.Random

class ProductRepositoryImplTest {

    private val product = Random.nextProductResponse()

    @Test
    fun `given catalog not downloaded when ensureCatalogDownloaded is called then fetches and persists the catalog`() =
        runTest {
            // Given
            val api = FakeProductApi(products = listOf(product))
            val dao = FakeProductDao()
            val prefs = FakeAppPreferences()
            val repository = ProductRepositoryImpl(api, dao, prefs)

            // When
            repository.ensureCatalogDownloaded()

            // Then
            assertTrue(prefs.isCatalogDownloaded.first())
            assertEquals(1, dao.observeAll().first().size)
        }

    @Test
    fun `given the fetch is interrupted when ensureCatalogDownloaded is called then the flag stays false`() = runTest {
        // Given
        val api = FakeProductApi(shouldThrow = true)
        val dao = FakeProductDao()
        val prefs = FakeAppPreferences()
        val repository = ProductRepositoryImpl(api, dao, prefs)

        // When
        val exception = runCatching { repository.ensureCatalogDownloaded() }.exceptionOrNull()

        // Then
        assertNotNull(exception)
        assertFalse(prefs.isCatalogDownloaded.first())
        assertTrue(dao.observeAll().first().isEmpty())
    }

    @Test
    fun `given catalog already downloaded when ensureCatalogDownloaded is called then it does not fetch again`() =
        runTest {
            // Given
            val api = FakeProductApi(products = listOf(product))
            val dao = FakeProductDao()
            val prefs = FakeAppPreferences(initialValue = true)
            val repository = ProductRepositoryImpl(api, dao, prefs)

            // When
            repository.ensureCatalogDownloaded()

            // Then
            assertTrue(dao.observeAll().first().isEmpty())
        }

    @Test
    fun `given products in the database when observeProducts is called then emits them mapped to domain models`() =
        runTest {
            // Given
            val dao = FakeProductDao()
            dao.insertAll(listOf(product.toEntity()))
            val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

            // When
            val products = repository.observeProducts().first()

            // Then
            assertEquals(1, products.size)
            assertEquals(product.id, products.first().id)
        }

    @Test
    fun `given a product id when observeProduct is called then emits the matching product`() = runTest {
        // Given
        val dao = FakeProductDao()
        dao.insertAll(listOf(product.toEntity()))
        val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

        // When
        val result = repository.observeProduct(product.id).first()

        // Then
        assertEquals(product.id, result?.id)
    }

    @Test
    fun `given a product id that does not exist when observeProduct is called then emits null`() = runTest {
        // Given
        val dao = FakeProductDao()
        dao.insertAll(listOf(product.toEntity()))
        val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

        // When
        val result = repository.observeProduct(id = product.id + 1).first()

        // Then
        assertEquals(null, result)
    }
}
