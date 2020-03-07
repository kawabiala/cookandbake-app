package com.pingwinek.jens.cookandbake

import android.app.Application
import android.os.AsyncTask
import androidx.room.Room
import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB

class DatabaseService {

    companion object {
        fun getDatabase(application: Application) : PingwinekCooksDB {
            return Room.databaseBuilder(application, PingwinekCooksDB::class.java, "PingwinekCooks")
                .fallbackToDestructiveMigration()
                .build()
        }

        fun resetDatabase(application: Application) {
            class ResetInBackgroundTask :AsyncTask<Unit, Unit, Unit>() {
                override fun doInBackground(vararg params: Unit?) {
                    getDatabase(application).clearAllTables()
                }
            }
            ResetInBackgroundTask().execute()
        }
    }

}