package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_INSTRUCTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_TITLE
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

class InstructionFragment : androidx.fragment.app.Fragment() {

    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeModel = activity?.run {
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[RecipeViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_instruction, container, false)
        val instructionView = view.findViewById<TextView>(R.id.instruction)

        recipeModel.recipeData.observe(viewLifecycleOwner) { recipe ->
            instructionView.text = recipe?.instruction
        }

        if (this.context is RecipeActivity) {
            instructionView.setOnClickListener {
                val instructionIntent = Intent(activity, InstructionEditActivity::class.java).also {
                    it.putExtra(EXTRA_RECIPE_TITLE, recipeModel.recipeData.value?.title)
                    it.putExtra(EXTRA_RECIPE_INSTRUCTION, recipeModel.recipeData.value?.instruction)
                }
                (this.context as RecipeActivity).saveInstructionLauncher.launch(instructionIntent)
            }
        } else {
            Log.w(this::class.java.name, "context is not RecipeActivity")
        }

        return view
    }
}