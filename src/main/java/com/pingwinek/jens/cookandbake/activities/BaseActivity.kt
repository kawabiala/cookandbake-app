package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksNavigationBar
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksTopAppBar

/**
 */
abstract class BaseActivity : AppCompatActivity() {

    private var title: String = ""
    private var showHamburger = false
    private var optionItemLeft: PingwinekCooksComposables.OptionItem? = null
    private var optionItemMid: PingwinekCooksComposables.OptionItem? = null
    private var optionItemRight: PingwinekCooksComposables.OptionItem? = null

    private val dropDownOptions = mutableListOf<PingwinekCooksComposables.OptionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PingwinekCooksAppTheme {
                BaseScaffold()
            }
        }
    }

    @Composable
    fun BaseScaffold() {
        Scaffold(
            topBar = {
                PingwinekCooksTopAppBar(title, showHamburger, dropDownOptions, optionItemLeft,
                    this.optionItemMid,
                    this.optionItemRight
                )
            },
            bottomBar = { PingwinekCooksNavigationBar() }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ScaffoldContent()
            }
        }
    }

    @Composable
    abstract fun ScaffoldContent()

    fun configureTopBar(
        title: String,
        showHamburger: Boolean = false,
        optionItemLeft: PingwinekCooksComposables.OptionItem? = null,
        optionItemMid: PingwinekCooksComposables.OptionItem? = null,
        optionItemRight: PingwinekCooksComposables.OptionItem? = null
    ) {
        this.title = title
        this.showHamburger = showHamburger
        this.optionItemLeft = optionItemLeft
        this.optionItemRight = optionItemMid
        this.optionItemRight = optionItemRight
    }

    fun addDropDownOptionItem(optionItem: PingwinekCooksComposables.OptionItem) {
        dropDownOptions.add(optionItem)
    }

}