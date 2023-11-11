package net.araymond.eel

import android.os.Build
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import java.text.DecimalFormat
import java.time.ZoneId

/**
 * Contains global values
 *
 * Some values are used as a dirty method of transferring data between navigation
 */
object Values {
    // Localization
    var dateFormat = "MM-dd-yyyy"
    var timeFormat = "HH:mm"
    var balanceFormat = DecimalFormat("#,##0.00")

    // Time tracking constants
    var UTCTimeZone: ZoneId = ZoneId.of("UTC")
    var localTimeZone: ZoneId = ZoneId.systemDefault()  // local time zone for this device

    // Preferences
    var preferences: MutableMap<String, Int> = mutableMapOf(
        "currencyPreference" to 0,
        "accountSortingPreference" to 0,
        "transactionSortingPreference" to 1,
        "themePreference" to 0
    )

    val legacyThemes = arrayOf(
        "Nebula", "Bright"
    )

    const val name = "eel"
    const val version = "11 November 2023"
    const val sourceCodeLink = "https://www.nebulacentre.net/projects/eel.git"

    val themes =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                "System default", "Material You (Dark)", "Material You (Light)", "Nebula", "Bright"
            )
        }
        else {
            legacyThemes
        }

    // Currency options
    val currencies = arrayOf("$", "€", "¥", "£")

    // Sorting options
    val accountSortingOptions = arrayOf(
        "Name (ascending)", "Name (descending)", "Amount (ascending)", "Amount (descending)",
        "Transaction date (ascending)", "Transaction date (descending)"
    )
    val transactionSortingOptions = arrayOf(
        "Date (ascending)", "Date (descending)", "Amount (ascending)", "Amount (descending)"
    )

    // Value arrays
    var transactions = ArrayList<Transaction>()     // Main transaction list
    var categories = ArrayList<String>()
    var accountNames = ArrayList<String>()

    // Net value
    var total: Double = 0.0

    // Variables to hold data between navigation
    lateinit var currentTransaction: Transaction

    // Global snackbar information
    lateinit var scope: CoroutineScope
    lateinit var snackbarHostState: SnackbarHostState

    // Used to avoid repeating same snackbar message
    var lastSnackbarMessage = ""
}