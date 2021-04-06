package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

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

        recipeModel.fileListData.observe(viewLifecycleOwner) { fileList ->
            if (!fileList.isEmpty()) {
                recipeModel.loadFile(fileList[0].fileName)
            }
        }

        recipeModel.file.observe(viewLifecycleOwner) { parcelFileDescriptor ->
            pdfView.setImageBitmap(generateBitmap(parcelFileDescriptor))
        }
/*
        pdfView.setOnClickListener {
            Log.i(this::class.java.name, "click")
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

    private fun generateBitmap(pfd: ParcelFileDescriptor) : Bitmap? {
        var image: Bitmap? = null

        val renderer = PdfRenderer(pfd)
        val page = renderer.openPage(0)
        if (page.width > 0 && page.height > 0) {
            val img = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(img, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            image = img
        }
        page.close()
        renderer.close()
        pfd.close()

        return image
    }
}