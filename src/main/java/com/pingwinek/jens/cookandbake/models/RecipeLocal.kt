package com.pingwinek.jens.cookandbake.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index(value = ["remoteId"], unique = true)])
data class RecipeLocal(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") override var id: Int = 0,
    @ColumnInfo(name = "remoteId") val remoteId: Int?,
    override val title: String,
    override val description: String?,
    override val instruction: String?,
    override var lastModified: Long = Date().time,
    val flagAsDeleted: Boolean = false
) : Recipe() {

    constructor(
        title: String,
        description: String?,
        instruction: String?
    ) : this(
        0,
        null,
        title,
        description,
        instruction
    )

    override fun getUpdated(recipe: Recipe): RecipeLocal {
        return RecipeLocal(id, remoteId, recipe.title, recipe.description, recipe.instruction)
    }

    fun getUpdated(title: String, description: String?, instruction: String?): RecipeLocal {
        return RecipeLocal(id, remoteId, title, description, instruction)
    }

    fun getDeleted(): RecipeLocal {
        return RecipeLocal(id, remoteId, title, description, instruction, Date().time, true)
    }

    companion object {

        fun newFromRemote(remote: RecipeRemote) : RecipeLocal {
            return RecipeLocal(0, remote.id, remote.title, remote.description, remote.instruction, remote.lastModified)
        }
    }
}