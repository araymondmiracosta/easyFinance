package net.araymond.application

import java.io.Serializable
import java.time.ZonedDateTime

/**
 * Represents a transaction on an account.
 *
 * @param category The category
 * @param description The description
 * @param amount The amount
 * @param utcDateTime The utc time of this transaction
 * @param accountName The account associated with this transaction
 */
class Transaction(
    var category: String,
    var description: String,
    var amount: Double,
    var utcDateTime: ZonedDateTime,
    var accountName: String
) : Serializable {
    /**
     * Assigns this transaction's data with the given data.
     *
     * @param category The category
     * @param description The description
     * @param amount The amount
     * @param utcDateTime The utc time of this transaction
     * @param account The account associated with this transaction
     */
    fun editTransaction(category: String, description: String, amount: Double,
                        utcDateTime: ZonedDateTime, account: String) {
        this.category = category
        this.description = description
        this.amount = amount
        this.accountName = account
        this.utcDateTime = utcDateTime
    }
}