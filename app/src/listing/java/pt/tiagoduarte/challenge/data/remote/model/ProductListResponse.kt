package pt.tiagoduarte.challenge.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponse(
    val products: List<ProductResponse>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
