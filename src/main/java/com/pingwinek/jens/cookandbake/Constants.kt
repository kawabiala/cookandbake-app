package com.pingwinek.jens.cookandbake

const val LOCAL_DOMAIN = "http://10.0.2.2/strato/pingwinek"
const val REMOTE_DOMAIN = "https://www.pingwinek.de"
//const val DOMAIN = REMOTE_DOMAIN
const val DOMAIN = LOCAL_DOMAIN
const val BASEURL = "$DOMAIN/cookandbake"

const val DATAPROTECTIONPATH = "$BASEURL/datenschutz"
const val IMPRESSUMPATH = "$BASEURL/impressum"
const val REGISTERPATH = "$BASEURL/auth/register"
const val LOGINPATH = "$BASEURL/auth/login"
const val REFRESHPATH = "$BASEURL/auth/refresh"
const val LOSTPASSWORDPATH = "$BASEURL/auth/lost_password"
const val NEWPASSWORDPATH = "$BASEURL/auth/new_password"
const val CHANGEPASSWORDPATH = "$BASEURL/auth/change_password"
const val CONFIRMREGISTRATIONPATH = "$BASEURL/auth/confirm_registration"
const val LOGOUTPATH = "$BASEURL/auth/logout"
const val UNSUBSCRIBEPATH = "$BASEURL/auth/unsubscribe"

const val RECIPEPATH = "$BASEURL/api/recipe/"
const val INGREDIENTPATH = "$BASEURL/api/ingredient/"

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

const val REQUEST_CODE_TITLE = 1
const val REQUEST_CODE_INSTRUCTION = 2
const val REQUEST_CODE_INGREDIENT = 3

const val OPTION_MENU_REGISTER = 1
const val OPTION_MENU_LOGIN = 2
const val OPTION_MENU_LOGOUT = 3
const val OPTION_MENU_CLOSE = 6
const val OPTION_MENU_RECIPES = 4
const val OPTION_MENU_LOST_PASSWORD = 5
const val OPTION_MENU_DONE = 7
const val OPTION_MENU_IMPRESSUM = 8
const val OPTION_MENU_DATAPROTECTION = 9
const val OPTION_MENU_MANAGE_ACCOUNT = 10
const val OPTION_MENU_DELETE = 11
const val OPTION_MENU_SHARE = 12
const val OPTION_MENU_REFRESH = 13

const val SHARED_PREFERENCES_FILE = "preferences"

/**
 * 60000 milliseconds = 60 seconds = 1 minute
 * this could go into a config file
 */
const val MIN_UPDATE_INTERVAL = 60000
