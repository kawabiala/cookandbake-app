package com.pingwinek.jens.cookandbake.db

import androidx.room.*
import com.pingwinek.jens.cookandbake.models.FileLocal

@Dao
interface FileDAO {

    /**
     * Returns the id of the inserted file
     */
    @Insert
    fun insert(file: FileLocal) : Long

    /**
     * Returns the number of update rows
     */
    @Update
    fun update(file: FileLocal) : Int

    /**
     * Returns the number of deleted rows
     */
    @Delete
    fun delete(file: FileLocal) : Int

    /**
     *
     */
    @Query("SELECT * FROM fileLocal")
    fun selectAll() : Array<FileLocal>

    /**
     *
     */
    @Query("SELECT * FROM fileLocal WHERE entity = :entity AND entityId = :entityId")
    fun selectAllForEntityId(entity: String, entityId: Int) : Array<FileLocal>

    /**
     *
     */
    @Query("SELECT * FROM fileLocal WHERE id = :id")
    fun select(id: Int) : FileLocal?

    /**
     *
     */
    @Query("SELECT * FROM fileLocal WHERE remoteId = :remoteId")
    fun selectForRemoteId(remoteId: Int) : FileLocal?

    /**
     *
     */
    @Query("SELECT * FROM fileLocal WHERE fileName = :fileName")
    fun selectForFileName(fileName: String) : FileLocal?
}