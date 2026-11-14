package com.yourssu.reststopmate.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourssu.reststopmate.ui.theme.GaugeFill
import com.yourssu.reststopmate.ui.theme.GaugeTrack
import com.yourssu.reststopmate.ui.theme.RestStopColors

// ③ 주행 중 홈 왼쪽의 세로 게이지. fraction = 채울 비율 (0.0 = 비어 있음, 1.0 = 꽉 참)
// 게이지는 아래에서부터 찬다. 맨 아래에 위치 아이콘.
@Composable
internal fun DistanceProgress(
    fraction: Float,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(targetValue = fraction.coerceIn(0f, 1f), label = "distance")

    Column(
        modifier = modifier.width(56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 연한 배경 막대 (게이지 전체 길이)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(GaugeTrack, RoundedCornerShape(28.dp)),
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animated)
                .align(Alignment.BottomCenter)
                .background(GaugeFill, RoundedCornerShape(28.dp))
            )
        }

        // 맨 아래 둥근 위치 아이콘
        Surface(shape = CircleShape, color = GaugeFill, contentColor = Color.White) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.padding(12.dp).size(28.dp),
            )
        }
    }
}

// 비율 3개를 나란히 비교
@Preview(showBackground = true, widthDp = 300, heightDp = 460)
@Composable
private fun DistanceProgressPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Row(Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            DistanceProgress(fraction = 0.2f)
            DistanceProgress(fraction = 0.6f)
            DistanceProgress(fraction = 1f)
        }
    }
}

// Interactive 모드에서 버튼을 눌러 애니메이션을 확인
@Preview(showBackground = true, widthDp = 300, heightDp = 520)
@Composable
private fun DistanceProgressAnimationPreview() {
    var fraction by remember { mutableFloatStateOf(0.2f) }
    MaterialTheme(colorScheme = RestStopColors) {
        Column(
            Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DistanceProgress(fraction = fraction, modifier = Modifier.height(360.dp))
            Button(onClick = { fraction = if (fraction < 0.5f) 0.9f else 0.2f }) {
                Text("바꾸기")
            }
        }
    }
}
