package pt.tiagoduarte.challenge.listing.presentation.productdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import pt.tiagoduarte.challenge.fakes.FakeProductRepository
import pt.tiagoduarte.challenge.random.nextProduct
import pt.tiagoduarte.challenge.rules.MainDispatcherRule
import kotlin.random.Random

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `given a product id when the ViewModel is created then the state starts as Loading`() =
        runTest {
            // Given
            val product = Random.nextProduct()

            // When
            val viewModel = setupViewModel(
                productId = product.id,
                repository = FakeProductRepository(listOf(product))
            )

            // Then
            viewModel.uiState.test {
                assertEquals(ProductDetailUiState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given a saved product when the state is observed then it becomes Loaded with every detail`() =
        runTest {
            // Given
            val product = Random.nextProduct()
            val viewModel = setupViewModel(
                productId = product.id,
                repository = FakeProductRepository(listOf(product, Random.nextProduct()))
            )

            // When / Then
            viewModel.uiState.test {
                assertEquals(ProductDetailUiState.Loading, awaitItem())
                assertEquals(
                    ProductDetailUiState.Loaded(
                        ProductDetailUiModel(
                            title = product.title,
                            price = product.price,
                            discountPercentage = product.discountPercentage,
                            stock = product.stock,
                            rating = product.rating,
                            imageUrl = product.thumbnail,
                        ),
                    ),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `given an id with no saved product when the state is observed then it becomes NotFound`() =
        runTest {
            // Given
            val product = Random.nextProduct()
            val viewModel = setupViewModel(
                productId = product.id + 1,
                repository = FakeProductRepository(listOf(product))
            )

            // When / Then
            viewModel.uiState.test {
                assertEquals(ProductDetailUiState.Loading, awaitItem())
                assertEquals(ProductDetailUiState.NotFound, awaitItem())
            }
        }

    private fun setupViewModel(productId: Int, repository: FakeProductRepository) =
        ProductDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(ProductDetailViewModel.PRODUCT_ID_ARG to productId)),
            repository = repository,
        )
}
