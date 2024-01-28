package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.OptionMenu

/**
 * Provides consistent layout frame including support action bar.
 * Simplifies usage of option menu via custom [OptionMenu].
 * The option menu is best set up inside the onCreate lifecycle function
 */
abstract class BaseActivity : AppCompatActivity() {

    val optionMenu = OptionMenu()

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
    }

    /*
    /////////////////////////////////////////////
    / options Menue
    /////////////////////////////////////////////
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { optionMenu.setMenu(it) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return optionMenu.invokeAction(item.itemId)
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