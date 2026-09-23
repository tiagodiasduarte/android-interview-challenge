package pt.tiagoduarte.challenge.fakes

import pt.tiagoduarte.challenge.data.remote.api.ProductApi
import pt.tiagoduarte.challenge.data.remote.model.ProductListResponse
import pt.tiagoduarte.challenge.data.remote.model.ProductResponse
import java.io.IOException

class FakeProductApi(
    private val products: List<ProductResponse> = emptyList(),
    var shouldThrow: Boolean = false,
) : ProductApi {

    override suspend fun getProducts(limit: Int): ProductListResponse {
        if (shouldThrow) throw IOException("Network error")
        return ProductListResponse(products = products, total = products.size, skip = 0, limit = limit)
    }
}
