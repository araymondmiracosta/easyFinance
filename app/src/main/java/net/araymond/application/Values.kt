package net.araymond.application

import java.text.DecimalFormat
import java.util.Locale

object Values {
    var total = 0
    var language = "en"
    var country = "us"
    var dateFormat = "MM-dd-yyyy"
    var timeFormat = "HH:mm"
    var currency = "$"
    var currencies = arrayOf("$", "€", "¥", "£")
    var transactions = ArrayList<Transaction>()
    var categories = ArrayList<String>()
    var accountNames = ArrayList<String>()
    var locale = Locale(language, country)
    var balanceFormat = DecimalFormat("##.00")
    lateinit var currentTransaction: Transaction
}