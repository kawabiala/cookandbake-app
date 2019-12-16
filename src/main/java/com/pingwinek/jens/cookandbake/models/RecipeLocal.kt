package com.pingwinek.jens.cookandbake.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONException
import org.json.JSONObject

@Entity(indices = [Index(value = ["remoteId"], unique = true)])
data class RecipeLocal(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") override var rowid: Int = 0,
    @ColumnInfo(name = "remoteId") val remoteId: Int?,
    override val title: String,
    override val description: String?,
    override val instruction: String?
) : Recipe() {

    fun getUpdated(title: String, description: String?, instruction: String?) : RecipeLocal {
        return RecipeLocal(rowid, remoteId, title, description, instruction)
    }

    companion object {

        fun fromRemote(remote: RecipeRemote) : RecipeLocal {
            return RecipeLocal(0, remote.rowid, remote.title, remote.description, remote.instruction)
        }
    }
}