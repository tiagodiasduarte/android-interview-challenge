package pt.tiagoduarte.challenge.random

import pt.tiagoduarte.challenge.data.local.db.ProductEntity
import pt.tiagoduarte.challenge.data.local.db.normalizeForSearch
import kotlin.random.Random

fun Random.nextProductEntity(
    id: Int = nextInt(1, 100_000),
    title: String = nextString(),
    titleNormalized: String = title.normalizeForSearch(),
    description: String = nextString(),
    descriptionNormalized: String = description.normalizeForSearch(),
    price: Double = nextDouble(1.0, 1_000.0),
    discountPercentage: Double = nextDouble(0.0, 50.0),
    rating: Double = nextDouble(0.0, 5.0),
    stock: Int = nextInt(0, 500),
    thumbnail: String = nextUrl(),
) = ProductEntity(
    id = id,
    title = title,
    titleNormalized = titleNormalized,
    description = description,
    descriptionNormalized = descriptionNormalized,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    thumbnail = thumbnail,
)
