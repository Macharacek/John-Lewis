package com.test.jl.data

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.test.jl.component.PriceDeserializer

data class Products(val products: List<Product>)

data class Product(
    val productId: String, val title: String, val colorSwatches: List<ColorSwatches>, val price: Price
)

@JsonDeserialize(using = PriceDeserializer::class)
data class Price(val was: String, val then1: String?, val then2: String?, val now: String, val currency: String?)

data class ColorSwatches(val color: String, val skuId: String?)

data class OutputProducts(val products: List<OutputProduct>)

data class OutputProduct(val productId: String, val title: String, val colorSwatches: List<OutputColorSwatches>,
                         val nowPrice: String, val priceLabel: String?)

data class OutputColorSwatches(val color: String, val rgbColor: String, val skuId: String?)