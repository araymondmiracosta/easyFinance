package net.araymond.application

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object Utility {

    /**
     * TimePickerDialog is not officially implemented.
     * https://stackoverflow.com/questions/75853449/timepickerdialog-in-jetpack-compose
     */
    @Composable
    fun TimePickerDialog(
        title: String = "Select Time",
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit,
        toggle: @Composable () -> Unit = {},
        content: @Composable () -> Unit,
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
            ) {
                toggle()
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                        style = MaterialTheme.typography.labelMedium
                    )
                    content()
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onDismissRequest
                        ) { Text("Cancel") }
                        TextButton(
                            onClick = onConfirm
                        ) { Text("OK") }
                    }
                }
            }
        }
    }

    fun indexFromName(accountName: String): Int {
        for (i in Values.accounts.indices) {
            if (accountName.compareTo(Values.accounts[i].name) == 0) {
                return i
            }
        }
        return -1
    }

    fun readAccounts() {
        for (account in Values.accounts) {
            Values.accountsNames = ArrayList()
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