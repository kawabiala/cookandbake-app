package com.pingwinek.jens.cookandbake.lib.sync

interface SourceLocal <TLocal: ModelLocal> : Source<TLocal> {

    suspend fun getForRemoteId(remoteId: Int) : TLocal?
}