package com.koco.personalfinancetracker.presentation.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.koco.personalfinancetracker.R
import com.koco.personalfinancetracker.presentation.add_expense.AddExpenseUiEvent
import com.koco.personalfinancetracker.presentation.add_expense.AddExpenseViewModel
import com.koco.personalfinancetracker.presentation.add_expense.ExpenseDropDown
import com.koco.personalfinancetracker.presentation.home.HomeViewModel
import com.koco.personalfinancetracker.presentation.home.TransactionItem
import com.koco.personalfinancetracker.utils.Utils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel(), expenseViewModel: AddExpenseViewModel = hiltViewModel()) {
    val state = viewModel.expenses.collectAsState(initial = emptyList())
    var filterType by remember { mutableStateOf("All") }
    var categoryType by remember { mutableStateOf("All") }
    var dateRange by remember { mutableStateOf("All Time") }
    var menuExpanded by remember { mutableStateOf(false) }

    val filteredTransactions = when (filterType) {
        "Expense" -> state.value.filter { it.type == "Expense" }
        "Budget" -> state.value.filter { it.type == "Budget" }
        else -> state.value
    }

    val categoryFilteredTransactions = if (categoryType == "All") {
        filteredTransactions
    } else {
        filteredTransactions.filter { it.title == categoryType }
    }

    val finalFilteredTransactions = categoryFilteredTransactions.filter { transaction ->
        true
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                // Back Button
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )

                // Title
                Text(
                    text = "Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                // Three Dots Menu
                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { menuExpanded = !menuExpanded },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Content area for the transaction list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    // Dropdowns
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }),
                        exit = slideOutVertically(targetOffsetY = { -it  }),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            // Type Filter Dropdown
                            ExpenseDropDown(
                                listOfItems = listOf("All", "Expense", "Budget"),
                                onItemSelected = { selected ->
                                    filterType = selected
                                    menuExpanded = false // Close menu after selection
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ExpenseDropDown(
                                listOf(
                                    "All",
                                    "Grocery",
                                    "Bills",
                                    "Food",
                                    "Shopping",
                                    "Transport",
                                    "Dining Out",
                                    "Entertainment",
                                    "Education",
                                    "Debt Payments",
                                    "Other Expenses"
                                ),
                                onItemSelected = { selected ->
                                    categoryType = selected
                                    menuExpanded = false
                                })
                        }
                    }
                }
                items(finalFilteredTransactions) { item ->
                    TransactionItem(
                        title = item.title,
                        amount = item.amount.toString(),
                        date = item.date,
                        note = item.note,
                        color = if (item.type == "Budget") Color.Green else Color.Red,
                        onUpdate = { navController.navigate("/add_exp/${item.id}")},
                        onDelete = { expenseViewModel.onEvent(AddExpenseUiEvent.OnDeleteExpenseClicked(item)) },
                        modifier = Modifier.animateItemPlacement(tween(100))
                    )
                }
            }
        }
    }
}