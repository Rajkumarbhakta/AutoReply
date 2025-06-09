package com.rkbapps.autoreply.ui.screens.addeditautoreply.choose_contact

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.rkbapps.autoreply.models.Contact
import com.rkbapps.autoreply.ui.composables.CommonSearchBar
import com.rkbapps.autoreply.ui.screens.addeditautoreply.AddEditAutoReplyScreenViewModel
import com.rkbapps.autoreply.ui.theme.surfaceColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseContactScreen(
    navController: NavHostController,
    viewModel: AddEditAutoReplyScreenViewModel
) {

    val query = remember { mutableStateOf("") }
    val context = LocalContext.current


    val contacts = viewModel.contacts.collectAsStateWithLifecycle()

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (granted) {
                    viewModel.loadContacts()
                    Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        )


    LaunchedEffect(key1 = viewModel.isContactPermissionGranted(context)) {
        if (viewModel.isContactPermissionGranted(context) == false) {
            takePermission(permissionLauncher)
        }else{
            viewModel.loadContacts()
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Select Contacts")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, "Back Icon")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(color = surfaceColor)
                    .padding(10.dp)
            ){
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    Text("Done")
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = innerPadding
        ) {
            item {
                CommonSearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = query.value
                ) {
                    query.value = it
                }
            }
            item {
                Text(
                    "Contacts",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(contacts.value) {
                ContactItem(contact = it)
            }

        }

    }


}


@Composable
fun ContactItem(modifier: Modifier = Modifier,contact: Contact) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color = Color.Cyan)
        ){
            AsyncImage(
                model = contact.photoUri,
                contentDescription = "Contact Image",
                modifier = Modifier.fillMaxSize()
            )
        }
        Column {
            Text(
                contact.name?:"",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(contact.phoneNumber, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = true,
            onCheckedChange = { },
        )
    }


}

fun takePermission(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
}
