package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import java.util.*

const val EXTRA_RECIPE_ID = "recipeID"

class RecipeListingActivity : BaseActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe_listing)

        val recipeList = LinkedList<Recipe>()

        val viewAdapter = RecipeListingAdapter(recipeList)

        findViewById<RecyclerView>(R.id.recipeList).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@RecipeListingActivity)
            adapter = viewAdapter
        }

        recipeListingModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(RecipeListingViewModel::class.java)
        val recipeListData = recipeListingModel.recipeListData

        recipeListData.observe(this, Observer { newRecipeList: LinkedList<Recipe>? ->
            newRecipeList?.let { nrl ->
                recipeList.clear()
                recipeList.addAll(nrl.sortedBy { recipe ->
                    recipe.title
                })
                viewAdapter.notifyDataSetChanged()
            }
        })

        authService = recipeListingModel.authService
        configureOptionMenu()
    }

    override fun onResume() {
        super.onResume()
        loadData()

        if (authService.isLoggedIn()) {
            optionMenu.addMenuEntry(
                OPTION_MENU_LOGIN,
                resources.getString(R.string.logged_in_as, authService.getStoredAccount()),
                R.drawable.ic_login_person_black,
                true
            ) {
                AlertDialog.Builder(this).apply {
                    setMessage(resources.getString(
                        R.string.logged_in_as,
                        authService.getStoredAccount()?.getEmail()
                    ))
                    setPositiveButton("Ok") { _, _ ->
                        // do nothing
                    }
                }.create().show()
                true
            }
        } else {
            optionMenu.addMenuEntry(
                OPTION_MENU_LOGIN,
                resources.getString(R.string.login),
                R.drawable.ic_login_person_outline_black,
                true
                ) {
                startActivity(Intent(this@RecipeListingActivity, ManageAccountActivity::class.java))
                true
            }
        }
    }

    private fun configureOptionMenu() {
        optionMenu.apply {
            addMenuEntry(OPTION_MENU_MANAGE_ACCOUNT, resources.getString(R.string.manage_account)) {
                startActivity(Intent(this@RecipeListingActivity, ManageAccountActivity::class.java))
                true
            }
            addMenuEntry(OPTION_MENU_IMPRESSUM, resources.getString(R.string.impressum)) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("url", IMPRESSUMPATH))
                true
            }
            addMenuEntry(OPTION_MENU_DATAPROTECTION, resources.getString(R.string.dataprotection)) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("url", DATAPROTECTIONPATH))
                true
            }
        }
    }

    fun onRecipeItemClick(recipeItem: View) {
        openRecipeItem(recipeItem.tag as Int)
    }

    fun onNewRecipeClick(button: View) {
        openRecipeItem(null)
    }

    private fun loadData() {
        recipeListingModel.loadData()
    }

    private fun openRecipeItem(itemId: Int?) {
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

        (viewHolder.recipeListItem.getViewById(R.id.itemTitle) as TextView).text = recipeList[position].title
        (viewHolder.recipeListItem.getViewById(R.id.itemDescription) as TextView).text = recipeList[position].description
        viewHolder.recipeListItem.tag = recipeList[position].id
    }
}

class RecipeListingViewHolder(val recipeListItem: ConstraintLayout) : RecyclerView.ViewHolder(recipeListItem)