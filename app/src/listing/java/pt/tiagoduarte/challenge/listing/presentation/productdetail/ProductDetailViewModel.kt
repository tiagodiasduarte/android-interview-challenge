package pt.tiagoduarte.challenge.listing.presentation.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ProductRepository,
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle[PRODUCT_ID_ARG])

    val uiState: StateFlow<ProductDetailUiState> = repository.observeProduct(productId)
        .map { product ->
            product?.let { ProductDetailUiState.Loaded(it.toUiModel()) } ?: ProductDetailUiState.NotFound
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ProductDetailUiState.Loading,
        )

    private fun Product.toUiModel() = ProductDetailUiModel(
        title = title,
        price = price,
        discountPercentage = discountPercentage,
        stock = stock,
        rating = rating,
        imageUrl = thumbnail,
    )

    companion object {
        const val PRODUCT_ID_ARG = "productId"
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
