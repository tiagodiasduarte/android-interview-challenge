package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import pt.tiagoduarte.challenge.ui.theme.PreviewDevices
import pt.tiagoduarte.challenge.ui.theme.SpaceSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsRoute(
    onProductClick: (productId: Int) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    ProductsScreen(
        productsUiState = uiState,
        products = products,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onProductClick = onProductClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    productsUiState: ProductsUiState,
    products: LazyPagingItems<ProductUiModel>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProductClick: (productId: Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = stringResource(R.string.product_list_title),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface

                ),
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
        when (productsUiState) {
            ProductsUiState.Loading -> ProductsLoadingContent(modifier = contentModifier)
            ProductsUiState.Error -> ProductsErrorContent(modifier = contentModifier)
            ProductsUiState.Loaded -> ProductsLoadedContent(
                products = products,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onProductClick = onProductClick,
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
private fun PagingErrorContent(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        Text(
            text = stringResource(R.string.product_list_load_error),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.product_list_retry))
        }
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
private fun ProductsLoadedContent(
    products: LazyPagingItems<ProductUiModel>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProductClick: (productId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        ProductSearchField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(start = SpaceSize.large, top = SpaceSize.large, end = SpaceSize.large),
        )

        val refresh = products.loadState.refresh
        val noResults = products.itemCount == 0 && refresh is LoadState.NotLoading
        if (refresh is LoadState.Error) {
            PagingErrorContent(
                onRetry = products::retry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSize.large)
                    .wrapContentSize(),
            )
        } else if (noResults) {
            Text(
                text = stringResource(R.string.product_search_no_results),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSize.large)
                    .wrapContentSize(),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(SpaceSize.large),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            ) {
                items(count = products.itemCount, key = products.itemKey { it.id }) { index ->
                    products[index]?.let { product -> ProductItem(product, onClick = { onProductClick(product.id) }) }
                }
                if (products.loadState.append is LoadState.Error) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PagingErrorContent(onRetry = products::retry)
                    }
                }
            }
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
        ProductsScreen(ProductsUiState.Loaded, previewPagingItems(products), searchQuery = "", onSearchQueryChange = {}, onProductClick = {})
    }
}

@PreviewDevices
@Composable
private fun ProductsLoadingPreview() {
    AppTheme {
        ProductsScreen(ProductsUiState.Loading, previewPagingItems(), searchQuery = "", onSearchQueryChange = {}, onProductClick = {})
    }
}

@PreviewDevices
@Composable
private fun ProductsErrorPreview() {
    AppTheme {
        ProductsScreen(ProductsUiState.Error, previewPagingItems(), searchQuery = "", onSearchQueryChange = {}, onProductClick = {})
    }
}

@PreviewDevices
@Composable
private fun ProductsNoResultsPreview() {
    AppTheme {
        ProductsScreen(ProductsUiState.Loaded, previewPagingItems(), searchQuery = "kiwi", onSearchQueryChange = {}, onProductClick = {})
    }
}

@Composable
private fun previewPagingItems(products: List<ProductUiModel> = emptyList()): LazyPagingItems<ProductUiModel> =
    flowOf(PagingData.from(products)).collectAsLazyPagingItems()


