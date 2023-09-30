package net.araymond.application

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import java.text.DecimalFormat
import java.time.ZoneId
import java.util.Locale

object Values {
    var total = 0
    var language = "en"
    var country = "us"
    var dateFormat = "MM-dd-yyyy"
    var timeFormat = "HH:mm"
    var UTCTimeZone = ZoneId.of("UTC")
    var localTimeZone = ZoneId.systemDefault()
    var currency = "$"
    var currencies = arrayOf("$", "€", "¥", "£")
    var transactions = ArrayList<Transaction>()
    var categories = ArrayList<String>()
    var accountNames = ArrayList<String>()
    var locale = Locale(language, country)
    var balanceFormat = DecimalFormat("#,##0.00")
    lateinit var currentTransaction: Transaction
    lateinit var scope: CoroutineScope
    lateinit var snackbarHostState: SnackbarHostState
    var lastSnackbarMessage = ""
}