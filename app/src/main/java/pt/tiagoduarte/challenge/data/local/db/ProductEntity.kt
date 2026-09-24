package pt.tiagoduarte.challenge.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(defaultValue = "")
    val titleNormalized: String,
    val description: String,
    @ColumnInfo(defaultValue = "")
    val descriptionNormalized: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val thumbnail: String,
)
