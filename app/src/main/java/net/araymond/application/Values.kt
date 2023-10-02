package net.araymond.application

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import java.text.DecimalFormat
import java.time.ZoneId
import java.util.Locale

/**
 * Contains global values
 *
 * Some values are used as a dirty method of transferring data between navigation
 */
object Values {
    // Localization
    var language = "en"
    var country = "us"
    var locale = Locale(language, country)
    var dateFormat = "MM-dd-yyyy"
    var timeFormat = "HH:mm"
    var currency = "$"
    var balanceFormat = DecimalFormat("#,##0.00")

    // Time tracking constants
    var UTCTimeZone: ZoneId = ZoneId.of("UTC")
    var localTimeZone: ZoneId = ZoneId.systemDefault()  // local time zone for this device

    // Currency options
    var currencies = arrayOf("$", "€", "¥", "£")

    // Value arrays
    var transactions = ArrayList<Transaction>()     // Main transaction list
    var categories = ArrayList<String>()
    var accountNames = ArrayList<String>()

    // Variables to hold data between navigation
    lateinit var currentTransaction: Transaction

    // Global snackbar information
    lateinit var scope: CoroutineScope
    lateinit var snackbarHostState: SnackbarHostState

    // Used to avoid repeating same snackbar message
    var lastSnackbarMessage = ""
}