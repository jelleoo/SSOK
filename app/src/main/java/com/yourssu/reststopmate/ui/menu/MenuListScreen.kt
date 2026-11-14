package com.yourssu.reststopmate.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.data.Menu
import com.yourssu.reststopmate.ui.components.MenuCard
import com.yourssu.reststopmate.ui.components.PageHeading
import com.yourssu.reststopmate.ui.theme.Background
import com.yourssu.reststopmate.ui.theme.Panel
import com.yourssu.reststopmate.ui.theme.RestStopColors

internal enum class SortOrder(val label: String) {
    PriceHigh("높은 가격순"),
    PriceLow("낮은 가격순"),
    Name("이름순"),
}

@Composable
internal fun MenuListScreen(
    restStopName: String,
    menus: List<Menu>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var sort by rememberSaveable {mutableStateOf(SortOrder.PriceHigh) }

    val gridState = rememberLazyGridState()
    LaunchedEffect(sort) { gridState.scrollToItem(0) }

    val sortedMenus = remember(menus, sort) {
        when (sort) {
            SortOrder.PriceHigh -> menus.sortedByDescending { it.priceWon }
            SortOrder.PriceLow  -> menus.sortedBy { it.priceWon }
            SortOrder.Name      -> menus.sortedBy { it.name }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Panel,
            shape = RoundedCornerShape(32.dp),
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PageHeading("$restStopName 메뉴")
                    SortDropdown(
                        selected = sort,
                        onSelect = { sort = it },
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(sortedMenus, key = {it.id}) {menu -> MenuCard(
                        name = menu.name,
                        description = menu.description,
                        priceWon = menu.priceWon,
                        imageRes = menu.imageRes,
                        imageUrl = menu.imageUrl,
                        )}
                }
            }
        }
    }
}

@Composable
private fun SortDropdown(
    selected: SortOrder,
    onSelect: (SortOrder) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected.label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOrder.entries.forEach { order ->
                DropdownMenuItem(
                    text = { Text(order.label) },
                    onClick = {
                        onSelect(order)
                        expanded = false
                    },
                )
            }
        }
    }
}

// Preview 전용 가짜 데이터. 기존 MockData.kt(My/Search용)는 건드리지 않는다.
private val previewMenus = listOf(
    Menu("m1", "매운철판돈까스", "매콤한 돈까스와 치즈의 조합", 9900, R.drawable.pork_cutlet),
    Menu("m2", "해물라면", "새우와 홍합이 들어간 얼큰한 라면", 6500, R.drawable.seafood_ramen),
    Menu("m3", "치즈돈까스", null, 10500, R.drawable.pork_cutlet),
    Menu("m4", "짬뽕라면", "불맛 가득한 짬뽕 스타일 라면", 7000, R.drawable.seafood_ramen),
    Menu("m5", "등심돈까스", "바삭한 등심 돈까스", 9000, R.drawable.pork_cutlet),
    Menu("m6", "아주 긴 이름의 해물라면 스페셜 세트 메뉴", null, 12500, R.drawable.seafood_ramen),
)

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun MenuListScreenPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            MenuListScreen(
                restStopName = "화성휴게소",
                menus = previewMenus,
                onBack = { },
            )
        }
    }
}
