package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.util.Base64InputStream
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel
import java.io.InputStream

class PdfFragment : androidx.fragment.app.Fragment() {

    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeModel = activity?.run {
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
                .get(RecipeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf, container, false)
        val pdfView = view.findViewById<ImageView>(R.id.pdfView)

        recipeModel.recipeFileInputStream.observe(viewLifecycleOwner, { fileInputStream ->
            pdfView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream))
        })
/*
        recipeModel.recipeData.observe(viewLifecycleOwner, { recipe ->
            recipe?.uri?.let { pdfUri ->
                getBitmap(pdfUri)?.let {
                    pdfView.setImageBitmap(it)
                }
            } ?: pdfView.setImageBitmap(null)
        })

 */
/*
        pdfView.setOnClickListener {
            Log.i(this::class.java.name, "click")
            Log.i(this::class.java.name, "RecipeDate: ${recipeModel.recipeData.value}")
            Log.i(this::class.java.name, "PdfUri: ${recipeModel.recipeData.value?.uri}")
            recipeModel.recipeData.value?.uri?.let {
                Log.i(this::class.java.name, it.toString())
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(it, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }
        }

 */

        return view
    }

    private fun getBitmap(uri: Uri) : Bitmap? {
        var image: Bitmap? = null

        activity?.contentResolver?.let { contentResolver ->
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            parcelFileDescriptor?.let { fileDescriptor ->
                val renderer = PdfRenderer(fileDescriptor)
                val page = renderer.openPage(0)
                if (page.width > 0 && page.height > 0) {
                    val img = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(img, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    image = img
                }
                page.close()
                renderer.close()
                fileDescriptor.close()
            }
        }

        return image
    }

    private fun generateBitmap(inputStream: InputStream) : Bitmap? {
        return null
    }

}