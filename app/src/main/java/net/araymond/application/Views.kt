package net.araymond.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
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
import net.araymond.application.ui.theme.ApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    fun mainDraw(navHostController: NavHostController) {
        val scrollState = rememberScrollState()

        ApplicationTheme {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = Values.snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Finance")
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    navHostController.navigate("Settings Activity")
                                }
                            ) {
                                Icon(Icons.Filled.Settings, "Settings")
                            }
                        }
                    )
                },
                content = {
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 75.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)) {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                        ) {
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

            if (accountNameInput.isNotEmpty()) {
                accountBalance = Utility.getAccountTotal(accountName).toString()
                accountNameIsEmpty = false
                accountBalanceIsNotNumber = false
                title = "Edit Account"
            }

            if (deleteDialog) {
                if (Viewlets.confirmDialog("Delete Account", "Are you sure you want to delete this account? All transactions will be removed.")) {
                    if (Utility.removeAccount(context, accountNameInput)) {
                        navHostController.navigate("Main Activity")
                        Utility.showSnackbar("Account successfully deleted")
                    }
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
                            IconButton(
                                onClick = {
                                    if (!fieldEnabled) {
                                        navHostController.navigate("Main Activity")
                                    }
                                    else {
                                        navHostController.navigateUp()
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.ArrowBack, "")
                            }
                        },
                        actions = {
                            if (accountNameInput.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        deleteDialog = !deleteDialog
                                    }
                                ) {
                                    Icon(Icons.Filled.Delete, "Delete Account")
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
                    var nameCheck = true
                    if (accountName.isNotEmpty() && (!accountNameIsEmpty) && (!accountBalanceIsNotNumber)) {
                        Values.accountNames.forEach {
                            if (it.replace(" ", "") == accountName.replace(" ", "")) {
                                nameCheck = false
                            }
                        }
                    }
                    if (nameCheck) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Apply") },
                            icon = { Icon(Icons.Default.Check, "") },
                            onClick = {
                                val writeSuccess: Boolean
                                val snackbarMessage: String

                                fieldEnabled = false

                                if (accountNameInput.isNotEmpty()) {
                                    writeSuccess = Utility.changeAccountName(
                                        context,
                                        accountNameInput,
                                        accountName
                                    )
                                    snackbarMessage = "Account information saved"
                                } else {
                                    val openingTransaction = Transaction(
                                        "Opening deposit",
                                        "",
                                        accountBalance.toDouble(),
                                        ZonedDateTime.now(Values.UTCTimeZone),
                                        accountName
                                    )
                                    writeSuccess =
                                        Utility.newTransaction(openingTransaction, context)
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
                        )
                    }
                }
            )
        }
    }

    /**
     * Draws the transaction creating, viewing and editing screen
     *
     * @param navHostController The main navHostController for this application
     * @param context The main context for this application
     * @param transaction If not null, then displays the information for this transaction and
     *                      allows editing
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState",
        "CoroutineCreationDuringComposition"
    )
    @Composable
    fun generateNewTransactionView(navHostController: NavHostController, context: Context, transaction: Transaction?) {
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

            if (isTransfer) {
                transactionAccountLabel = "Transfer source account"
            }
            else {
                transactionAccountLabel = "Transaction account"
            }

            if (transaction != null) {  // Actual transaction object given as parameter, need to fill in vars
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
                    if(Viewlets.confirmDialog("Delete transaction", "Are you sure you want to delete this transaction?")) {
                        if (Utility.removeTransaction(transaction, context)) {
                            fieldEnabled = false
                            navHostController.navigateUp()
                            Utility.showSnackbar("Transaction removed")
                        }
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
                            IconButton(
                                onClick = {
                                    navHostController.navigateUp()
                                }
                            ) {
                                Icon(Icons.Filled.ArrowBack, "")
                            }
                        },
                        actions = {
                            if (transaction != null) {
                                if (fieldEnabled) {
                                    IconButton(
                                        onClick = {
                                            deleteDialog = !deleteDialog    // Known bug, after first attempt, user has to press button twice
                                        }
                                    ) {
                                        Icon(Icons.Filled.Delete, "Remove transaction")
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        fieldEnabled = !fieldEnabled
                                    }
                                ) {
                                    if (fieldEnabled) {
                                        Icon(Icons.Filled.Info, "View transaction")
                                    }
                                    else {
                                        Icon(Icons.Filled.Create, "Edit transaction")
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
                                    if (transaction == null) {
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
                                    if (transaction != null) {
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
                                                context
                                            )
                                            snackbarMessage = "New transfer added"
                                        }
                                        else {
                                            writeSuccess =
                                                Utility.newTransaction(newTransaction, context)
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
            if (createDialog) {
                Viewlets.exportCSVPathSelector()
            }
            if (openDialog && (Viewlets.confirmDialog("Import ledger", "Existing ledger information will be deleted. Are you sure you want to continue?"))) {
                Viewlets.importCSVPathSelector(context)
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
                    Surface(modifier = Modifier.padding(top = 50.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Viewlets.settingsLabel("Accounts")
                            Viewlets.settingsButton(
                                 "Add new account", ""
                            ) {
                                navHostController.navigate("New Account Activity")
                            }
                            Viewlets.settingsDivider()
                            Viewlets.settingsLabel("Preferences")
                            Utility.setPreference("currencyPreference",
                                Viewlets.settingsDropdown(
                                Utility.getPreference("currencyPreference"), "Currency", Values.currencies
                            ), context)
                            Utility.setPreference("accountSortingPreference",
                            Viewlets.settingsDropdown(
                                Utility.getPreference("accountSortingPreference"), "Account sorting"
                                ,Values.accountSortingOptions
                            ), context)
                            Viewlets.settingsDivider()
                            Viewlets.settingsLabel("Data")
                            Viewlets.settingsButton("Import ledger", "Import account and transaction data from a CSV file") {
                                openDialog = !openDialog
                            }
                            Viewlets.settingsButton("Export ledger", "Export account and transaction data to a CSV file") {
                                createDialog = !createDialog
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
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun generateAccountSpecificView(navHostController: NavHostController, accountName: String) {
        ApplicationTheme {
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
                            IconButton(
                                onClick = {
                                    navHostController.navigateUp()
                                }
                            ) {
                                Icon(Icons.Filled.ArrowBack, "")
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    navHostController.navigate("Edit Account Activity/$accountName")
                                }
                            ) {
                                Icon(Icons.Filled.Create, "Edit Account")
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
                                .verticalScroll(rememberScrollState())
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
                                        text = Values.currencies[Utility.getPreference("currencyPreference")] + Values.balanceFormat.format(Utility.getAccountTotal(accountName)),
                                        style = TextStyle(fontSize = 19.sp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(vertical = 15.dp))
                            Viewlets.generateTransactionScroller(navHostController, Utility.getAccountTransactions(accountName), true)
                        }
                    }
                }
            )
        }
    }
}