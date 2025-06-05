package com.rkbapps.autoreply.screens.addeditautoreply

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.MatchingType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAutoReplyScreen(
    navController: NavHostController,
    viewModel: AddEditAutoReplyScreenViewModel = hiltViewModel()
) {

    val matchingTypeList = MatchingType.entries
    val autoReplyObject = viewModel.autoReplyObject
    val autoReplyAddStatus = viewModel.autoReplyAddStatus.collectAsStateWithLifecycle()

    val sendMessage = remember { mutableStateOf(autoReplyObject?.send ?: "") }
    val receiveMessage = remember { mutableStateOf(autoReplyObject?.receive ?: "") }
    val selectedMatchingType =
        remember { mutableStateOf(autoReplyObject?.matchingType ?: MatchingType.EXACT) }

    val addEditErrorDialogShow = remember { mutableStateOf(false) }


    LaunchedEffect(autoReplyAddStatus.value) {
        if (autoReplyAddStatus.value.data != null
            && !autoReplyAddStatus.value.isLoading
            && !autoReplyAddStatus.value.isError) {
            navController.navigateUp()
        }
        if (autoReplyAddStatus.value.isError) {
            addEditErrorDialogShow.value = true
        }
    }


    if (addEditErrorDialogShow.value) {
        AlertDialog(
            onDismissRequest = {
                addEditErrorDialogShow.value = false
            },
            title = {
                Text("Error")
            },
            text = {
                Text(autoReplyAddStatus.value.message ?: "Something went wrong")
            },
            confirmButton = {
                TextButton(onClick = {
                    addEditErrorDialogShow.value = false
                }) {
                    Text("Dismiss")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            val title = remember {
                if (autoReplyObject != null) { "Edit Auto Reply" } else {
                    "Add Auto Reply"
                }
            }
            AppBar( title = title ) {
                navController.navigateUp()
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ){
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val data = autoReplyObject?.copy(
                            receive = receiveMessage.value,
                            send = sendMessage.value,
                            matchingType = selectedMatchingType.value
                        ) ?: AutoReplyEntity(
                            receive = receiveMessage.value,
                            send = sendMessage.value,
                            matchingType = selectedMatchingType.value
                        )
                        if (autoReplyObject == null) viewModel.addNewAutoReply(data) else viewModel.addNewAutoReply(
                            data,
                            AddEditType.EDIT
                        )
                    }
                ) {
                    Text(
                        text = if (autoReplyObject != null) "Update Auto Reply" else "Add Auto Reply",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TitleSubtitleCombo(
                "Trigger condition",
                "Choose when to send auto reply",
            )


            OutlinedTextField(
                value = receiveMessage.value,
                onValueChange = {
                    receiveMessage.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                placeholder = {
                    Text(text = "Enter keywords or phrases")
                },
                label = {
                    val msg = when (selectedMatchingType.value) {
                        MatchingType.EXACT -> "matches"
                        MatchingType.CONTAINS -> "contains"
                        MatchingType.STARTS_WITH -> "starts with"
                    }
                    Text(
                        text = "Message $msg",
                    )
                }
            )
            HorizontalDivider()

            Column {
                Text(
                    text = "Match Type",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                matchingTypeList.forEach {
                    MatchingTypeListUi(
                        type = it,
                        selectedMatchingType = selectedMatchingType.value,
                        onSelect = {
                            selectedMatchingType.value = it
                        })
                }
            }

            HorizontalDivider()

            TitleSubtitleCombo(
                "Auto reply message",
                "This message will be sent automatically",
            )

            OutlinedTextField(
                value = sendMessage.value,
                onValueChange = {
                    if (it.length <= AddEditAutoReplyScreenViewModel.MAX_CHARACTER_LIMIT) {
                        sendMessage.value = it
                    }
                },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                placeholder = {
                    Text(text = "Enter your auto reply message")
                },
                label = {
                    Text(text = "Reply message")
                },
                supportingText = {
                    Text(
                        "${sendMessage.value.length} / ${AddEditAutoReplyScreenViewModel.MAX_CHARACTER_LIMIT}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }

            )
        }
    }
}


@Composable
fun TitleSubtitleCombo(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title:String, onBackPressed: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton (
                onClick = onBackPressed,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
fun MatchingTypeListUi(
    modifier: Modifier = Modifier,
    type: MatchingType,
    selectedMatchingType: MatchingType, onSelect: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedMatchingType == type,
            onClick = onSelect
        )
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = modifier.fillMaxWidth(),
        ) {
            Text(
                text = type.value,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = type.meaning,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }


}



@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun AddEditPreview(){
    val navController = rememberNavController()
    val viewModel = AddEditAutoReplyScreenViewModel(
        repository = AddEditAutoReplyScreenRepository(
            autoReplyDao = AutoReplyDaoFake()
        ),
        saveStateHandle = SavedStateHandle(),
        gson = Gson()
    )

    AddEditAutoReplyScreen(navController = navController, viewModel = viewModel)
}

class AutoReplyDaoFake(): AutoReplyDao{
    override suspend fun insertAutoReply(autoReply: AutoReplyEntity) {
        //TODO("Not yet implemented")
    }

    override suspend fun deleteAutoReply(autoReply: AutoReplyEntity) {
        //TODO("Not yet implemented")
    }

    override suspend fun updateAutoReply(autoReply: AutoReplyEntity) {
        //TODO("Not yet implemented")
    }

    override suspend fun getAutoReplyById(id: Int): AutoReplyEntity? =null

    override fun getAllAutoReplies(): Flow<List<AutoReplyEntity>> = flowOf(emptyList())

    override suspend fun deleteAutoReplyById(id: Int) {
        //TODO("Not yet implemented")
    }

    override suspend fun updateAutoReplyActiveStatus(id: Int, isActive: Boolean) {
        //TODO("Not yet implemented")
    }

    override fun getActiveAutoReplies(): Flow<List<AutoReplyEntity>> = flowOf(emptyList())

    override fun getInactiveAutoReplies(): Flow<List<AutoReplyEntity>> = flowOf(emptyList())

}