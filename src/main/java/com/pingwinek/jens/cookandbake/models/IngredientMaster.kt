package com.pingwinek.jens.cookandbake.models

import android.util.Log
import androidx.room.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Entity(indices = [Index(value = ["remoteId"], unique = true)])
class IngredientMaster(
    id: Int = 0,
    remoteId: Int?,
    val recipeId: Int,
    quantity: Double?,
    unity: String?,
    name: String,
    lastModified: Long = Date().time,
    flagAsDeleted: Boolean = false
) {

    constructor(
        recipeId: Int,
        quantity: Double?,
        unity: String?,
        name: String
    ) : this(
        0,
        null,
        recipeId,
        quantity,
        unity,
        name
    ) {
        syncStatus = SyncStatus.LOCAL_INSERT_PENDING
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int = 0

    @Ignore
    var remoteId: Int? = remoteId
        set(value) {
            updateLastModified()
            field = value
        }

    var quantity: Double? = quantity
        set(value) {
            updateLastModified()
            field = value
        }

    var unity: String? = unity
        set(value) {
            updateLastModified()
            field = value
        }

    var name: String = name
        set(value) {
            updateLastModified()
            field = value
        }

    var lastModified: Long = Date().time

    var flagAsDeleted: Boolean = false

    var syncStatus: SyncStatus = SyncStatus.SYNCHRONIZED

    private fun updateLastModified() {
        lastModified = Date().time
    }

    enum class SyncStatus {
        SYNCHRONIZED,
        LOCAL_UPDATE_PENDING,
        REMOTE_UPDATE_PENDING,
        LOCAL_INSERT_PENDING,
        REMOTE_INSERT_PENDING,
        LOCAL_DELETE_PENDING,
        REMOTE_DELETE_PENDING
    }

    fun delete(): IngredientMaster {
        updateLastModified()
        flagAsDeleted = true
        return this
    }

    fun update(ingredient: Ingredient) {
        if (ingredient.id != this.id) {
            Log.w(this::class.java.name, "Trying to update ingredient with different id - refused.")
            return
        }

        if (ingredient.recipeId != this.recipeId) {
            Log.w(this::class.java.name, "Trying to update ingredient with different recipeId - refused.")
            return
        }

        when {
            ingredient.id != this.id -> {
                Log.w(this::class.java.name, "Trying to update ingredient with different id - refused.")
                return
            }
            ingredient.recipeId != this.recipeId -> {
                Log.w(this::class.java.name, "Trying to update ingredient with different recipeId - refused.")
                return
            }
            this.remoteId == null -> {}
            ingredient.lastModified > this.lastModified -> {
                syncStatus = SyncStatus.LOCAL_UPDATE_PENDING
                this.quantity = ingredient.quantity
                this.unity = ingredient.unity
                this.name = ingredient.name
                this.lastModified = ingredient.lastModified
//            this.flagAsDeleted = ingredient.flagAsDeleted
            }
            else -> {
                syncStatus = SyncStatus.REMOTE_UPDATE_PENDING
            }
        }
    }

    fun asMap(): Map<String, String> {
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["recipe_id"] = recipeId.toString()
        map["quantity"] = quantity.toString()
        map["unity"] = unity ?: ""
        map["name"] = name
        map["lastModified"] = lastModified.toString()

        return map
    }

    override fun toString(): String {
        return JSONObject(asMap()).toString()
    }

    companion object {

        fun parse(jsonObject: JSONObject): IngredientMaster {
            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                0
            }

            val recipeId = jsonObject.optInt("recipe_id", -1)

            val quantity = try {
                if (jsonObject.isNull("quantity")) {
                    null
                } else {
                    jsonObject.getDouble("quantity")
                }
            } catch (jsonException: JSONException) {
                null
            }

            val unity = try {
                if (jsonObject.isNull("unity")) {
                    null
                } else {
                    jsonObject.getString("unity")
                }
            } catch (jsonException: JSONException) {
                null
            }

            val name = jsonObject.optString("name", "IngredientRemote")

            val lastModified = try {
                if (jsonObject.isNull("lastModified")) {
                    null
                } else {
                    jsonObject.getLong("lastModified")
                }
            } catch (jsonException: JSONException) {
                null
            }

            return when (lastModified) {
                null -> IngredientMaster(id, null, recipeId, quantity, unity, name)
                else -> IngredientMaster(id, null, recipeId, quantity, unity, name, lastModified)
            }
        }

    }
}