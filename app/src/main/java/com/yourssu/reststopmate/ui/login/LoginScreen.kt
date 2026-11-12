package com.yourssu.reststopmate.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.ui.SessionUiState
import com.yourssu.reststopmate.ui.SessionStatus
import com.yourssu.reststopmate.ui.theme.RestStopColors

@Composable
fun LoginScreen(state: SessionUiState, onGuestClick: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
        val wide = maxWidth >= 840.dp
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = if (wide) 48.dp else 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text("휴게소 메이트", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("가는 길에 만나는 맛있는 휴식", fontSize = 22.sp)
            Text("게스트로 시작하고 가까운 휴게소와 추천 메뉴를 살펴보세요.",
                color = Color(0xFF62646C), fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            if (wide) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    ProviderButton("카카오 로그인 · 준비 중", R.raw.kakao_logo,
                        Color(0xFFFEE500), Color.Black, Modifier.weight(1f))
                    ProviderButton("네이버 로그인 · 준비 중", R.raw.naver_logo,
                        Color(0xFF03C75A), Color.White, Modifier.weight(1f))
                    GuestButton(state.saving, onGuestClick, Modifier.weight(1f))
                }
            } else {
                GuestButton(state.saving, onGuestClick, Modifier.fillMaxWidth())
                ProviderButton("카카오 로그인 · 준비 중", R.raw.kakao_logo,
                    Color(0xFFFEE500), Color.Black, Modifier.fillMaxWidth())
                ProviderButton("네이버 로그인 · 준비 중", R.raw.naver_logo,
                    Color(0xFF03C75A), Color.White, Modifier.fillMaxWidth())
            }
            Text("게스트 이용 상태는 이 기기에 저장됩니다.", color = Color(0xFF62646C), fontSize = 16.sp)
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun GuestButton(saving: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Button(onClick = onClick, enabled = !saving,
        modifier = modifier.heightIn(min = 72.dp), shape = RoundedCornerShape(8.dp)) {
        if (saving) {
            CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(12.dp))
        }
        Text(if (saving) "시작하는 중…" else "게스트로 시작하기", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProviderButton(label: String, logo: Int, background: Color, foreground: Color, modifier: Modifier) {
    Button(onClick = {}, enabled = false,
        modifier = modifier.heightIn(min = 72.dp), shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(disabledContainerColor = background, disabledContentColor = foreground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        val request = ImageRequest.Builder(LocalContext.current).data(logo)
            .decoderFactory(SvgDecoder.Factory()).build()
        Image(rememberAsyncImagePainter(request), contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}



@Preview(widthDp = 1280, heightDp = 720, showBackground = true)
@Composable
private fun LoginPreview() {
    MaterialTheme(colorScheme = RestStopColors) { Surface { LoginScreen(SessionUiState(SessionStatus.Login)) {} } }
}

@Preview(widthDp = 440, heightDp = 800, showBackground = true)
@Composable
private fun CompactLoginPreview() {
    MaterialTheme(colorScheme = RestStopColors) { Surface { LoginScreen(SessionUiState(SessionStatus.Login)) {} } }
}
