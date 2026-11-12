package com.yourssu.reststopmate.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.ui.theme.*
import com.yourssu.reststopmate.ui.components.*
import com.yourssu.reststopmate.data.mock.*


@Composable
internal fun HomeScreen(onSearch: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)) {
        PageHeading("Home")
        Surface(shape = RoundedCornerShape(28.dp), color = Panel) {
            Column(Modifier.fillMaxWidth().padding(32.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("게스트로 이용 중", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("가까운 휴게소 화면이 여기에 연결됩니다.", color = Muted, fontSize = 20.sp)
                Button(onClick = onSearch, shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp)) {
                    Text("휴게소 검색하기", fontSize = 20.sp)
                }
            }
        }
    }
}
