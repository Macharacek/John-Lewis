package com.test.jl

import com.test.jl.data.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIT {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate
    @MockBean
    lateinit var restTemplate: RestTemplate
    @Value("\${url}")
    lateinit var url: String

    @Before
    fun setUp() {
        val products = Products(
            listOf(
                Product(
                    "100", "first",
                    listOf(ColorSwatches("Blue", "456")),
                    Price("50.00", null, "40.00", "30.00", "GBP")
                ),
                Product(
                    "101", "second",
                    listOf(ColorSwatches("Silver", "457")),
                    Price("5.00", null, null, "4.50", "GBP")
                ),
                Product(
                    "102", "third",
                    listOf(ColorSwatches("Teal", "458")),
                    Price("120.00", "100.00", "70.00", "45.00", "GBP")
                )

            )
        )

        Mockito.`when`(restTemplate.getForObject(url, Products::class.java)).thenReturn(products)
    }

    @Test
    fun showWasNow() {
        val result = queryProducts("ShowWasNow")
        val defaultResult = queryProducts("")

        val outputProducts = outputProducts(listOf("Was £120, now £45", "Was £50, now £30", "Was £5.00, now £4.50"))

        assertEquals(outputProducts, result)
        assertEquals(result, defaultResult)
    }

    @Test
    fun showWasThenNow() {
        val result = queryProducts("ShowWasThenNow")

        val outputProducts = outputProducts(listOf("Was £120, then £70, now £45",
            "Was £50, then £40, now £30", "Was £5.00, then , now £4.50"))

        assertEquals(outputProducts, result)
    }

    @Test
    fun showPercDscount() {
        val result = queryProducts("ShowPercDscount")

        val outputProducts = outputProducts(
            listOf("62.50% off - now £45", "40.00% off - now £30", "10.00% off - now £4.50"))

        assertEquals(outputProducts, result)
    }

    private fun queryProducts(labelType: String) = testRestTemplate.getForObject(
        "/products?labelType={labelType}", OutputProducts::class.java, mapOf("labelType" to labelType)
    )

    private fun outputProducts(priceLabels: List<String>): OutputProducts {
        return OutputProducts(
            listOf(
                OutputProduct(
                    "102", "third",
                    listOf(OutputColorSwatches("Teal", "008080", "458")),
                    "£45", priceLabels[0]
                ),
                OutputProduct(
                    "100", "first",
                    listOf(OutputColorSwatches("Blue", "0000FF", "456")),
                    "£30", priceLabels[1]
                ),
                OutputProduct(
                    "101", "second",
                    listOf(OutputColorSwatches("Silver", "C0C0C0", "457")),
                    "£4.50", priceLabels[2]
                )
            )
        )
    }

}