package com.pingwinek.jens.cookandbake.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pingwinek.jens.cookandbake.lib.sync.ModelLocal
import java.util.*

@Entity(indices = [Index(value = ["remoteId"], unique = true)])
data class FileLocal(
    @PrimaryKey(autoGenerate = true) override val id: Int,
    override val fileName: String,
    override var lastModified: Long = Date().time
    ) : ModelLocal, File {

    override var entityId: Int? = null
        set(value) {
            updateLastModified()
            field = value
        }

    override var entity: String? = null
        set(value) {
            updateLastModified()
            field = value
        }

    override var remoteId: Int? = null
        set(value) {
            updateLastModified()
            field = value
        }

    var flagAsDeleted: Boolean = false
        set(value) {
            updateLastModified()
            field = value
        }

    private fun updateLastModified() {
        lastModified = Date().time
    }
}