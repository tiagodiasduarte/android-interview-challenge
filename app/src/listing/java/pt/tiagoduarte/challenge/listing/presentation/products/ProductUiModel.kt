package pt.tiagoduarte.challenge.listing.presentation.products

enum class RatingCategory { LOW, MEDIUM, HIGH }

data class ProductUiModel(
    val id: Int,
    val title: String,
    val rating: Double,
    val ratingCategory: RatingCategory,
)
