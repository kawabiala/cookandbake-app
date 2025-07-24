package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
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

    private lateinit var labelModel: LabelManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        labelModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[LabelManagementViewModel::class.java]

        val tagListData = labelModel.availableTagListData

        val optionBack = PingwinekCooksComposableHelpers.OptionItem(
            R.string.back,
            Icons.AutoMirrored.Outlined.ArrowBack
        ) {
            finish()
        }

        val onAddLabel: (String) -> Unit = { label ->
            labelModel.addLabel(label)
        }

        val onDeleteLabel: (Tag) -> Unit = { tag ->
            labelModel.deleteLabel(tag)
        }

        val onUpdateLabel: (Tag, String) -> Unit = { tag, label ->
            labelModel.updateLabel(tag, label)
        }

        setContent {

            PingwinekCooksAppTheme {

                val tagsWithCount by tagListData.observeAsState()
                var tagEditMode by remember(tagsWithCount) { mutableStateOf(LabelManagementActivity.TagEditMode.SHOW) }

                val onChangeTagEditMode: (TagEditMode) -> Unit = { mode ->
                    tagEditMode = mode
                }

                val onFabClicked: () -> Unit = {
                    tagEditMode = TagEditMode.ADD
                }

                PingwinekCooksScaffold(
                    title = getString(R.string.manage_labels),
                    optionItemLeft = optionBack,
                    navigationBarVisible = false,
                    showFab = (tagEditMode == TagEditMode.SHOW),
                    fabIcon = Icons.Filled.Add,
                    fabIconLabel = getString(R.string.add_label),
                    fabContainerColor = MaterialTheme.colorScheme.primary,
                    fabIconColor = MaterialTheme.colorScheme.onPrimary,
                    onFabClicked = onFabClicked
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
        labelModel.loadData()
    }
}