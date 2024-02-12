package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.theming.OptionItem
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import java.util.LinkedList

class RecipeListingActivity : BaseActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //addContentView(R.layout.activity_recipe_listing)

        auth = Firebase.auth

        recipeListingModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeListingViewModel::class.java)

        val recipeListData = recipeListingModel.recipeListData
        /*
        val viewAdapter = RecipeListingAdapter(recipeListData, this)

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

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            openRecipeItem(null)
        }

        configureOptionMenu()

         */
    }

    override fun onResume() {
        super.onResume()
        recipeListingModel.loadData()

        configureTopBar(
            "Test",
            true,
            OptionItem("left", Icons.AutoMirrored.Filled.ArrowBack) { Log.e(this::class.java.name, "ArrowBack clicked") },
            OptionItem("person", Icons.Filled.Person) { Log.e(this::class.java.name, "Person clicked") },
            null
        )

        addOptionItem(OptionItem("HamburgerOption 1", Icons.Filled.Build) { Log.e(this::class.java.name, "Option 1 clicked") })
/*
        if (auth.currentUser != null) {
            optionMenu.addMenuEntry(
                R.id.OPTION_MENU_LOGIN,
                resources.getString(R.string.logged_in_as, auth.currentUser!!.email),
                R.drawable.ic_login_person_black,
                true
            ) {
                startActivity(Intent(this@RecipeListingActivity, SignInActivity::class.java))
                true
            }
        } else {
            optionMenu.addMenuEntry(
                R.id.OPTION_MENU_LOGIN,
                resources.getString(R.string.login),
                R.drawable.ic_login_person_outline_black,
                true
            ) {
                startActivity(Intent(this@RecipeListingActivity, SignInActivity::class.java))
                true
            }
        }

 */

////////////////////////////////////////////////////////////////////////////////////////////////////
//// Data Migration - used one time for migrating data Jan 2024
////////////////////////////////////////////////////////////////////////////////////////////////////
/*
        AlertDialog.Builder(this).apply {
            setMessage("Migrate?")
            setPositiveButton("yes") { _, _ ->
                recipeListingModel.migrateData()
            }
            setNegativeButton("no") { _, _ ->

            }
        }.show()
*/
////////////////////////////////////////////////////////////////////////////////////////////////////


    }

    @Composable
    override fun ScaffoldContent() {
        Text("RecipeListing")
    }
/*
    private fun configureOptionMenu() {
        optionMenu.apply {
            addMenuEntry(R.id.OPTION_MENU_REFRESH, resources.getString(R.string.refresh)) {
                refresh()
                true
            }
            addMenuEntry(R.id.OPTION_MENU_IMPRESSUM, resources.getString(R.string.impressum)) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
                true
            }
            addMenuEntry(R.id.OPTION_MENU_DATAPROTECTION, resources.getString(R.string.dataprotection)) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
                true
            }
        }
    }
*/
    fun onRecipeItemClick(recipeItem: View) {
        openRecipeItem(recipeItem.tag as String)
    }

    private fun refresh() {
        recipeListingModel.loadData()
    }

    private fun openRecipeItem(itemId: String?) {
        val intent = Intent(this, RecipeActivity::class.java)
        itemId?.let {
            intent.apply {
                putExtra(EXTRA_RECIPE_ID, it)
            }
        }
        startActivity(intent)
    }
}

class RecipeListingAdapter(recipeListData: LiveData<LinkedList<Recipe>>, owner: LifecycleOwner) :
    RecyclerView.Adapter<RecipeListingViewHolder>() {

    private var recipeList: LinkedList<Recipe>
    init {
        recipeList = recipeListData.value ?: LinkedList()
        recipeListData.observe(owner) {
            recipeList = it
            notifyDataSetChanged()
        }
    }

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