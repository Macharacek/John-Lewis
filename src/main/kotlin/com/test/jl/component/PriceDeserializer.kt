package com.test.jl.component

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.TextNode
import com.test.jl.data.Price
import java.io.IOException

class PriceDeserializer @JvmOverloads constructor(c: Class<*>? = null) : StdDeserializer<Price>(c) {

    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Price {
        val node: TreeNode = jsonParser.codec.readTree(jsonParser)
        val now = node.get("now")
        val currency = node.get("currency")
        val then1 = node.get("then1")
        val then2 = node.get("then2")

        return if (now is TextNode) {
            val was = node.get("was")

            Price(
                asText(was), asText(then1), asText(then2), asText(now), asText(currency)
            )
        } else {
            val from = now.get("from")
            val to = now.get("to")

            Price(
                asText(from), asText(then1), asText(then2), asText(to), asText(currency)
            )
        }
    }

    private fun asText(node: TreeNode) = if (node is TextNode) node.asText() else ""

}