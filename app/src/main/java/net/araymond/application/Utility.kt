package net.araymond.application

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.launch
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.time.ZonedDateTime

object Utility {

    private fun readAccounts() {
        Values.accountNames = ArrayList()
        var duplicate = false
        Values.transactions.forEach{ transaction ->
            Values.accountNames.forEach { accountName ->
                if (accountName == transaction.accountName) {
                    duplicate = true
                }
            }
            if (duplicate) {
                duplicate = false
            }
            else {
                Values.accountNames.add(transaction.accountName)
            }
        }
    }

    fun getAccountTotal(accountName: String): Double {
        var accountTotal = 0.0
        Values.transactions.forEach{ transaction ->
            if (transaction.accountName == accountName) {
                accountTotal += transaction.amount
            }
        }

        return accountTotal
    }

    private fun readTransactions() {
        Values.transactions = sortTransactionListByRecentDateFirst(Values.transactions)
    }

    private fun readCategories() {
        Values.transactions.forEach{ transaction ->
            Values.categories.add(transaction.category)
        }

        val duplicatesRemoved: HashSet<String> = HashSet(Values.categories)
        Values.categories = ArrayList(duplicatesRemoved)
    }

    fun readLedgerSaveData(context: Context): Boolean {
        return try {
            val inputLedgerStream = context.openFileInput("ledger")
            val objectInputLedgerStream = ObjectInputStream(inputLedgerStream)
            Values.transactions = objectInputLedgerStream.readObject() as ArrayList<Transaction>
            objectInputLedgerStream.close()
            inputLedgerStream.close()

            true
        } catch (exception: Exception) {
            false
        }
    }

    fun readCurrencySaveData(context: Context): Boolean {
        return try {
            val inputCurrencyStream = context.openFileInput("currency")
            val objectInputCurrencyStream = ObjectInputStream(inputCurrencyStream)
            Values.currency = objectInputCurrencyStream.readObject() as String
            objectInputCurrencyStream.close()
            inputCurrencyStream.close()

            true
        } catch (exception: Exception) {
            false
        }
    }

    private fun writeSaveData(data: Any, file: String, context: Context): Boolean {
        return try {
            val outputStream = context.openFileOutput(file, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(data)
            outputStream.flush()
            outputStream.close()
            objectOutputStream.close()

            true
        } catch (exception: Exception) {
            false
        }
    }

    private fun writeLedgerData(context: Context): Boolean {
        return (writeSaveData(Values.transactions, "ledger", context))
    }

    fun writeCurrencyData(context: Context): Boolean {
        return (writeSaveData(Values.currency, "currency", context))
    }

    private fun sortTransactionListByRecentDateFirst(list: ArrayList<Transaction>): ArrayList<Transaction> {
        return (list.sortedByDescending { it.utcDateTime }.toCollection(ArrayList()))
    }

    private fun sortTransactionListByRecentDateLast(list: ArrayList<Transaction>): ArrayList<Transaction> {
        return (list.sortedBy { it.utcDateTime }.toCollection(ArrayList()))
    }

    fun calculateTransactionRunningBalance(transaction: Transaction, transactionList: ArrayList<Transaction>): Double {
        var currentRunningBalance = 0.0

        sortTransactionListByRecentDateLast(transactionList).forEach {  // Have the oldest one first, so we can count from there
            if (transaction.accountName == it.accountName) {
                currentRunningBalance += it.amount
                if (it == transaction) {
                    return currentRunningBalance
                }
            }
        }

        return -1.0
    }

    fun readAll() {
        readTransactions()
        readCategories()
        readAccounts()
    }

    fun newTransaction(transaction: Transaction, context: Context): Boolean {
        Values.transactions.add(transaction)
        Values.transactions = sortTransactionListByRecentDateFirst(Values.transactions)
        readAll()
        return (writeLedgerData(context))
    }

    fun removeTransaction(transaction: Transaction, context: Context): Boolean {
        Values.transactions.remove(transaction)
        readAll()
        return (writeLedgerData(context))
    }

    fun editTransaction(transaction: Transaction, context: Context, category: String,
                        description: String, amount: Double, utcDateTime: ZonedDateTime,
                        accountName: String): Boolean {
        transaction.editTransaction(category, description, amount, utcDateTime, accountName)
        readAll()
        return (writeLedgerData(context))
    }

    fun convertUtcTimeToLocalDateTime(utcDateTime: ZonedDateTime): ZonedDateTime {
        return (utcDateTime.withZoneSameInstant(Values.localTimeZone))
    }

    fun convertLocalDateTimeToUTC(localDateTime: ZonedDateTime): ZonedDateTime {
        return (localDateTime.withZoneSameInstant(Values.UTCTimeZone))
    }

    fun getAccountTransactions(accountName: String): ArrayList<Transaction> {
        var accountTransactions = ArrayList<Transaction>()
        Values.transactions.forEach{ transaction->
            if (transaction.accountName == accountName) {
                accountTransactions.add(transaction)
            }
        }
        return accountTransactions
    }

    fun changeAccountName(context: Context, oldAccountName: String, newAccountName: String): Boolean {
        var accountTransactions = getAccountTransactions(oldAccountName)
        accountTransactions.forEach{ transaction ->
            transaction.editTransaction(transaction.category, transaction.description,
                transaction.amount, transaction.utcDateTime, newAccountName)
        }
        readAll()
        return (writeLedgerData(context))
    }

    fun removeAccount(context: Context, accountName: String): Boolean {
        var writeSucceed = true
        var accountTransactions = getAccountTransactions(accountName)
        accountTransactions.forEach{ transaction ->
            if (!(removeTransaction(transaction, context))) {
                writeSucceed = false
            }
        }
        return (writeSucceed)
    }

    fun showSnackbar(message: String) {
        Log.d("Snackbar", Values.lastSnackbarMessage)
        Log.d("Snackbar", message)
        if (message.compareTo(Values.lastSnackbarMessage) != 0) {
            Values.lastSnackbarMessage = message
            Values.scope.launch {
                Values.snackbarHostState.showSnackbar(
                    message = message, duration = SnackbarDuration.Short
                )
            }
        }
    }

    fun writeCSV(outputStream: OutputStream) {
        val header = "date,category,description,amount,account\n"
        outputStream.write(header.toByteArray())
        Values.transactions.forEach{ transaction ->
            val date = transaction.utcDateTime.toString().substring(0,
                transaction.utcDateTime.toString().length - 5)  // Remove redundant [UTC] tag
            val category = transaction.category
            val description = transaction.description
            val amount = transaction.amount.toString()
            val account = transaction.accountName

            val line = "$date,$category,$description,$amount,$account\n"

            outputStream.write(line.toByteArray())
        }
    }
}