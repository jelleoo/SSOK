package com.yourssu.reststopmate.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.ui.theme.*
import com.yourssu.reststopmate.ui.components.*
import com.yourssu.reststopmate.data.mock.*


@Composable
internal fun TabMenu(selected: String, onSelect: (String) -> Unit, horizontal: Boolean, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(28.dp), color = Panel, shadowElevation = 5.dp) {
        @Composable
        fun Entry(tab: String, itemModifier: Modifier) {
            Surface(onClick = { onSelect(tab) }, selected = selected == tab,
                modifier = itemModifier.testTag("tab_$tab"), shape = RoundedCornerShape(24.dp),
                color = if (selected == tab) Selected else Panel) {
                Text(tab, Modifier.padding(horizontal = 24.dp, vertical = 25.dp),
                    fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (horizontal) {
            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Home", "Search", "My").forEach { Entry(it, Modifier.weight(1f)) }
            }
        } else {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Home", "Search", "My").forEach { Entry(it, Modifier.fillMaxWidth()) }
            }
        }
    }
}
