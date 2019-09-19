package com.pingwinek.jens.cookandbake

const val LOCAL_DOMAIN = "http://10.0.2.2/strato/pingwinek"
const val DOMAIN = "https://www.pingwinek.de"
//const val DOMAIN = LOCAL_DOMAIN
const val BASEURL = "$DOMAIN/cookandbake"

const val REGISTERPATH = "$BASEURL/auth/register"
const val LOGINPATH = "$BASEURL/auth/login"
const val REFRESHPATH = "$BASEURL/auth/refresh"
const val RECIPEPATH = "$BASEURL/api/recipe/"
const val INGREDIENTPATH = "$BASEURL/api/ingredient/"

const val LOGIN_EVENT = "login"
const val LOGOUT_EVENT = "logout"

const val EXTRA_RECIPE_TITLE = "extraRecipeTitle"
const val EXTRA_RECIPE_DESCRIPTION = "extraRecipeDescription"
const val EXTRA_RECIPE_INSTRUCTION = "extraRecipeInstruction"

const val EXTRA_INGREDIENT_ID = "extraIngredientID"
const val EXTRA_INGREDIENT_NAME = "extraIngredientName"
const val EXTRA_INGREDIENT_QUANTITY = "extraIngredientQuantity"
const val EXTRA_INGREDIENT_UNITY = "extraIngredientUnity"

const val REQUEST_CODE_TITLE = 1
const val REQUEST_CODE_INSTRUCTION = 2
const val REQUEST_CODE_INGREDIENT = 3

const val SHARED_PREFERENCES_FILE = "preferences"
