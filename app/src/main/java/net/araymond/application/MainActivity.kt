package net.araymond.application

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            initialize()
            applicationNavHost(this)
        }
    }

    private fun initialize() {
        if (Utility.readLedgerSaveData(this)) {
            Log.d("INFO", "Ledger data read successfully")
        }
        if (Utility.readCurrencySaveData(this)) {
            Log.d("INFO", "Currency preference read successfully")
        }
        Utility.readAccounts()
        Utility.readCategories()
    }

    @Composable
    fun applicationNavHost(context: Context) {
        val navHostController = rememberNavController()
        NavHost(navController = navHostController, startDestination = "Main Activity") {
            composable("Main Activity") {
                Views.mainDraw(navHostController)
            }
            composable("New Account Activity") {
                Views.generateAccountCreationView(navHostController, context)
            }
            composable("New Transaction Activity") {
                Views.generateNewTransactionView(navHostController, context)
            }
            composable("Settings Activity") {
                Views.generateSettingsView(navHostController, context)
            }
        }
    }
}