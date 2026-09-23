package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(repository: ProductRepository) : ViewModel() {

    val products: StateFlow<ProductsUiState> = repository.observeProducts()
        .map<_, ProductsUiState> { ProductsUiState.Loaded(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ProductsUiState.Loading
        )

    init {
        viewModelScope.launch {
            repository.ensureCatalogDownloaded()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
