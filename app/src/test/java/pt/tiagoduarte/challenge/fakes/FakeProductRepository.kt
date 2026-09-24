package pt.tiagoduarte.challenge.fakes

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
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
    var failPageLoads: Boolean = false,
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
        // A real Pager, so screens see the same loading states as with Room
        return Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            FakePagingSource(products.value.filter { it.title.contains(query, ignoreCase = true) })
        }.flow
    }

    override fun observeHasProducts(): Flow<Boolean> = products.map { it.isNotEmpty() }

    override fun observeProduct(id: Int): Flow<Product?> =
        products.map { list -> list.find { it.id == id } }

    // One page with every product, or an error while failPageLoads is on; checked on every load so retries work
    private inner class FakePagingSource(private val items: List<Product>) : PagingSource<Int, Product>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> =
            if (failPageLoads) {
                LoadResult.Error(IOException("Page load failed"))
            } else {
                LoadResult.Page(data = items, prevKey = null, nextKey = null)
            }

        override fun getRefreshKey(state: PagingState<Int, Product>): Int? = null
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
