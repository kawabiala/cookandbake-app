package com.pingwinek.jens.cookandbake.lib.sync

import java.util.LinkedList

/**
 * Interface for data sources. All methods return a [Promise],
 * that can be used to check the [Promise.Status] and to
 * retrieve the result in case of success.
 *
 * @param T the type of data this source can provide
 */
interface Source<T: Model> {

    /**
     * Requires all data from given data source
     *
     * @return a [Promise] containing a linked list of given type
     */
    suspend fun getAll() : LinkedList<T>

    /**
     * Requires one instance of given data
     *
     * @param id of required instance
     * @return a [Promise] containing an instance of given type
     */
    suspend fun get(id: String) : T?

    /**
     * Inserts a new instance to the given source.
     *
     * @param item the instance to be inserted
     * @return a [Promise] containing the inserted instance
     */
    suspend fun new(item: T) : T?

    /**
     * Updates the given instance in the source.
     *
     * @param item The instance to be updated
     * @return a [Promise] containing the updated instance
     */
    suspend fun update(item: T) : T?

    /**
     * Deletes the instance with the given id
     *
     * @param id of the instance to be deleted
     * @return a [Promise]
     */
    suspend fun delete(item: T) : Boolean
}