package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.labelManagementActivity.ScaffoldContent
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.viewModels.LabelManagementViewModel

class LabelManagementActivity : AppCompatActivity() {

    enum class TagEditMode {
        SHOW, ADD, UPDATE
    }

    private lateinit var tagModel: LabelManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tagModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[LabelManagementViewModel::class.java]

        val tagListData = tagModel.availableTagListData

        val optionBack = PingwinekCooksComposableHelpers.OptionItem(
            R.string.back,
            Icons.AutoMirrored.Outlined.ArrowBack
        ) {
            finish()
        }

        val onAddLabel: (String, String, Int) -> Unit = { label, color, sort ->
            tagModel.addLabel(label, color, sort)
        }

        val onDeleteLabel: (Tag) -> Unit = { tag ->
            tagModel.deleteLabel(tag)
        }

        val onUpdateLabel: (Tag, String, String, Int) -> Unit = { tag, label, color, sort ->
            tagModel.updateLabel(tag, label, color, sort)
        }

        setContent {

            PingwinekCooksAppTheme {

                val tagsWithCount by tagListData.observeAsState()
                var tagEditMode by remember(tagsWithCount) { mutableStateOf(TagEditMode.SHOW) }

                val onChangeTagEditMode: (TagEditMode) -> Unit = { mode ->
                    tagEditMode = mode
                }

                val optionAdd = PingwinekCooksComposableHelpers.OptionItem(
                    R.string.add_label,
                    Icons.Outlined.Add
                ) {
                    tagEditMode = TagEditMode.ADD
                }

                PingwinekCooksScaffold(
                    title = getString(R.string.manage_labels),
                    optionItemLeft = optionBack,
                    optionItemRight = if (tagEditMode == TagEditMode.SHOW) optionAdd else null,
                    navigationBarVisible = false,
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues,
                        tagsWithCount,
                        tagEditMode,
                        onChangeTagEditMode,
                        onAddLabel,
                        onDeleteLabel,
                        onUpdateLabel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tagModel.loadData()
    }
}