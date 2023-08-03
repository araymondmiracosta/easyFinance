package net.araymond.application

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

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
                            Column(modifier = Modifier.fillMaxSize()) {
                                generateAccountScrollView()
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
                                            navHostController.navigateUp()
                                            scope.launch {
                                                snackbarHostState.showSnackbar("New account saved")
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
                val year: Int
                val month: Int
                val day: Int
                val calendar = Calendar.getInstance()

                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
                calendar.time = Date()

                var stringDate by remember { mutableStateOf("") }

                var datePickerState = rememberDatePickerState()
                val confirmEnabled by derivedStateOf { datePickerState.selectedDateMillis != null }

                var openDatePickerDialog by remember { mutableStateOf(false) }

                val simpleDateFormat = SimpleDateFormat(Values.dateFormat)

                Scaffold(
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
                            }
                        )
                    },
                    content = {
                        Surface(modifier = Modifier.padding(vertical = 70.dp, horizontal = 16.dp)) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState()
                                )) {
                                Row {
                                    // Deposit or withdrawal
                                    Switch(
                                        checked = isPositiveTransaction,
                                        onCheckedChange = {
                                            isPositiveTransaction = it
                                        },
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 20.dp))
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
                                        Values.accountsNames.forEach { selectedOption ->
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
                                    modifier = Modifier.fillMaxWidth().onFocusChanged()  {
                                        if (it.isFocused) {
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
                                                    var milliseconds = datePickerState.selectedDateMillis
                                                    if (milliseconds != null) {
                                                        milliseconds += 86400000
                                                    }
                                                    stringDate = simpleDateFormat.format(milliseconds)
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
                            }
                        }
                    }
                )
            }
        }

        @Composable
        fun generateSettingsView(navHostController: NavHostController) {

        }

        @Composable
        fun generateAccountScrollView() {
            for (account in Values.accounts) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clip(shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .clickable(true, null, null, onClick = {
                                // Account specific screen
                            })
                            .padding(15.dp),
                    ) {
                        Text(text = account.name, style = TextStyle(fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(text = account.balance.toString(), style = TextStyle(fontSize = 19.sp))
                    }
                }
            }
        }

        @Composable
        fun generateTransactionScrollView() {
            for (account in Values.accounts) {
                for (i in account.transactions.size - 1 downTo -1 step -1) {   // i = account.getTransactions().size - 1; i >= -1; i--

                }
            }
        }
    }
}