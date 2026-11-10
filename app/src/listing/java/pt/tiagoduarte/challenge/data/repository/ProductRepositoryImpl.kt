package pt.tiagoduarte.challenge.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pt.tiagoduarte.challenge.data.local.db.ProductDao
import pt.tiagoduarte.challenge.data.local.db.normalizeForSearch
import pt.tiagoduarte.challenge.mapper.toEntity
import pt.tiagoduarte.challenge.data.remote.api.ProductApi
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import pt.tiagoduarte.challenge.mapper.toProduct
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao,
) : ProductRepository {

    override suspend fun ensureCatalogDownloaded() {
        if (dao.observeHasProducts().first()) return
        val products = api.getProducts().products.map { it.toEntity() }
        dao.replaceAll(products)
    }

    override fun observePagedProducts(query: String): Flow<PagingData<Product>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { dao.pagingSource(searchQuery(query)) },
        ).flow.map { pagingData -> pagingData.map { it.toProduct() } }

    override fun observeHasProducts(): Flow<Boolean> = dao.observeHasProducts()

    override fun observeProduct(id: Int): Flow<Product?> =
        dao.observeById(id).map { it?.toProduct() }

    /**
     * Builds a query that matches products containing every term of [query] in their title or
     * description, in any order, listing the ones whose title has every term first.
     * Lists every product when there are no terms to search for.
     */
    private fun searchQuery(query: String): RoomRawQuery {
        val terms = query.normalizeForSearch().split(WHITESPACE).filter { it.isNotEmpty() }
        if (terms.isEmpty()) return RoomRawQuery(ALL_PRODUCTS)

        val where = terms.joinToString(" AND ") { TERM_MATCHES }
        val titleMatchesAll = terms.joinToString(" AND ") { TITLE_MATCHES }
        val arguments = terms.flatMap { listOf(it, it) } + terms

        return RoomRawQuery(
            sql = "SELECT * FROM products WHERE $where ORDER BY ($titleMatchesAll) DESC, id",
        ) { statement ->
            arguments.forEachIndexed { index, term ->
                statement.bindText(
                    index + 1,
                    "%${term.escapeLike()}%"
                )
            }
        }
    }

    private fun String.escapeLike(): String = replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")

    private companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 5

        const val ALL_PRODUCTS = "SELECT * FROM products ORDER BY id"
        const val TITLE_MATCHES = "titleNormalized LIKE ? ESCAPE '\\'"
        const val TERM_MATCHES = "($TITLE_MATCHES OR descriptionNormalized LIKE ? ESCAPE '\\')"
        val WHITESPACE = "\\s+".toRegex()
    }
}
