package net.araymond.application

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch

/* TODO
- Rewrite Utility class
 */

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
                var accountNameLabel by remember { mutableStateOf("Account Name (name must not be empty)")}
                var accountBalanceLabel by remember { mutableStateOf("Account Balance (balance must be a number)")}
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
                        Surface(modifier = Modifier.padding(vertical = 75.dp, horizontal = 16.dp)) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = accountName,
                                    singleLine = true,
                                    isError = accountNameIsEmpty,
                                    onValueChange = {
                                        accountName = it
                                        if (it.isEmpty()) {
                                            accountNameIsEmpty = true
                                            accountNameLabel = "Account Name (name must not be empty)"
                                        }
                                        else {
                                            accountNameIsEmpty = false
                                            accountNameLabel = "Account Name"
                                        }
                                    },
                                    label = {
                                        Text(accountNameLabel)
                                    }
                                )
                                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = accountBalance,
                                    singleLine = true,
                                    isError = accountBalanceIsNotNumber,
                                    onValueChange = {
                                        accountBalance = it
                                        if (it.toDoubleOrNull() != null && it.isNotEmpty()) {
                                            accountBalanceIsNotNumber = false
                                            accountBalanceLabel = "Account Balance"
                                        }
                                        else {
                                            accountBalanceIsNotNumber = true
                                            accountBalanceLabel = "Account Balance (balance must be a number)"
                                        }
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

        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @Composable
        fun generateNewTransactionView(navHostController: NavHostController) {
            ApplicationTheme {
                Scaffold {

                }
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
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)).clip(shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth().clickable(true, null, null, onClick = {
                                // Account specific screen
                            }).padding(15.dp),
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