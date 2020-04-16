package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.OptionMenu

/*
Sets option menu, handles user interaction with login / logout and defines handler for login and logout events
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
/*
    protected open fun getOptionsMenu() : OptionMenu? {
        return null
    }
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
/*
class OptionMenu {

    inner class OptionMenuItem(val itemName: String, val action: (Int) -> Boolean) {
        var iconId: Int? = null
        var ifRoom: Boolean = false
    }

    private val options: MutableMap<Int, OptionMenuItem> = mutableMapOf()

    fun addMenuEntry(itemId: Int, itemName: String, action: (Int) -> Boolean) : OptionMenuItem {
        val optionMenuItem = OptionMenuItem(itemName, action)
        options[itemId] = optionMenuItem
        return optionMenuItem
    }

    fun getMenuEntries() : Map<Int, OptionMenuItem> {
        return options
    }

    fun invokeAction(itemId: Int) : Boolean {
        return options[itemId]?.action?.invoke(itemId) ?: false
    }
}

 */