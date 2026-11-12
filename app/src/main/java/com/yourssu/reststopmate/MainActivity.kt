package com.yourssu.reststopmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourssu.reststopmate.data.SessionRepository
import com.yourssu.reststopmate.ui.*
import com.yourssu.reststopmate.ui.login.LoginScreen
import com.yourssu.reststopmate.ui.navigation.MainTabsScreen
import com.yourssu.reststopmate.ui.theme.RestStopColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val repository = remember { SessionRepository(applicationContext) }
            val session: SessionViewModel = viewModel(factory = SessionViewModel.factory(repository))
            val state by session.state.collectAsStateWithLifecycle()
            MaterialTheme(colorScheme = RestStopColors) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Authentication is a root state, never a destination in Home's back stack.
                    when (state.status) {
                        SessionStatus.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        SessionStatus.Login -> LoginScreen(state, session::startGuest)
                        SessionStatus.Home -> MainTabsScreen()
                        SessionStatus.ReadError -> SessionErrorScreen(state.error.orEmpty(), session::observeSession)
                    }
                }
            }
        }
    }
}
