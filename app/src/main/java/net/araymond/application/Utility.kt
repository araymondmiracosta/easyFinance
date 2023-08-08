package net.araymond.application

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object Utility {

    fun indexFromName(accountName: String): Int {
        for (i in Values.accounts.indices) {
            if (accountName.compareTo(Values.accounts[i].name) == 0) {
                return i
            }
        }
        return -1
    }

    fun readAccounts() {
        Values.accountsNames = ArrayList()
        for (account in Values.accounts) {
            Values.accountsNames.add(account.name)
        }
    }

    fun readCategories() {
        var transactions: ArrayList<Transaction>
        for (account in Values.accounts) {
            transactions = account.transactions
            for (transaction in transactions) {
                Values.categories.add(transaction.category)
            }
        }
        val duplicatesRemoved: HashSet<String> = HashSet(Values.categories)
        Values.categories = ArrayList(duplicatesRemoved)
    }

    fun readSaveData(context: Context): Boolean {
        return try {
            val inputStream = context.openFileInput("ledger")
            val objectInputStream = ObjectInputStream(inputStream)
            Values.accounts = objectInputStream.readObject() as ArrayList<Account>
            objectInputStream.close()
            inputStream.close()
            true
        } catch (exception: Exception) {
            false
        }
    }

    fun writeSaveData(context: Context): Boolean {
        return try {
            val outputStream = context.openFileOutput("ledger", Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(Values.accounts)
            readAccounts()
            outputStream.flush()
            outputStream.close()
            objectOutputStream.close()
            true
        } catch (exception: Exception) {
            false
        }
    }
}