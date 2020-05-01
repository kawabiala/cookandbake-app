package com.pingwinek.jens.cookandbake.db

import android.app.Application
import android.os.AsyncTask
import androidx.room.Room
import com.pingwinek.jens.cookandbake.utils.SingletonHolder

class DatabaseService private constructor(application: Application){

    val pingwinekCooksDB = Room.databaseBuilder(application, PingwinekCooksDB::class.java, "PingwinekCooks")
        .fallbackToDestructiveMigration()
        .build()

    fun resetDatabase() {
        class ResetInBackgroundTask :AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                pingwinekCooksDB.clearAllTables()
            }
        }
        ResetInBackgroundTask().execute()
    }

    companion object : SingletonHolder<DatabaseService, Application>(::DatabaseService)

}