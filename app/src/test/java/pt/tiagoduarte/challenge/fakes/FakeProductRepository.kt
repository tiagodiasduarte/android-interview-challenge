package pt.tiagoduarte.challenge.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import java.io.IOException

class FakeProductRepository(
    initialProducts: List<Product> = emptyList(),
    var shouldThrow: Boolean = false,
    private val downloadGate: CompletableDeferred<Unit>? = null,
) : ProductRepository {

    private val products = MutableStateFlow(initialProducts)

    var ensureCatalogDownloadedCallCount = 0
        private set

    val searchQueries = mutableListOf<String>()

    override suspend fun ensureCatalogDownloaded() {
        ensureCatalogDownloadedCallCount++
        downloadGate?.await()
        if (shouldThrow) throw IOException("Network error")
    }

    override fun observePagedProducts(query: String): Flow<PagingData<Product>> {
        searchQueries += query
        return products.map { list ->
            PagingData.from(list.filter { it.title.contains(query, ignoreCase = true) })
        }
    }

    override fun observeHasProducts(): Flow<Boolean> = products.map { it.isNotEmpty() }

    override fun observeProduct(id: Int): Flow<Product?> =
        products.map { list -> list.find { it.id == id } }
}
