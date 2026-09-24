package pt.tiagoduarte.challenge.listing.presentation.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import pt.tiagoduarte.challenge.ui.theme.PreviewDevices
import pt.tiagoduarte.challenge.ui.theme.SpaceSize

private val MaxContentWidth = 1000.dp
private val MaxImageSize = 480.dp

@Composable
fun ProductDetailRoute(
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailScreen(uiState = uiState, onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreen(uiState: ProductDetailUiState, onBackClick: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.product_detail_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.product_detail_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (uiState) {
            ProductDetailUiState.Loading -> ProductDetailLoadingContent(modifier = contentModifier)
            ProductDetailUiState.NotFound -> ProductDetailNotFoundContent(modifier = contentModifier)
            is ProductDetailUiState.Loaded -> ProductDetailLoadedContent(
                product = uiState.product,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun ProductDetailLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProductDetailNotFoundContent(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.product_detail_not_found),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(SpaceSize.large)
            .wrapContentSize(),
    )
}

@Composable
private fun ProductDetailLoadedContent(product: ProductDetailUiModel, modifier: Modifier = Modifier) {
    val isWideWindow = currentWindowAdaptiveInfo().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SpaceSize.large),
        contentAlignment = Alignment.TopCenter,
    ) {
        val contentModifier = Modifier.widthIn(max = MaxContentWidth)
        if (isWideWindow) {
            Row(
                modifier = contentModifier,
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.xlarge),
            ) {
                ProductDetailImage(imageUrl = product.imageUrl, modifier = Modifier.weight(1f))
                ProductDetailInfo(product = product, modifier = Modifier.weight(1f))
            }
        } else {
            Column(
                modifier = contentModifier,
                verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
            ) {
                ProductDetailImage(imageUrl = product.imageUrl)
                ProductDetailInfo(product = product)
            }
        }
    }
}

@Composable
private fun ProductDetailImage(imageUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .widthIn(max = MaxImageSize)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(SpaceSize.large)),
    )
}

@Composable
private fun ProductDetailInfo(product: ProductDetailUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
            ProductDetailRow(
                label = stringResource(R.string.product_detail_price),
                value = stringResource(R.string.product_detail_price_value, product.price),
            )
            HorizontalDivider()
            ProductDetailRow(
                label = stringResource(R.string.product_detail_discount),
                value = stringResource(R.string.product_detail_discount_value, product.discountPercentage),
            )
            HorizontalDivider()
            ProductDetailRow(
                label = stringResource(R.string.product_detail_stock),
                value = product.stock.toString(),
            )
            HorizontalDivider()
            ProductDetailRow(
                label = stringResource(R.string.product_detail_rating),
                value = product.rating.toString(),
            )
        }
    }
}

@Composable
private fun ProductDetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@PreviewDevices
@Composable
private fun ProductDetailLoadedPreview() {
    AppTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState.Loaded(
                ProductDetailUiModel(
                    title = "Smartphone Samsung Galaxy",
                    price = 799.99,
                    discountPercentage = 10.5,
                    stock = 12,
                    rating = 4.5,
                    imageUrl = "",
                ),
            ),
            onBackClick = {},
        )
    }
}

@PreviewDevices
@Composable
private fun ProductDetailLoadingPreview() {
    AppTheme {
        ProductDetailScreen(uiState = ProductDetailUiState.Loading, onBackClick = {})
    }
}

@PreviewDevices
@Composable
private fun ProductDetailNotFoundPreview() {
    AppTheme {
        ProductDetailScreen(uiState = ProductDetailUiState.NotFound, onBackClick = {})
    }
}
