package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.domain.model.Product
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
        when (productsUiState) {
            ProductsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .wrapContentSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProductsUiState.Loaded -> {
                ProductListContent(
                    products = productsUiState.products,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun ProductListContent(products: List<Product>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        items(products, key = Product::id) { product ->
            ProductItem(product)
        }
    }
}


@Composable
private fun ProductItem(product: Product, modifier: Modifier = Modifier) {
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
            RatingIcon(rating = product.rating)
        }
    }
}

@PreviewDevices
@Composable
private fun ProductsContentPreview() {
    val product = Product(
        id = 1,
        title = "Smartphone Samsung Galaxy",
        description = "A high-end smartphone",
        price = 799.99,
        discountPercentage = 10.0,
        rating = 2.5,
        stock = 12,
        thumbnail = "",
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


