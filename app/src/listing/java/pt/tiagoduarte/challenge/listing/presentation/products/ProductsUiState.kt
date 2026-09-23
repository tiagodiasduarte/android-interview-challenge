package pt.tiagoduarte.challenge.listing.presentation.products

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data object Loaded : ProductsUiState
    data object Error : ProductsUiState
}
