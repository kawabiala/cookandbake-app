package com.pingwinek.jens.cookandbake.models

import com.pingwinek.jens.cookandbake.lib.sync.Model

interface File: Model {
    val fileName: String
    var entityId: Int?
    var entity: String?
}