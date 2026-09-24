package pt.tiagoduarte.challenge.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val thumbnail: String
)
