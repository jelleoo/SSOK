package com.yourssu.reststopmate.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourssu.reststopmate.ui.theme.*
import com.yourssu.reststopmate.ui.components.*
import com.yourssu.reststopmate.data.mock.*

import com.yourssu.reststopmate.ui.home.HomeScreen
import com.yourssu.reststopmate.ui.menu.MenuListRoute
import com.yourssu.reststopmate.ui.search.SearchScreen
import com.yourssu.reststopmate.ui.my.MyScreen

@Composable
fun MainTabsScreen() {
    var selectedTab by rememberSaveable { mutableStateOf("Home") }
    var query by rememberSaveable { mutableStateOf("") }
    // Keep independent drag positions while switching tabs.
    val searchScroll = rememberLazyListState()
    val frequentScroll = rememberLazyListState()
    val mealScroll = rememberLazyListState()
    val myScroll = rememberScrollState()
    val nearbyScroll = rememberLazyListState()

    var menuRestStopId by rememberSaveable { mutableStateOf<String?>(null) }
    var menuRestStopName by rememberSaveable { mutableStateOf("") }

    BackHandler(enabled = selectedTab != "Home") { selectedTab = "Home" }

    BackHandler(enabled = selectedTab == "Home" && menuRestStopId != null) { menuRestStopId = null }

    BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
        val compact = maxWidth < 700.dp
        @Composable
        fun Content(modifier: Modifier) {
            Box(modifier) {
                when (selectedTab) {
                    "Search" -> SearchScreen(query, { query = it }, searchScroll)
                    "My" -> MyScreen(frequentScroll, mealScroll, myScroll)
                    else -> {
                        val id = menuRestStopId
                        if (id != null) {
                            MenuListRoute(restStopId = id, restStopName = menuRestStopName, onBack = { menuRestStopId = null })
                        } else {
                            HomeScreen(
                                compact = compact,
                                nearbyListState = nearbyScroll,
                                onRestStopClick = { restStop ->
                                    menuRestStopId = restStop.id
                                    menuRestStopName = restStop.name
                                },
                                onSearch = { selectedTab = "Search" },
                            )
                        }
                    }
                }
            }
        }
        if (compact) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Content(Modifier.weight(1f).fillMaxWidth())
                TabMenu(selectedTab, { selectedTab = it }, horizontal = true, modifier = Modifier.fillMaxWidth())
            }
        } else {
            Row(Modifier.fillMaxSize().padding(28.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                TabMenu(selectedTab, { selectedTab = it }, horizontal = false, modifier = Modifier.width(220.dp))
                Content(Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}
