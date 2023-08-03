package net.araymond.application

import android.content.Context
import android.os.Bundle
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
        Utility.readSaveData(this)
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
                Views.generateSettingsView(navHostController)
            }
        }
    }
}