package com.pingwinek.jens.cookandbake.models

abstract class Recipe {
    abstract var rowid: Int
    abstract val title: String
    abstract val description: String?
    abstract val instruction: String?
    var lastModified: Long = java.util.Date().time
}