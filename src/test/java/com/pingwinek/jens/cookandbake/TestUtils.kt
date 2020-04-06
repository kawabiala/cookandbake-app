package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

class TestUtils {

    companion object {
        fun <T : Any> mockSingletonHolderInstance(singleton: KClass<T>, mock: T) {
            val clsSingletonHolder = singleton.companionObject?.superclasses?.find {
                it.qualifiedName == SingletonHolder::class.qualifiedName
            } ?: return

            val instanceField = clsSingletonHolder.memberProperties.find {
                it.name == "instance"
            } ?: return

            instanceField.isAccessible = true
            if (instanceField.getter.call(singleton.companionObjectInstance) == null) {
                if (instanceField is KMutableProperty<*>) {
                    instanceField.setter.call(singleton.companionObjectInstance, mock)
                }
            }
        }
    }
}