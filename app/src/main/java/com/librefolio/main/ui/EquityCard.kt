package com.librefolio.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Expanded
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Get the Equity Card Composable for the main UX.
 *
 * @param equityCardData The display data for the given equity.
 * @param windowSizeClass The screen dimension class for the device.
 */
@Composable
fun EquityCard(
    equityCardData: EquityCardData,
    windowSizeClass: WindowSizeClass
) {
    val bigStyle = when(windowSizeClass.widthSizeClass) {
        Expanded -> MaterialTheme.typography.bodyLarge
        Medium -> MaterialTheme.typography.bodyMedium
        Compact -> MaterialTheme.typography.bodySmall
        else -> MaterialTheme.typography.bodySmall
    }

    val smallStyle = when(windowSizeClass.widthSizeClass) {
        Expanded -> MaterialTheme.typography.labelLarge
        Medium -> MaterialTheme.typography.labelMedium
        Compact -> MaterialTheme.typography.labelSmall
        else -> MaterialTheme.typography.labelSmall
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Row {
            Column(
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = equityCardData.ticker,
                    modifier = Modifier.padding(all = 10.dp),
                    style = bigStyle
                )

                Text(
                    text = equityCardData.name,
                    modifier = Modifier.padding(all = 10.dp),
                    style = smallStyle
                )
            }

            Column(
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = equityCardData.formattedPrice,
                    modifier = Modifier.padding(all = 10.dp),
                    style = bigStyle
                )

                Text(
                    text = equityCardData.timeAgo,
                    modifier = Modifier.padding(all = 10.dp),
                    style = smallStyle
                )
            }

            Column(
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = equityCardData.amountFormattedPrice,
                    modifier = Modifier.padding(all = 10.dp),
                    style = bigStyle
                )

                Text(
                    text = equityCardData.amountUnits,
                    modifier = Modifier.padding(all = 10.dp),
                    style = smallStyle
                )
            }
        }
    }
}