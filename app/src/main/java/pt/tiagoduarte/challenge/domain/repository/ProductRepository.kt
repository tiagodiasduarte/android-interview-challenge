package pt.tiagoduarte.challenge.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.tiagoduarte.challenge.domain.model.Product

interface ProductRepository {
    suspend fun ensureCatalogDownloaded()
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(id: Int): Flow<Product?>
}
