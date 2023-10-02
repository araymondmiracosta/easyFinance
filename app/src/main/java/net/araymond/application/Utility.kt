package net.araymond.application

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.time.ZonedDateTime
import java.util.Scanner

/**
 * Contains various functions used to modify, write read data, etc
 */
object Utility {

    /**
     * Populates Values.accountNames with the account names found in transactions
     */
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

    /**
     * Returns the balance of the given account
     *
     * @param accountName The account name
     *
     * @return Balance of the account
     */
    fun getAccountTotal(accountName: String): Double {
        var accountTotal = 0.0
        Values.transactions.forEach{ transaction ->
            if (transaction.accountName == accountName) {
                accountTotal += transaction.amount
            }
        }

        return accountTotal
    }

    /**
     * Populates Values.categories with the categories found in transactions
     */
    private fun readCategories() {
        Values.transactions.forEach{ transaction ->
            Values.categories.add(transaction.category)
        }

        val duplicatesRemoved: HashSet<String> = HashSet(Values.categories)
        Values.categories = ArrayList(duplicatesRemoved)
    }

    /**
     * Reads in saved ledger data from private app storage
     *
     * @param context The main context for this application
     *
     * @return If reading the transaction list succeeded
     */
    fun readLedgerSaveData(context: Context): Boolean {
        return try {
            val inputLedgerStream = context.openFileInput("ledger")
            val objectInputLedgerStream = ObjectInputStream(inputLedgerStream)
            // Need to resolve
            Values.transactions = objectInputLedgerStream.readObject() as ArrayList<Transaction>
            objectInputLedgerStream.close()
            inputLedgerStream.close()

            true
        } catch (exception: Exception) {
            false
        }
    }

    /**
     * Reads in the saved currency preference
     *
     * @param context The main context for this application
     *
     * @return If reading the currency preference succeeded
     */
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

    /**
     * Writes the given data object to the given file name
     *
     * @param data The data object to write out
     * @param file The file name to write to
     * @param context The main context for this application
     *
     * @return If writing the data succeeded
     */
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

    /**
     * Writes the ledger data to private app storage
     *
     * @param context The main context for this application
     *
     * @return If writing the transaction list succeeded
     */
    private fun writeLedgerData(context: Context): Boolean {
        return (writeSaveData(Values.transactions, "ledger", context))
    }

    /**
     * Writes the currency preference to private app storage
     *
     * @param context The main context for this application
     *
     * @return If writing the transaction list succeeded
     */
    fun writeCurrencyData(context: Context): Boolean {
        return (writeSaveData(Values.currency, "currency", context))
    }

    /**
     * Sorts the given transaction list in descending order (recent date first [0])
     *
     * @param list The transaction list to sort
     *
     * @return The sorted list
     */
    fun sortTransactionListDescendingOrder(list: ArrayList<Transaction>): ArrayList<Transaction> {
        return (list.sortedByDescending { it.utcDateTime }.toCollection(ArrayList()))
    }

    /**
     * Sorts the given transaction list in ascending order (recent date last [size - 1]
     *
     * @param list The transaction list to sort
     *
     * @return The sorted list
     */
    fun sortTransactionListAscendingOrder(list: ArrayList<Transaction>): ArrayList<Transaction> {
        return (list.sortedBy { it.utcDateTime }.toCollection(ArrayList()))
    }

    /**
     * Returns the running balance (balance of the account if no other transactions occurred after
     * the given one) of the transactions in transactionList on a per account basis.
     *
     * @param transaction The transaction to calculate the balance to
     * @param transactionList The transaction list to iterate through
     *
     * @return The running balance
     */
    fun calculateTransactionRunningBalance(transaction: Transaction, transactionList: ArrayList<Transaction>): Double {
        var currentRunningBalance = 0.0

        sortTransactionListAscendingOrder(transactionList).forEach {  // Have the oldest one first, so we can count from there
            if (transaction.accountName == it.accountName) {
                currentRunningBalance += it.amount
                if (it == transaction) {
                    return currentRunningBalance
                }
            }
        }

        return -1.0
    }

    /**
     * Function to call list populating functions
     */
    fun readAll() {
        readCategories()
        readAccounts()
    }

    /**
     * Adds the given transaction to the main transaction list.
     *
     * @param transaction The transaction to add
     * @param context The main context for this application
     *
     * @return If writing the transaction list succeeded
     */
    fun newTransaction(transaction: Transaction, context: Context): Boolean {
        Values.transactions.add(transaction)
        Values.transactions = sortTransactionListDescendingOrder(Values.transactions)
        readAll()
        return (writeLedgerData(context))
    }

    /**
     * Transfers the amount in the given transaction from its associated account to the given
     * destinationAccount.
     *
     * @param transaction The source transaction
     * @param destinationAccount The destination account to transfer to
     * @param context The main context for this application
     *
     * @return If writing the transaction list succeeded
     */
    fun newTransfer(transaction: Transaction, destinationAccount: String, context: Context) : Boolean {
        transaction.amount = (-1) * (kotlin.math.abs(transaction.amount))
        val destinationTransaction = newTransaction(Transaction(transaction.category,
            transaction.description, kotlin.math.abs(transaction.amount), transaction.utcDateTime,
            destinationAccount), context)
        return (destinationTransaction && newTransaction(transaction, context))
    }

    /**
     * Removes the given transaction from the main transaction list
     *
     * @param transaction The transaction to remove
     * @param context The main context for this application
     *
     * @return If writing the transaction list succeeded
     */
    fun removeTransaction(transaction: Transaction, context: Context): Boolean {
        Values.transactions.remove(transaction)
        readAll()
        return (writeLedgerData(context))
    }

    /**
     * Assigns the given transaction's data to the given data
     *
     * @param transaction The transaction to edit
     * @param context The main context for this application
     * @param category The category
     * @param description The description
     * @param amount The amount
     * @param utcDateTime The utc time of this transaction
     * @param accountName The account associated with this transaction
     *
     * @return If writing the transaction list succeeded
     */
    fun editTransaction(transaction: Transaction, context: Context, category: String,
                        description: String, amount: Double, utcDateTime: ZonedDateTime,
                        accountName: String): Boolean {
        transaction.editTransaction(category, description, amount, utcDateTime, accountName)
        readAll()
        return (writeLedgerData(context))
    }

    /**
     * Converts the UTC time in the given ZonedDateTime object to the local time of this device
     *
     * @param utcDateTime The ZonedDateTime object
     *
     * @return A new ZonedDateTime object containing the same time in the local time zone
     */
    fun convertUtcTimeToLocalDateTime(utcDateTime: ZonedDateTime): ZonedDateTime {
        return (utcDateTime.withZoneSameInstant(Values.localTimeZone))
    }

    /**
     * Converts the local time in the given ZonedDateTime object to UTC time
     *
     * @param localDateTime The ZonedDateTime object containing the local time of this device
     *
     * @return A new ZonedDateTime object containing the same time, but in UTC
     */
    fun convertLocalDateTimeToUTC(localDateTime: ZonedDateTime): ZonedDateTime {
        return (localDateTime.withZoneSameInstant(Values.UTCTimeZone))
    }

    /**
     * Returns a transaction list containing all transaction associated with a given account
     *
     * @param accountName The account
     *
     * @return A transaction list
     */
    fun getAccountTransactions(accountName: String): ArrayList<Transaction> {
        val accountTransactions = ArrayList<Transaction>()
        Values.transactions.forEach{ transaction->
            if (transaction.accountName == accountName) {
                accountTransactions.add(transaction)
            }
        }
        return accountTransactions
    }

    /**
     * Changes the account name on all transactions with oldAccountName to newAccountName
     *
     * @param context The main context for this application
     * @param oldAccountName The old account name to change
     * @param newAccountName The new account name to use
     *
     * @return If writing the transaction list succeeded
     */
    fun changeAccountName(context: Context, oldAccountName: String, newAccountName: String): Boolean {
        val accountTransactions = getAccountTransactions(oldAccountName)
        accountTransactions.forEach{ transaction ->
            transaction.editTransaction(transaction.category, transaction.description,
                transaction.amount, transaction.utcDateTime, newAccountName)
        }
        readAll()
        return (writeLedgerData(context))
    }

    /**
     * Removes all transactions from the given account (effectively deleting the account)
     *
     * @param context The main context for this account
     * @param accountName The account to be removed
     *
     * @return If writing the transaction list succeeded
     */
    fun removeAccount(context: Context, accountName: String): Boolean {
        var writeSucceed = true
        val accountTransactions = getAccountTransactions(accountName)
        accountTransactions.forEach{ transaction ->
            if (!(removeTransaction(transaction, context))) {
                writeSucceed = false
            }
        }
        return (writeSucceed)
    }

    /**
     * Shows a snackbar popup with the given message
     *
     * @param message The message to show on the snackbar
     */
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

    /**
     * Removes all transactions from the main transaction list
     *
     * @param context The main context of this application
     *
     * @return If writing the transaction list succeeded
     */
    fun clearTransactions(context: Context): Boolean {
        Values.transactions.clear()
        return (writeLedgerData(context))
    }

    /**
     * Writes a CSV formatted representation of the main transaction list
     *
     * @param outputStream An output stream to write to
     */
    fun writeCSV(outputStream: OutputStream) {
        val header = "date,category,description,amount,account\n"
        outputStream.write(header.toByteArray())
        sortTransactionListAscendingOrder(Values.transactions).forEach{ transaction ->
            val date = transaction.utcDateTime.toString()
            val category = transaction.category
            val description = transaction.description
            val amount = transaction.amount.toString()
            val account = transaction.accountName

            val line = "$date,$category,$description,$amount,$account\n"

            outputStream.write(line.toByteArray())
        }

        showSnackbar("Ledger data successfully exported")
    }

    /**
     * Populates the main transaction list from a CSV formatted representation
     *
     * @param context The main context of this application
     * @param inputStream The input stream to read from
     */
    fun readCSV(context: Context, inputStream: InputStream) {
        val scannerInput = Scanner(inputStream)
        var line = scannerInput.nextLine().split(",")
        val otherFormat = (line[0] == "id")     // Check for import format from another app
        val backup = ArrayList<Transaction>()    // Make backup in case
        Values.transactions.forEach {transaction ->
            backup.add(transaction)
        }

        if (scannerInput.hasNext()) {   // Clear transactions if the file checks out
            clearTransactions(context)
        }

        val dateIndex: Int
        val categoryIndex: Int
        val descriptionIndex: Int
        val amountIndex: Int
        val accountIndex: Int

        if (!otherFormat) {
            dateIndex = 0
            categoryIndex = 1
            descriptionIndex = 2
            amountIndex = 3
            accountIndex = 4
        }
        else {  // Foreign CSV import
            dateIndex = 8
            categoryIndex = 3
            descriptionIndex = 4
            amountIndex = 5
            accountIndex = 9
        }

        // Handle initial amount field from other apps

        val initialTransactionList = ArrayList<Transaction>()

        try {
            while (scannerInput.hasNext()) {
                line = scannerInput.nextLine().split(",")
                val category = line[categoryIndex]
                val description = line[descriptionIndex]
                val amount = line[amountIndex].toDouble()
                val date = ZonedDateTime.parse(line[dateIndex])
                val account = line[accountIndex]
                val newTransaction = Transaction(category, description, amount, date, account)
                var transactionFound = false

                // Handle foreign import
                if (otherFormat) {
                    // Handle initial amount
                    // If the account name is not in temp accounts
                    initialTransactionList.forEach {
                        if (account == it.accountName) {
                            transactionFound = true
                        }
                    }
                    if (!transactionFound) {
                        // Mark that the initial amount for this account has been recorded
                        initialTransactionList.add(
                            Transaction(
                                "Opening Deposit", "", line[10].toDouble(),
                                ZonedDateTime.now(), account
                            )
                        )
                    }

                    // Handle transfers, line[11] equals destination account
                    if (line[11] != "null") {
                        // Use Utility.newTransfer to create a new transaction object
                        newTransfer(newTransaction, line[11], context)
                    }
                    else {
                        newTransaction(newTransaction, context)
                    }
                }
                else {
                    newTransaction(newTransaction, context)
                }
            }

            // TODO: Fix initial amount code
            // Iterate through all accounts for initial transactions
            initialTransactionList.forEach { transaction ->
                // Oldest transaction of tempTransactionList is index 0
                val tempTransactionList = sortTransactionListAscendingOrder(
                    getAccountTransactions(transaction.accountName)
                )
                val openingDepositTransactionDate = (tempTransactionList[0].utcDateTime)
                    .minusMinutes(5)    // So running balance is correct

                transaction.utcDateTime = openingDepositTransactionDate

                newTransaction(transaction, context)
            }

            showSnackbar("Ledger data successfully imported")
        } catch (exception: Exception) {
            Values.transactions = backup
            showSnackbar("File corrupted, unable to import ledger data")
        }

        readAll()
        writeLedgerData(context)
    }
}