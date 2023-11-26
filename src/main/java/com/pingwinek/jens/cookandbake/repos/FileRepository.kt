package com.pingwinek.jens.cookandbake.repos

import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.File
import com.pingwinek.jens.cookandbake.models.FileLocal
import com.pingwinek.jens.cookandbake.models.FileRemote
import com.pingwinek.jens.cookandbake.sources.FileManagerLocal
import com.pingwinek.jens.cookandbake.sources.FileManagerRemote
import com.pingwinek.jens.cookandbake.sources.FileSourceLocal
import com.pingwinek.jens.cookandbake.sources.FileSourceRemote
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

class FileRepository private constructor(private val application: PingwinekCooksApplication): AuthService.AuthenticationListener {

    private val fileSourceLocal = application.getServiceLocator().getService(FileSourceLocal::class.java)
    private val fileSourceRemote = application.getServiceLocator().getService(FileSourceRemote::class.java)
    private val fileManagerLocal = FileManagerLocal(application)
    private val fileManagerRemote = FileManagerRemote(application)
    private val syncService = application.getServiceLocator().getService(SyncService::class.java)

    private val repoListData = MutableLiveData<LinkedList<FileLocal>>()
    val fileListData = repoListData.map() {
        LinkedList<File>().apply {
            it.forEach { file ->
                if (!file.flagAsDeleted) {
                    add(file)
                }
            }
        }
    }

    override fun onLogin() {
        TODO("Not yet implemented")
    }

    override fun onLogout() {
        TODO("Not yet implemented")
    }

    suspend fun deleteFile(name: String) {
        TODO("Not yet implemented")
    }

    suspend fun loadFile(name: String) : ParcelFileDescriptor? {
        return fileManagerLocal.loadFile(name)
    }

    suspend fun getFilesForEntityId(entity: String, entityId: Int) {
        updateFileList(fileSourceLocal.getAllForEntityId(entity, entityId))
        syncFilesForEntityId(entity, entityId)
        updateFileList(fileSourceLocal.getAllForEntityId(entity, entityId))
    }

    suspend fun newFile(entity: String, entityId: Int, parcelFileDescriptor: ParcelFileDescriptor, type: NetworkRequest.ContentType) {
        val newLocalFileName = fileManagerLocal.newFile(parcelFileDescriptor)
        fileSourceLocal.new(FileLocal(0, newLocalFileName).apply {
            this.entity = entity
            this.entityId = entityId
        })
        syncFilesForEntityId(entity, entityId)
        updateFileList(fileSourceLocal.getAllForEntityId(entity, entityId))
    }

    suspend fun updateFile(name: String, parcelFileDescriptor: ParcelFileDescriptor) {
        val fileLocal = fileSourceLocal.getForFileName(name) ?: return
        val entity = fileLocal.entity ?: return
        val entityId = fileLocal.entityId ?: return
        fileManagerLocal.updateFile(parcelFileDescriptor, name)
        fileSourceLocal.update(fileLocal)
        syncFilesForEntityId(entity, entityId)
        updateFileList(fileSourceLocal.getAllForEntityId(entity, entityId))
    }

    private suspend fun syncFilesForEntityId(entity: String, entityId: Int) {
        val locals = fileSourceLocal.getAllForEntityId(entity, entityId)
        val remotes = fileSourceRemote.getAllForEntityId(entityId)
        syncService.sync(locals, remotes)
    }

    private fun updateFileList(fileList: LinkedList<FileLocal>) {
        val updatedList = repoListData.value ?: LinkedList()
        updatedList.apply {
            clear()
            addAll(fileList.filter {
                !it.flagAsDeleted
            })
        }
    }

    companion object : SingletonHolder<FileRepository, PingwinekCooksApplication>(::FileRepository)
}