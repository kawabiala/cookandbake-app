package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.labelManagementActivity.ScaffoldContent
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.viewModels.LabelManagementViewModel

class LabelManagementActivity : AppCompatActivity() {

    private lateinit var labelModel: LabelManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        labelModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[LabelManagementViewModel::class.java]

        val labelListData = labelModel.labelListData

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

        setContent {

            PingwinekCooksAppTheme {

                val labels by labelListData.observeAsState()

                PingwinekCooksScaffold(
                    title = getString(R.string.manage_labels),
                    optionItemLeft = optionBack,
                    navigationBarVisible = false
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues,
                        labels,
                        onAddLabel,
                        onDeleteLabel
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