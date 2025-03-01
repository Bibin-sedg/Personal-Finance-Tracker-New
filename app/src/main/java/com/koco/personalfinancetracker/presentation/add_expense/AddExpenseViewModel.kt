package com.koco.personalfinancetracker.presentation.add_expense

import androidx.lifecycle.viewModelScope
import com.koco.personalfinancetracker.presentation.navigation.base.AddExpenseNavigationEvent
import com.koco.personalfinancetracker.presentation.navigation.base.BaseViewModel
import com.koco.personalfinancetracker.presentation.navigation.base.NavigationEvent
import com.koco.personalfinancetracker.presentation.navigation.base.UiEvent
import com.koco.personalfinancetracker.data.dao.ExpenseDao
import com.koco.personalfinancetracker.data.model.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel() {

    private val _expense = MutableStateFlow<ExpenseEntity?>(null)
    val expense: StateFlow<ExpenseEntity?> = _expense

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            dao.insertExpense(expenseEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    suspend fun updateExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            dao.updateExpense(expenseEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    fun getExpenseById(id: Int): ExpenseEntity? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                dao.getExpenseById(id)
            }
        }
    }

    fun loadExpenseById(id: Int) {
        viewModelScope.launch {
            dao.getExpenseById(id)?.let { expenseEntity ->
                _expense.value = expenseEntity
            }
        }
    }

    suspend fun deleteExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            dao.deleteExpense(expenseEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddExpenseUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = addExpense(event.expenseEntity)
                        if (result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddExpenseUiEvent.OnUpdateExpenseClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = updateExpense(event.expenseEntity)
                        if (result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddExpenseUiEvent.OnDeleteExpenseClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = deleteExpense(event.expenseEntity)
                        if (result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddExpenseUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }

        }
    }
}

sealed class AddExpenseUiEvent : UiEvent() {
    data class OnAddExpenseClicked(val expenseEntity: ExpenseEntity) : AddExpenseUiEvent()
    data class OnUpdateExpenseClicked(val expenseEntity: ExpenseEntity) : AddExpenseUiEvent()
    data class OnDeleteExpenseClicked(val expenseEntity: ExpenseEntity) : AddExpenseUiEvent()
    object OnBackPressed : AddExpenseUiEvent()
}