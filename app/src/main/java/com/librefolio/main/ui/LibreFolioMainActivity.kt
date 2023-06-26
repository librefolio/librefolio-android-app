package com.librefolio.main.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.librefolio.main.ui.UIConstants.Companion.ABOUT_NAVIGATION_ROUTE
import com.librefolio.main.ui.UIConstants.Companion.HOME_NAVIGATION_ROUTE
import com.librefolio.main.ui.UIConstants.Companion.getAllRowNames
import com.librefolio.main.viewmodel.LibreFolioViewModel
import com.librefolio.main.R
import com.librefolio.main.ui.LibreFolioMainActivity.Companion.LOG_TAG

/**
 * LibreFolio main activity
 */
@AndroidEntryPoint
class LibreFolioMainActivity : ComponentActivity() {

    companion object {
        val LOG_TAG = LibreFolioMainActivity::class.simpleName
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: LibreFolioViewModel by viewModels()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MainScaffold(viewModel, windowSizeClass)
        }

        // Currently update only when the app opens
        viewModel.updateDatabase()
    }
}

/**
 * Main scaffold, the entire UX to be set singularly into the Activity content.
 *
 * @param viewModel The viewModel for the equity data.
 * @param windowSizeClass The screen dimension class for the device.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: LibreFolioViewModel,
    windowSizeClass: WindowSizeClass
) {
    val navController = rememberNavController()
    val equitylistState = rememberLazyListState()
    val aboutListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.trending_up),
                            contentDescription = stringResource(
                                id = R.string.home_content_description
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .padding(all = 3.dp)
                        )

                        Text(
                            stringResource(R.string.app_name),
                            modifier = Modifier.padding(all = 3.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                })
        },
        bottomBar = {
            BottomAppBar {
                Row {
                    Box {
                        IconButton(onClick = {
                            Log.d(LOG_TAG, "Home clicked.")
                            navController.navigate(HOME_NAVIGATION_ROUTE)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = stringResource(
                                    id = R.string.home_content_description
                                ),
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = {
                            Log.d(LOG_TAG, "About clicked.")
                            navController.navigate(ABOUT_NAVIGATION_ROUTE)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(
                                    id = R.string.info_content_description
                                ),
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HOME_NAVIGATION_ROUTE,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(HOME_NAVIGATION_ROUTE) {
                EquitiesContent(
                    equitylistState,
                    viewModel,
                    windowSizeClass
                )
            }
            composable(ABOUT_NAVIGATION_ROUTE) {
                AboutContent(
                    aboutListState
                )
            }
        }
    }
}

/**
 * About main content, e.g. a short "About Librefolio" page.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutContent(
    aboutListState: LazyListState
) {
    LazyColumn(state = aboutListState) {
        stickyHeader {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(id = R.string.about_title),
                    modifier = Modifier.padding(all = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(id = R.string.about_author),
                    modifier = Modifier.padding(all = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    stringResource(id = R.string.about_copyright),
                    modifier = Modifier.padding(all = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    stringResource(id = R.string.about_version),
                    modifier = Modifier.padding(all = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

/**
 * Equities main content, including the row header and all equity data formatted.
 *
 * @param listState The main LazyColumn scroll state, hoisted above to persist between navigation actions.
 * @param viewModel The viewModel for the equity data.
 * @param windowSizeClass The screen dimension class for the device.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EquitiesContent(
    equitylistState: LazyListState,
    viewModel: LibreFolioViewModel,
    windowSizeClass: WindowSizeClass
) {
    // Get state from viewModel
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    // Sort by the actual cash amount owned, descending
    val sortedEquities = uiState.value.sortedBy { -it.currentPriceCents * it.quantity }

    LazyColumn(state = equitylistState) {
        stickyHeader {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    getAllRowNames().map { rowResourceId ->
                        Text(
                            stringResource(id = rowResourceId),
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .weight(weight = 1f, fill = true),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        items(items = sortedEquities) { equity ->
            EquityCardData.fromEquityInfo(equity)?.let {
                EquityCard(
                    it,
                    windowSizeClass
                )
            }
        }
    }

}