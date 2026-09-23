package pt.tiagoduarte.challenge.data.remote.api

import pt.tiagoduarte.challenge.data.remote.model.ProductListResponse
import retrofit2.http.GET

interface ProductApi {

    @GET("products")
    suspend fun getProducts(): ProductListResponse
}
