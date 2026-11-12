package com.yourssu.reststopmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
internal fun RestAreaRow(records: List<RestAreaRecord>, state: LazyListState, tag: String) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cardWidth = (maxWidth * 0.38f).coerceIn(240.dp, 520.dp)
        // LazyRow uses touch dragging and fling; no scrollbar or wheel handler is added.
        LazyRow(state = state, modifier = Modifier.fillMaxWidth().testTag(tag),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            items(records, key = { it.name }) { record ->
                Surface(modifier = Modifier.width(cardWidth), color = Panel,
                    shape = RoundedCornerShape(30.dp), shadowElevation = 8.dp) {
                    Column(Modifier.padding(26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(record.name, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        Text(record.visit, fontSize = 19.sp, color = Muted)
                    }
                }
            }
        }
    }
}
