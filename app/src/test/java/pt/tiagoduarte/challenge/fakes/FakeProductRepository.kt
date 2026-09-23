package pt.tiagoduarte.challenge.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository

class FakeProductRepository(
    initialProducts: List<Product> = emptyList(),
) : ProductRepository {

    private val products = MutableStateFlow(initialProducts)

    var ensureCatalogDownloadedCallCount = 0
        private set

    override suspend fun ensureCatalogDownloaded() {
        ensureCatalogDownloadedCallCount++
    }

    override fun observeProducts(): Flow<List<Product>> = products

    override fun observeProduct(id: Int): Flow<Product?> =
        products.map { list -> list.find { it.id == id } }
}
