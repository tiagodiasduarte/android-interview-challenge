package pt.tiagoduarte.challenge.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val titleNormalized: String = title.normalizeForSearch(),
    val description: String,
    val descriptionNormalized: String = description.normalizeForSearch(),
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val thumbnail: String,
)
