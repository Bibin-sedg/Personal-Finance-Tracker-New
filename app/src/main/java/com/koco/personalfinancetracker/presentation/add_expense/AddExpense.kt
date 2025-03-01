package com.koco.personalfinancetracker.presentation.add_expense

import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.koco.personalfinancetracker.R
import com.koco.personalfinancetracker.presentation.navigation.base.AddExpenseNavigationEvent
import com.koco.personalfinancetracker.presentation.navigation.base.NavigationEvent
import com.koco.personalfinancetracker.data.model.ExpenseEntity
import com.koco.personalfinancetracker.presentation.home.HomeViewModel
import com.koco.personalfinancetracker.presentation.ui.theme.Typography
import com.koco.personalfinancetracker.utils.NotificationHelper
import com.koco.personalfinancetracker.utils.Utils

@Composable
fun AddExpense(
    navController: NavController,
    isIncome: Boolean,
    transactionId: Int? = null,
    viewModel: AddExpenseViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val expense by viewModel.expense.collectAsState()
    val categoryBudgets by homeViewModel.categoryBudgets.collectAsState()
    val name = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val date = remember { mutableLongStateOf(0L) }
    val note = remember { mutableStateOf("") }


    val menuExpanded = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                AddExpenseNavigationEvent.MenuOpenedClicked -> {
                    menuExpanded.value = true
                }
                else->{}
            }
        }
    }
    LaunchedEffect(expense) {
        expense?.let {
            name.value = it.title
            amount.value = it.amount.toString()
            date.longValue = Utils.getMilliFromDate(it.date)
            note.value = it.note
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            ConstraintLayout(modifier = Modifier) {
                val (nameRow, card, topBar) = createRefs()
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                    Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Black),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable {
                                viewModel.onEvent(AddExpenseUiEvent.OnBackPressed)
                            })
                    Text(
                        text = "Add ${if (isIncome) "Budget" else "Expense"}",
                        style = Typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )

                }

                DataForm(modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },  onAddExpenseClick = { model ->
                    if (transactionId != null && transactionId != -1) {
                        viewModel.onEvent(AddExpenseUiEvent.OnUpdateExpenseClicked(model.copy(id = transactionId)))
                    } else {
                        viewModel.onEvent(AddExpenseUiEvent.OnAddExpenseClicked(model))
                    }
                }, isIncome = isIncome)



            }
            BudgetVsSpending(viewModel = homeViewModel)
        }

    }
}

@Composable
fun DataForm(
    modifier: Modifier,
    onAddExpenseClick: (model: ExpenseEntity) -> Unit,
    isIncome: Boolean
) {

    val name = remember {
        mutableStateOf("")
    }
    val amount = remember {
        mutableStateOf("")
    }
    val date = remember {
        mutableLongStateOf(0L)
    }
    val note = remember {
        mutableStateOf("")
    }
    val dateDialogVisibility = remember {
        mutableStateOf(false)
    }
    val type = remember {
        mutableStateOf(if (isIncome) "Budget" else "Expense")
    }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleComponent(title = "category")
        ExpenseDropDown(
             listOf(
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
            onItemSelected = {
                name.value = it
            })
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("amount")
        OutlinedTextField(
            value = amount.value,
            onValueChange = { newValue ->
                amount.value = newValue.filter { it.isDigit() || it == '.' }
            }, textStyle = TextStyle(color = Color.Black),
            visualTransformation = { text ->
                val out = "$" + text.text
                val currencyOffsetTranslator = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return offset + 1
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        return if (offset > 0) offset - 1 else 0
                    }
                }

                TransformedText(AnnotatedString(out), currencyOffsetTranslator)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text(text = if (isIncome) "Enter Budget" else "Amount") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("date")
        OutlinedTextField(value = if (date.longValue == 0L) "" else Utils.formatDateToHumanReadableForm(
            date.longValue
        ),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateDialogVisibility.value = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
            ),
            placeholder = { Text(text = "Select date") })
        Spacer(modifier = Modifier.size(24.dp))
        if (!isIncome) {
            TitleComponent("note")
            OutlinedTextField(
                value = note.value,
                onValueChange = { newValue ->
                    note.value = newValue
                }, textStyle = TextStyle(color = Color.Black),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = { Text(text = "Add Note") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                    disabledPlaceholderColor = Color.Black,
                    focusedTextColor = Color.Black,
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
        }

        Button(
            onClick = {
                val model = ExpenseEntity(
                    null,
                    name.value,
                    amount.value.toDoubleOrNull() ?: 0.0,
                    Utils.formatDateToHumanReadableForm(date.longValue),
                    type.value,
                    note.value
                )
                onAddExpenseClick(model)
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Add ${if (isIncome) "Budget" else "Expense"}",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
    if (dateDialogVisibility.value) {
        ExpenseDatePickDialog(onDateSelected = {
            date.longValue = it
            dateDialogVisibility.value = false
        }, onDismiss = {
            dateDialogVisibility.value = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePickDialog(
    onDateSelected: (date: Long) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L
    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = { onDateSelected(selectedDate) }) {
            Text(text = "Confirm")
        }
    }, dismissButton = {
        TextButton(onClick = { onDateSelected(selectedDate) }) {
            Text(text = "Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TitleComponent(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color.LightGray
    )
    Spacer(modifier = Modifier.size(10.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDropDown(listOfItems: List<String>, onItemSelected: (item: String) -> Unit) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val selectedItem = remember {
        mutableStateOf(listOfItems[0])
    }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
        OutlinedTextField(
            value = selectedItem.value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = TextStyle(fontFamily = FontFamily.Default, color = Color.Black),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

                )
        )
        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { }) {
            listOfItems.forEach {
                DropdownMenuItem(text = { Text(text = it) }, onClick = {
                    selectedItem.value = it
                    onItemSelected(selectedItem.value)
                    expanded.value = false
                })
            }
        }
    }
}

@Composable
fun BudgetVsSpending(viewModel: HomeViewModel) {
    val categoryBudgets by viewModel.categoryBudgets.collectAsState()
    val categorySpending by viewModel.categorySpending.collectAsState()
    val context = LocalContext.current
    val notifiedCategories = remember { mutableStateOf(mutableSetOf<String>()) }

    LaunchedEffect(categorySpending) {
        categorySpending.forEach { (category, spent) ->
            val budget = categoryBudgets[category] ?: 0.0

            when {
                spent > budget && category !in notifiedCategories.value -> {
                    NotificationHelper.NotificationHelper.sendNotification(
                        context,
                        "Over Budget!",
                        "⚠ You have exceeded your budget for $category!"
                    )
                    notifiedCategories.value.add(category)
                }
                spent >= budget * 0.9 && category !in notifiedCategories.value -> {
                    NotificationHelper.NotificationHelper.sendNotification(
                        context,
                        "Near Budget Limit",
                        "⚠ You are nearing your budget for $category!"
                    )
                    notifiedCategories.value.add(category)
                }
            }
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Budget vs. Spending", style = Typography.bodyLarge)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(categoryBudgets.keys.toList()) { category ->
                val budget = categoryBudgets[category] ?: 0.0
                val spent = categorySpending[category] ?: 0.0
                val remaining = budget - spent

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = category+":- ", style = Typography.headlineSmall)
                        Row {
                            Row {
                                Text(text = "Budget:  ")
                                Text(text = "${Utils.formatCurrency(budget)}  ", color = Color.Green)
                            }
                            Row {
                                Text(text = "-  Spent:  ")
                                Text(text = "${Utils.formatCurrency(spent)}", color = Color.Red)
                            }
                        }
                        Row{
                            Text(text = "Remaining: ")
                            Text(
                                text = "${Utils.formatCurrency(remaining)}",
                                color = if (remaining >= 0) Color.Green else Color.Red
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
