package pt.tiagoduarte.challenge.data.remote

import pt.tiagoduarte.challenge.data.remote.model.ProductListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("products")
    suspend fun getProducts(@Query("limit") limit: Int = 0): ProductListResponse
}
