package com.koco.personalfinancetracker.presentation.stats

import com.github.mikephil.charting.data.Entry
import com.koco.personalfinancetracker.presentation.navigation.base.BaseViewModel
import com.koco.personalfinancetracker.presentation.navigation.base.UiEvent
import com.koco.personalfinancetracker.data.dao.ExpenseDao
import com.koco.personalfinancetracker.data.model.ExpenseSummary
import com.koco.personalfinancetracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel() {
    val entries = dao.getAllExpenseByDate()
    val topEntries = dao.getTopExpenses()
    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }

    override fun onEvent(event: UiEvent) {
    }
}