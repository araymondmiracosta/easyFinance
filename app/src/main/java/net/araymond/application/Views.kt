package net.araymond.application

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.araymond.application.ui.theme.ApplicationTheme

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
                                        navHostController.navigate("Settings Activity")
                                    }
                                ) {
                                    Icon(Icons.Filled.Settings, null)
                                }
                            }
                        )
                    },
                    content = {
                        Surface(modifier = Modifier.padding(vertical = 65.dp, horizontal = 16.dp)) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(text = Build.VERSION.SDK_INT.toString() + " " + Build.VERSION_CODES.S.toString())
                                generateAccountScrollView()
                            }
                        }
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "New Transaction") },
                            icon = { Icon(Icons.Default.Add, "") },
                            elevation = FloatingActionButtonDefaults.elevation(50.dp),
                            onClick = { navHostController.navigate("New Transaction Activity") }
                        )
                    }
                )
            }
        }

        @Composable
        fun generateAccountCreationView(navHostController: NavHostController) {
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
                    Box() {
                        Text(text = account.getName(), style = TextStyle(fontSize = 18.sp))
                        Text(text = account.getBalance().toString(), style = TextStyle(fontSize = 16.sp))
                    }
                }
            }
        }

        @Composable
        fun generateTransactionScrollView() {
            for (account in Values.accounts) {
                for (i in account.getTransactions().size - 1 downTo -1 step -1) {

                }
            }
        }
    }
}