package com.koco.personalfinancetracker.presentation.home

import androidx.lifecycle.viewModelScope
import com.koco.personalfinancetracker.presentation.navigation.base.BaseViewModel
import com.koco.personalfinancetracker.presentation.navigation.base.HomeNavigationEvent
import com.koco.personalfinancetracker.presentation.navigation.base.UiEvent
import com.koco.personalfinancetracker.data.dao.ExpenseDao
import com.koco.personalfinancetracker.data.model.ExpenseEntity
import com.koco.personalfinancetracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel() {
    val expenses = dao.getAllExpense()
    private val _categoryBudgets = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categoryBudgets: StateFlow<Map<String, Double>> = _categoryBudgets
    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddExpense)
                }
            }

            is HomeUiEvent.OnAddBudgetClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddBudget)
                }
            }

            is HomeUiEvent.OnSeeAllClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToSeeAll)
                }
            }
        }
    }



    init {
        calculateCategoryBudgets()
    }

    // Calculate category budgets based on stored transactions
    private fun calculateCategoryBudgets() {
        viewModelScope.launch {
            expenses.collectLatest { list ->
                val budgetMap = mutableMapOf<String, Double>()
                list.filter { it.type == "Budget" }.forEach { budget ->
                    budgetMap[budget.title] = budgetMap.getOrDefault(budget.title, 0.0) + budget.amount
                }
                _categoryBudgets.value = budgetMap
            }
        }
    }

    fun getBalance(list: List<ExpenseEntity>): String {
        var balance = 0.0
        for (expense in list) {
            if (expense.type == "Budget") {
                balance += expense.amount
            } else {
                balance -= expense.amount
            }
        }
        return Utils.formatCurrency(balance)
    }

    fun getTotalExpense(list: List<ExpenseEntity>): String {
        var total = 0.0
        for (expense in list) {
            if (expense.type != "Budget") {
                total += expense.amount
            }
        }

        return Utils.formatCurrency(total)
    }

    fun getTotalBudget(list: List<ExpenseEntity>): String {
        var totalIncome = 0.0
        for (expense in list) {
            if (expense.type == "Budget") {
                totalIncome += expense.amount
            }
        }
        return Utils.formatCurrency(totalIncome)
    }
}

sealed class HomeUiEvent : UiEvent() {
    data object OnAddExpenseClicked : HomeUiEvent()
    data object OnAddBudgetClicked : HomeUiEvent()
    data object OnSeeAllClicked : HomeUiEvent()
}