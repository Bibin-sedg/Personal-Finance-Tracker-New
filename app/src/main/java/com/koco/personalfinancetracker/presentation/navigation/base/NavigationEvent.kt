package com.koco.personalfinancetracker.presentation.navigation.base

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
}

sealed class AddExpenseNavigationEvent : NavigationEvent() {
    object MenuOpenedClicked : AddExpenseNavigationEvent()
}

sealed class HomeNavigationEvent : NavigationEvent() {
    object NavigateToAddExpense : HomeNavigationEvent()
    object NavigateToAddBudget : HomeNavigationEvent()
    object NavigateToSeeAll : HomeNavigationEvent()
}