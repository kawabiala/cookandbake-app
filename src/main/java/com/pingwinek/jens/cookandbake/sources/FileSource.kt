package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.models.File
import java.util.*

/**
 * Source for retrieving and manipulating files
 *
 * @param T a subtype of [File]
 */
interface FileSource<T: File> : Source<T> {

    suspend fun getAllForEntityId(entity: String, entityId: Int) : LinkedList<T>
}