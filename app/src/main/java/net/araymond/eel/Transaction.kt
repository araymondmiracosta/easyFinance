package net.araymond.eel

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
        Values.lastTransactionID = this.hashCode()
    }

    /**
     * Returns true if this transaction equals another transaction
     *
     * @param other The other transaction to compare
     *
     * @return If this transaction equals the other one
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (category != other.category) return false
        if (description != other.description) return false
        if (amount != other.amount) return false
        if (utcDateTime != other.utcDateTime) return false
        if (accountName != other.accountName) return false

        return true
    }

    /**
     * Returns the unique ID for this transaction
     *
     * @return The id
     */
    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + utcDateTime.hashCode()
        result = 31 * result + accountName.hashCode()
        return result
    }
}