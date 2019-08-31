package com.pingwinek.jens.cookandbake.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest

const val LOGIN_EVENT = "login"
const val LOGOUT_EVENT = "logout"

/*
Sets option menu, handles user interaction with login / logout and defines handler for login and logout events
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val domain = "https://www.pingwinek.de"
//    doesn't work (Exception in CronetUrlRequest: net::ERR_INVALID_URL):
//    protected val domain = "localhost/strato/pingwinek"
    protected val baseUrl = "$domain/cookandbake"
    protected val authPath = "$baseUrl/authenticate"
    protected val registerPath = "$baseUrl/auth/register"
    protected val loginPath = "$baseUrl/auth/login"

    protected lateinit var networkRequest: NetworkRequest

    /*
    /////////////////////////////////////////////
    / First the lifecycle methods
    /////////////////////////////////////////////
     */

    // when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        super.setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))

        networkRequest = NetworkRequest.getInstance(this.application)

        val intentFilter = IntentFilter().apply {
            addAction(LOGOUT_EVENT)
            addAction(LOGIN_EVENT)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, intentFilter)
    }

    // when the activity is finally destroyed
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver)
    }

    /*
    /////////////////////////////////////////////
    / options Menue
    /////////////////////////////////////////////
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.registerOption -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.loginOption -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logoutOption -> {
                //logout()
                true
            }
            R.id.recipesOption -> {
                val intent = Intent(this, RecipeListingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
    /////////////////////////////////////////////
    / login/logout related stuff
    /////////////////////////////////////////////
     */

    private val loginReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                when (intent.action) {
                    LOGIN_EVENT -> onLogin(intent)
                    LOGOUT_EVENT -> onLogout(intent)
                }
            }
        }
    }

    open fun onLogin(intent: Intent) {

    }

    open fun onLogout(intent: Intent) {
        startActivity(Intent(this, LoginActivity::class.java))
    }


    /*
    /////////////////////////////////////////////
    / layout and view management
    /////////////////////////////////////////////
     */

    protected fun addContentView (viewId: Int) {
        val baseLayout = findViewById<View>(R.id.mainContent) as ViewGroup
        val layoutInflater = this.layoutInflater
        layoutInflater.inflate(viewId, baseLayout)
    }
}