package com.pingwinek.jens.cookandbake.lib

import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem

class OptionMenu {

    inner class OptionMenuItem(val itemName: String, val action: (Int) -> Boolean) {
        var iconId: Int? = null
        var ifRoom: Boolean = false
    }

    private val options: MutableMap<Int, OptionMenuItem> = mutableMapOf()

    private var menu: Menu? = null

    fun setMenu(menu: Menu) {
        this.menu = menu
        updateMenu(menu)
    }

    fun getMenuEntry(itemId: Int): OptionMenuItem? {
        return options[itemId]
    }

    fun addMenuEntry(itemId: Int, itemName: String, action: (Int) -> Boolean) {
        addMenuEntry(itemId, itemName, null, action)
    }

    fun addMenuEntry(
        itemId: Int,
        itemName: String,
        iconId: Int?,
        action: (Int) -> Boolean
    ) {
        addMenuEntry(itemId, itemName, iconId, false, action)
    }

    fun addMenuEntry(
        itemId: Int,
        itemName: String,
        iconId: Int?,
        ifRoom: Boolean,
        action: (Int) -> Boolean
    ) {
        val optionMenuItem = OptionMenuItem(itemName, action).also {
            it.iconId = iconId
            it.ifRoom = ifRoom
        }
        options[itemId] = optionMenuItem
        menu?.let {
            updateMenuItem(it, itemId, optionMenuItem)
        }
    }

    fun updateMenuEntry(itemId: Int, optionMenuItem: OptionMenuItem) {
        options[itemId] = optionMenuItem
        menu?.let { updateMenuItem(it, itemId, optionMenuItem) }
    }

    fun deleteMenuEntry(itemId: Int) {
        options.remove(itemId)
        menu?.removeItem(itemId)
    }

    fun getMenuEntries() : Map<Int, OptionMenuItem> {
        return options
    }

    fun invokeAction(itemId: Int) : Boolean {
        return options[itemId]?.action?.invoke(itemId) ?: false
    }

    private fun updateMenu(menu: Menu) {
        options.forEach { option ->
            updateMenuItem(menu, option)
        }
    }

    private fun updateMenuItem(menu: Menu, option: Map.Entry<Int, OptionMenuItem>) {
        updateMenuItem(menu, option.key, option.value)
    }

    private fun updateMenuItem(menu: Menu, key: Int, optionMenuItem: OptionMenuItem) {
        val menuItem = menu.findItem(key)
        if (menuItem == null) {
            menu.add(NONE, key, NONE, optionMenuItem.itemName).apply {
                optionMenuItem.iconId?.let {
                    setIcon(it)
                }
                if (optionMenuItem.ifRoom) {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                }
            }
        } else {
            menuItem.setTitle(optionMenuItem.itemName)
            optionMenuItem.iconId?.let {
                menuItem.setIcon(it)
            }
            if (optionMenuItem.ifRoom) {
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            }
        }
    }

}