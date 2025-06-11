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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    val data = remember { viewModel.data.id }
    val rule = viewModel.rule.collectAsStateWithLifecycle()
    val ruleAddUpdateStatus = viewModel.ruleAddUpdateStatus.collectAsStateWithLifecycle().value
    val isDeleteAlertDialogOpen = remember { mutableStateOf(false) }

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
    ) { innerPadding ->

        if (isDeleteAlertDialogOpen.value){
            AlertDialog(
                onDismissRequest = { isDeleteAlertDialogOpen.value = false },
                title = { Text("Delete Rule") },
                text = { Text("Are you sure you want to delete this rule?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteRule(data)
                            isDeleteAlertDialogOpen.value = false
                            navController.navigateUp()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isDeleteAlertDialogOpen.value = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }


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
                    horizontalArrangement = if(data==null) Arrangement.End  else Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (data!=null){
                        Button(
                            onClick = {
                                isDeleteAlertDialogOpen.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.addNewAutoReply(autoReplyEntity = rule.value)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        )
                    ) {
                        Text(text = if (data == null) "Save" else "Update")
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
                .background(color = MaterialTheme.colorScheme.secondaryContainer),
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