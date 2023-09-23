package net.araymond.application

import java.io.Serializable
import java.time.ZonedDateTime

class Transaction(
    var category: String,
    var description: String,
    var amount: Double,
    var utcDateTime: ZonedDateTime,
    var accountName: String
) : Serializable {
    fun editTransaction(category: String, description: String, amount: Double,
                        utcDateTime: ZonedDateTime, account: String) {
        this.category = category
        this.description = description
        this.amount = amount
        this.accountName = account
        this.utcDateTime = utcDateTime
    }
}