package com.koco.personalfinancetracker.presentation.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koco.personalfinancetracker.data.model.ExpenseEntity

@Composable
fun ExpensePieChart(transactions: List<ExpenseEntity>) {
    // Filter only Expense type transactions
    val filteredTransactions = transactions.filter { it.type == "Expense" }

    val categoryTotals = filteredTransactions
        .groupBy { it.title }
        .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }

    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta,
        Color.Gray, Color.Black, Color.DarkGray, Color.LightGray
    )

    val totalAmount = categoryTotals.values.sum()
    val pieData = categoryTotals.entries.mapIndexed { index, entry ->
        CategoryExpense(entry.key, entry.value, colors[index % colors.size])
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(300.dp)) {
            var startAngle = 0f
            pieData.forEach { category ->
                val sweepAngle = (category.amount / totalAmount) * 360f
                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
                startAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(8.dp)) {
            pieData.forEach { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(category.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${category.category}: ${category.amount}", fontSize = 14.sp)
                }
            }
        }
    }
}