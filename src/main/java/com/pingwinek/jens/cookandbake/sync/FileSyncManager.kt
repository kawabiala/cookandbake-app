package com.pingwinek.jens.cookandbake.sync

import com.pingwinek.jens.cookandbake.lib.sync.ModelLocal
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SyncManager
import com.pingwinek.jens.cookandbake.models.File
import com.pingwinek.jens.cookandbake.models.FileLocal
import com.pingwinek.jens.cookandbake.models.FileRemote
import com.pingwinek.jens.cookandbake.sources.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class FileSyncManager(
    private val recipeSourceLocal: RecipeSourceLocal,
    private val fileSourceLocal: FileSourceLocal,
    private val fileSourceRemote: FileSourceRemote,
    private val fileManagerLocal: FileManagerLocal,
    private val fileManagerRemote: FileManagerRemote,
    syncLogic: SyncLogic<FileLocal, FileRemote>
) : SyncManager<FileLocal, FileRemote>(fileSourceLocal, fileSourceRemote, syncLogic) {

    /**
     * [File] has no parent
     */
    override suspend fun getLocalParent(parentId: Int): ModelLocal? {
        return null
    }

    /**
     * [File] has no parents, and recipeId should not be deemed the parentId
     *
     * returns an empty list
     */
    override suspend fun getLocalsByParent(parentId: Int): LinkedList<FileLocal> {
        return LinkedList()
    }

    /**
     * [File] has no parents, and recipeId should not be deemed the parentId
     *
     * returns an empty list
     */
    override suspend fun getRemotesByParent(parentId: Int): LinkedList<FileRemote> {
        return LinkedList()
    }

    override suspend fun newLocal(remote: FileRemote) {
        val parcelFileDescriptor = fileManagerRemote.load(remote.fileName)
        /*
        val fileName = fileManagerLocal.newFile(parcelFileDescriptor, remote.fileName)

         */
        val recipeLocal = remote.entityId?.let { recipeSourceLocal.getForRemoteId(it) }
        fileSourceLocal.new(
            FileLocal(
                0,
                remote.fileName
            ).apply {
                entityId = recipeLocal?.id
            }
        )
    }

    override suspend fun newRemote(local: FileLocal) {
        // Make sure that remote file is not created twice
        Mutex().withLock {
            // Check if remote file has been created in the meantime
            if (fileSourceLocal.get(local.id)?.remoteId != null) return

            // Retrieve remote recipe id
            val remoteRecipeId = local.entityId?.let { recipeSourceLocal.toRemoteId(it) }

            // Create remote file from local file and remote recipe id
            //TODO replace placeholder for Entity
            val newFile = fileSourceRemote.new(
                FileRemote(
                    0,
                    local.fileName,
                    remoteRecipeId,
                    "EntityPlaceHolder",
                    local.lastModified
                )) ?: return

            fileSourceLocal.update(
                FileLocal(
                    local.id,
                    local.fileName,
                    local.lastModified
                ).apply {
                    remoteId = newFile.id
                }
            )
        }
    }

    override suspend fun updateLocal(local: FileLocal, remote: FileRemote) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRemote(local: FileLocal, remote: FileRemote) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocal(local: FileLocal) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRemote(remote: FileRemote) {
        TODO("Not yet implemented")
    }
}