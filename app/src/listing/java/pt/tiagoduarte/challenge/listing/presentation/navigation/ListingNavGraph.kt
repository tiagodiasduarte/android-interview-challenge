package pt.tiagoduarte.challenge.listing.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.tiagoduarte.challenge.listing.presentation.productdetail.ProductDetailRoute
import pt.tiagoduarte.challenge.listing.presentation.products.ProductsRoute

@Composable
fun ListingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = ProductsDestination, modifier = modifier) {
        composable<ProductsDestination> { backStackEntry ->
            ProductsRoute(
                onProductClick = { productId ->
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(ProductDetailDestination(productId))
                    }
                },
            )
        }
        composable<ProductDetailDestination> {
            ProductDetailRoute(onBackClick = dropUnlessResumed { navController.navigateUp() })
        }
    }
}
