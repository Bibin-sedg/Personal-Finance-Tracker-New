package com.koco.personalfinancetracker.data.model

data class ExpenseSummary(
    val type: String,
    val date: String,
    val total_amount: Double,
    val note: String,
)