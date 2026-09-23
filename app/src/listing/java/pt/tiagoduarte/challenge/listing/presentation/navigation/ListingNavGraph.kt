package pt.tiagoduarte.challenge.listing.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.tiagoduarte.challenge.listing.presentation.products.ProductsRoute

private const val PRODUCTS_ROUTE = "products_route"

@Composable
fun ListingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = PRODUCTS_ROUTE, modifier = modifier) {
        composable(PRODUCTS_ROUTE) {
            ProductsRoute()
        }
    }
}
