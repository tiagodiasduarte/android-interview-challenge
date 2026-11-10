package pt.tiagoduarte.challenge.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProductsDestination

@Serializable
data class ProductDetailDestination(val productId: Int)
