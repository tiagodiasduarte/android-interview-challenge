package pt.tiagoduarte.challenge.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val thumbnail: String
)
