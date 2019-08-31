package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.pingwinek.jens.cookandbake.viewModels.InstructionViewModel
import com.pingwinek.jens.cookandbake.R

class InstructionActivity : BaseActivity() {

    private lateinit var instructionViewModel: InstructionViewModel
    private lateinit var instructionView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_instruction)

        Log.i(this::class.java.name, "ActionBar is null: " +  (supportActionBar == null))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        instructionViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(InstructionViewModel::class.java)

        instructionView = findViewById(R.id.editInstruction)

        instructionViewModel.recipeData.observe(this, Observer { recipe ->
            instructionView.text = recipe?.instruction
            supportActionBar?.title = recipe?.title
        })

        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            intent.extras?.getInt(EXTRA_RECIPE_ID)?.let { id ->
                instructionViewModel.load(id)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.instruction_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                save()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        instructionViewModel.save(instructionView.text.toString())
        finish()
    }

}