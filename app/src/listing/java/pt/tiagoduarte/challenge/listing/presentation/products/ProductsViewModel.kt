package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(repository: ProductRepository) : ViewModel() {

    private val catalogStatus = MutableStateFlow(CatalogStatus.DOWNLOADING)

    val uiState: StateFlow<ProductsUiState> = combine(repository.observeHasProducts(), catalogStatus) { hasProducts, status ->
        when {
            hasProducts || status == CatalogStatus.DOWNLOADED -> ProductsUiState.Loaded
            status == CatalogStatus.FAILED -> ProductsUiState.Error
            else -> ProductsUiState.Loading
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ProductsUiState.Loading
        )

    val products: Flow<PagingData<ProductUiModel>> = repository.observePagedProducts()
        .map { pagingData -> pagingData.map { it.toUiModel() } }
        .cachedIn(viewModelScope)

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
