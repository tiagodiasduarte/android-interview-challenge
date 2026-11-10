package pt.tiagoduarte.challenge.presentation.productdetail

data class ProductDetailUiModel(
    val title: String,
    val price: Double,
    val discountPercentage: Double,
    val stock: Int,
    val rating: Double,
    val imageUrl: String,
)
