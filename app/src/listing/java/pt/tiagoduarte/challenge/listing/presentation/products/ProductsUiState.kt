package pt.tiagoduarte.challenge.listing.presentation.products

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Loaded(val products: List<ProductUiModel>) : ProductsUiState
    data object Error : ProductsUiState
}
