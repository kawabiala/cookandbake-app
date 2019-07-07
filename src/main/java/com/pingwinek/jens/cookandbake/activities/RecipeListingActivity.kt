package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeListingViewModel
import java.util.*


public const val EXTRA_RECIPE_ID = "recipeID"

class RecipeListingActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recipeListingModel: RecipeListingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe_listing)

        var recipeList = LinkedList<Recipe>();

        viewManager = LinearLayoutManager(this)
        viewAdapter = RecipeListingAdapter(recipeList)

        recyclerView = findViewById<RecyclerView>(R.id.recipeList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        recipeListingModel = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application))
            .get(RecipeListingViewModel::class.java)
        val recipeListData = recipeListingModel.recipeListData

        recipeListData.observe(this, Observer { newRecipeList: LinkedList<Recipe>? ->
            newRecipeList?.let { nrl ->
                recipeList.clear()
                recipeList.addAll(nrl)
                viewAdapter.notifyDataSetChanged()
            }
        })

        recipeListingModel.loadData()
    }

    fun onRecipeItemClick(recipeItem: View) {
        openRecipeItem(recipeItem.tag as Int)
    }

    fun onNewRecipeClick(button: View) {
        openRecipeItem(null)
    }

    fun openRecipeItem(itemId: Int?) {
        val intent = Intent(this, RecipeActivity::class.java)
        itemId?.let { _itemId ->
            intent.apply {
                putExtra(EXTRA_RECIPE_ID, _itemId)
            }
        }
        startActivity(intent)
    }
}

class RecipeListingAdapter(private var recipeList: LinkedList<Recipe>) :
    RecyclerView.Adapter<RecipeListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListingViewHolder {

        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_recipe_list_item, parent, false) as ConstraintLayout

        constraintLayout.minHeight = 40

        return RecipeListingViewHolder(constraintLayout)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(viewHolder: RecipeListingViewHolder, position: Int) {
        Log.i("OnBindViewHolder", "Position: $position")
        (viewHolder.recipeListItem.getViewById(R.id.itemTitle) as TextView).text = recipeList[position].title
        (viewHolder.recipeListItem.getViewById(R.id.itemDescription) as TextView).text = recipeList[position].description
        viewHolder.recipeListItem.tag = recipeList[position].id
    }
}

class RecipeListingViewHolder(val recipeListItem: ConstraintLayout) : RecyclerView.ViewHolder(recipeListItem)