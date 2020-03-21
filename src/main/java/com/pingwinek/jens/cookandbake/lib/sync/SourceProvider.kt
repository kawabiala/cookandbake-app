package com.pingwinek.jens.cookandbake.lib.sync

object SourceProvider {

    val localSources = HashMap<Class<*>, Source<*>>()
    val remoteSources = HashMap<Class<*>, Source<*>>()

    inline fun <reified T> registerLocalSource(source: Source<T>) {
        localSources[T::class.java] = source
    }

    inline fun <reified T> registerRemoteSource(source: Source<T>) {
        remoteSources[T::class.java] = source
    }

    inline fun <reified T> getLocalSource() : Source<T>? {
        return try {
            localSources[T::class.java] as Source<T>
        } catch (classCastException: ClassCastException) {
            null
        }
    }

    fun <T> getLocalSource(localClass: Class<T>) : Source<T>? {
        return try {
            localSources[localClass] as Source<T>
        } catch (classCastException: ClassCastException) {
            null
        }
    }

    inline fun <reified T> getRemoteSource() : Source<T>? {
        return try {
            remoteSources[T::class.java] as Source<T>
        } catch (classCastException: ClassCastException) {
            null
        }
    }

    fun <T> getRemoteSource(remoteClass: Class<T>) : Source<T>? {
        return try {
            remoteSources[remoteClass] as Source<T>
        } catch (classCastException: ClassCastException) {
            null
        }
    }

    inline fun <reified T> removeLocalSource() {
        removeLocalSource(
            T::class.java
        )
    }

    inline fun <reified T> removeRemoteSource() {
        removeRemoteSource(
            T::class.java
        )
    }

    fun <T> removeLocalSource(localClass: Class<T>) {
        localSources.remove(localClass)
    }

    fun <T> removeRemoteSource(remoteClass: Class<T>) {
        remoteSources.remove(remoteClass)
    }

    fun removeLocalSources() {
        localSources.clear()
    }

    fun removeRemoteSources() {
        remoteSources.clear()
    }

    fun removeSources() {
        removeLocalSources()
        removeRemoteSources()
    }
}