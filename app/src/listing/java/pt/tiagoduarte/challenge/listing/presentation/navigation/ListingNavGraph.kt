package pt.tiagoduarte.challenge.listing.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.tiagoduarte.challenge.listing.presentation.products.ProductsRoute

private const val ROUTE_LIST = "list"

@Composable
fun ListingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = ROUTE_LIST, modifier = modifier) {
        composable(ROUTE_LIST) {
            ProductsRoute()
        }
    }
}
