package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import java.util.*

const val EXTRA_RECIPE_ID = "recipeID"

class RecipeListingActivity : BaseActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe_listing)

        auth = Firebase.auth

        recipeListingModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeListingViewModel::class.java)

        val recipeListData = recipeListingModel.recipeListData
        val recipeList = recipeListData.value ?: LinkedList()
        val viewAdapter = RecipeListingAdapter(recipeList)

        recipeListData.observe(this, Observer {
            viewAdapter.notifyDataSetChanged()
        })

        findViewById<RecyclerView>(R.id.recipeList).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@RecipeListingActivity)
            adapter = viewAdapter
        }

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.recipeListSwipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
            swipeRefreshLayout.isRefreshing = false
        }

        configureOptionMenu()
    }

    override fun onResume() {
        super.onResume()
        recipeListingModel.loadData()
/*
        if (recipeListingModel.authService.isLoggedIn()) {
            optionMenu.addMenuEntry(
                R.id.OPTION_MENU_LOGIN,
                resources.getString(R.string.logged_in_as, recipeListingModel.authService.getStoredAccount()?.getEmail()),
                R.drawable.ic_login_person_black,
                true
            ) {
                AlertDialog.Builder(this).apply {
                    setMessage(resources.getString(
                        R.string.logged_in_as,
                        recipeListingModel.authService.getStoredAccount()?.getEmail()
                    ))
                    setPositiveButton("Ok") { _, _ ->
                        // do nothing
                    }
                }.create().show()
                true
            }
        } else {
            optionMenu.addMenuEntry(
                R.id.OPTION_MENU_LOGIN,
                resources.getString(R.string.login),
                R.drawable.ic_login_person_outline_black,
                true
                ) {
                startActivity(Intent(this@RecipeListingActivity, ManageAccountActivity::class.java))
                true
            }
        }

 */

        if (auth.currentUser != null) {
            optionMenu.addMenuEntry(
                R.id.OPTION_MENU_LOGIN,
                resources.getString(R.string.logged_in_as, auth.currentUser!!.email),
                R.drawable.ic_login_person_black,
                true
            ) {
                AlertDialog.Builder(this).apply {
                    setMessage(resources.getString(
                        R.string.logged_in_as,
                        auth.currentUser!!.email
                    ))
                    setPositiveButton("Ok") { _, _ ->
                        // do nothing
                    }
                }.create().show()
                true
            }
        } else {
            optionMenu.addMenuEntry(
                R.id.OPTION_MENU_LOGIN,
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
            addMenuEntry(R.id.OPTION_MENU_REFRESH, resources.getString(R.string.refresh)) {
                refresh()
                true
            }
            addMenuEntry(R.id.OPTION_MENU_MANAGE_ACCOUNT, resources.getString(R.string.manage_account)) {
                startActivity(Intent(this@RecipeListingActivity, ManageAccountActivity::class.java))
                true
            }
            addMenuEntry(R.id.OPTION_MENU_IMPRESSUM, resources.getString(R.string.impressum)) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
                true
            }
            addMenuEntry(R.id.OPTION_MENU_DATAPROTECTION, resources.getString(R.string.dataprotection)) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
//                    .putExtra("url", URL_DATAPROTECTION))
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
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

    private fun refresh() {
        recipeListingModel.loadData(true)
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
        viewHolder.recipeTitle.text = recipeList[position].title
        viewHolder.recipeDescription.text = recipeList[position].description
        viewHolder.recipeListItem.tag = recipeList[position].id
    }
}

class RecipeListingViewHolder(val recipeListItem: ConstraintLayout) : RecyclerView.ViewHolder(recipeListItem) {
    val recipeTitle = recipeListItem.getViewById(R.id.itemTitle) as TextView
    val recipeDescription = recipeListItem.getViewById(R.id.itemDescription) as TextView
}