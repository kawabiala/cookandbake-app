package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex

/**
 * List, that provides drag and drop functionality of list items.
 *
 * @param spacing: the spacing between list items
 * @param listContent: expects a sorted list of items
 * @param key: function defining how to derive a key from an item
 * @param activeItem: the active item or null, if no item is active
 * @param onChangeActiveItem: use this function to indicate a change of the active item; provide null for deactivation of any items
 * @param onChangeSort: returns the a map of items that have changed their sort order with the items as keys and the new sort order as values
 * @param itemComposable: the composable to be rendered per item; receives as parameters the item, the activation status, functions to indicate change of activation status, vertical change of drag position and stop of dragging
 */
@Composable
fun <T : Any> DragAndDropList(
    spacing: Dp = 0.dp,
    listContent: List<T>,
    key: (T) -> Any,
    activeItem: T?,
    onChangeActiveItem: (T?) -> Unit,
    onChangeSort: (Map<T, Int>) -> Unit,
    itemComposable: @Composable (t: T, active: Boolean, onChangeActive: () -> Unit, onDrag: (Float) -> Unit, onDragStopped: () -> Unit) -> Unit
) {
    val density = LocalDensity.current

    val mapContent: MutableMap<T, Y> by remember(listContent) {
        mutableStateOf( //the map needs to mutable, but is not expected to trigger recomposition
            mutableMapOf<T, Y>().also { map ->
                listContent.forEach { t ->
                    map[t] = Y(null, 0f)
                }
            }
        )
    }

    var mapOffsetsY: Map<T, Float> by remember(listContent) {
        mutableStateOf(
            listContent.associateBy({it}, {0f})
        )
    }

    var offsetY: Float by remember(listContent) { mutableFloatStateOf(0f) }
    var temporaryRank: Int? by remember(listContent) { mutableStateOf(null) }

    fun getRank (positionsY: List<Float>, positionY: Float): Int {
        var rank = 0

        for (item in 1..<positionsY.size) {
            val posYLower = positionsY[item - 1]
            val posYHigher = positionsY[item]
            val midPosY = posYLower + (posYHigher - posYLower) / 2
            if (positionY >= midPosY) {
                rank = item
            }
        }

        return rank
    }

    fun resetActive() {
        offsetY = 0f
        temporaryRank = null
    }

    fun updateOffsets(oldRank: Int, newRank: Int) {
        val height = mapContent[activeItem]?.height

        if (height == null) Log.e("DragAndDropList", "no height defined for active item")

        height?.let { h ->
            val heightPlusSpacing = density.run { spacing.toPx() } + h
            val updatedMap = mutableMapOf<T, Float>()

            mapOffsetsY.keys.forEachIndexed { index, t ->
                if (t == activeItem) {
                    updatedMap[t] = offsetY
                } else if (index in (oldRank + 1)..newRank) {
                    updatedMap[t] = heightPlusSpacing * -1
                } else if (index in newRank..<oldRank) {
                    updatedMap[t] = heightPlusSpacing
                } else {
                    updatedMap[t] = 0f
                }
            }

            mapOffsetsY = updatedMap
        }
    }

    val onChangeActive = fun(activeItem: T?) {
        if (activeItem != null) resetActive()
        onChangeActiveItem(activeItem)
    }

    val onDrag = fun (deltaOffsetY: Float) {
        activeItem?.let { ai ->
            offsetY += deltaOffsetY

            val basePosY = mapContent[ai]?.positionY

            basePosY?.let { bpY ->

                val newRank = getRank(
                    mapContent.values.mapNotNull { y -> y.positionY }.toList(), bpY + offsetY
                )

                if (temporaryRank != newRank) {
                    val oldRank = listContent.indexOfFirst { t -> t == ai }

                    updateOffsets(oldRank, newRank)

                    temporaryRank = newRank
                }
            }
        }
    }

    val onDragStopped = fun() {
        temporaryRank?.let { tr ->
            val oldRank = listContent.indexOf(activeItem)

            var preliminaryOffsetY = 0f
            val spacingFloat = density.run { spacing.toPx() }

            val mapResorted = mutableMapOf<T, Int>().also { map ->
                for (i in listContent.indices) {
                    if (i == oldRank) {
                        map[listContent[i]] = tr
                    } else if (i in (oldRank+1)..tr ) {
                        map[listContent[i]] = i-1
                        preliminaryOffsetY += ((mapContent[listContent[i]]?.height ?: 0f) + spacingFloat)
                    } else if (i in tr..<oldRank) {
                        map[listContent[i]] = i+1
                        preliminaryOffsetY -= ((mapContent[listContent[i]]?.height ?: 0f) + spacingFloat)
                    }
                }
            }

            onChangeSort(mapResorted)
            offsetY = preliminaryOffsetY
            onChangeActiveItem(null)
        }
    }

    val reportPosition: (T, Float, Float) -> Unit = { t, positionY, height ->
        mapContent[t] = Y(positionY, height)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {

        items(
            items = listContent,
            key = key
        ) { t ->

            val zIndex = if (t == activeItem) 1f else 0f
            val shadow = density.run { (if (t == activeItem) 10f else 0f).toDp() }
            val localOffsetY = if (t == activeItem) offsetY else (mapOffsetsY[t] ?: 0f)

            val changeActive = fun() {
                onChangeActive(if (t == activeItem) null else t)
            }

            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        reportPosition(t, coordinates.positionInRoot().y, coordinates.boundsInRoot().height)
                    }
                    .offset { Offset(0f, localOffsetY).round() }
                    .zIndex(zIndex)
                    .shadow(shadow)
            ) {
                itemComposable(t, t == activeItem, changeActive, onDrag, onDragStopped)
            }
        }
    }
}

data class Y(
    val positionY: Float?,
    val height: Float
)