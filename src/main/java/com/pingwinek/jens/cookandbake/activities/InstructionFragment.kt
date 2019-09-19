package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_INSTRUCTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_TITLE
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.REQUEST_CODE_INSTRUCTION
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

class InstructionFragment : Fragment() {

    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeModel = activity?.run {
            ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
                .get(RecipeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_instruction, container, false)
        val instructionView = view.findViewById<TextView>(R.id.instruction)

        recipeModel.recipeData.observe(this, Observer { recipe ->
            instructionView.text = recipe?.instruction
        })

        instructionView.setOnClickListener {
            activity?.startActivityForResult(Intent(activity, InstructionActivity::class.java).also {
                it.putExtra(EXTRA_RECIPE_TITLE, recipeModel.recipeData.value?.title)
                it.putExtra(EXTRA_RECIPE_INSTRUCTION, recipeModel.recipeData.value?.instruction)
            }, REQUEST_CODE_INSTRUCTION)
        }

        return view
    }
}