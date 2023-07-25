package net.araymond.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.araymond.application.ui.theme.ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            ApplicationTheme {
                applicationNavHost()
//            }
        }
    }

    @Composable
    fun applicationNavHost() {
        val navHostController = rememberNavController()
        NavHost(navController = navHostController, startDestination = "Main Activity") {
            composable("Main Activity") {
                Views.mainDraw(navHostController)
            }
            composable("New Account Activity") {
                Views.generateAccountCreationView(navHostController)
            }
            composable("New Transaction Activity") {
                Views.generateNewTransactionView(navHostController)
            }
            composable("Settings Activity") {
                Views.generateSettingsView(navHostController)
            }
        }
    }
}