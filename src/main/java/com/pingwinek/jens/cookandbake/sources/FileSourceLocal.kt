package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB
import com.pingwinek.jens.cookandbake.lib.sync.SourceLocal
import com.pingwinek.jens.cookandbake.models.FileLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.processNextEventInCurrentThread
import java.util.*

class FileSourceLocal private constructor(private val db: PingwinekCooksDB)
    : FileSource<FileLocal>, SourceLocal<FileLocal> {

    override suspend fun getAll(): LinkedList<FileLocal> {
        return LinkedList(db.fileDAO().selectAll().asList())
    }

    override suspend fun getAllForEntityId(entity: String, entityId: Int): LinkedList<FileLocal> {
        return LinkedList(db.fileDAO().selectAllForEntityId(entity, entityId).asList())
    }

    suspend fun getForFileName(fileName: String) : FileLocal? {
        return db.fileDAO().selectForFileName(fileName)
    }

    override suspend fun get(id: Int): FileLocal? {
        return db.fileDAO().select(id)
    }

    override suspend fun getForRemoteId(remoteId: Int): FileLocal? {
        return db.fileDAO().selectForRemoteId(remoteId)
    }

    override suspend fun new(item: FileLocal): FileLocal? {
        /*
        remoteId is unique -> if we already have a file with the same remoteId,
        we delete it, before we insert the new one
         */
        item.remoteId?.let { remoteId ->
            getForRemoteId(remoteId)?.let { delete(it.id) }
        }

        val newId = db.fileDAO().insert(item)
        return db.fileDAO().select(newId.toInt())!!
    }

    override suspend fun update(item: FileLocal): FileLocal? {
        val updated = db.fileDAO().update(item)
        return if (updated > 0) {
            get(item.id)!!
        } else {
            null
        }
    }

    override suspend fun delete(id: Int): Boolean {
        val toDelete = get(id) ?: return false
        return delete(toDelete)
    }

    suspend fun flagAsDeleted(id: Int) : FileLocal? {
        val toFlag = get(id)?.apply {
            flagAsDeleted = true
        } ?: return null
        return update(toFlag)
    }

    private fun delete (item: FileLocal): Boolean {
        return db.fileDAO().delete(item) > 0
    }

    companion object : SingletonHolder<FileSourceLocal, PingwinekCooksDB>(::FileSourceLocal)

}