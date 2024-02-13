package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pingwinek.jens.cookandbake.lib.OptionMenu
import com.pingwinek.jens.cookandbake.theming.OptionItem
import com.pingwinek.jens.cookandbake.theming.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.theming.PingwinekCooksTopAppBar

/**
 * Provides consistent layout frame including support action bar.
 * Simplifies usage of option menu via custom [OptionMenu].
 * The option menu is best set up inside the onCreate lifecycle function
 */
abstract class BaseActivity : AppCompatActivity() {

    val optionMenu = OptionMenu()

    private var title: String = ""
    private var showHamburger = false
    private var optionItem1: OptionItem? = null
    private var optionItem2: OptionItem? = null
    private var optionItem3: OptionItem? = null

    private val hamburgerOptions = mutableListOf<OptionItem>()

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
                BaseScaffold()
            }
        }

        //super.setContentView(R.layout.activity_base)
        //setSupportActionBar(findViewById(R.id.toolbar))
    }

    @Composable
    fun BaseScaffold() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        Scaffold(
            topBar = {
                PingwinekCooksTopAppBar(title, showHamburger, drawerState, hamburgerOptions, optionItem1, optionItem2, optionItem3)
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ScaffoldContent()
                /*
                PingwinekCooksHamburgerMenu(drawerState, hamburgerOptions) {

                }*/
            }
        }
    }

    @Composable
    abstract fun ScaffoldContent()

    fun configureTopBar(
        title: String,
        showHamburger: Boolean,
        optionItem1: OptionItem?,
        optionItem2: OptionItem?,
        optionItem3: OptionItem?
    ) {
        this.title = title
        this.showHamburger = showHamburger
        this.optionItem1 = optionItem1
        this.optionItem2 = optionItem2
        this.optionItem3 = optionItem3
    }

    fun addOptionItem(optionItem: OptionItem) {
        hamburgerOptions.add(optionItem)
    }

    /*
    /////////////////////////////////////////////
    / options Menue
    /////////////////////////////////////////////
     */
/*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { optionMenu.setMenu(it) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return optionMenu.invokeAction(item.itemId)
    }

 */

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