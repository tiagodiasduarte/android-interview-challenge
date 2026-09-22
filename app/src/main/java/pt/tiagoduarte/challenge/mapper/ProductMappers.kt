package pt.tiagoduarte.challenge.mapper

import pt.tiagoduarte.challenge.data.local.db.ProductEntity
import pt.tiagoduarte.challenge.data.remote.model.ProductResponse
import pt.tiagoduarte.challenge.domain.model.Product

fun ProductResponse.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    thumbnail = thumbnail
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    thumbnail = thumbnail
)
