package com.pingwinek.jens.cookandbake.lib.sync

import com.pingwinek.jens.cookandbake.lib.sync.Model

interface ModelLocal : Model {
    val remoteId: Int?
}