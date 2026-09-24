package pt.tiagoduarte.challenge.stubs

import pt.tiagoduarte.challenge.data.remote.model.ProductListResponse
import pt.tiagoduarte.challenge.utils.readFromJSONToModel

const val GET_PRODUCTS_SUCCESS_RESPONSE = "get_products_success_response.json"
const val GET_PRODUCTS_ERROR_RESPONSE = "get_products_error_response.json"

fun productListResponseStub() = readFromJSONToModel<ProductListResponse>(GET_PRODUCTS_SUCCESS_RESPONSE)

fun productsResponseStub() = productListResponseStub().products

