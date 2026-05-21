package com.example.studentmanageapp.ui.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmanageapp.ui.navigation.BottomItem

@Composable
fun ScrollBottomBar(
    items: List<BottomItem>,
    selectedRoute: String?,
    onItemClick: (BottomItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(items) { item ->

            val isSelected = item.screen.route == selectedRoute

            Column(
                modifier = Modifier
                    .width(72.dp)
                    .background(
                        if (isSelected)
                            Color(0xFFE0E0E0) // ✅ 선택된 것만 회색 배경
                        else
                            Color.Transparent
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() }, // ✅ 아이템별
                        indication = LocalIndication.current
                    ) {
                        onItemClick(item)
                    }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = item.screen.icon,
                    contentDescription = item.screen.label,
                    tint = Color.Black // ❗ 색 안 바꿈 (요청사항)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.screen.label,
                    fontSize = 11.sp,
                    color = Color.Black
                )
            }
        }
    }
}
