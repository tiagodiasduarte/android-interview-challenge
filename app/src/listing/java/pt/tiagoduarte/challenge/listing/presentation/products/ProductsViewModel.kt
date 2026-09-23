package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(repository: ProductRepository) : ViewModel() {

    private val catalogStatus = MutableStateFlow(CatalogStatus.DOWNLOADING)

    val products: StateFlow<ProductsUiState> = combine(repository.observeProducts(), catalogStatus) { products, status ->
        when {
            products.isNotEmpty() || status == CatalogStatus.DOWNLOADED ->
                ProductsUiState.Loaded(products.map { it.toUiModel() })
            status == CatalogStatus.FAILED -> ProductsUiState.Error
            else -> ProductsUiState.Loading
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ProductsUiState.Loading
        )

    init {
        viewModelScope.launch {
            catalogStatus.value = try {
                repository.ensureCatalogDownloaded()
                CatalogStatus.DOWNLOADED
            } catch (e: CancellationException) {
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") _: Exception) {
                CatalogStatus.FAILED
            }
        }
    }

    private fun ratingCategoryOf(rating: Double): RatingCategory = when {
        rating < MEDIUM_RATING_MIN -> RatingCategory.LOW
        rating <= MEDIUM_RATING_MAX -> RatingCategory.MEDIUM
        else -> RatingCategory.HIGH
    }

    private fun Product.toUiModel() = ProductUiModel(
        id = id,
        title = title,
        rating = rating,
        ratingCategory = ratingCategoryOf(rating),
    )

    private enum class CatalogStatus { DOWNLOADING, DOWNLOADED, FAILED }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val MEDIUM_RATING_MIN = 3.0
        const val MEDIUM_RATING_MAX = 4.0
    }
}
