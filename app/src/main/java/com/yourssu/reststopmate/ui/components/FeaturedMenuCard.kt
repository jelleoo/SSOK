package com.yourssu.reststopmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.data.FeaturedMenu
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.RestStopColors

// 휴게소 카드 안의 "식당 추천 메뉴": 큰 사진 아래에 이름과 설명
@Composable
internal fun FeaturedMenuCard(
    menu: FeaturedMenu,
    modifier: Modifier = Modifier,
) {
    val defaultImage = painterResource(R.drawable.pork_cutlet)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AsyncImage(
            model = menu.imageUrl,
            contentDescription = menu.name,
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
            placeholder = defaultImage,
            error = defaultImage,
            fallback = defaultImage,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(text = menu.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp)

            if (menu.description != null) {
                Text(
                    text = menu.description,
                    color = Muted,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 760)
@Composable
private fun FeaturedMenuCardPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            FeaturedMenuCard(
                menu = FeaturedMenu("안성국밥", "진한 사골 국물에 밥을 말아 먹는 안성 대표 국밥 메뉴", null),
                modifier = Modifier.width(340.dp),
            )
            FeaturedMenuCard(
                menu = FeaturedMenu("한우소머리국밥", null, null),
                modifier = Modifier.width(340.dp),
            )
        }
    }
}
