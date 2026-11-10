package pt.tiagoduarte.challenge.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.tiagoduarte.challenge.domain.model.Product

interface ProductRepository {
    suspend fun ensureCatalogDownloaded()
    fun observePagedProducts(query: String = ""): Flow<PagingData<Product>>
    fun observeHasProducts(): Flow<Boolean>
    fun observeProduct(id: Int): Flow<Product?>
}
