package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pingwinek.jens.cookandbake.Ingredient
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.RecipeViewModel
import com.pingwinek.jens.cookandbake.activities.IngredientListingFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.activity_recipe.view.*

import kotlinx.android.synthetic.main.fragment_ingredient_listing.view.*
import kotlinx.android.synthetic.main.recyclerview_ingredient_list_item.view.*
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [IngredientListingFragment.OnListFragmentInteractionListener] interface.
 */
class IngredientListingFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var recipeModel: RecipeViewModel;

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
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientListingAdapter
        }

        recipeModel.ingredientListData.observe(this, Observer { ingredients ->
            ingredients?.let { nil ->
                Log.i(this::class.java.name, "Change in ingredientlist observed")
                ingredientList.clear()
                ingredientList.addAll(nil)
                ingredientListingAdapter.notifyDataSetChanged()
            }
        })

        view.addIngredientButton.setOnClickListener { v ->
            listener?.let { l ->
                l.onListFragmentInteraction(null)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
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

        fun onListFragmentInteraction(id: Int?)
    }
}

/**
 * [RecyclerView.Adapter] that can display a [Ingredient] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class IngredientListingAdapter(
    private val mValues: List<Ingredient>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<IngredientListingAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val id = v.tag as Int?
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            listener?.onListFragmentInteraction(id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_ingredient_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = mValues[position]
        holder.quantityView.text = ingredient.quantity.toString()
        holder.unityView.text = ingredient.unity
        holder.nameView.text = ingredient.name
        holder.buttonView.tag = ingredient.id

        with(holder.mView) {
            tag = ingredient.id
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

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