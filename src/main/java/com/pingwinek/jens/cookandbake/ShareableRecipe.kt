package com.pingwinek.jens.cookandbake

import android.content.res.Resources
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import java.util.*

class ShareableRecipe(recipe: Recipe, private val ingredients: LinkedList<Ingredient>) {

    val title = recipe.title
    val description = recipe.description
    val instruction = recipe.instruction
    val ingredientsAsText = ingredients.joinToString(System.lineSeparator()) { ingredient ->
        StringBuilder().apply {
            append("- ")
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
                append("$ingredientsAsText${System.lineSeparator()}${System.lineSeparator()}")
            }
            instruction?.let {
                if (instruction.isNotEmpty()) {
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
            ingredients.forEach { ingredient ->
                append("<li>${ingredient.quantity} ${ingredient.unity} ${ingredient.name}</li>")
            }
            append("</ul>")
            append("<p>$instruction</p>")
            append("<p>$poweredBy</p>")
            append("</div></body>")
            append("</html>")
        }.toString()
    }
}