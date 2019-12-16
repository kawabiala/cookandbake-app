package com.pingwinek.jens.cookandbake.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONException
import org.json.JSONObject

@Entity(indices = [Index(value = ["remoteId"], unique = true)])
data class IngredientLocal(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") override val id: Int?,
    val remoteId: Int?,
    override val recipeId: Int,
    override val quantity: Double?,
    override val unity: String?,
    override val name: String
) : Ingredient() {

    fun getUpdated(quantity: Double?, unity: String?, name: String) : IngredientLocal {
        return IngredientLocal(id, remoteId, recipeId, quantity, unity, name)
    }

    companion object {

        fun newFromRemote(remote: IngredientRemote, recipeId: Int) : IngredientLocal {
            return IngredientLocal(
                0,
                remote.id,
                recipeId,
                remote.quantity,
                remote.unity,
                remote.name
            )
        }
    }
}