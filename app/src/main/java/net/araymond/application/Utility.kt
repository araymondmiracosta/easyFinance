package net.araymond.application

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object Utility {

    fun indexFromName(accountName: String): Int {
        var i = 0
        Values.accounts.forEach {
            if (accountName.compareTo(it.name) == 0) {
                return i
            }
            i++
        }
        return -1
    }

    fun readAccounts() {
        Values.accountsNames = ArrayList()
        for (account in Values.accounts) {
            Values.accountsNames.add(account.name)
        }
    }

    fun readTransactions() {
        Values.transactions.clear()
        Values.accounts.forEach{ account ->     // Iterate through accounts
            account.balance = 0.0
            account.transactions.forEach{ transaction ->    // Iterate through transactions on this account
                account.balance += transaction.amount
                Values.transactions.add(transaction)    // Add this transaction to master transactions list
            }
        }
        Values.transactions = sortTransactionListByDate(Values.transactions)
    }

    fun readCategories() {
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
            Values.accounts = objectInputLedgerStream.readObject() as ArrayList<Account>
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

    fun writeLedgerData(context: Context): Boolean {
        return (writeSaveData(Values.accounts, "ledger", context))
    }

    fun writeCurrencyData(context: Context): Boolean {
        return (writeSaveData(Values.currency, "currency", context))
    }

    private fun sortTransactionListByDate(list: ArrayList<Transaction>): ArrayList<Transaction> {
        return (list.sortedByDescending { it.localDateTime }.toCollection(ArrayList()))
    }

    fun calculateTransactionRunningBalance(transaction: Transaction): Double {
        val transactionAccount = Values.accounts[indexFromName(transaction.account.name)]
        val accountTransactions = sortTransactionListByDate(transactionAccount.transactions).reversed()
        var currentRunningBalance = 0.0

        accountTransactions.forEach {
            currentRunningBalance += it.amount
            if (it == transaction) {
                return currentRunningBalance
            }
        }

        return -1.0
    }
}