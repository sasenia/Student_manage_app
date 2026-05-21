package com.example.studentmanageapp.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreCounterRow(
    number: Int,
    name: String,
    score: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 번호
        Text(
            text = number.toString(),
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center
        )

        // 이름
        Text(
            text = name,
            modifier = Modifier.weight(1.4f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        // 감소
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(36.dp)
        ) {
            Text("-", fontSize = 18.sp)
        }

        // 점수
        Text(
            text = score.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface // 🔥 항상 검정
        )

        // 증가
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(36.dp)
        ) {
            Text("+", fontSize = 18.sp)
        }
    }
}
