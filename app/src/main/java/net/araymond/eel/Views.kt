package net.araymond.eel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.araymond.eel.ui.theme.ApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

/**
 * Contains the primary UI screen drawing functions
 */
object Views {
    /**
     * Creates the main view (account carousel and transaction list)
     *
     * @param navHostController The main navHostController for this application
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")    // Shutup about padding warnings
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun mainDraw(navHostController: NavHostController, context: Context) {
        ApplicationTheme {
            val scrollState = rememberScrollState()
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                Utility.setTransactionSortingPreference(Viewlets.dropdownDialog(
                    currentIndex = Utility.getPreference("transactionSortingPreference"),
                    label = "Sort transactions",
                    options = Values.transactionSortingOptions,
                    onDismiss = {
                        showDialog = false
                    }
                ), context)
            }
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = Values.name)
                        },
                        actions = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Sort transactions")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        showDialog = true
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.List, "Sort transactions")
                                }
                            }
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "View asset ledger")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigate("Asset Activity")
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.SwapHoriz, "View asset ledger")
                                }
                            }
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Settings")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigate("Settings Activity")
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.Settings, "Settings")
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 65.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)) {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = ("${Values.currencies[Utility.getPreference("currencyPreference")]}${Values.balanceFormat.format(Values.total)}"),
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.padding(vertical = 12.dp))
                            Viewlets.generateAccountScroller(navHostController)
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            Viewlets.generateTransactionScroller(
                                navHostController,
                                Values.transactions, false
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (!scrollState.isScrollInProgress && Values.transactions.isNotEmpty()) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "New Transaction") },
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            icon = { Icon(Icons.Default.Add, "") },
                            onClick = { navHostController.navigate("New Transaction Activity") }
                        )
                    }
                }
            )
        }
    }

    /**
     * Draws the account creation, viewing and editing screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     * @param accountNameInput If not empty, then displays the information for this account and
     *                          allows editing
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun generateAccountCreationView(navHostController: NavHostController, context: Context, accountNameInput: String) {
        ApplicationTheme {
            var accountName by remember { mutableStateOf(accountNameInput) }
            var accountBalance by remember { mutableStateOf("")}
            var title = "New Account"
            val accountNameLabel by remember { mutableStateOf("Account name")}
            val accountBalanceLabel by remember { mutableStateOf("Account balance")}
            var accountNameIsEmpty = true
            var accountBalanceIsNotNumber = true
            var fieldEnabled = true
            var deleteDialog by remember { mutableStateOf(false) }
            var delete by remember { mutableStateOf (false) }

            if (accountNameInput.isNotEmpty()) {
                accountBalance = Utility.getAccountTotal(accountName, Values.transactions).toString()
                accountNameIsEmpty = false
                accountBalanceIsNotNumber = false
                title = "Edit Account"
            }

            if (deleteDialog) {
                Viewlets.confirmDialog(
                    "Delete Account",
                    "Are you sure you want to delete this account? All transactions will be removed.",
                    { deleteDialog = false },
                    { delete = true }
                )
            }
            if (delete) {
                if (Utility.removeAccount(accountNameInput, Values.transactions, context)) {
                    navHostController.navigate("Main Activity")
                    Utility.showSnackbar("Account successfully deleted")
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(title)
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        if (!fieldEnabled) {
                                            navHostController.navigate("Main Activity")
                                        } else {
                                            navHostController.navigateUp()
                                        }
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        },
                        actions = {
                            if (accountNameInput.isNotEmpty()) {
                                PlainTooltipBox(
                                    tooltip = {
                                        Text(style = Values.tooltipStyle, text = "Delete account")
                                    }
                                ) {
                                    IconButton(
                                        onClick = {
                                            delete = false
                                            deleteDialog = true
                                        },
                                        modifier = Modifier.tooltipAnchor()
                                    ) {
                                        Icon(Icons.Filled.Delete, "Delete account")
                                    }
                                }
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
                                readOnly = !fieldEnabled,
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
                            if (accountNameInput.isEmpty()) {
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                OutlinedTextField(
                                    readOnly = !fieldEnabled,
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
                    }
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Apply") },
                        icon = { Icon(Icons.Default.Check, "") },
                        onClick = {
                            var nameCheck = false
                            if (accountName.isNotEmpty() && (!accountNameIsEmpty) && (!accountBalanceIsNotNumber)) {
                                nameCheck = true
                                // Search through all existing account names and verify the
                                // new account name does not match any of them (strip whitespace)
                                Values.accountNames.forEach {
                                    if (it.replace(" ", "") == accountName.replace(" ", "")) {
                                        nameCheck = false
                                    }
                                }
                            }

                            if (nameCheck) {
                                val writeSuccess: Boolean
                                val snackbarMessage: String

                                fieldEnabled = false

                                if (accountNameInput.isNotEmpty()) {
                                    writeSuccess = Utility.changeAccountName(
                                        accountNameInput,
                                        accountName,
                                        Values.transactions,
                                        context
                                    )
                                    snackbarMessage = "Account information saved"
                                } else {
                                    val openingTransaction = Transaction(
                                        "Opening Deposit",
                                        "",
                                        accountBalance.toDouble(),
                                        ZonedDateTime.now(Values.UTCTimeZone),
                                        accountName
                                    )
                                    writeSuccess =
                                        Utility.newTransaction(openingTransaction, Values.transactions, context)
                                    snackbarMessage = "New account saved"
                                }
                                if (writeSuccess) {
                                    if (accountNameInput.isNotEmpty()) {
                                        navHostController.navigate("Main Activity")
                                    } else {
                                        navHostController.navigateUp()
                                    }
                                    Utility.showSnackbar(snackbarMessage)
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    /**
     * Draws the transaction creating, viewing and editing screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     * @param transactionID If viewing set to true, then specifies the transaction to view, as
     *                      given by its id
     * @param givenAccountName If not null, holds the account this transaction should be associated with
     * @param viewTransaction If this transaction should be opened in viewing mode
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState",
        "CoroutineCreationDuringComposition"
    )
    @Composable
    fun generateNewTransactionView(navHostController: NavHostController, context: Context, transactionID: Int, givenAccountName: String?, viewTransaction: Boolean) {
        val scrollState = rememberScrollState()

        ApplicationTheme {
            var isPositiveTransaction by remember { mutableStateOf(false) }
            var transactionAmount by remember { mutableStateOf("") }
            var transactionAmountIsNotNumber = true
            val transactionAmountLabel by remember { mutableStateOf("Transaction amount") }
            var transactionAccountLabel by remember { mutableStateOf("Transaction account") }
            var accountNameListIsExpanded by remember { mutableStateOf(false) }
            var accountName by remember { mutableStateOf("") }
            var categoryListIsExpanded by remember { mutableStateOf(false) }
            var category by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var title by remember { mutableStateOf("New Transaction") }
            var isTransfer by remember { mutableStateOf (false) }
            var accountNameTransferListIsExpanded by remember { mutableStateOf(false) }
            var accountNameTransfer by remember { mutableStateOf("") }

            var localDate = LocalDate.now() // Must initialize
            var localTime = LocalTime.now() // Must initialize

            var fieldEnabled by remember { mutableStateOf(false) }
            var deleteDialog by remember { mutableStateOf(false) }
            var delete by remember { mutableStateOf(false) }

            if (givenAccountName != null) {
                accountName = givenAccountName
            }

            if (isTransfer) {
                transactionAccountLabel = "Transfer source account"
            }
            else {
                transactionAccountLabel = "Transaction account"
            }

            val transaction: Transaction = if (viewTransaction) {
                Utility.getTransactionByHashCode(transactionID, Values.transactions)!!
            } else {
                // Avoid null error, should not be used if initialized here
                Transaction("", "", 0.0, ZonedDateTime.now(), "")
            }

            if (viewTransaction) {  // Actual transaction object given as parameter, need to fill in vars
                if (transaction.amount > 0) {
                    isPositiveTransaction = true
                }
                transactionAmount = "" + abs(transaction.amount)
                transactionAmountIsNotNumber = false
                accountName = transaction.accountName
                category = transaction.category
                description = transaction.description
                localDate = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalDate()
                localTime = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalTime()
                if (fieldEnabled) {
                    title = "Edit Transaction"
                }
                else {
                    title = "View Transaction"
                }
                if (deleteDialog) {     // If the user pressed the delete button, confirm
                    Viewlets.confirmDialog(
                        "Delete transaction",
                        "Are you sure you want to delete this transaction?",
                        { deleteDialog = false },
                        { delete = true }
                    )
                }
                if (delete) {
                    if (Utility.removeTransaction(transaction, Values.transactions, context)) {
                        fieldEnabled = false
                        navHostController.navigateUp()
                        Utility.showSnackbar("Transaction removed")
                    }
                }
            }
            else {
                fieldEnabled = true
            }

            val dateFormatter = DateTimeFormatter.ofPattern(Values.dateFormat)
            val timeFormatter = DateTimeFormatter.ofPattern(Values.timeFormat)

            var stringDate by remember { mutableStateOf(localDate.format(dateFormatter)) }
            var openDatePickerDialog by remember { mutableStateOf(false) }

            var openTimePickerDialog by remember { mutableStateOf(false) }
            var hour : Int
            var minute : Int
            var stringTime = localTime.format(timeFormatter)

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                                Text(title)
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        },
                        actions = {
                            if (viewTransaction) {
                                if (fieldEnabled) {
                                    PlainTooltipBox(
                                        tooltip = {
                                            Text(style = Values.tooltipStyle, text = "Remove transaction")
                                        }
                                    ) {
                                        IconButton(
                                            onClick = {
                                                delete = false
                                                deleteDialog = true
                                            },
                                            modifier = Modifier.tooltipAnchor()
                                        ) {
                                            Icon(Icons.Filled.Delete, "Remove transaction")
                                        }
                                    }
                                }
                                PlainTooltipBox(
                                    tooltip = {
                                        if (fieldEnabled) {
                                            Text(style = Values.tooltipStyle, text = "View transaction")
                                        }
                                        else {
                                            Text(style = Values.tooltipStyle, text = "Edit transaction")
                                        }
                                    }
                                ) {
                                    IconButton(
                                        onClick = {
                                            fieldEnabled = !fieldEnabled
                                        },
                                        modifier = Modifier.tooltipAnchor()
                                    ) {
                                        if (fieldEnabled) {
                                            Icon(Icons.Filled.Info, "View transaction")
                                        } else {
                                            Icon(Icons.Filled.Create, "Edit transaction")
                                        }
                                    }
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier.padding(top = 75.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    if (!isTransfer) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Deposit or withdrawal
                                            Text("Deposit ")
                                            Checkbox(
                                                checked = isPositiveTransaction,
                                                onCheckedChange = {
                                                    isPositiveTransaction = it
                                                },
                                                enabled = fieldEnabled
                                            )
                                        }
                                    }
//                                    Spacer(modifier = Modifier.padding(vertical = 3.dp))
                                    if (!viewTransaction) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Transfer")
                                            Checkbox(
                                                checked = isTransfer,
                                                onCheckedChange = {
                                                                  isTransfer = !isTransfer
                                                },
                                                enabled = fieldEnabled,
                                            )
                                        }
                                    }
                                }
                               Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                                // amount
                                OutlinedTextField(
                                    readOnly = !fieldEnabled,
                                    modifier = Modifier.fillMaxWidth(),
                                    value = transactionAmount,
                                    prefix = {
                                        if (!isTransfer) {
                                            if (!isPositiveTransaction) {
                                                Text("-", color = Color.Red)
                                            } else {
                                                Text("+", color = Color.Green)
                                            }
                                        }
                                    },
                                    suffix = {
                                        Text(Values.currencies[Utility.getPreference("currencyPreference")])
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
                                    },
                                )
                            }
                            Spacer(modifier = Modifier.padding(vertical = 8.dp))
                            // Account name
                            if (fieldEnabled) {
                                ExposedDropdownMenuBox(
                                    expanded = accountNameListIsExpanded,
                                    onExpandedChange = {
                                        accountNameListIsExpanded = !accountNameListIsExpanded
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = accountName,
                                        readOnly = !(fieldEnabled),
                                        onValueChange = {
                                            accountName = it
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        label = {
                                            Text(transactionAccountLabel)
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountNameListIsExpanded)
                                        },
                                        isError = accountName.isEmpty(),
                                    )
                                    ExposedDropdownMenu(
                                        expanded = accountNameListIsExpanded,
                                        onDismissRequest = {
                                            accountNameListIsExpanded = false
                                        }
                                    ) {
                                        Values.accountNames.forEach { selectedOption ->    // Issue: only most recent account name shown
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
                                if (isTransfer) {   // Transfer destination account
                                    Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = accountNameTransferListIsExpanded,
                                        onExpandedChange = {
                                            accountNameTransferListIsExpanded = !accountNameTransferListIsExpanded
                                        }
                                    ) {
                                        OutlinedTextField(
                                            value = accountNameTransfer,
                                            readOnly = !(fieldEnabled),
                                            onValueChange = {
                                                accountNameTransfer = it
                                            },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            label = {
                                                Text("Transfer destination account")
                                            },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountNameTransferListIsExpanded)
                                            },
                                            isError = accountNameTransfer.isEmpty() || (accountNameTransfer == accountName),
                                        )
                                        ExposedDropdownMenu(
                                            expanded = accountNameTransferListIsExpanded,
                                            onDismissRequest = {
                                                accountNameTransferListIsExpanded = false
                                            }
                                        ) {
                                            Values.accountNames.forEach { selectedOption ->    // Issue: only most recent account name shown
                                                DropdownMenuItem(onClick = {
                                                    accountNameTransfer = selectedOption
                                                    accountNameTransferListIsExpanded = false
                                                },
                                                    text = {
                                                        Text(selectedOption)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = accountName,
                                    onValueChange = {
                                        accountName = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text("Transaction account")
                                    },
                                    isError = accountName.isEmpty()
                                )
                            }
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            // Category
                            if (Values.categories.isNotEmpty() && fieldEnabled) {
                                ExposedDropdownMenuBox(
                                    expanded = categoryListIsExpanded,
                                    onExpandedChange = {
                                        categoryListIsExpanded = !categoryListIsExpanded
                                    }
                                ) {
                                    OutlinedTextField(
                                        readOnly = (!fieldEnabled),
                                        value = category,
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
                                    readOnly = (!fieldEnabled),
                                    value = category,
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
                                readOnly = (!fieldEnabled),
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
                                readOnly = true,
                                value = stringDate,
                                onValueChange = {
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged {
                                        if (it.isFocused && fieldEnabled) {             // onClick does not work, jerryrigged solution
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
                                                val milliseconds = datePickerState.selectedDateMillis as Long
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
                                readOnly = true,
                                value = stringTime,
                                onValueChange = {
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged {
                                        if (it.isFocused && fieldEnabled) {             // onClick does not work, jerryrigged solution
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
                                Viewlets.TimePickerDialog(
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
                    if (!scrollState.isScrollInProgress && fieldEnabled) {
                        var snackbarMessage: String
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Apply") },
                            icon = { Icon(Icons.Default.Check, "") },
                            onClick = {
                                // Check that input fields are valid
                                if ((!transactionAmountIsNotNumber) && (accountName.isNotEmpty()) && (stringDate.isNotEmpty()) && (stringTime.isNotEmpty())) {
                                    fieldEnabled = false
                                    val writeSuccess: Boolean
                                    val localTimeCorrectedToUTCTime = Utility.convertLocalDateTimeToUTC(    // Transactions store date and time in UTC
                                        ZonedDateTime.of(localDate, localTime, Values.localTimeZone))

                                    if (!isPositiveTransaction) {
                                        transactionAmount = "-$transactionAmount"
                                    }
                                    if (viewTransaction) {
                                        writeSuccess = Utility.editTransaction(transaction, context, category, description,
                                            transactionAmount.toDouble(), localTimeCorrectedToUTCTime, accountName)
                                        snackbarMessage = "Transaction changes saved"
                                    }
                                    else {  // New transaction
                                        val newTransaction = Transaction(category, description, transactionAmount.toDouble(), localTimeCorrectedToUTCTime, accountName)
                                        if (isTransfer) {
                                            writeSuccess = Utility.newTransfer(
                                                newTransaction,
                                                accountNameTransfer,
                                                Values.transactions,
                                                context
                                            )
                                            snackbarMessage = "New transfer added"
                                        }
                                        else {
                                            writeSuccess =
                                                Utility.newTransaction(newTransaction, Values.transactions, context)
                                            snackbarMessage = "New transaction added"
                                        }
                                    }
                                    if (writeSuccess) {
                                        navHostController.navigateUp()
                                        Utility.showSnackbar(snackbarMessage)
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }
    }

    /**
     * Draws the about screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun generateAboutView(navHostController: NavHostController, context: Context) {
        ApplicationTheme {
            var browserOpen by remember { mutableStateOf(false) }
            if (browserOpen) {
                LocalUriHandler.current.openUri(Values.sourceCodeLink)
                browserOpen = false
            }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "About")
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier.padding(top = 50.dp)) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_new_foreground),
                                contentDescription = "Application icon"
                            )
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            Viewlets.settingsButton(
                                title = "eel",
                                text = "EEL Easy Ledger",
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.padding(vertical = 5.dp))
                            Viewlets.settingsButton(
                                title = "Version",
                                text = Values.version,
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.padding(vertical = 5.dp))
                            Viewlets.settingsButton(
                                title = "Source Code",
                                text = Values.sourceCodeLink,
                                onClick = {
                                    browserOpen = true
                                }
                            )

//                            Text(
//                                text = "eel (EEL Easy Ledger)",
//                                style = TextStyle(
//                                    fontSize = 22.sp,
//                                    color = MaterialTheme.colorScheme.onSurface
//                                )
//                            )
//                            Spacer(modifier = Modifier.padding(vertical = 5.dp))
//                            Text(
//                                text = Values.version,
//                                style = TextStyle(
//                                    fontSize = 18.sp,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            )
                        }
                    }
                }
            )
        }
    }

    /**
     * Draws the settings screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun generateSettingsView(navHostController: NavHostController, context: Context) {
        ApplicationTheme {
            var createDialog by remember { mutableStateOf(false) }
            var openDialog by remember { mutableStateOf(false) }
            var currencyDialog by remember { mutableStateOf(false) }
            var accountSortingDialog by remember { mutableStateOf(false) }
            var confirm by remember { mutableStateOf(false) }
            var themeDialog by remember { mutableStateOf(false) }

            // Export ledger
            if (createDialog) {
                Viewlets.exportCSVPathSelector(
                    onDismiss = {
                        createDialog = false
                    }
                )
            }

            // Import ledger
            if (openDialog) {
                confirm = false
                Viewlets.confirmDialog(
                    "Import ledger",
                    "Existing ledger information will be deleted. Are you sure you want to continue?",
                    { openDialog = false },
                    { confirm = true }
                )
            }
            if (confirm) {
                Viewlets.importCSVPathSelector(context)
            }

            // Currency
            if (currencyDialog) {
                Utility.setPreference("currencyPreference", Viewlets.dropdownDialog(
                    currentIndex = Utility.getPreference("currencyPreference"),
                    label = "Currency",
                    options = Values.currencies,
                    onDismiss = { currencyDialog = false }
                ), context)
            }

            // Sort accounts
            if (accountSortingDialog) {
                Utility.setPreference("accountSortingPreference", Viewlets.dropdownDialog(
                    currentIndex = Utility.getPreference("accountSortingPreference"),
                    label = "Sort accounts",
                    options = Values.accountSortingOptions,
                    onDismiss = { accountSortingDialog = false }
                ), context)
            }

            // Theme
            if (themeDialog) {
                Utility.setPreference("themePreference", Viewlets.dropdownDialog(
                    currentIndex = Utility.getPreference("themePreference"),
                    label = "Theme",
                    options =  Values.themes,
                    onDismiss = { themeDialog = false }
                ), context)
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Settings")
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier.padding(top = 50.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Accounts
                            Viewlets.settingsLabel("Accounts")
                            // Add new account
                            Viewlets.settingsButton(
                                 "Add new account", ""
                            ) {
                                navHostController.navigate("New Account Activity")
                            }

                            Viewlets.settingsDivider()

                            // Assets
                            Viewlets.settingsLabel("Assets")
                            // Add new asset
                            Viewlets.settingsButton(
                                "Add new asset", ""
                            ) {
                                navHostController.navigate("New asset Activity")
                            }

                            Viewlets.settingsDivider()

                            // Preferences
                            Viewlets.settingsLabel("Preferences")
                            // Currency
                            Viewlets.settingsButton("Currency", Values.currencies[Utility.getPreference("currencyPreference")]) {
                                currencyDialog = true
                            }
                            // Sort accounts
                            Viewlets.settingsButton("Sort accounts", Values.accountSortingOptions[Utility.getPreference("accountSortingPreference")]) {
                                accountSortingDialog = true
                            }
                            // Theme
                            Viewlets.settingsButton("Theme", Values.themes[Utility.getPreference("themePreference")]) {
                                themeDialog = true
                            }

                            Viewlets.settingsDivider()

                            // Data
                            Viewlets.settingsLabel("Data")
                            // Import ledger
                            Viewlets.settingsButton("Import ledger", "Import account and transaction data from a CSV file") {
                                confirm = false
                                openDialog = true
                            }
                            // Export ledger
                            Viewlets.settingsButton("Export ledger", "Export account and transaction data to a CSV file") {
                                createDialog = true
                            }

                            Viewlets.settingsDivider()

                            // Miscellaneous
                            Viewlets.settingsLabel("Miscellaneous")
                            // About
                            Viewlets.settingsButton("About", "View information about this application") {
                                navHostController.navigate("About Activity")
                            }
                        }
                    }
                }
            )
        }
    }

    /**
     * Draws the account specific screen to show transactions specific to this account
     *
     * @param navHostController The main navHostController for this application
     * @param accountName The account to show information of
     * @param context The main context for this application
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun generateAccountSpecificView(navHostController: NavHostController, accountName: String, context: Context) {
        ApplicationTheme {
            val scrollState = rememberScrollState()
            var showDialog by remember { mutableStateOf(false) }
            var showChart by remember { mutableStateOf(false) }

            if (showDialog) {
                Utility.setTransactionSortingPreference(Viewlets.dropdownDialog(
                    currentIndex = Utility.getPreference("transactionSortingPreference"),
                    label = "Sort transactions",
                    options = Values.transactionSortingOptions,
                    onDismiss = {
                        showDialog = false
                    }
                ), context)
            }
            Scaffold(
                snackbarHost = {
                               SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "View Account")
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        },
                        actions = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "View chart")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        showChart = !showChart
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ShowChart, "View chart")
                                }
                            }
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Sort transactions")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        showDialog = true
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.List, "Sort transactions")
                                }
                            }
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Edit account")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigate("Edit Account Activity/$accountName")
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.Create, "Edit account")
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 75.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                        .fillMaxHeight()) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .padding(15.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        text = accountName,
                                        style = TextStyle(
                                            fontSize = 22.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    Text(
                                        text = Values.currencies[Utility.getPreference("currencyPreference")] + Values.balanceFormat.format(Utility.getAccountTotal(accountName, Values.transactions)),
                                        style = TextStyle(fontSize = 19.sp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            if (showChart) {
                                Viewlets.generateAccountGraph(accountName)
                                Spacer(modifier = Modifier.padding(vertical = 110.dp))
                            }
                            Viewlets.generateTransactionScroller(navHostController, Utility.sortTransactionListByPreference(Utility.getAccountTransactions(accountName, Values.transactions), Utility.getPreference("transactionSortingPreference")), true)
                        }
                    }
                },
                floatingActionButton = {
                    Log.d("", scrollState.isScrollInProgress.toString())
                    if (!scrollState.isScrollInProgress) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "New Transaction") },
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            icon = { Icon(Icons.Default.Add, "") },
                            onClick = { navHostController.navigate("New Transaction Activity/$accountName") }
                        )
                    }
                }
            )
        }
    }

    /**
     * Draws the asset creation, viewing and editing screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     * @param assetNameInput If not empty, then displays the information for this asset and
     *                          allows editing
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun generateAssetCreationView(navHostController: NavHostController, context: Context, assetNameInput: String) {
        ApplicationTheme {
            var assetName by remember { mutableStateOf(assetNameInput) }
            var assetValue by remember { mutableStateOf("")}
            var title = "New Asset"
            val assetNameLabel by remember { mutableStateOf("Asset name")}
            val assetValueLabel by remember { mutableStateOf("Asset value")}
            var assetNameIsEmpty = true
            var assetValueIsNotNumber = true
            var fieldEnabled = true
            var deleteDialog by remember { mutableStateOf(false) }
            var delete by remember { mutableStateOf (false) }

            if (assetNameInput.isNotEmpty()) {
                assetValue = Utility.getAccountTotal(assetName, Values.assetTransactions).toString()
                assetNameIsEmpty = false
                assetValueIsNotNumber = false
                title = "Edit Asset"
            }

            if (deleteDialog) {
                Viewlets.confirmDialog(
                    "Delete Asset",
                    "Are you sure you want to delete this asset?",
                    { deleteDialog = false },
                    { delete = true }
                )
            }
            if (delete) {
                if (Utility.removeAccount(assetNameInput, Values.assetTransactions, context)) {
                    navHostController.navigate("Main Activity")
                    Utility.showSnackbar("Asset successfully deleted")
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(title)
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        if (!fieldEnabled) {
                                            navHostController.navigate("Main Activity")
                                        } else {
                                            navHostController.navigateUp()
                                        }
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        },
                        actions = {
                            if (assetNameInput.isNotEmpty()) {
                                PlainTooltipBox(
                                    tooltip = {
                                        Text(style = Values.tooltipStyle, text = "Delete asset")
                                    }
                                ) {
                                    IconButton(
                                        onClick = {
                                            delete = false
                                            deleteDialog = true
                                        },
                                        modifier = Modifier.tooltipAnchor()
                                    ) {
                                        Icon(Icons.Filled.Delete, "Delete asset")
                                    }
                                }
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
                                readOnly = !fieldEnabled,
                                modifier = Modifier.fillMaxWidth(),
                                value = assetName,
                                singleLine = true,
                                isError = assetNameIsEmpty,
                                onValueChange = {
                                    assetName = it
                                    assetNameIsEmpty = it.isEmpty()
                                },
                                label = {
                                    Text(assetNameLabel)
                                }
                            )
                            if (assetNameInput.isEmpty()) {
                                Spacer(modifier = Modifier.padding(vertical = 15.dp))
                                OutlinedTextField(
                                    readOnly = !fieldEnabled,
                                    modifier = Modifier.fillMaxWidth(),
                                    value = assetValue,
                                    singleLine = true,
                                    isError = assetValueIsNotNumber,
                                    onValueChange = {
                                        assetValue = it
                                        assetValueIsNotNumber =
                                            !(it.toDoubleOrNull() != null && it.isNotEmpty())
                                    },
                                    label = {
                                        Text(assetValueLabel)
                                    }
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Apply") },
                        icon = { Icon(Icons.Default.Check, "") },
                        onClick = {
                            var nameCheck = false
                            if (assetName.isNotEmpty() && (!assetNameIsEmpty) && (!assetValueIsNotNumber)) {
                                nameCheck = true
                                // Search through all existing asset names and verify the
                                // new asset name does not match any of them (strip whitespace)
                                Values.assetNames.forEach {
                                    if (it.replace(" ", "") == assetName.replace(" ", "")) {
                                        nameCheck = false
                                    }
                                }
                            }

                            if (nameCheck) {
                                val writeSuccess: Boolean
                                val snackbarMessage: String

                                fieldEnabled = false

                                if (assetNameInput.isNotEmpty()) {
                                    writeSuccess = Utility.changeAccountName(
                                        assetNameInput,
                                        assetName,
                                        Values.assetTransactions,
                                        context
                                    )
                                    snackbarMessage = "Asset information saved"
                                } else {
                                    val openingTransaction = Transaction(
                                        "",
                                        "",
                                        assetValue.toDouble(),
                                        ZonedDateTime.now(Values.UTCTimeZone),
                                        assetName
                                    )
                                    writeSuccess =
                                        Utility.newTransaction(openingTransaction, Values.assetTransactions, context)
                                    snackbarMessage = "New asset saved"
                                }
                                if (writeSuccess) {
                                    if (assetNameInput.isNotEmpty()) {
                                        navHostController.navigate("Asset Activity")
                                    } else {
                                        navHostController.navigateUp()
                                    }
                                    Utility.showSnackbar(snackbarMessage)
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    /**
     * Draws the asset specific view
     *
     * @param navHostController The NavHostController for this application
     * @param context The context for this application
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun generateAssetSpecificView(navHostController: NavHostController, context: Context, assetName: String) {
        ApplicationTheme {
            val scrollState = rememberScrollState()
            // Sort transactions
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                Utility.setTransactionSortingPreference(Viewlets.dropdownDialog(
                    currentIndex = Utility.getPreference("transactionSortingPreference"),
                    label = "Sort transactions",
                    options = Values.transactionSortingOptions,
                    onDismiss = {
                        showDialog = false
                    }
                ), context)
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "View Asset")
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        },
                        actions = {
                            // Sort transactions
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Sort transactions")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        showDialog = true
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.List, "Sort transactions")
                                }
                            }
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Edit asset")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigate("Edit Asset Activity/$assetName")
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.Create, "Edit asset")
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 75.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                        .fillMaxHeight()) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .padding(15.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        text = assetName,
                                        style = TextStyle(
                                            fontSize = 22.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    Text(
                                        text = Values.currencies[Utility.getPreference("currencyPreference")] + Values.balanceFormat.format(Utility.getMostRecentTransaction(assetName, Values.assetTransactions).amount),
                                        style = TextStyle(fontSize = 19.sp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            Viewlets.generateAssetGraph(assetName)
                            Spacer(modifier = Modifier.padding(vertical = 110.dp))
                            Viewlets.generateAssetChangePointList(navHostController, Values.assetTransactions, assetName)
                        }
                    }
                },
                floatingActionButton = {
                    if (!scrollState.isScrollInProgress) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "New Change") },
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            icon = { Icon(Icons.Default.Add, "") },
                            onClick = { navHostController.navigate("New Asset Change Point Activity/$assetName") }
                        )
                    }
                }
            )
        }
    }

    /**
     * Draws the main asset view
     *
     * @param navHostController The NavHostController for this application
     * @param context The context for this application
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun generateAssetView(navHostController: NavHostController, context: Context) {
        ApplicationTheme {
            val scrollState = rememberScrollState()
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Assets")
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ){
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    }
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "Navigate up")
                                }
                            }
                        }
                    )
                }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 65.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = ("${Values.currencies[Utility.getPreference("currencyPreference")]}${Values.balanceFormat.format(Utility.calculateAssetTotal())}"),
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Spacer(modifier = Modifier.padding(vertical = 12.dp))
                        Viewlets.generateAssetScroller(navHostController)
                    }
                }
            }
        }
    }

    /**
     * Draws the asset change creating, viewing and editing screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     * @param transactionID The ID of the asset change point to view and/ or edit
     * @param assetName The name of the asset to add the change point to
     * @param viewChangePoint If the selected change point should be opened in viewing mode
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState",
        "CoroutineCreationDuringComposition"
    )
    @Composable
    fun generateNewAssetChangePointView(navHostController: NavHostController, context: Context, transactionID: Int, assetName: String, viewChangePoint: Boolean) {
        val scrollState = rememberScrollState()

        ApplicationTheme {
            var isPositiveTransaction by remember { mutableStateOf(true) }
            var transactionAmount by remember { mutableStateOf("") }
            var transactionAmountIsNotNumber = true
            val transactionAmountLabel by remember { mutableStateOf("Current value") }
            var description by remember { mutableStateOf("") }
            var title by remember { mutableStateOf("New Asset Change Point") }

            var localDate = LocalDate.now() // Must initialize
            var localTime = LocalTime.now() // Must initialize

            var fieldEnabled by remember { mutableStateOf(false) }
            var deleteDialog by remember { mutableStateOf(false) }
            var delete by remember { mutableStateOf(false) }

            val transaction: Transaction = if (viewChangePoint) {
                Utility.getTransactionByHashCode(transactionID, Values.assetTransactions)!!
            } else {
                // Avoid null error, should not be used if initialized here
                Transaction("", "", 0.0, ZonedDateTime.now(), "")
            }

            if (viewChangePoint) {  // Actual transaction object given as parameter, need to fill in vars
                if (transaction.amount > 0) {
                    isPositiveTransaction = true
                }
                transactionAmount = "" + abs(transaction.amount)
                transactionAmountIsNotNumber = false
                description = transaction.description
                localDate = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalDate()
                localTime = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalTime()
                if (fieldEnabled) {
                    title = "Edit change point"
                }
                else {
                    title = "View change point"
                }
                if (deleteDialog) {     // If the user pressed the delete button, confirm
                    Viewlets.confirmDialog(
                        "Delete change",
                        "Are you sure you want to delete this change point?",
                        { deleteDialog = false },
                        { delete = true }
                    )
                }
                if (delete) {
                    if (Utility.removeTransaction(transaction, Values.assetTransactions, context)) {
                        fieldEnabled = false
                        navHostController.navigateUp()
                        Utility.showSnackbar("Change point removed")
                    }
                }
            }
            else {
                fieldEnabled = true
            }

            val dateFormatter = DateTimeFormatter.ofPattern(Values.dateFormat)
            val timeFormatter = DateTimeFormatter.ofPattern(Values.timeFormat)

            var stringDate by remember { mutableStateOf(localDate.format(dateFormatter)) }
            var openDatePickerDialog by remember { mutableStateOf(false) }

            var openTimePickerDialog by remember { mutableStateOf(false) }
            var hour : Int
            var minute : Int
            var stringTime = localTime.format(timeFormatter)

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(title)
                        },
                        navigationIcon = {
                            PlainTooltipBox(
                                tooltip = {
                                    Text(style = Values.tooltipStyle, text = "Navigate up")
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        navHostController.navigateUp()
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(Icons.Filled.ArrowBack, "")
                                }
                            }
                        },
                        actions = {
                            if (viewChangePoint) {
                                if (fieldEnabled) {
                                    PlainTooltipBox(
                                        tooltip = {
                                            Text(style = Values.tooltipStyle, text = "Remove change point")
                                        }
                                    ) {
                                        IconButton(
                                            onClick = {
                                                delete = false
                                                deleteDialog = true
                                            },
                                            modifier = Modifier.tooltipAnchor()
                                        ) {
                                            Icon(Icons.Filled.Delete, "Remove change point")
                                        }
                                    }
                                }
                                PlainTooltipBox(
                                    tooltip = {
                                        if (fieldEnabled) {
                                            Text(style = Values.tooltipStyle, text = "View change point")
                                        }
                                        else {
                                            Text(style = Values.tooltipStyle, text = "Edit change point")
                                        }
                                    }
                                ) {
                                    IconButton(
                                        onClick = {
                                            fieldEnabled = !fieldEnabled
                                        },
                                        modifier = Modifier.tooltipAnchor()
                                    ) {
                                        if (fieldEnabled) {
                                            Icon(Icons.Filled.Info, "View change point")
                                        } else {
                                            Icon(Icons.Filled.Create, "Edit change point")
                                        }
                                    }
                                }
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier.padding(top = 75.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Deposit or withdrawal
                                        Text("Positive ")
                                        Checkbox(
                                            checked = isPositiveTransaction,
                                            onCheckedChange = {
                                                isPositiveTransaction = it
                                            },
                                            enabled = fieldEnabled
                                        )
                                    }
//                                    Spacer(modifier = Modifier.padding(vertical = 3.dp))
                                }
                                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                                // Amount
                                OutlinedTextField(
                                    readOnly = !fieldEnabled,
                                    modifier = Modifier.fillMaxWidth(),
                                    value = transactionAmount,
                                    suffix = {
                                        Text(Values.currencies[Utility.getPreference("currencyPreference")])
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
                                    },
                                    colors = if (isPositiveTransaction) OutlinedTextFieldDefaults.colors(Color.Green) else OutlinedTextFieldDefaults.colors(Color.Red)
                                )
                            }
                            Spacer(modifier = Modifier.padding(vertical = 8.dp))
                            // Description
                            OutlinedTextField(
                                readOnly = (!fieldEnabled),
                                modifier = Modifier.fillMaxWidth(),
                                value = description,
                                label = {
                                    Text("Change point description")
                                },
                                onValueChange = {
                                    description = it
                                }
                            )
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            // Date
                            OutlinedTextField(
                                readOnly = true,
                                value = stringDate,
                                onValueChange = {
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged {
                                        if (it.isFocused && fieldEnabled) {             // onClick does not work, jerryrigged solution
                                            openDatePickerDialog = true
                                        }
                                    },
                                label = {
                                    Text("Change point date")
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
                                                val milliseconds = datePickerState.selectedDateMillis as Long
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
                                readOnly = true,
                                value = stringTime,
                                onValueChange = {
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged {
                                        if (it.isFocused && fieldEnabled) {             // onClick does not work, jerryrigged solution
                                            openTimePickerDialog = true
                                        }
                                    },
                                label = {
                                    Text("Change point time")
                                },
                                isError = stringTime.isEmpty(),
                            )
                            if (openTimePickerDialog) {
                                val timePickerState = rememberTimePickerState(localTime.hour, localTime.minute)     // Need to set initial params here for hour of day in locale non-specific form
                                Viewlets.TimePickerDialog(
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
                    if (!scrollState.isScrollInProgress && fieldEnabled) {
                        var snackbarMessage: String
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Apply") },
                            icon = { Icon(Icons.Default.Check, "") },
                            onClick = {
                                // Check that input fields are valid
                                if ((!transactionAmountIsNotNumber) && (assetName.isNotEmpty()) && (stringDate.isNotEmpty()) && (stringTime.isNotEmpty())) {
                                    fieldEnabled = false
                                    val writeSuccess: Boolean
                                    val localTimeCorrectedToUTCTime = Utility.convertLocalDateTimeToUTC(    // Transactions store date and time in UTC
                                        ZonedDateTime.of(localDate, localTime, Values.localTimeZone))

                                    if (!isPositiveTransaction) {
                                        transactionAmount = "-$transactionAmount"
                                    }
                                    if (viewChangePoint) {
                                        writeSuccess = Utility.editTransaction(transaction, context, "", description,
                                            transactionAmount.toDouble(), localTimeCorrectedToUTCTime, assetName)
                                        snackbarMessage = "Change point modifications saved"
                                    }
                                    else {  // New transaction
                                        val newTransaction = Transaction("", description, transactionAmount.toDouble(), localTimeCorrectedToUTCTime, assetName)
                                        writeSuccess =
                                            Utility.newTransaction(newTransaction, Values.assetTransactions, context)
                                        snackbarMessage = "New change point added"
                                    }
                                    if (writeSuccess) {
                                        navHostController.navigateUp()
                                        Utility.showSnackbar(snackbarMessage)
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}