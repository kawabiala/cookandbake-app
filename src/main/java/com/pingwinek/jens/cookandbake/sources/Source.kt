package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Model
import java.util.LinkedList

/**
 * Interface for data sources.
 *
 * @param T the type of data this source can provide
 */
interface Source<T: Model> {

    /**
     * Requires all data from given data source
     *
     * @return a linked list of given type
     */
    suspend fun getAll() : LinkedList<T>

    /**
     * Requires one instance of given data
     *
     * @param id of required instance
     * @return an instance of given type
     */
    suspend fun get(id: String) : T?

    /**
     * Inserts a new instance to the given source.
     *
     * @param item the instance to be inserted
     * @return the inserted instance
     */
    suspend fun new(item: T) : T?

    /**
     * Updates the given instance in the source.
     *
     * @param item The instance to be updated
     * @return the updated instance
     */
    suspend fun update(item: T) : T?

    /**
     * Deletes the instance with the given id
     *
     * @param item of the instance to be deleted
     * @return true if successful, otherwise false
     */
    suspend fun delete(item: T) : Boolean
}