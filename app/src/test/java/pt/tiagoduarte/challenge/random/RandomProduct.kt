package pt.tiagoduarte.challenge.random

import pt.tiagoduarte.challenge.domain.model.Product
import kotlin.random.Random

fun Random.nextProduct(
    id: Int = nextInt(1, 100_000),
    title: String = nextString(),
    description: String = nextString(),
    price: Double = nextDouble(1.0, 1_000.0),
    discountPercentage: Double = nextDouble(0.0, 50.0),
    rating: Double = nextDouble(0.0, 5.0),
    stock: Int = nextInt(0, 500),
    thumbnail: String = nextUrl(),
) = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    thumbnail = thumbnail,
)
