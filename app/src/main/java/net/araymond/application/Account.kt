package net.araymond.application

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

class Account(var name: String, var balance: Double) : Serializable {
    var transactions: ArrayList<Transaction> = ArrayList()

    fun newTransaction(
        category: String?,
        description: String?,
        amount: Double,
        date: LocalDate?,
        time: LocalTime?
    ) {
        transactions.add(Transaction(category!!, description!!, amount, date!!, time!!))
        balance += amount
    }

    fun removeTransaction(index: Int) {
        transactions.removeAt(index)
    }
}