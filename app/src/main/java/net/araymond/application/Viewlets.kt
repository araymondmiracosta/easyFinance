package net.araymond.application

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

object Viewlets {

    @Composable
    fun settingsDivider() {
        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
    }

    @Composable
    fun settingsLabel(label: String, firstLabel: Boolean) {
//        if (!firstLabel) {
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
//        }
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
                    Spacer(modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }

    @Composable
    fun confirmDialog(title: String, message: String): Boolean {
        var dialogIsOpen by remember { mutableStateOf(true) }
        var optionValue by remember { mutableStateOf(false) }
        if (dialogIsOpen) {
            Dialog(
                onDismissRequest = {
                    dialogIsOpen = false
                },
            ) {
                Surface {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
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
                            )
                        )
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            Spacer(Modifier.weight(1f))
                            TextButton(
                                onClick = {
                                   dialogIsOpen = false
                                }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = {
                                    optionValue = true
                                    dialogIsOpen = false
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
           }
        }
        return optionValue
    }

    @Composable
    fun settingsDropdown(value: String, label: String, options: Array<String>): String {
        var dialogIsOpen by remember { mutableStateOf(false) }
        var tempValue by remember { mutableStateOf(value) }
        var optionValue by remember { mutableStateOf(value) }
        Column(
            modifier = Modifier
                .clickable(enabled = true,
                    onClick = {
                        dialogIsOpen = true
                    }
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 17.sp,
                ),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            Text(
                text = optionValue,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
        }
        if (dialogIsOpen) {
            Dialog(
                onDismissRequest = {
                    dialogIsOpen = false
                },
            ) {
                Surface {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
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
                                )
                                Text(selectedOption)
                            }
                        }
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            Spacer(Modifier.weight(1f))
                            TextButton(
                                onClick = {
                                    dialogIsOpen = false
                                }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = {
                                    optionValue = tempValue
                                    dialogIsOpen = false
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
        return optionValue
    }

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

    @Composable
    fun generateAccountScroller(navHostController: NavHostController) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Values.accountNames.forEach{ accountName ->
                var accountTotal = Utility.getAccountTotal(accountName)
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = Values.currency + Values.balanceFormat.format(accountTotal),
                            style = TextStyle(fontSize = 19.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun generateTransactionScroller(navHostController: NavHostController, transactions: ArrayList<Transaction>, showRunningBalance: Boolean) {
        var dateFormatter = DateTimeFormatter.ofPattern(Values.dateFormat)
        var timeFormatter = DateTimeFormatter.ofPattern(Values.timeFormat)
        transactions.forEach {transaction ->
            var localDate = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalDate()
            var localTime = Utility.convertUtcTimeToLocalDateTime(transaction.utcDateTime).toLocalTime()
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            text = "(" + Values.currency + Values.balanceFormat.format(transaction.amount.absoluteValue) + ")",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Red
                            )
                        )
                    }
                    else {
                        Text(
                            text = Values.currency + Values.balanceFormat.format(transaction.amount),
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
                            text = Values.currency + Values.balanceFormat.format(
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