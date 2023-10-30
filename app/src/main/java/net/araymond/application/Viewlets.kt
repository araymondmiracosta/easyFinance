package net.araymond.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import net.araymond.application.ui.theme.Green
import net.araymond.application.ui.theme.Red
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

/**
 * Contains functions which draw UI elements that are used in building screens (pages)
 */
object Viewlets: ComponentActivity() {

    /**
     * Creates a file picker dialog to select a CSV file to import transaction data from
     *
     * @param context The main context for this application
     */
    @Composable
    fun importCSVPathSelector(context: Context) {
        val contentResolver = LocalContext.current.contentResolver
        val filePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    contentResolver.openInputStream(uri)?.use {
                        Utility.readCSV(context, it)
                    }
                }
            }
        LaunchedEffect(Unit) {
            filePicker.launch(arrayOf("text/*"))
        }
    }

    /**
     * Creates a file picker dialog to select a CSV file to export transaction data to
     *
     */
    @Composable
    fun exportCSVPathSelector(onDismiss: () -> Unit) {
        val contentResolver = LocalContext.current.contentResolver
        val filePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use {
                        Utility.writeCSV(it)
                    }
                }
            }
        LaunchedEffect(Unit) {
            filePicker.launch("ledger.csv")
            onDismiss()
        }
    }

    /**
     * Draws a divider (thin line) used in the settings screen
     */
    @Composable
    fun settingsDivider() {
        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
    }

    /**
     * Draws a label (header) for a section, used in the settings screen
     */
    @Composable
    fun settingsLabel(label: String) {
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 15.sp
                ),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
    }

    /**
     * Draws a function customizable button, used in the settings screen
     *
     * @param title The title (header) of the button
     * @param text The small text below the title
     * @param onClick The function to execute when the button is pressed
     */
    @Composable
    fun settingsButton(title: String, text: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = true, onClick = onClick)
                .padding(top = 10.dp)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = text,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(bottom = 15.dp))
                }
            }
        }
    }

    /**
     * Draws a dialog to confirm something
     *
     * @param title The title of the dialog
     * @param message The message below the title
     *
     * @return If the user confirmed something
     */
    @Composable
    fun confirmDialog(title: String, message: String, onDismiss: () -> Unit, onConfirm: () -> Unit): Boolean {
        var optionValue by remember { mutableStateOf(false) }
        Dialog(
            onDismissRequest = {
                               onDismiss()
            },
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .padding(horizontal = 10.dp),
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(                                                   // TODO: Need to make text look better (smaller font, etc)
                        text = message,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .padding(horizontal = 10.dp),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Row(
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                onDismiss.invoke()
                            }
                        ) {
                            Text(
                                text = "Cancel",
                                style = LocalTextStyle.current.merge(color = MaterialTheme.colorScheme.primary),
                            )
                        }
                        TextButton(
                            onClick = {
                                optionValue = true
                                onConfirm.invoke()
                                onDismiss.invoke()
                            }
                        ) {
                            Text(
                                text = "OK",
                                style = LocalTextStyle.current.merge(color = MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                }
            }
        }
        return optionValue
    }

    @Composable
    fun dropdownDialog(currentIndex: Int, label: String, options: Array<String>, onDismiss: () -> Unit): Int {
        val value = options[currentIndex]
        var tempValue by remember { mutableStateOf(value) }
        var optionValue by remember { mutableStateOf(value) }

        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .padding(horizontal = 16.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    options.forEach { selectedOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        tempValue = selectedOption
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (tempValue == selectedOption),
                                onClick = {
                                    tempValue = selectedOption
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurface,
                                )
                            )
                            Text(
                                text = selectedOption,
                                style = LocalTextStyle.current.merge(color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                optionValue = tempValue
                                onDismiss.invoke()
                            }
                        ) {
                            Text(
                                text = "OK",
                                style = LocalTextStyle.current.merge(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
        return options.indexOf(tempValue)
    }

    /**
     * Time picker dialog used as it is not officially implemented at this time
     * Used from: https://stackoverflow.com/questions/75853449/timepickerdialog-in-jetpack-compose
     */
    @Composable
    fun TimePickerDialog(
        title: String = "Select Time",
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit,
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

    /**
     * Creates a carousel with clickable account tiles to access the account specific screen
     *
     * @param navHostController The main navHostController for this application
     */
    @Composable
    fun generateAccountScroller(navHostController: NavHostController) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            val preference = Utility.getPreference("accountSortingPreference")
            val currency = Utility.getPreference("currencyPreference")
            Utility.sortAccountListByPreference(Values.accountNames, preference).forEach{ accountName ->
                val accountTotal = Utility.getAccountTotal(accountName)
                Row {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clip(shape = RoundedCornerShape(10.dp))
                            .clickable(true, null, null, onClick = {
                                // Account specific screen
                                navHostController.navigate("Account Specific Activity/$accountName")
                            })
                            .padding(15.dp),
                    ) {
                        Text(
                            text = accountName,
                            style = TextStyle(
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.inverseSurface
                            )
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = Values.currencies[currency] + Values.balanceFormat.format(accountTotal),
                            style = TextStyle(fontSize = 19.sp, color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }
    }

    /**
     * Creates a scrollable list of the transactions in the given transaction list
     *
     * @param navHostController The main navHostController for this application
     * @param transactions The transaction list to iterate through
     * @param showRunningBalance If the running balance should be displayed for each transaction
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun generateTransactionScroller(navHostController: NavHostController, transactions: ArrayList<Transaction>, showRunningBalance: Boolean) {
        val dateFormatter = DateTimeFormatter.ofPattern(Values.dateFormat)
        val timeFormatter = DateTimeFormatter.ofPattern(Values.timeFormat)
        val currency = Utility.getPreference("currencyPreference")
        transactions.forEach { transaction ->
            val localDate = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalDate()
            val localTime = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalTime()
            Row(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = true, onClick = {
                        Values.currentTransaction = transaction
                        navHostController.navigate("View Transaction Activity")
                    })
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = transaction.category,  // category
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = transaction.accountName,     // account
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = localDate.format(dateFormatter) + " @ " + localTime.format(timeFormatter),     // date and time
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.surfaceTint
                        )
                    )
                }
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    if (transaction.amount < 0) {   // If amount is negative
                        Text(
                            text = "(" + Values.currencies[currency] + Values.balanceFormat.format(transaction.amount.absoluteValue) + ")",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Red
                            )
                        )
                    }
                    else {
                        Text(
                            text = Values.currencies[currency] + Values.balanceFormat.format(transaction.amount),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green
                            )
                        )
                    }
                    if (showRunningBalance) {
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(
                            text = Values.currencies[currency] + Values.balanceFormat.format(
                                Utility.calculateTransactionRunningBalance(
                                    transaction,
                                    Values.transactions
                                )
                            ),
                            style = TextStyle(
                                fontSize = 18.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
        }
   }
}