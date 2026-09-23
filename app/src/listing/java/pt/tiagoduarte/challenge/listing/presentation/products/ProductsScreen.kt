package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import pt.tiagoduarte.challenge.ui.theme.PreviewDevices
import pt.tiagoduarte.challenge.ui.theme.SpaceSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsRoute(
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.products.collectAsStateWithLifecycle()

    ProductsScreen(uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(productsUiState: ProductsUiState) {
    Scaffold(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(R.string.product_list_title),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface

                ),
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (productsUiState) {
            ProductsUiState.Loading -> ProductsLoadingContent(modifier = contentModifier)
            ProductsUiState.Error -> ProductsErrorContent(modifier = contentModifier)
            is ProductsUiState.Loaded -> ProductsLoadedContent(
                products = productsUiState.products,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun ProductsLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProductsErrorContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(SpaceSize.large)
            .wrapContentSize()
    ) {
        Text(
            text = stringResource(R.string.product_list_error),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ProductsLoadedContent(products: List<ProductUiModel>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        items(products, key = ProductUiModel::id) { product ->
            ProductItem(product)
        }
    }
}

@PreviewDevices
@Composable
private fun ProductsContentPreview() {
    val product = ProductUiModel(
        id = 1,
        title = "Smartphone Samsung Galaxy",
        rating = 2.5,
        ratingCategory = RatingCategory.LOW,
    )

    val products = (1..20).map { id ->
        product.copy(id = id)
    }
    AppTheme {
        ProductsScreen(ProductsUiState.Loaded(products = products))
    }
}

@PreviewDevices
@Composable
private fun ProductsLoadingPreview() {
    AppTheme {
        ProductsScreen(ProductsUiState.Loading)
    }
}

@PreviewDevices
@Composable
private fun ProductsErrorPreview() {
    AppTheme {
        ProductsScreen(ProductsUiState.Error)
    }
}


