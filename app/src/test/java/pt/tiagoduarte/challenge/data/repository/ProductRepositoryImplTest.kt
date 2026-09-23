package pt.tiagoduarte.challenge.data.repository

import androidx.paging.testing.asSnapshot
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pt.tiagoduarte.challenge.data.local.db.AppDatabase
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.fakes.FakeAppPreferences
import pt.tiagoduarte.challenge.fakes.FakeProductApi
import pt.tiagoduarte.challenge.fakes.FakeProductDao
import pt.tiagoduarte.challenge.random.nextProductEntity
import pt.tiagoduarte.challenge.random.nextProductResponse
import pt.tiagoduarte.challenge.mapper.toEntity
import pt.tiagoduarte.challenge.mapper.toProduct
import pt.tiagoduarte.challenge.rules.DatabaseTestRule
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ProductRepositoryImplTest {

    @get:Rule
    val databaseRule = DatabaseTestRule(AppDatabase::class)

    private val database: AppDatabase get() = databaseRule.database

    private val product = Random.nextProductResponse()

    private val galaxy = Random.nextProductEntity(
        id = 1,
        title = "Smartphone Samsung Galaxy",
        description = "Large screen"
    )
    private val brulee = Random.nextProductEntity(
        id = 2,
        title = "Crème Brûlée",
        description = "French dessert"
    )
    private val kiwi = Random.nextProductEntity(
        id = 3,
        title = "Kiwi",
        description = "Fresh fruit sold at the cafe"
    )
    private val searchCatalog = listOf(galaxy, brulee, kiwi)


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
    fun `given the fetch is interrupted when ensureCatalogDownloaded is called then the flag stays false`() =
        runTest {
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
    fun `given products in the database when observePagedProducts is called then emits them mapped to domain models`() =
        runTest {
            // Given
            val dao = FakeProductDao()
            dao.insertAll(listOf(product.toEntity()))
            val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

            // When
            val products = repository.observePagedProducts().asSnapshot()

            // Then
            assertEquals(listOf(product.toEntity().toProduct()), products)
        }

    @Test
    fun `given more products than a page when observePagedProducts is called then emits only the first page`() =
        runTest {
            // Given
            val dao = FakeProductDao()
            dao.insertAll((1..30).map { Random.nextProductResponse(id = it).toEntity() })
            val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

            // When
            val products = repository.observePagedProducts().asSnapshot()

            // Then
            assertEquals((1..20).toList(), products.map { it.id })
        }

    @Test
    fun `given products in the database when observeHasProducts is called then emits true`() =
        runTest {
            // Given
            val dao = FakeProductDao()
            dao.insertAll(listOf(product.toEntity()))
            val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

            // When
            val hasProducts = repository.observeHasProducts().first()

            // Then
            assertTrue(hasProducts)
        }

    @Test
    fun `given a product id when observeProduct is called then emits the matching product`() =
        runTest {
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
    fun `given a product id that does not exist when observeProduct is called then emits null`() =
        runTest {
            // Given
            val dao = FakeProductDao()
            dao.insertAll(listOf(product.toEntity()))
            val repository = ProductRepositoryImpl(FakeProductApi(), dao, FakeAppPreferences())

            // When
            val result = repository.observeProduct(id = product.id + 1).first()

            // Then
            assertEquals(null, result)
        }

    @Test
    fun `given terms out of order when searching then matches the product`() = runTest {
        // Given
        database.productDao().insertAll(searchCatalog)
        val query = "galaxy samsung"

        // When
        val results = search(query)

        // Then
        assertEquals(listOf(galaxy).map { it.toProduct() }, results)
    }

    @Test
    fun `given a query in a different case when searching then matches the product`() = runTest {
        // Given
        database.productDao().insertAll(searchCatalog)
        val query = "SMARTPHONE"

        // When
        val results = search(query)

        // Then
        assertEquals(listOf(galaxy).map { it.toProduct() }, results)
    }

    @Test
    fun `given a query without accents when searching a product with accents then matches it`() =
        runTest {
            // Given
            database.productDao().insertAll(searchCatalog)
            val query = "creme brulee"

            // When
            val results = search(query)

            // Then
            assertEquals(listOf(brulee).map { it.toProduct() }, results)
        }

    @Test
    fun `given a query with accents when searching a product without them then matches it`() =
        runTest {
            // Given
            database.productDao().insertAll(searchCatalog)
            val query = "café"

            // When
            val results = search(query)

            // Then
            assertEquals(listOf(kiwi).map { it.toProduct() }, results)
        }

    @Test
    fun `given a term from the description when searching then matches the product`() = runTest {
        // Given
        database.productDao().insertAll(searchCatalog)
        val query = "fresh"

        // When
        val results = search(query)

        // Then
        assertEquals(listOf(kiwi).map { it.toProduct() }, results)
    }

    @Test
    fun `given part of a word when searching then matches the product`() = runTest {
        // Given
        database.productDao().insertAll(searchCatalog)
        val query = "phone"

        // When
        val results = search(query)

        // Then
        assertEquals(listOf(galaxy).map { it.toProduct() }, results)
    }

    @Test
    fun `given one term that matches and one that does not when searching then returns nothing`() =
        runTest {
            // Given
            database.productDao().insertAll(searchCatalog)
            val query = "samsung kiwi"

            // When
            val results = search(query)

            // Then
            assertTrue(results.isEmpty())
        }

    @Test
    fun `given a LIKE wildcard when searching then treats it as plain text`() = runTest {
        // Given
        database.productDao().insertAll(searchCatalog)
        val query = "%"

        // When
        val results = search(query)

        // Then
        assertTrue(results.isEmpty())
    }

    @Test
    fun `given terms split between title and description when searching then matches the product`() =
        runTest {
            // Given
            database.productDao().insertAll(searchCatalog)
            val query = "fresh kiwi"

            // When
            val results = search(query)

            // Then
            assertEquals(listOf(kiwi).map { it.toProduct() }, results)
        }

    @Test
    fun `given a term in one title and another description when searching then lists the title match first`() =
        runTest {
            // Given
            val smoothie = Random.nextProductEntity(
                id = 4,
                title = "Smoothie",
                description = "With kiwi and apple"
            )
            database.productDao().insertAll(searchCatalog + smoothie)

            // When
            val results = search("kiwi")

            // Then
            assertEquals(listOf(kiwi, smoothie).map { it.toProduct() }, results)
        }

    @Test
    fun `given a blank query when searching then emits every product`() = runTest {
        // Given
        database.productDao().insertAll(searchCatalog)

        // When
        val results = search("   ")

        // Then
        assertEquals(searchCatalog.map { it.toProduct() }, results)
    }

    private suspend fun search(query: String): List<Product> =
        ProductRepositoryImpl(FakeProductApi(), database.productDao(), FakeAppPreferences())
            .observePagedProducts(query)
            .asSnapshot()
}
