package pt.tiagoduarte.challenge.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.tiagoduarte.challenge.data.local.db.ProductEntity
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.random.nextProductEntity
import pt.tiagoduarte.challenge.random.nextProductResponse
import kotlin.random.Random

class ProductMappersTest {

    @Test
    fun `given a product response when toEntity is called then maps every field to the entity`() {
        // Given
        val response = Random.nextProductResponse()

        // When
        val entity = response.toEntity()

        // Then
        assertEquals(
            ProductEntity(
                id = response.id,
                title = response.title,
                titleNormalized = response.title.lowercase(),
                description = response.description,
                descriptionNormalized = response.description.lowercase(),
                price = response.price,
                discountPercentage = response.discountPercentage,
                rating = response.rating,
                stock = response.stock,
                thumbnail = response.thumbnail,
            ),
            entity,
        )
    }

    @Test
    fun `given a title and description with accents when toEntity is called then normalizes them for search`() {
        // Given
        val response = Random.nextProductResponse(title = "Crème Brûlée", description = "Fresh Café Dessert")

        // When
        val entity = response.toEntity()

        // Then
        assertEquals("creme brulee", entity.titleNormalized)
        assertEquals("fresh cafe dessert", entity.descriptionNormalized)
    }

    @Test
    fun `given a product entity when toProduct is called then maps every field to the domain model`() {
        // Given
        val entity = Random.nextProductEntity()

        // When
        val product = entity.toProduct()

        // Then
        assertEquals(
            Product(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                price = entity.price,
                discountPercentage = entity.discountPercentage,
                rating = entity.rating,
                stock = entity.stock,
                thumbnail = entity.thumbnail,
            ),
            product,
        )
    }
}
