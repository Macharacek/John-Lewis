package com.test.jl.service

import com.test.jl.data.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductService(
    @Value("\${url}") private val url: String,
    @Autowired private val restTemplate: RestTemplate
) {

    fun products(labelType: String?): OutputProducts? {
        val products = restTemplate.getForObject(url, Products::class.java) ?: return null

        val sorted = products.products
            .filter { it.price.was.toDoubleOrNull() != null }
            .filter { it.price.was.toDouble() > it.price.now.toDouble() }
            .sortedByDescending { it.price.was.toDouble().minus(it.price.now.toDouble()) }
            .map {
                OutputProduct(
                    it.productId,
                    it.title,
                    transformColors(it.colorSwatches),
                    transformPrice(it.price.now, it.price.currency),
                    setPriceLabel(labelType, it.price)
                )
            }

        return OutputProducts(sorted)
    }

    private fun transformColors(colorSwatches: List<ColorSwatches>): List<OutputColorSwatches> {
        return colorSwatches
            .map { OutputColorSwatches(it.color, colorsToRgb.getOrDefault(it.color, ""), it.skuId) }
    }

    private fun transformPrice(price: String, currency: String?): String {
        val double = price.toDouble()

        return (if (currency == "GBP") "£" else "") + if (double < 10) String.format("%.2f", double) else double.toInt()
    }

    private fun setPriceLabel(labelType: String?, price: Price) = when (labelType) {
        "ShowWasNow" -> "Was ${transformPrice(price.was, price.currency)}," +
                " now ${transformPrice(price.now, price.currency)}"

        "ShowWasThenNow" -> "Was ${transformPrice(price.was, price.currency)}," +
                " then ${selectThenPrice(price)}, now ${transformPrice(price.now, price.currency)}"

        "ShowPercDscount" -> "${calculateDiscount(price)}% off - now ${transformPrice(price.now, price.currency)}"

        else -> ""
    }

    private fun selectThenPrice(price: Price): String? {
        val selected = when {
            !price.then2.isNullOrBlank() -> price.then2
            !price.then1.isNullOrBlank() -> price.then1
            else -> ""
        }

        return if (!selected.isBlank()) transformPrice(selected, price.currency) else ""
    }

    private fun calculateDiscount(price: Price) =
        String.format("%.2f", 100 * (price.was.toDouble() - price.now.toDouble()) / price.was.toDouble())

    val colorsToRgb: Map<String, String> = mapOf(
        "Black" to "000000",
        "White" to "FFFFFF",
        "Red" to "FF0000",
        "Lime" to "00FF00",
        "Blue" to "0000FF",
        "Yellow" to "FFFF00",
        "Cyan" to "00FFFF",
        "Magenta" to "FF00FF",
        "Silver" to "C0C0C0",
        "Gray" to "808080",
        "Maroon" to "800000",
        "Olive" to "808000",
        "Green" to "008000",
        "Purple" to "800080",
        "Teal" to "008080",
        "Navy" to "000080"
    )

}
