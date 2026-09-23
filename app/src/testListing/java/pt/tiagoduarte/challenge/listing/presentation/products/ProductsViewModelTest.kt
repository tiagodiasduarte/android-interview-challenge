package pt.tiagoduarte.challenge.listing.presentation.products

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import pt.tiagoduarte.challenge.fakes.FakeProductRepository
import pt.tiagoduarte.challenge.random.nextProduct
import pt.tiagoduarte.challenge.rules.MainDispatcherRule
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `given no products loaded yet when the ViewModel is created then the state starts as Loading`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given the download is in progress and no saved products when products are observed then it stays Loading`() =
        runTest {
            // Given
            val downloadGate = CompletableDeferred<Unit>()
            val repository = FakeProductRepository(downloadGate = downloadGate)

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                advanceUntilIdle()
                expectNoEvents()

                downloadGate.complete(Unit)
                assertEquals(ProductsUiState.Loaded(emptyList()), awaitItem())
            }
        }

    @Test
    fun `given products in the repository when they are observed then the state becomes Loaded with ui models`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 4.5)
            val repository = FakeProductRepository(initialProducts = listOf(product))

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(
                    ProductsUiState.Loaded(
                        listOf(
                            ProductUiModel(
                                id = product.id,
                                title = product.title,
                                rating = product.rating,
                                ratingCategory = RatingCategory.HIGH,
                            ),
                        ),
                    ),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `given the download fails and no saved products when products are observed then the state is Error`() =
        runTest {
            // Given
            val repository = FakeProductRepository(shouldThrow = true)

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(ProductsUiState.Error, awaitItem())
            }
        }

    @Test
    fun `given the download fails with saved products when products are observed then the state is Loaded`() =
        runTest {
            // Given
            val product = Random.nextProduct()
            val repository =
                FakeProductRepository(initialProducts = listOf(product), shouldThrow = true)

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                val state = awaitItem() as ProductsUiState.Loaded
                assertEquals(listOf(product.id), state.products.map { it.id })
            }
        }

    @Test
    fun `given the download succeeds with no products when products are observed then the state is Loaded empty`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(ProductsUiState.Loaded(emptyList()), awaitItem())
            }
        }

    @Test
    fun `given the ViewModel is created when init runs then it ensures the catalog is downloaded`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            ProductsViewModel(repository)
            advanceUntilIdle()

            // Then
            assertEquals(1, repository.ensureCatalogDownloadedCallCount)
        }


    @Test
    fun `given a rating below 3 when products are observed then the rating category is LOW`() =
        runTest {
            // Given
            val repository =
                FakeProductRepository(initialProducts = listOf(Random.nextProduct(rating = 2.99)))

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                skipItems(1)
                assertEquals(RatingCategory.LOW, awaitRatingCategory())
            }
        }

    @Test
    fun `given a rating of exactly 3 when products are observed then the rating category is MEDIUM`() =
        runTest {
            // Given
            val repository =
                FakeProductRepository(initialProducts = listOf(Random.nextProduct(rating = 3.0)))

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                skipItems(1)
                assertEquals(RatingCategory.MEDIUM, awaitRatingCategory())
            }
        }

    @Test
    fun `given a rating of exactly 4 when products are observed then the rating category is MEDIUM`() =
        runTest {
            // Given
            val repository =
                FakeProductRepository(initialProducts = listOf(Random.nextProduct(rating = 4.0)))

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                skipItems(1)
                assertEquals(RatingCategory.MEDIUM, awaitRatingCategory())
            }
        }

    @Test
    fun `given a rating above 4 when products are observed then the rating category is HIGH`() =
        runTest {
            // Given
            val repository =
                FakeProductRepository(initialProducts = listOf(Random.nextProduct(rating = 4.01)))

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.products.test {
                skipItems(1)
                assertEquals(RatingCategory.HIGH, awaitRatingCategory())
            }
        }

    private suspend fun TurbineTestContext<ProductsUiState>.awaitRatingCategory(): RatingCategory =
        (awaitItem() as ProductsUiState.Loaded).products.single().ratingCategory
}
