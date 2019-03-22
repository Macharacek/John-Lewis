package com.test.jl.controller

import com.test.jl.data.OutputProducts
import com.test.jl.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GreetingController(
    @Autowired private val productService: ProductService
) {

    @GetMapping("/products")
    fun products(@RequestParam("labelType", required = false, defaultValue = "ShowWasNow")
                 labelType: String): OutputProducts? {
        return productService.products(labelType)
    }

}
