package com.rkbapps.autoreply.ui.screens.help_center

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.autoreply.navigation.NavigationRoutes
import com.rkbapps.autoreply.ui.screens.help_center.details_text.HOW_IT_WORKS_TECHNICAL
import com.rkbapps.autoreply.ui.screens.help_center.details_text.HOW_IT_WORKS
import com.rkbapps.autoreply.ui.screens.help_center.details_text.HOW_TO_USE
import com.rkbapps.autoreply.ui.theme.AutoReplyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Help Center")
                },
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
        }
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                data
            ) {
                GridItem(data = it) {
                    navController.navigate(
                        NavigationRoutes.ShowHtmlText(
                            title = it.title,
                            htmlText = it.detailDescription
                        )
                    )
                }
            }


        }
    }

}


@Composable
fun GridItem(modifier: Modifier = Modifier,
    data: HelpCenterData,
    onClick:()->Unit) {
    OutlinedCard(
    onClick = onClick
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(data.icon, contentDescription = "Security Icon")
            Text(data.title, style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HelpCenterScreenPreview() {
    AutoReplyTheme {
        HelpCenterScreen(navController = rememberNavController())
    }
}

data class HelpCenterData(
    val title:String,
    val icon: ImageVector,
    val detailDescription:String
)

private val data = listOf(
    HelpCenterData(
        title = "How it works",
        icon = Icons.Outlined.QuestionMark,
        detailDescription = HOW_IT_WORKS
    ),
    HelpCenterData(
        title = "How to Use",
        icon = Icons.Outlined.Book,
        detailDescription = HOW_TO_USE
    ),
    HelpCenterData(
        title = "How it works : Technical",
        icon = Icons.Outlined.QuestionMark,
        detailDescription = HOW_IT_WORKS_TECHNICAL
    )
)