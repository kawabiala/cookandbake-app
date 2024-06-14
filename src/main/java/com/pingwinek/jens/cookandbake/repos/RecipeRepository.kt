package com.pingwinek.jens.cookandbake.repos

import android.net.Uri
import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.SingletonHolder
import com.pingwinek.jens.cookandbake.lib.TypedQueue
import com.pingwinek.jens.cookandbake.lib.UriUtils
import com.pingwinek.jens.cookandbake.models.FileInfo
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.sources.FileSourceFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import java.io.File
import java.util.LinkedList

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    enum class RecipeActionMessage {
        ATTACHMENT_WITHOUT_NAME_OR_SIZE,
        ATTACHMENT_WITH_UNSUPPORTED_SIZE,
        ATTACHMENT_WITHOUT_TYPE_INFORMATION,
        ATTACHMENT_UPLOAD_FAILED,
        ATTACHMENT_DOWNLOAD_FAILED
    }

    private val recipeSourceFB = application.getServiceLocator().getService(RecipeSourceFB::class.java)
    private val uriUtils = application.getServiceLocator().getService(UriUtils::class.java)
    private val queue = TypedQueue<RecipeActionMessage>()

    suspend fun delete(recipe: Recipe) {
        recipeSourceFB.delete(recipe as RecipeFB)
    }

    suspend fun deleteAttachment(recipe: Recipe): Recipe {
        var returnRecipe = recipe

        try {
            returnRecipe = updateHasAttachment(recipe, false)
            deleteAttachment(getAttachmentDirPath(recipe.id))
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when deleting attachment: $exception")
        }

        return returnRecipe
    }

    suspend fun get(id: String): Recipe {
        return recipeSourceFB.get(id)
    }

    suspend fun getAll(): LinkedList<Recipe> {
        return try{
            LinkedList<Recipe>(recipeSourceFB.getAll())
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when retrieving recipe list: $exception")
            LinkedList<Recipe>()
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
            queue.addItem(RecipeActionMessage.ATTACHMENT_DOWNLOAD_FAILED)
            Log.e(this::class.java.name, "Error when retrieving attachment: $exception")
            null
        }
    }

    fun getRecipeActionMessageQueue(): TypedQueue<RecipeActionMessage> {
        return queue
    }

    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?
    ) : Recipe {
        return recipeSourceFB.new(RecipeFB(title, description, instruction))
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
            queue.addItem(RecipeActionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeActionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE}")
            return returnRecipe
        }
        if (size == null || size == 0.toLong()) {
            queue.addItem(RecipeActionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeActionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE}")
            return returnRecipe
        }
        if (size > MAX_ATTACHMENT_SIZE) {
            queue.addItem(RecipeActionMessage.ATTACHMENT_WITH_UNSUPPORTED_SIZE)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeActionMessage.ATTACHMENT_WITH_UNSUPPORTED_SIZE}")
            return returnRecipe
        }
        if (type.isNullOrEmpty()) {
            queue.addItem(RecipeActionMessage.ATTACHMENT_WITHOUT_TYPE_INFORMATION)
            Log.e(this::class.java.name, "Error when saving attachment ${RecipeActionMessage.ATTACHMENT_WITHOUT_TYPE_INFORMATION}")
            return returnRecipe
        }

        var suffix = File(name).extension
        if (suffix.isEmpty()) suffix = "tmp"

        // try to delete
        try {
            if (deleteAttachment(getAttachmentDirPath(recipe.id))) {
                returnRecipe = updateHasAttachment(recipe, false)
            }
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when deleting document ${getAttachmentDirPath(recipe.id)}: $exception")
        }

        // try to upload
        try {
            if (FileSourceFB.uploadFile(getAttachmentFilePath(recipe.id, suffix), uri)) {
                returnRecipe = updateHasAttachment(recipe, true)
            }
        } catch (exception: Exception) {
            queue.addItem(RecipeActionMessage.ATTACHMENT_UPLOAD_FAILED)
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
                hasAttachment)
        )
    }

    suspend fun updateRecipe(
        recipe: Recipe,
        title: String,
        description: String?,
        instruction: String?
    ): Recipe {
        return recipeSourceFB.update(RecipeFB(recipe.id, title, description, instruction, recipe.hasAttachment))
    }

    private suspend fun deleteAttachment(dirPath: String): Boolean {
        return try {
            val filePathList = FileSourceFB.listAll(dirPath)
            filePathList.forEach { filePath ->
                FileSourceFB.deleteFile(filePath)
            }
            true
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "error when deleting document $dirPath: $exception")
            false
        }
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository) {

        const val MAX_ATTACHMENT_SIZE: Long = 1024*1024

        private const val RECIPE_FILE_PATH = "recipe"
        private const val ATTACHMENT_FILE_PATH = "attachment"
        private const val ATTACHMENT_FILE_NAME = "attachment"

        private fun getAttachmentDirPath(id: String): String {
            return "$RECIPE_FILE_PATH/$id/$ATTACHMENT_FILE_PATH"
        }

        private fun getAttachmentFilePath(id: String, suffix: String): String {
            return "${getAttachmentDirPath(id)}/$ATTACHMENT_FILE_NAME.$suffix"
        }
    }

}