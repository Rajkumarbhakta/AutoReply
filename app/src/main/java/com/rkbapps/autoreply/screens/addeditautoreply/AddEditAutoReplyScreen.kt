package com.rkbapps.autoreply.screens.addeditautoreply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.MatchingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAutoReplyScreen(navController: NavHostController,viewModel: AddEditAutoReplyScreenViewModel = hiltViewModel()) {

    val matchingTypeList = viewModel.matchingTypeList
    val autoReplyObject = viewModel.autoReplyObject
    val autoReplyAddStatus = viewModel.autoReplyAddStatus.collectAsStateWithLifecycle()

    val sendMessage = remember { mutableStateOf(autoReplyObject?.send?:"") }
    val receiveMessage = remember { mutableStateOf(autoReplyObject?.receive?:"") }
    val selectedMatchingType = remember { mutableStateOf(autoReplyObject?.matchingType?:MatchingType.EXACT) }

    val addEditErrorDialogShow = remember { mutableStateOf(false) }


    LaunchedEffect(autoReplyAddStatus.value) {
        if(autoReplyAddStatus.value.data != null && !autoReplyAddStatus.value.isLoading && !autoReplyAddStatus.value.isError){
            navController.navigateUp()
        }
        if(autoReplyAddStatus.value.isError){
            addEditErrorDialogShow.value = true;
        }
    }


    if (addEditErrorDialogShow.value){
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
            TopAppBar(
                title = {
                    if (autoReplyObject != null) {
                        Text("Edit Auto Reply")
                    } else {
                        Text("Add Auto Reply")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                actions = {
                    OutlinedButton (
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
                            if(autoReplyObject==null) viewModel.addNewAutoReply(data) else viewModel.addNewAutoReply(data, AddEditType.EDIT)
                        }
                    ) {
                        if(autoReplyAddStatus.value.isLoading){
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }else{
                            Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                        }

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(vertical = 8.dp, horizontal = 16.dp)) {

            Text("Receive", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = receiveMessage.value,
                onValueChange = {
                    receiveMessage.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            Spacer(Modifier.height(10.dp))
            Column {
                matchingTypeList.forEach {
                    MatchingTypeListUi(type = it, selectedMatchingType = selectedMatchingType.value, onSelect = {
                        selectedMatchingType.value = it
                    })
                }
            }

            Text("Send", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = sendMessage.value,
                onValueChange = {
                    sendMessage.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

        }

    }
}

@Composable
fun MatchingTypeListUi(modifier: Modifier = Modifier,type: MatchingType,selectedMatchingType: MatchingType,onSelect:()-> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedMatchingType == type,
            onClick = onSelect
        )
        Spacer(Modifier.width(5.dp))
        Text(text = type.name)
    }


}