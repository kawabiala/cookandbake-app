package com.pingwinek.jens.cookandbake

const val COOKIE_NAME = "cookandbake"

const val EXTRA_RECIPE_TITLE = "com.pingwinek.jens.cookandbake.extraRecipeTitle"
const val EXTRA_RECIPE_DESCRIPTION = "com.pingwinek.jens.cookandbake.extraRecipeDescription"
const val EXTRA_RECIPE_INSTRUCTION = "com.pingwinek.jens.cookandbake.extraRecipeInstruction"

const val EXTRA_INGREDIENT_ID = "com.pingwinek.jens.cookandbake.extraIngredientID"
const val EXTRA_INGREDIENT_NAME = "com.pingwinek.jens.cookandbake.extraIngredientName"
const val EXTRA_INGREDIENT_QUANTITY = "com.pingwinek.jens.cookandbake.extraIngredientQuantity"
const val EXTRA_INGREDIENT_QUANTITY_VERBAL = "com.pingwinek.jens.cookandbake.extraIngredientQuantityVerbal"
const val EXTRA_INGREDIENT_UNITY = "com.pingwinek.jens.cookandbake.extraIngredientUnity"

const val ACTION_REGISTER_CONFIRMATION_SENT = "com.pingwinek.jens.cookandbake.registerConfirmationSent"
const val ACTION_LOGIN_CONFIRMATION_SENT = "com.pingwinek.jens.cookandbake.registerLoginSent"

/*
 We can't move this into resources.id, because it is used in place
 where only first 16 bit of int is allowed: startActivityForResult in FragmentActivity
 uses only 16 bit and fails, when providing larger int; resources.id can generate
 larger int.
 For some reason, resources integer doesn't work either.
 */
const val REQUEST_CODE_TITLE = 1
const val REQUEST_CODE_INSTRUCTION = 2
const val REQUEST_CODE_INGREDIENT = 3
const val REQUEST_CODE_PDF = 4

const val SHARED_PREFERENCES_FILE = "preferences"

/**
 * 60000 milliseconds = 60 seconds = 1 minute
 * this could go into a config file
 */
const val MIN_UPDATE_INTERVAL = 60000
