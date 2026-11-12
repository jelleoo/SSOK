package com.yourssu.reststopmate.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.ui.theme.*
import com.yourssu.reststopmate.ui.components.*
import com.yourssu.reststopmate.data.mock.*


@Composable
internal fun SearchScreen(query: String, onQuery: (String) -> Unit, scroll: LazyListState) {
    val matches = remember(query) { mockRestAreas.filter { it.name.contains(query.trim(), ignoreCase = true) } }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        OutlinedTextField(value = query, onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth(0.82f).align(Alignment.CenterHorizontally).testTag("rest_area_search"),
            placeholder = { Text("휴게소 이름을 검색하세요", fontSize = 22.sp) },
            singleLine = true, shape = RoundedCornerShape(24.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Panel, focusedContainerColor = Panel))
        SectionHeading(if (query.isBlank()) "최근 검색 항목" else "검색 결과")
        if (matches.isEmpty()) {
            Text("검색 결과가 없어요.", fontSize = 22.sp, color = Muted)
        } else {
            RestAreaRow(matches, scroll, "search_history")
        }
    }
}
