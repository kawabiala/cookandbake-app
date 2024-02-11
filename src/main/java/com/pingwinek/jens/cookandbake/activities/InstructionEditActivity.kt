package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_INSTRUCTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_TITLE
import com.pingwinek.jens.cookandbake.R

class InstructionEditActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_instruction)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val instructionView = findViewById<TextView>(R.id.editInstruction)

        intent.extras?.getString(EXTRA_RECIPE_TITLE)?.let { title ->
            supportActionBar?.title = title
        }

        intent.extras?.getString(EXTRA_RECIPE_INSTRUCTION)?.let { instruction ->
            instructionView.text = instruction
        }

        instructionView.requestFocus()

        optionMenu.apply {
            addMenuEntry(
                R.id.OPTION_MENU_DONE,
                resources.getString(R.string.save),
                R.drawable.ic_action_done_black,
                true
            ) {
                save()
                true
            }
        }
    }

    @Composable
    override fun ScaffoldContent() {
        TODO("Not yet implemented")
    }

    private fun save() {
        setResult(Activity.RESULT_OK, Intent().also {
            it.putExtra(EXTRA_RECIPE_INSTRUCTION, findViewById<TextView>(R.id.editInstruction).text.toString())
        })
        finish()
    }

}