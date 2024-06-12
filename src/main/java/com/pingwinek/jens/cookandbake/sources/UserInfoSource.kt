package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.UserInfo

/**
 * Source for retrieving and manipulating recipes
 *
 * @param T a subtype of [Recipe]
 */
interface UserInfoSource<T: UserInfo> : Source<T>