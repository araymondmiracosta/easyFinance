package net.araymond.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.araymond.application.ui.theme.ApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class Views {
    companion object {
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")    // Shutup about padding warnings
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun mainDraw(navHostController: NavHostController) {
            ApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "Finance")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        navHostController.navigate("New Account Activity")
                                    }
                                ) {
                                    Icon(Icons.Filled.Settings, null)
                                }
                            }
                        )
                    },
                    content = {
                        Surface(modifier = Modifier.padding(vertical = 75.dp, horizontal = 16.dp)) {
                            Column {
                                generateAccountScrollView()
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                generateTransactionScrollView()
                            }
                        }
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "New Transaction") },
                            icon = { Icon(Icons.Default.Add, "") },
                            onClick = { navHostController.navigate("New Transaction Activity") }
                        )
                    }
                )
            }
        }

        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun generateAccountCreationView(navHostController: NavHostController, context: Context) {
            ApplicationTheme {
                var accountName by remember { mutableStateOf("")}
                var accountBalance by remember { mutableStateOf("")}
                var accountNameLabel by remember { mutableStateOf("Account name")}
                var accountBalanceLabel by remember { mutableStateOf("Account balance")}
                var accountNameIsEmpty = true
                var accountBalanceIsNotNumber = true
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                var nameCheck = true
                Scaffold(
                    snackbarHost = {
                                   SnackbarHost(hostState = snackbarHostState)
                    },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "New Account")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    }
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        )
                    },
                    content = {
                        Surface(modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 70.dp, horizontal = 16.dp)) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = accountName,
                                    singleLine = true,
                                    isError = accountNameIsEmpty,
                                    onValueChange = {
                                        accountName = it
                                        accountNameIsEmpty = it.isEmpty()
                                    },
                                    label = {
                                        Text(accountNameLabel)
                                    }
                                )
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = accountBalance,
                                    singleLine = true,
                                    isError = accountBalanceIsNotNumber,
                                    onValueChange = {
                                        accountBalance = it
                                        accountBalanceIsNotNumber =
                                            !(it.toDoubleOrNull() != null && it.isNotEmpty())
                                    },
                                    label = {
                                        Text(accountBalanceLabel)
                                    }
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Apply") },
                            icon = { Icon(Icons.Default.Check, "") },
                            onClick = {
                                if ((!accountNameIsEmpty) && (!accountBalanceIsNotNumber)) {
                                    for (account in Values.accounts) {
                                        if (account.name == accountName) {
                                            scope.launch {
                                                nameCheck = false
                                                snackbarHostState.showSnackbar("An account with that name already exists.")
                                            }
                                        }
                                    }
                                    if (nameCheck) {
                                        Values.accounts.add(
                                            Account(
                                                accountName,
                                                accountBalance.toDouble()
                                            )
                                        )
                                        if (Utility.writeSaveData(context)) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("New account saved", duration = SnackbarDuration.Short)
                                                navHostController.navigateUp()
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
        @Composable
        fun generateNewTransactionView(navHostController: NavHostController, context: Context) {
            ApplicationTheme {
                var isPositiveTransaction by remember { mutableStateOf(false) }
                var transactionAmount by remember { mutableStateOf("") }
                var transactionAmountIsNotNumber = true
                var transactionAmountLabel by remember { mutableStateOf("Transaction amount") }
                var accountNameListIsExpanded by remember { mutableStateOf(false) }
                var accountName by remember { mutableStateOf("") }
                var categoryListIsExpanded by remember { mutableStateOf(false) }
                var category by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }

                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                var localDate = LocalDate.now() // Must initialize
                var localTime = LocalTime.now() // Must initialize

                var dateFormatter = DateTimeFormatter.ofPattern(Values.dateFormat)
                var timeFormatter = DateTimeFormatter.ofPattern(Values.timeFormat)

                var stringDate by remember { mutableStateOf(localDate.format(dateFormatter)) }
                var openDatePickerDialog by remember { mutableStateOf(false) }

                var openTimePickerDialog by remember { mutableStateOf(false) }
                var hour : Int
                var minute : Int
                var stringTime = localTime.format(timeFormatter)

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("New Transaction")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    }
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            },
                        )
                    },
                    content = {
                        Surface(modifier = Modifier.padding(vertical = 70.dp, horizontal = 16.dp)) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState()
                                )) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Deposit or withdrawal
                                    Text("Deposit")
                                    Checkbox(
                                        checked = isPositiveTransaction,
                                        onCheckedChange = {
                                            isPositiveTransaction = it
                                        },
                                    )
                                   Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                                    // amount
                                    OutlinedTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = transactionAmount,
                                        prefix = {
                                            if (!isPositiveTransaction) {
                                                Text("-", color = Color.Red)
                                            } else {
                                                Text("+", color = Color.Green)
                                            }
                                        },
                                        suffix = {
                                            Text(Values.currency)
                                        },
                                        singleLine = true,
                                        isError = transactionAmountIsNotNumber,
                                        onValueChange = {
                                            transactionAmount = it
                                            transactionAmountIsNotNumber =
                                                !(it.toDoubleOrNull() != null && it.isNotEmpty())
                                        },
                                        label = {
                                            Text(transactionAmountLabel)
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                // Account name
                                ExposedDropdownMenuBox(
                                    expanded = accountNameListIsExpanded,
                                    onExpandedChange = {
                                        accountNameListIsExpanded = !accountNameListIsExpanded
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = accountName,
                                        readOnly = true,
                                        onValueChange = {
//                                            accountName = it
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        label = {
                                            Text("Transaction account")
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountNameListIsExpanded)
                                        },
                                        isError = accountName.isEmpty()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = accountNameListIsExpanded,
                                        onDismissRequest = {
                                            accountNameListIsExpanded = false
                                        }
                                    ) {
                                        Values.accountsNames.forEach { selectedOption ->    // Issue: only most recent account name shown
                                            DropdownMenuItem(onClick = {
                                                accountName = selectedOption
                                                accountNameListIsExpanded = false
                                            },
                                                text = {
                                                    Text(selectedOption)
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                // Category
                                if (Values.categories.isNotEmpty()) {
                                    ExposedDropdownMenuBox(
                                        expanded = categoryListIsExpanded,
                                        onExpandedChange = {
                                            categoryListIsExpanded = !categoryListIsExpanded
                                        }
                                    ) {
                                        OutlinedTextField(
                                            value = category,
                                            readOnly = false,
                                            onValueChange = {
                                                category = it
                                            },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            label = {
                                                Text("Transaction category")
                                            },
                                            trailingIcon = {
                                                if (Values.categories.isNotEmpty()) {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = categoryListIsExpanded
                                                    )
                                                }
                                            },
                                            isError = category.isEmpty()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = categoryListIsExpanded,
                                            onDismissRequest = {
                                                categoryListIsExpanded = false
                                            }
                                        ) {
                                            Values.categories.forEach { selectedOption ->
                                                DropdownMenuItem(onClick = {
                                                    category = selectedOption
                                                    categoryListIsExpanded = false
                                                },
                                                    text = {
                                                        Text(selectedOption)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                else {
                                    OutlinedTextField(
                                        value = category,
                                        readOnly = false,
                                        onValueChange = {
                                            category = it
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = {
                                            Text("Transaction category")
                                        },
                                        isError = category.isEmpty()
                                    )
                                }
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                // Description
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = description,
                                    label = {
                                        Text("Transaction description")
                                    },
                                    onValueChange = {
                                        description = it
                                    }
                                )
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                // Date
                                OutlinedTextField(
                                    value = stringDate,
                                    readOnly = true,
                                    onValueChange = {
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged() {
                                            if (it.isFocused) {             // onClick does not work, jerryrigged solution
                                                openDatePickerDialog = true
                                            }
                                        },
                                    label = {
                                        Text("Transaction date")
                                    },
                                    isError = stringDate.isEmpty(),
                                )
                                if (openDatePickerDialog) {
                                    val datePickerState = rememberDatePickerState()
                                    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
                                    DatePickerDialog(
                                        onDismissRequest = {
                                                           openDatePickerDialog = false
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    openDatePickerDialog = false
                                                    var milliseconds = datePickerState.selectedDateMillis as Long
                                                    localDate = Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1) // Add one day to fix android bug
                                                    stringDate = localDate.format(dateFormatter)
                                                },
                                                content = {
                                                          Text("OK")
                                                },
                                                enabled = confirmEnabled.value
                                            )
                                        },
                                        dismissButton = {
                                            TextButton(
                                                onClick = {
                                                    openDatePickerDialog = false
                                                }
                                            ) {
                                                Text("Cancel")
                                            }
                                        }
                                    ) {
                                        DatePicker(state = datePickerState)
                                    }
                                }
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                // Time
                                OutlinedTextField(
                                    value = stringTime,
                                    readOnly = true,
                                    onValueChange = {
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged() {
                                            if (it.isFocused) {             // onClick does not work, jerryrigged solution
                                                openTimePickerDialog = true
                                            }
                                        },
                                    label = {
                                        Text("Transaction time")
                                    },
                                    isError = stringTime.isEmpty(),
                                )
                                if (openTimePickerDialog) {
                                    val timePickerState = rememberTimePickerState(localTime.hour, localTime.minute)     // Need to set initial params here for hour of day in locale non-specific form
                                    Utility.TimePickerDialog(
                                        onDismissRequest = {
                                                           openTimePickerDialog = false
                                        },
                                        onConfirm = {
                                            openTimePickerDialog = false
                                            hour = timePickerState.hour
                                            minute = timePickerState.minute
                                            localTime = LocalTime.of(hour, minute)
                                            stringTime = localTime.format(timeFormatter)
                                        },
                                    ) {
                                        TimePicker(state = timePickerState)
                                    }
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Apply") },
                            icon = { Icon(Icons.Default.Check, "") },
                            onClick = {
                                // Check that input fields are valid
                                if ( (!transactionAmountIsNotNumber) && (accountName.isNotEmpty()) && (stringDate.isNotEmpty()) && (stringTime.isNotEmpty()) ) {
                                    if (!isPositiveTransaction) {
                                        transactionAmount = "-$transactionAmount"
                                    }
                                    Values.accounts[Utility.indexFromName(accountName)].newTransaction(category, description, transactionAmount.toDouble(), localDate, localTime)
                                    if (Utility.writeSaveData(context)) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("New transaction added!", duration = SnackbarDuration.Short)
                                            navHostController.navigateUp()
                                        }
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }

        @Composable
        fun generateSettingsView(navHostController: NavHostController) {

        }

        @Composable
        fun generateAccountScrollView() {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                for (account in Values.accounts) {
                    Row {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clip(shape = RoundedCornerShape(10.dp))
                                .clickable(true, null, null, onClick = {
                                    // Account specific screen
                                })
                                .padding(15.dp),
                        ) {
                            Text(
                                text = account.name,
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = Values.balanceFormat.format(account.balance),
                                style = TextStyle(fontSize = 19.sp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                }
            }
        }

        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @Composable
        fun generateTransactionScrollView() {
            Scaffold {
                var dateFormatter = DateTimeFormatter.ofPattern(Values.dateFormat)
                var timeFormatter = DateTimeFormatter.ofPattern(Values.timeFormat)
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()
                ) {
                    for (account in Values.accounts) {
                        for (transaction in account.transactions.reversed()) {   // i = account.getTransactions().size - 1; i >= -1; i--
                            Row(
                                modifier = Modifier.clip(shape = RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
                                    .clickable(enabled = true, onClick = {
                                        // transaction specific screen
                                    })
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = transaction.category,  // category
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                    Spacer(modifier = Modifier.padding(2.dp))
                                    Text(
                                        text = account.name,     // account
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Spacer(modifier = Modifier.padding(2.dp))
                                    Text(
                                        text = transaction.date.format(dateFormatter) + " @ " + transaction.time.format(timeFormatter),     // date
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.surfaceTint
                                        )
                                    )
                                }
                                Spacer(Modifier.weight(1f).fillMaxWidth())
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    if (transaction.amount < 0) {   // If amount is negative
                                        Text(
                                            text = "(" + Values.currency + Values.balanceFormat.format(transaction.amount.absoluteValue) + ")",
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = net.araymond.application.ui.theme.Red
                                            )
                                        )
                                    }
                                    else {
                                        Text(
                                            text = Values.currency + Values.balanceFormat.format(transaction.amount),
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = net.araymond.application.ui.theme.Green
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.padding(15.dp))
                                }
                            }
                            Spacer(modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }
        }
    }
}