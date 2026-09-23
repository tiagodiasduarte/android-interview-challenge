package pt.tiagoduarte.challenge.fakes

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import pt.tiagoduarte.challenge.data.local.db.ProductDao
import pt.tiagoduarte.challenge.data.local.db.ProductEntity

class FakeProductDao : ProductDao {

    private val products = MutableStateFlow<List<ProductEntity>>(emptyList())

    override fun observeAll(): Flow<List<ProductEntity>> = products

    // Returns every product: running the raw search SQL needs a real Room database, see ProductRepositoryImplTest
    override fun pagingSource(query: RoomRawQuery): PagingSource<Int, ProductEntity> =
        products.value.asPagingSourceFactory().invoke()

    override fun observeHasProducts(): Flow<Boolean> = products.map { it.isNotEmpty() }

    override fun observeById(id: Int): Flow<ProductEntity?> =
        products.map { entities -> entities.find { it.id == id } }

    override suspend fun insertAll(products: List<ProductEntity>) {
        this.products.value = (products + this.products.value).distinctBy { it.id }
    }

    override suspend fun clearAll() {
        products.value = emptyList()
    }
}
