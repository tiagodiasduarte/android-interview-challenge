package pt.tiagoduarte.challenge.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pt.tiagoduarte.challenge.data.local.db.ProductDao
import pt.tiagoduarte.challenge.mapper.toEntity
import pt.tiagoduarte.challenge.data.local.prefs.AppPreferences
import pt.tiagoduarte.challenge.data.remote.api.ProductApi
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.domain.repository.ProductRepository
import pt.tiagoduarte.challenge.mapper.toProduct
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao,
    private val prefs: AppPreferences,
) : ProductRepository {

    override suspend fun ensureCatalogDownloaded() {
        if (prefs.isCatalogDownloaded.first()) return
        val response = api.getProducts()
        dao.clearAll()
        dao.insertAll(response.products.map { it.toEntity() })
        prefs.setCatalogDownloaded(true)
    }

    override fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { products -> products.map { it.toProduct() } }

    override fun observeProduct(id: Int): Flow<Product?> =
        dao.observeById(id).map { it?.toProduct() }
}
