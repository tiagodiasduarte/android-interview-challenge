package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.ui.theme.SpaceSize


@Composable
fun ProductItem(product: ProductUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Row(
            modifier = Modifier.padding(SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(SpaceSize.medium))

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = product.rating.toString()
                )
            }
            RatingIcon(ratingCategory = product.ratingCategory)
        }
    }
}

@Composable
private fun RatingIcon(ratingCategory: RatingCategory, modifier: Modifier = Modifier) {
    val (icon, descriptionRes) = when (ratingCategory) {
        RatingCategory.LOW -> Icons.Filled.SentimentDissatisfied to R.string.rating_low_content_description
        RatingCategory.MEDIUM -> Icons.Filled.SentimentSatisfied to R.string.rating_medium_content_description
        RatingCategory.HIGH -> Icons.Filled.SentimentVerySatisfied to R.string.rating_high_content_description
    }
    Icon(
        imageVector = icon,
        contentDescription = stringResource(descriptionRes),
        modifier = modifier
    )
}