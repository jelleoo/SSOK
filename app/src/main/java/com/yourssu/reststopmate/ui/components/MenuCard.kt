package com.yourssu.reststopmate.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.Panel
import com.yourssu.reststopmate.ui.theme.RestStopColors

@Composable
internal fun MenuCard(
    name: String,
    description: String?,
    priceWon: Int,
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                modifier = Modifier.size(140.dp, 100.dp).clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(imageRes),
                error = painterResource(imageRes),
                fallback = painterResource(imageRes),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold)

                if (description != null) {
                    Text(text = description,
                        color = Muted,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }

                Text(text = formatPrice(priceWon),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Figma 표기는 "9900원" 이지만 가독성을 위해 천단위 쉼표 추가함
internal fun formatPrice(priceWon: Int): String {
    return "%,d원".format(priceWon)
}

@Preview(showBackground = true, widthDp = 560)
@Composable
private fun MenuCardPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MenuCard(
                name = "매운철판돈까스",
                description = "매콤한 돈까스와 치즈의 조합",
                priceWon = 9900,
                imageRes = R.drawable.pork_cutlet,
            )
            MenuCard(
                name = "아주 긴 이름의 해물라면 스페셜 세트 메뉴",
                description = null,
                priceWon = 12500,
                imageRes = R.drawable.seafood_ramen,
            )
        }
    }
}
