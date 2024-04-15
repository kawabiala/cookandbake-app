package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import java.util.LinkedList

class RecipeListingActivity : BaseActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var recipeListData: LiveData<LinkedList<Recipe>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        recipeListingModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeListingViewModel::class.java)

        recipeListData = recipeListingModel.recipeListData

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
        configureTopBar(
            title = "Recipes",
            optionItemMid = PingwinekCooksComposables.OptionItem(
                "person",
                Icons.Filled.Person
            ) { Log.e(this::class.java.name, "Person clicked") }
        )


        configureDropDown(
            PingwinekCooksComposables.OptionItem(
                getString(R.string.dataprotection),
                Icons.Filled.Lock
            ) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("title", getString(R.string.dataprotection))
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
            },
            PingwinekCooksComposables.OptionItem(
                getString(R.string.impressum),
                Icons.Filled.Info
            ) {
                startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                    .putExtra("title", getString(R.string.impressum))
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
            }
        )

        configureNavigationBar(
            selectedItem = Navigation.RECIPE,
            enabled = true,
            onLoginClickAction = {
                startActivity(Intent(this, SignInActivity::class.java))
            }
        )

        configureFloatingActionButton(
            icon = Icons.Filled.Add,
            label = "Add",
            onClick = { openRecipeItem(null) }
        )
    }

    override fun onResume() {
        super.onResume()
        recipeListingModel.loadData()
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
    override fun ScaffoldContent(paddingValues: PaddingValues) {
        val recipes = recipeListData.observeAsState()
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            recipes.value?.forEachIndexed { index, recipe ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
                key(recipe.id) {
                    Recipe(recipe = recipe) { recipeId ->
                        openRecipeItem(recipeId)
                    }
                }
            }
        }
    }

    @Composable
    private fun Recipe(
        recipe: Recipe,
        onClick: (String) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(recipe.id) }
        ) {
            Text(recipe.title)
            Text(recipe.description ?: "")
        }
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