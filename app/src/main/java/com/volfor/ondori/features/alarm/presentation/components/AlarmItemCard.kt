package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm

@Composable
fun AlarmItemCard(
    alarm: Alarm
) {
    var checked by remember { mutableStateOf(alarm.enabled) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "8:30",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                        )
                        Text(
                            text = "AM"
                        )
                    }
                    Text(alarm.label, fontSize = 12.sp)
                    Text("Mon, Tue, Wed", fontSize = 12.sp)
                }
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardEnabled() {
    OndoriTheme {
        AlarmItemCard(
            Alarm(
                id = 0,
                label = "Enabled alarm",
                enabled = true,
            )
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardDisabled() {
    OndoriTheme {
        AlarmItemCard(
            Alarm(
                id = 0,
                label = "Disabled alarm",
                enabled = false,
            )
        )
    }
}