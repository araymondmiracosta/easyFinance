package net.araymond.application

import com.google.android.material.timepicker.TimeFormat
import java.text.DecimalFormat
import java.util.Locale

object Values {
    var total = 0
    var timeScheme = TimeFormat.CLOCK_24H
    var language = "en"
    var country = "us"
    var dateFormat = "MM-dd-yyyy"
    var timeFormat = "HH:mm"
    var currency = "$"
    @JvmField
    var accounts = ArrayList<Account>()
    @JvmField
    var categories = ArrayList<String>()
    @JvmField
    var accountsNames = ArrayList<String>()
    var locale = Locale(language, country)
    var balanceFormat = DecimalFormat("##.00")
}