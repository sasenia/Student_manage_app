package com.example.studentmanageapp.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CounterRow(
    name: String,
    count: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )

        IconButton(onClick = onDecrease) {
            Text("-", fontSize = 20.sp)
        }

        Text(
            text = count.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        IconButton(onClick = onIncrease) {
            Text("+", fontSize = 20.sp)
        }
    }
}
