package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.lib.OptionMenu
import com.pingwinek.jens.cookandbake.theming.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.theming.PingwinekCooksScaffold

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
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PingwinekCooksAppTheme {
                PingwinekCooksScaffold("Title") {
                    ScaffoldContent()
                }
            }
        }

        //super.setContentView(R.layout.activity_base)
        //setSupportActionBar(findViewById(R.id.toolbar))
    }

    @Composable
    abstract fun ScaffoldContent()

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
        /*
        val baseLayout = findViewById<View>(R.id.mainContent) as ViewGroup
        val layoutInflater = this.layoutInflater
        layoutInflater.inflate(viewId, baseLayout)

         */
    }
}