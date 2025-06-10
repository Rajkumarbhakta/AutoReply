package com.rkbapps.autoreply.ui.screens.addeditautoreply.addEdit

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.PersonRemoveAlt1
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rkbapps.autoreply.data.MatchingType
import com.rkbapps.autoreply.navigation.NavigationRoutes
import com.rkbapps.autoreply.ui.composables.CommonTextField
import com.rkbapps.autoreply.ui.screens.addeditautoreply.AddEditAutoReplyScreenViewModel
import com.rkbapps.autoreply.ui.screens.addeditautoreply.choose_contact.ChooseContactScreen
import com.rkbapps.autoreply.ui.screens.addeditautoreply.schedule.ManageScheduleScreen
import com.rkbapps.autoreply.ui.theme.primaryColor
import com.rkbapps.autoreply.ui.theme.secondaryColor
import com.rkbapps.autoreply.ui.theme.surfaceColor
import com.rkbapps.autoreply.utils.ChooseContactType
import com.rkbapps.autoreply.utils.ReplyType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAutoReplyScreen(
    navController: NavHostController,
    viewModel: AddEditAutoReplyScreenViewModel = hiltViewModel()
) {

    val navControllerAddEdit = rememberNavController()

    NavHost(navController = navControllerAddEdit, startDestination = NavigationRoutes.AddEdit) {
        composable<NavigationRoutes.AddEdit> {
            AddEditScreen(
                navControllerAddEdit = navControllerAddEdit,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable<NavigationRoutes.ChooseContact> {
            ChooseContactScreen(navController = navControllerAddEdit,viewModel = viewModel)
        }
        composable<NavigationRoutes.ManageSchedule> {
            ManageScheduleScreen(navController = navControllerAddEdit,viewModel = viewModel)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navControllerAddEdit: NavHostController,
    navController: NavHostController,
    viewModel: AddEditAutoReplyScreenViewModel
) {

    val context = LocalContext.current

    val rule = viewModel.rule.collectAsStateWithLifecycle()
    val ruleAddUpdateStatus = viewModel.ruleAddUpdateStatus.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto Reply Rule") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

        },
        containerColor = surfaceColor
    ) { innerPadding ->
        LaunchedEffect(ruleAddUpdateStatus.isError,ruleAddUpdateStatus.data) {
            when{
                ruleAddUpdateStatus.isError->{
                    withContext (Dispatchers.Main){
                        Toast.makeText(
                            context, ruleAddUpdateStatus.message ?: "Something went wrong", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ruleAddUpdateStatus.data!=null->{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Rule saved successfully", Toast.LENGTH_SHORT
                        ).show()
                        navController.navigateUp()
                    }
                }
            }
        }



        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = innerPadding
        ) {
            item {
                Spacer(Modifier.height(10.dp))
            }
            item {
                CommonTextField(
                    text = rule.value.name,
                    labelText = "Rule Name",
                    placeholderText = "Enter a name for this rule",
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val update = rule.value.copy(name = it)
                    viewModel.updateRule(update)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)){
                    Text("Trigger Condition", style = MaterialTheme.typography.titleMedium)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Spacer(Modifier.width(10.dp))
                        }
                        items(
                            items = MatchingType.entries
                        ) {
                            ConditionItem(
                                selected = it == rule.value.matchingType,
                                type = it
                            ) { type ->
                                val update = rule.value.copy(matchingType = type)
                                viewModel.updateRule(update)
                            }
                        }
                        item {
                            Spacer(Modifier.width(10.dp))
                        }
                    }

                    CommonTextField(
                        text = rule.value.trigger,
                        placeholderText = "Enter keywords or phrases",
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val update = rule.value.copy(trigger = it)
                        viewModel.updateRule(update)
                    }
                }
            }

            item {
                val text = if (rule.value.delay==0L) "" else rule.value.delay.toString()
                CommonTextField(
                    text =  text,
                    labelText = "Delay Before Reply",
                    placeholderText = "Enter delay in milliseconds",
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    trailingText = "ms",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                ) {
                    val delay = it.toLongOrNull() ?: 0L
                    val update = rule.value.copy(delay = delay)
                    viewModel.updateRule(update)
                }
            }

            item {
                CommonTextField(
                    text = rule.value.reply,
                    labelText = "Auto Reply Message",
                    placeholderText = "Enter keywords or phrases",
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                ) {
                    val update = rule.value.copy(reply = it)
                    viewModel.updateRule(update)
                }
            }
            item {
                Text(
                    "Target Audience",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Spacer(Modifier.width(10.dp))
                    }
                    items(
                        items = ReplyType.entries
                    ) {
                        TargetAudienceItems(
                            selected = it == rule.value.replyType,
                            type = it
                        ) { type ->
                            val update = rule.value.copy(replyType = type)
                            viewModel.updateRule(update)
                        }
                    }
                    item {
                        Spacer(Modifier.width(10.dp))
                    }
                }
            }
            item {
                AnimatedVisibility(visible = rule.value.replyType == ReplyType.INDIVIDUAL) {
                    CardItems(
                        icon = Icons.Outlined.PersonRemoveAlt1,
                        title = "Exclude Contacts",
                        subtitle = "Select contacts to exclude from this auto reply",
                    ) {
                        viewModel.setContactChooseType(ChooseContactType.EXCLUDE)
                        navControllerAddEdit.navigate(NavigationRoutes.ChooseContact)
                    }
                }
            }
            item {
                AnimatedVisibility(visible = rule.value.replyType == ReplyType.INDIVIDUAL) {
                    CardItems(
                        icon = Icons.Outlined.PersonAddAlt1,
                        title = "Include Contacts",
                        subtitle = "Select contacts to include in this auto reply",
                    ) {
                        viewModel.setContactChooseType(ChooseContactType.INCLUDE)
                        navControllerAddEdit.navigate(NavigationRoutes.ChooseContact)
                    }
                }
            }
            item {
                Text(
                    "Schedule",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                CardItems(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Schedule",
                    subtitle = "Set a schedule for this auto reply",
                ) {
                    navControllerAddEdit.navigate(NavigationRoutes.ManageSchedule)
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = secondaryColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Delete")
                    }
                    Button(
                        onClick = {
                            viewModel.addNewAutoReply(autoReplyEntity = rule.value)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }

    }
}


@Composable
fun CardItems(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = secondaryColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Group Icon"
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Light,
                    color = Color(0xff5C738A)
                ),
            )
        }

        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Forward Icon",
            )
        }

    }

}


@Composable
fun TargetAudienceItems(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    type: ReplyType = ReplyType.INDIVIDUAL,
    onSelect: (ReplyType) -> Unit = { }
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (selected) primaryColor else secondaryColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onSelect(type)
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            type.value.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(10.dp)
        )
    }
}
@Composable
fun ConditionItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    type: MatchingType = MatchingType.STARTS_WITH,
    onSelect: (MatchingType) -> Unit = { }
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (selected) primaryColor else secondaryColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onSelect(type)
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            type.value.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(10.dp)
        )
    }
}



/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAutoReplyScreen(
    navController: NavHostController,
    viewModel: AddEditAutoReplyScreenViewModel = hiltViewModel()
) {

    val matchingTypeList = MatchingType.entries
    val autoReplyObject = viewModel.autoReplyObject
    val autoReplyAddStatus = viewModel.autoReplyAddStatus.collectAsStateWithLifecycle()

    val sendMessage = remember { mutableStateOf(autoReplyObject?.reply ?: "") }
    val receiveMessage = remember { mutableStateOf(autoReplyObject?.trigger ?: "") }
    val selectedMatchingType =
        remember { mutableStateOf(autoReplyObject?.matchingType ?: MatchingType.EXACT) }

    val addEditErrorDialogShow = remember { mutableStateOf(false) }


    LaunchedEffect(autoReplyAddStatus.value) {
        if (autoReplyAddStatus.value.data != null
            && !autoReplyAddStatus.value.isLoading
            && !autoReplyAddStatus.value.isError
        ) {
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
                if (autoReplyObject != null) {
                    "Edit Auto Reply"
                } else {
                    "Add Auto Reply"
                }
            }
            AppBar(title = title) {
                navController.navigateUp()
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val data = autoReplyObject?.copy(
                            trigger = receiveMessage.value,
                            reply = sendMessage.value,
                            matchingType = selectedMatchingType.value
                        ) ?: AutoReplyEntity(
                            trigger = receiveMessage.value,
                            reply = sendMessage.value,
                            matchingType = selectedMatchingType.value,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
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
}*/


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
fun AppBar(title: String, onBackPressed: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
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
        verticalAlignment = Alignment.CenterVertically
    ) {
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