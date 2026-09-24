package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.ui.theme.SpaceSize

@Composable
fun ProductItem(product: ProductUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = product.rating.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
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