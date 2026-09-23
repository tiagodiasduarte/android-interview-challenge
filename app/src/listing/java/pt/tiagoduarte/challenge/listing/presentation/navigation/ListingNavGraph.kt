package pt.tiagoduarte.challenge.listing.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.tiagoduarte.challenge.listing.presentation.productdetail.ProductDetailRoute
import pt.tiagoduarte.challenge.listing.presentation.productdetail.ProductDetailViewModel
import pt.tiagoduarte.challenge.listing.presentation.products.ProductsRoute

private const val PRODUCTS_ROUTE = "products_route"
private const val PRODUCT_DETAIL_ROUTE = "product_detail_route/{${ProductDetailViewModel.PRODUCT_ID_ARG}}"

private fun productDetailRoute(productId: Int) = "product_detail_route/$productId"

@Composable
fun ListingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = PRODUCTS_ROUTE, modifier = modifier) {
        composable(PRODUCTS_ROUTE) {
            ProductsRoute(onProductClick = { productId -> navController.navigate(productDetailRoute(productId)) })
        }
        composable(
            route = PRODUCT_DETAIL_ROUTE,
            arguments = listOf(navArgument(ProductDetailViewModel.PRODUCT_ID_ARG) { type = NavType.IntType }),
        ) {
            ProductDetailRoute(onBackClick = navController::popBackStack)
        }
    }
}
