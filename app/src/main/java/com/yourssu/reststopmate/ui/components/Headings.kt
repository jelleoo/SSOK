package com.yourssu.reststopmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
internal fun PageHeading(title: String) {
    Text(title, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
}

@Composable
internal fun SectionHeading(title: String) {
    Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
}
