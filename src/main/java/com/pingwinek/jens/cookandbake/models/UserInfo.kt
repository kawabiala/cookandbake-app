package com.pingwinek.jens.cookandbake.models

import com.pingwinek.jens.cookandbake.lib.sync.Model

abstract class UserInfo: Model {
    abstract val crashlyticsEnabled: Boolean
}