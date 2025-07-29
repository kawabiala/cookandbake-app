package com.pingwinek.jens.cookandbake.models

abstract class Tag: Model {
    abstract val label: String
    abstract val color: String
    abstract val sort: Int
}