package com.pingwinek.jens.cookandbake.activities


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.pingwinek.jens.cookandbake.activities.IngredientListingFragment.OnListFragmentInteractionListener
import com.pingwinek.jens.cookandbake.databinding.FragmentIngredientListingBinding
import com.pingwinek.jens.cookandbake.databinding.RecyclerviewIngredientListItemBinding
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.utils.Utils.quantityToString
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel
import java.util.LinkedList

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [IngredientListingFragment.OnListFragmentInteractionListener] interface.
 */
class IngredientListingFragment : androidx.fragment.app.Fragment() {

    private var _binding: FragmentIngredientListingBinding? = null
    private val binding get() = _binding!!

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeModel = activity?.run {
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[RecipeViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientListingBinding.inflate(inflater, container, false)
        val view = binding.root

        val ingredientListingAdapter = IngredientListingAdapter(recipeModel.ingredientListData, listener, viewLifecycleOwner)

        binding.list.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = ingredientListingAdapter
        }

        binding.addIngredientButton.setOnClickListener {
            listener?.onListFragmentSaveIngredient(null)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        fun onListFragmentSaveIngredient(ingredient: Ingredient?)

        fun onListFragmentDeleteIngredient(ingredient: Ingredient)
    }
}

/**
 * [RecyclerView.Adapter] that can display a [Ingredient] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
@SuppressLint("NotifyDataSetChanged")
class IngredientListingAdapter(
    ingredientListData: LiveData<LinkedList<Ingredient>>,
    private val listener: OnListFragmentInteractionListener?,
    owner: LifecycleOwner
) : RecyclerView.Adapter<IngredientListingAdapter.ViewHolder>() {

    private var _binding: RecyclerviewIngredientListItemBinding? = null
    private val binding get() = _binding!!

    private var ingredientList: LinkedList<Ingredient>

    init {
        ingredientList = ingredientListData.value ?: LinkedList()
        ingredientListData.observe(owner) {
            ingredientList = it
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = RecyclerviewIngredientListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredientList[position]
        holder.quantityView.text = if (ingredient.quantityVerbal?.trim().isNullOrEmpty()) {
            quantityToString(ingredient.quantity)
        } else {
            ingredient.quantityVerbal
        }
        holder.unityView.text = ingredient.unity
        holder.nameView.text = ingredient.name
        holder.buttonView.setOnClickListener {
            listener?.onListFragmentDeleteIngredient(ingredient)
        }
        holder.mView.setOnClickListener{
                listener?.onListFragmentSaveIngredient(ingredient)
        }
    }

    override fun getItemCount(): Int = ingredientList.size

    inner class ViewHolder(mBinding: RecyclerviewIngredientListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        val mView = mBinding.root
        val quantityView: TextView = mBinding.quantityView
        val unityView: TextView = mBinding.unityView
        val nameView: TextView = mBinding.nameView
        val buttonView: ImageButton = mBinding.deleteIngredientButton

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}