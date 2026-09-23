package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(repository: ProductRepository) : ViewModel() {

    private val catalogStatus = MutableStateFlow(CatalogStatus.DOWNLOADING)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val products: Flow<PagingData<ProductUiModel>> = _searchQuery
        .map { it.trim() }
        .debounce { query -> if (query.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
        .distinctUntilChanged()
        .flatMapLatest { query -> repository.observePagedProducts(query) }
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

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
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
        const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val MEDIUM_RATING_MIN = 3.0
        const val MEDIUM_RATING_MAX = 4.0
    }
}
