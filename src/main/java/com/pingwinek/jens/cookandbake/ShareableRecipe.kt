package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import java.util.LinkedList

class ShareableRecipe(
    recipe: Recipe,
    private val ingredients: LinkedList<Ingredient>,
    private val ingredientHeader: String,
    private val instructionHeader: String) {

    val title = recipe.title
    val description = recipe.description
    val instruction = recipe.instruction

    private val ingredientsAsText = ingredients
        .sortedBy { ingredient -> ingredient.sort }
        .joinToString(System.lineSeparator()) { ingredient ->
            StringBuilder().apply {
                if (ingredient.isGroupHeader) {
                    append("${System.lineSeparator()}")
                } else {
                    append("- ")
                }

                ingredient.quantity?.let {
                    if (it > 0) {
                        append("$it ")
                    }
                }

                ingredient.unity?.let {
                    if (it.isNotEmpty()) {
                        append(("$it "))
                    }
                }

                append(ingredient.name)
            }.toString()
    }

    val subject = "$title - $description"

    fun getPlainText(poweredBy: String?): String {
        return StringBuffer().apply {
            append("$title${System.lineSeparator()}")
            description?.let {
                if (description.isNotEmpty()) {
                    append("$description${System.lineSeparator()}")
                }
            }
            append(System.lineSeparator())
            if (ingredientsAsText.isNotEmpty()) {
                append("$ingredientHeader:${System.lineSeparator()}")
                append("$ingredientsAsText${System.lineSeparator()}${System.lineSeparator()}")
            }
            instruction?.let {
                if (instruction.isNotEmpty()) {
                    append("$instructionHeader:${System.lineSeparator()}")
                    append("$instruction${System.lineSeparator()}${System.lineSeparator()}")
                }
            }
            append(poweredBy)
        }.toString()
    }

    fun getHtml(poweredBy: String?): String {
        return StringBuffer().apply {
            append("<html>")
            append("<body><div>")
            append("<p><b>$title</b></p>")
            append("<p>$description</p>")
            append("<ul>")
            ingredients.sortedBy { ingredient -> ingredient.sort } .forEach { ingredient ->
                if (ingredient.isGroupHeader) {
                    append("<li><b>${ingredient.name}</b></li>")
                } else {
                    append("<li>${ingredient.quantity} ${ingredient.unity} ${ingredient.name}</li>")
                }
            }
            append("</ul>")
            append("<p>$instruction</p>")
            append("<p>$poweredBy</p>")
            append("</div></body>")
            append("</html>")
        }.toString()
    }
}