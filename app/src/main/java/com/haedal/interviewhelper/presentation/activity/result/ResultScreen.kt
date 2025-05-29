package com.haedal.interviewhelper.presentation.activity.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.presentation.theme.Typography

@Composable
fun ResultScreen(
    resultList: List<ResultItem>,
    serverMessage: String,
    feedback: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("📩 $serverMessage", style = Typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Text("총평", style = Typography.titleMedium)
        Text(feedback, style = Typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(resultList) { item ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("🗣️ \"${item.sentence}\"", style = Typography.titleMedium)
                    item.emotions
                        .sortedByDescending { it.score }
                        .take(3)
                        .forEach {
                            Text("→ ${it.label}: ${"%.2f".format(it.score)}")
                        }
                }
                HorizontalDivider()
            }
        }
    }
}
