package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
 * An item of the list is draggable, if it is active. To provide the vertical offset of the dragging and the stop, use the
 * draggable modifier. It can be attached to any element inside the item. It will result in dragging the whole item.
 * Letting an item drop, results in resorting of the list and deactivates the item.
 *
 * @param spacing: the spacing between list items
 * @param listContent: expects a list of items
 * @param key: function defining how to derive a key from an item
 * @param sort: function providing the sorting position as Integer
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
    sort: (T) -> Int,
    activeItem: T?,
    onChangeActiveItem: (T?) -> Unit,
    onChangeSort: (Map<T, Int>) -> Unit,
    itemComposable: @Composable (t: T, active: Boolean, onChangeActive: () -> Unit, onDrag: (Float) -> Unit, onDragStopped: () -> Unit) -> Unit
) {
    val density = LocalDensity.current

    val lazyListState = rememberLazyListState()

    data class SortItem(
        val sort: Int,
        var y: Float? = null,
        var height: Float? = null,
        val offsetY: Float = 0f
    )

    val mappedContent: MutableMap<T, SortItem> = mutableMapOf<T, SortItem>().also { map ->
        listContent.sortedBy { t -> sort(t) }.forEachIndexed { index, t ->
            map.put(t, SortItem(index))
        }
    }

    var mapOffsetsY: Map<T, Float> by remember(listContent) {
        mutableStateOf(
            listContent.associateBy({it}, {0f})
        )
    }

    var offsetY: Float by remember(listContent) { mutableFloatStateOf(0f) }
    var temporaryRank: Int? by remember(listContent) { mutableStateOf(null) }

    fun getSort (positionY: Float): Int {
            var rank = 0
        val sorted = mappedContent.entries.sortedBy { entry ->
            entry.value.sort
        }.mapNotNull { entry ->
            entry.value.y?.let { _ -> entry }
        }

        for (item in 1..< sorted.size) {
            val posYLower = sorted[item - 1].value.y!!
            val posYHigher = sorted[item].value.y!!
            val midPosY = posYLower + (posYHigher - posYLower) / 2
            if (positionY >= midPosY) {
                rank = item
            }
        }

        return sorted[rank].value.sort
    }

    fun resetActive() {
        offsetY = 0f
        temporaryRank = null
    }

    fun updateOffsets(oldRank: Int, newRank: Int) {
        val height = mappedContent[activeItem]?.height

        if (height == null) Log.e("DragAndDropList", "no height defined for active item")

        height?.let { h ->
            val heightPlusSpacing = density.run { spacing.toPx() } + h

            mapOffsetsY = mappedContent.map { entry ->
                if (entry.key == activeItem) {
                    Pair(entry.key, offsetY)
                } else if (entry.value.sort in (oldRank + 1)..newRank) {
                    Pair(entry.key, heightPlusSpacing * -1)
                } else if (entry.value.sort in newRank..<oldRank) {
                    Pair(entry.key, heightPlusSpacing)
                } else {
                    Pair(entry.key, 0f)
                }
            }.toMap()
        }
    }

    val onChangeActive = fun(activeItem: T?) {
        if (activeItem != null) resetActive()
        onChangeActiveItem(activeItem)
    }

    val onDrag = fun (deltaOffsetY: Float) {
        activeItem?.let {
            mappedContent[it]?.let { ai ->

                offsetY += deltaOffsetY

                val basePosY = ai.y

                basePosY?.let { bpY ->
                    val newRank = getSort(bpY + offsetY)

                    if (temporaryRank != newRank) {
                        val oldRank = ai.sort

                        updateOffsets(oldRank, newRank)
                        temporaryRank = newRank
                    }
                }
            }
        }
    }

    val onDragStopped = fun() {
        temporaryRank?.let { tr ->
            val oldRank = activeItem?.let { mappedContent[it]?.sort } ?: -1

            var preliminaryOffsetY = 0f
            val spacingFloat = density.run { spacing.toPx() }

            val mapResorted = mappedContent.mapNotNull { entry ->
                val oldSort = entry.value.sort

                if (oldSort == oldRank) {
                    Pair(entry.key,tr)
                } else if (oldSort in (oldRank+1)..tr ) {
                    preliminaryOffsetY += ((entry.value.height ?: 0f) + spacingFloat)
                    Pair (entry.key,oldSort-1)
                } else if (oldSort in tr..<oldRank) {
                    preliminaryOffsetY -= ((entry.value.height ?: 0f) + spacingFloat)
                    Pair(entry.key,oldSort+1)
                } else {
                    Pair(entry.key, oldSort)
                }
            }.toMap()

            onChangeSort(mapResorted)
            offsetY = preliminaryOffsetY
            onChangeActiveItem(null)
        }
    }

    val reportPosition: (T, Float, Float) -> Unit = { t, positionY, height ->
        mappedContent[t]?.y = positionY
        mappedContent[t]?.height = height
    }

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {

        items(
            items = mappedContent.entries.sortedBy { entry -> entry.value.sort }
        ) {
            entry ->

            val zIndex = if (entry.key == activeItem) 1f else 0f
            val shadow = density.run { (if (entry.key == activeItem) 10f else 0f).toDp() }
            val localOffsetY = if (entry.key == activeItem) offsetY else (mapOffsetsY[entry.key] ?: 0f)

            val changeActive = fun() {
                onChangeActive(if (entry == activeItem) null else entry.key)
            }

            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        reportPosition(entry.key, coordinates.positionInRoot().y, coordinates.boundsInRoot().height)
                    }
                    .offset { Offset(0f, localOffsetY).round() }
                    .zIndex(zIndex)
                    .shadow(shadow)
            ) {
                itemComposable(entry.key, entry.key == activeItem, changeActive, onDrag, onDragStopped)
            }

        }

    }
}