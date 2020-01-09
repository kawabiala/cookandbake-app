package com.pingwinek.jens.cookandbake.activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.Utils.quantityToString
import com.pingwinek.jens.cookandbake.activities.IngredientListingFragment.OnListFragmentInteractionListener
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel
import kotlinx.android.synthetic.main.fragment_ingredient_listing.view.*
import kotlinx.android.synthetic.main.recyclerview_ingredient_list_item.view.*
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [IngredientListingFragment.OnListFragmentInteractionListener] interface.
 */
class IngredientListingFragment : androidx.fragment.app.Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var recipeModel: RecipeViewModel

    private var ingredientList = LinkedList<Ingredient>()

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
        val view = inflater.inflate(R.layout.fragment_ingredient_listing, container, false)

        val ingredientListingAdapter = IngredientListingAdapter(ingredientList, listener)

        view.list.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = ingredientListingAdapter
        }

        recipeModel.ingredientListData.observe(this, Observer { ingredients ->
            ingredients?.let { newIngredientList ->
                ingredientList.clear()
                ingredientList.addAll(newIngredientList)
                ingredientListingAdapter.notifyDataSetChanged()
            }
        })

        view.addIngredientButton.setOnClickListener {
            listener?.onListFragmentInteraction(null)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {

        fun onListFragmentInteraction(ingredient: Ingredient?)
    }
}

/**
 * [RecyclerView.Adapter] that can display a [Ingredient] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class IngredientListingAdapter(
    private val ingredientList: List<Ingredient>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<IngredientListingAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val ingredient = v.tag as Ingredient?
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            listener?.onListFragmentInteraction(ingredient)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_ingredient_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredientList[position]
        holder.quantityView.text = quantityToString(ingredient.quantity)
        holder.unityView.text = ingredient.unity
        holder.nameView.text = ingredient.name
        holder.buttonView.tag = ingredient.id

        with(holder.mView) {
            tag = ingredient
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = ingredientList.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val quantityView: TextView = mView.quantityView
        val unityView: TextView = mView.unityView
        val nameView: TextView = mView.nameView
        val buttonView = mView.deleteIngredientButton

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}