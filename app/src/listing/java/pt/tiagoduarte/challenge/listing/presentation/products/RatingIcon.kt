package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class RatingCategory { LOW, MEDIUM, HIGH }

fun ratingCategoryOf(rating: Double): RatingCategory = when {
    rating < 3.0 -> RatingCategory.LOW
    rating <= 4.0 -> RatingCategory.MEDIUM
    else -> RatingCategory.HIGH
}

@Composable
fun RatingIcon(rating: Double, modifier: Modifier = Modifier) {
    val (icon, description) = when (ratingCategoryOf(rating)) {
        RatingCategory.LOW -> Icons.Filled.SentimentDissatisfied to "Low rating"
        RatingCategory.MEDIUM -> Icons.Filled.SentimentSatisfied to "Medium rating"
        RatingCategory.HIGH -> Icons.Filled.SentimentVerySatisfied to "High rating"
    }
    Icon(imageVector = icon, contentDescription = description, modifier = modifier)
}
