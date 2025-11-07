package com.example.studentmanageapp.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GenderRadio(label: String, selected: String, onSelect: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 12.dp)
            .height(48.dp) // 클릭 영역 확대
    ) {
        RadioButton(
            selected = selected == label,
            onClick = { onSelect(label) },
            modifier = Modifier.scale(1.4f), // 라디오 버튼 크기 확대
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSecondary,
                disabledSelectedColor = MaterialTheme.colorScheme.surface
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp // 텍스트 크기 키움
        )
    }
}