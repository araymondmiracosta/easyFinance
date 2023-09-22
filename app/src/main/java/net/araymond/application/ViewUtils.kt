package net.araymond.application

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

object ViewUtils {

    @Composable
    fun settingsDivider() {
        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
    }

    @Composable
    fun settingsLabel(label: String, firstLabel: Boolean) {
        if (!firstLabel) {
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }
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
    fun settingsButton(text: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = true, onClick = onClick)
                .padding(bottom = 15.dp)
                .padding(top = 15.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 17.sp
                )
            )
        }
    }

    @Composable
    fun confirmDialog(label: String): Boolean {
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
                        Text(                                                   // TODO: Need to make text look better (smaller font, etc)
                            text = label,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = 16.dp),
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Row {
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
                .padding(top = 15.dp)
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
                        Row {
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
}