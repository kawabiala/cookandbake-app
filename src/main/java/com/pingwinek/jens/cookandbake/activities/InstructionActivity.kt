package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.pingwinek.jens.cookandbake.*

class InstructionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_instruction)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()

        intent.extras?.getString(EXTRA_RECIPE_TITLE)?.let { title ->
            supportActionBar?.title = title
        }

        intent.extras?.getString(EXTRA_RECIPE_INSTRUCTION)?.let { instruction ->
            findViewById<TextView>(R.id.editInstruction).text = instruction
        }
    }

    override fun getOptionsMenu(): OptionMenu {
        return OptionMenu().apply {
            addMenuEntry(OPTION_MENU_DONE, resources.getString(R.string.save)) {
                save()
                true
            }.apply {
                iconId = R.drawable.ic_action_done
                ifRoom = true
            }
        }
    }

    private fun save() {
        setResult(Activity.RESULT_OK, Intent().also {
            it.putExtra(EXTRA_RECIPE_INSTRUCTION, findViewById<TextView>(R.id.editInstruction).text.toString())
        })
        finish()
    }

}