package com.pingwinek.jens.cookandbake.repos

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.graphics.scale
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.SingletonHolder
import com.pingwinek.jens.cookandbake.lib.TypedQueue
import com.pingwinek.jens.cookandbake.lib.UriUtils
import com.pingwinek.jens.cookandbake.models.FileInfo
import com.pingwinek.jens.cookandbake.models.ImageInfo
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.sources.FileSourceFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.LinkedList
import java.util.Random

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    enum class RecipeExceptionMessage {
        ATTACHMENT_DELETE_FAILED,
        ATTACHMENT_DOWNLOAD_FAILED,
        ATTACHMENT_UPLOAD_FAILED,
        ATTACHMENT_WITHOUT_NAME_OR_SIZE,
        ATTACHMENT_WITHOUT_TYPE_INFORMATION,
        ATTACHMENT_WITH_UNSUPPORTED_SIZE,
        IMAGE_GALLERY_DOWNLOAD_URIS_FAILED,
        RECIPE_LIST_LOAD_FAILED,
        RECIPE_UPDATE_FAILED
    }

    private val recipeSourceFB = application.getServiceLocator().getService(RecipeSourceFB::class.java)
    private val uriUtils = application.getServiceLocator().getService(UriUtils::class.java)
    private val queue = TypedQueue<RecipeExceptionMessage>(10)

    suspend fun addImage(recipe: Recipe, uri: Uri) : ImageInfo? {
        var imageName = uriUtils.getNameForUri(uri)
        if (imageName.isNullOrEmpty()) imageName = ""

        val imageId = "${getRandomString(SIZE_OF_RANDOM_STRING)}.${FileFormat.SUFFIX}"

        var bitmap: Bitmap? = uriUtils.toBitmap(uri)

        if (bitmap == null) {
            Log.e(this::class.java.name, "bitmap is null")
            return null
        }

        bitmap = scaleBitmap(bitmap)

        return uploadImage(getImageGalleryFilePATH(recipe.id, imageId), imageName, bitmap)
    }

    suspend fun delete(recipe: Recipe) {
        recipeSourceFB.delete(recipe as RecipeFB)
    }

    suspend fun deleteAttachment(recipe: Recipe): Recipe {
        var returnRecipe = recipe

        try {
            returnRecipe = updateHasAttachment(recipe, false)
            deleteAttachment(getAttachmentDirPath(recipe.id))
        } catch (exception: Exception) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_DELETE_FAILED)
            Log.e(this::class.java.name, "Error when deleting attachment: $exception")
        }

        return returnRecipe
    }

    suspend fun deleteImage(recipe: Recipe, imageId: String) {
        FileSourceFB.deleteFile(getImageGalleryFilePATH(recipe.id, imageId))
    }

    suspend fun get(id: String): Recipe {
        return recipeSourceFB.get(id)
    }

    suspend fun getAll(): LinkedList<Recipe> {
        return try{
            LinkedList<Recipe>(recipeSourceFB.getAll())
        } catch (exception: Exception) {
            queue.addItem(RecipeExceptionMessage.RECIPE_LIST_LOAD_FAILED)
            Log.e(this::class.java.name, "Error when retrieving recipe list: $exception")
            LinkedList<Recipe>()
        }
    }

    suspend fun getAllImageGallery(recipe: Recipe): List<ImageInfo> {
        return try {
            FileSourceFB.listAllImages(getImageGalleryDirPath(recipe.id))
        } catch (exception: Exception) {
            queue.addItem(RecipeExceptionMessage.IMAGE_GALLERY_DOWNLOAD_URIS_FAILED)
            Log.e(this::class.java.name, "Error when retrieving image gallery uris: $exception")
            listOf<ImageInfo>()
        }
    }

    suspend fun getAttachment(recipe: Recipe): FileInfo? {
        val cacheDir = application.applicationContext.cacheDir
        return try {
            val filePathList = FileSourceFB.listAll(getAttachmentDirPath(recipe.id))
            if (filePathList.isNotEmpty()) {
                FileSourceFB.getFile(cacheDir, filePathList[0])
            } else {
                null
            }
        } catch (exception: Exception) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_DOWNLOAD_FAILED)
            Log.e(this::class.java.name, "Error when retrieving attachment: $exception")
            null
        }
    }

    fun getRecipeExceptionMessageQueue(): TypedQueue<RecipeExceptionMessage> {
        return queue
    }

    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?,
        tags: List<String>
    ) : Recipe {
        return recipeSourceFB.new(RecipeFB(title, description, instruction, tags))
    }

    fun registerQueueListener(listener: TypedQueue.QueueListener) {
        queue.registerListener(listener)
    }

    suspend fun saveAttachment(recipe: Recipe, uri: Uri): Recipe {
        var returnRecipe = recipe

        val name = uriUtils.getNameForUri(uri)
        val size = uriUtils.getSizeForUri(uri)
        val type = uriUtils.getTypeForUri(uri)

        if (name.isNullOrEmpty()) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeExceptionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE}")
            return returnRecipe
        }
        if (size == null || size == 0.toLong()) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeExceptionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE}")
            return returnRecipe
        }
        if (size > MAX_ATTACHMENT_SIZE) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_WITH_UNSUPPORTED_SIZE)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeExceptionMessage.ATTACHMENT_WITH_UNSUPPORTED_SIZE}")
            return returnRecipe
        }
        if (type.isNullOrEmpty()) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_WITHOUT_TYPE_INFORMATION)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeExceptionMessage.ATTACHMENT_WITHOUT_TYPE_INFORMATION}")
            return returnRecipe
        }

        var suffix = File(name).extension
        if (suffix.isEmpty()) suffix = "tmp"

        // try to delete
        try {
            deleteAttachment(getAttachmentDirPath(recipe.id))
            returnRecipe = updateHasAttachment(recipe, false)
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when deleting document ${getAttachmentDirPath(recipe.id)}: $exception")
        }

        // try to upload
        try {
            if (FileSourceFB.uploadFile(getAttachmentFilePath(recipe.id, suffix), uri)) {
                returnRecipe = updateHasAttachment(recipe, true)
            }
        } catch (exception: Exception) {
            queue.addItem(RecipeExceptionMessage.ATTACHMENT_UPLOAD_FAILED)
            Log.e(this::class.java.name, "Error when attaching document: $exception")
        }

        return returnRecipe
    }

    fun unregisterQueueListener(listener: TypedQueue.QueueListener) {
        queue.unregisterListener(listener)
    }

    private suspend fun updateHasAttachment(recipe: Recipe, hasAttachment: Boolean): Recipe {
        return recipeSourceFB.update(
            RecipeFB(
                recipe.id,
                recipe.title,
                recipe.description,
                recipe.instruction,
                recipe.tags,
                hasAttachment)
        )
    }

    suspend fun updateImageName(recipe: Recipe, imageName: String): ImageInfo? {
        return FileSourceFB.updateImageName(getImageGalleryDirPath(recipe.id), imageName)
    }

    suspend fun updateRecipe(
        recipe: Recipe,
        title: String,
        description: String?,
        instruction: String?,
        tags: List<String>
    ): Recipe {
        return try {
            recipeSourceFB.update(RecipeFB(recipe.id, title, description, instruction, tags, recipe.hasAttachment))
        } catch (exception: Exception) {
            queue.addItem(RecipeExceptionMessage.RECIPE_UPDATE_FAILED)
            Log.e(this::class.java.name, "Updating recipe failed: $exception")
            recipe
        }
    }

    private suspend fun deleteAttachment(dirPath: String) {
            val filePathList = FileSourceFB.listAll(dirPath)
            filePathList.forEach { filePath ->
                FileSourceFB.deleteFile(filePath)
            }
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository) {

        private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
        private const val SIZE_OF_RANDOM_STRING = 20
        const val MAX_PIXELS_LONGEST_SIZE = 1000
        const val MAX_ATTACHMENT_SIZE: Long = 1024*1024*4

        private object FileFormat {
            val compressFormat = Bitmap.CompressFormat.PNG
            const val SUFFIX = "png"
        }

        private const val RECIPE_FILE_PATH = "recipe"
        private const val ATTACHMENT_FILE_PATH = "attachment"
        private const val ATTACHMENT_FILE_NAME = "attachment"
        private const val IMAGE_GALLERY_DIR_PATH = "imageGallery"

        private fun getAttachmentDirPath(id: String): String {
            return "$RECIPE_FILE_PATH/$id/$ATTACHMENT_FILE_PATH"
        }

        private fun getAttachmentFilePath(id: String, suffix: String): String {
            return "${getAttachmentDirPath(id)}/$ATTACHMENT_FILE_NAME.$suffix"
        }

        private fun getImageGalleryDirPath(id: String): String {
            return "$RECIPE_FILE_PATH/$id/$IMAGE_GALLERY_DIR_PATH"
        }

        private fun getImageGalleryFilePATH(id: String, imageId: String) : String {
            return "${getImageGalleryDirPath(id)}/$imageId"
        }

        private fun getRandomString(sizeOfRandomString: Int): String {
            val random = Random()
            val sb = StringBuilder(sizeOfRandomString)
            for (i in 0 until sizeOfRandomString)
                sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
            return sb.toString()
        }

        private fun scaleBitmap(bitmap: Bitmap): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val longestSide = if(width > height) width else height

            val compressFactor = if(longestSide > MAX_PIXELS_LONGEST_SIZE) longestSide / MAX_PIXELS_LONGEST_SIZE else 1
            Log.i(this::class.java.name, "scaleBitmap: compressFactor $compressFactor")

            return bitmap.scale(width / compressFactor, height / compressFactor)
        }

        private suspend fun uploadImage(pathString: String, imageName: String, bitmap: Bitmap): ImageInfo? {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(FileFormat.compressFormat, 100, outputStream)
            val inputStream = ByteArrayInputStream(outputStream.toByteArray())

            val returnValue = FileSourceFB.uploadInputStream(
                pathString,
                inputStream,
                imageName)

            inputStream.close()
            outputStream.close()
            bitmap.recycle()

            return returnValue
        }

    }

}