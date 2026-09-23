package pt.tiagoduarte.challenge.listing.presentation.products

import pt.tiagoduarte.challenge.domain.model.Product

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Loaded(val products: List<Product>) : ProductsUiState
}
