package pt.tiagoduarte.challenge.listing.presentation.productdetail

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Loaded(val product: ProductDetailUiModel) : ProductDetailUiState
    data object NotFound : ProductDetailUiState
}
