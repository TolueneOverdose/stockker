package com.example.stockko.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StockInfoCard(stock: com.example.stockko.data.model.StockItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9FAFB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Company Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            InfoRow("Symbol", stock.symbol)
            InfoRow("Name", stock.name)
            InfoRow("Current Price", stock.price)

            val changeColor = if (stock.priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
            val changePrefix = if (stock.priceChange >= 0) "+" else ""

            InfoRow(
                label = "Change",
                value = "$changePrefix${stock.priceChange} (${changePrefix}0.00%)",
                valueColor = changeColor
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                thickness = 1.dp,
                color = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}