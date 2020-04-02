package com.pingwinek.jens.cookandbake.lib.sync

interface SourceLocal <TLocal: ModelLocal> : Source<TLocal> {

    fun getForRemoteId(remoteId: Int) : Promise<TLocal>
}