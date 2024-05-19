package com.agworks.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agworks.jettip.components.InputField
import com.agworks.jettip.ui.theme.JetTipTheme
import com.agworks.jettip.utils.calculateTip
import com.agworks.jettip.utils.calculateTotalPerPerson
import com.agworks.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column {
                    MainContent()
                }

            }
        }
    }
}


@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}


@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(15.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$ ${"%.2f".format(totalPerPerson)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}



@Composable
fun MainContent() {
    BillForm { billAMt ->
        Log.d("Amount", "Main Content ${billAMt.toInt() * 100}")
    }
}


@Composable
fun BillForm(
    onValChange: (String) -> Unit = {
    }
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val splitPeopleState = remember {
        mutableIntStateOf(1)
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }
    val totalTipState = remember {
        mutableDoubleStateOf(0.0)
    }
    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()

    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }
    TopHeader(totalPerPerson = totalPerPersonState.doubleValue)
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill ",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })

            /// Split Section

            if (validState) {
            Row(
                modifier = Modifier
                    .padding(3.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Split",
                    modifier = Modifier
                        .align(
                            alignment = Alignment.CenterVertically
                        )
                )
                Spacer(
                    modifier = Modifier
                        .width(120.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 3.dp
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    RoundIconButton(
                        modifier = Modifier,
                        imageVector = Icons.Default.Remove,
                        onClick = {
                            if (splitPeopleState.intValue > 1) {
                                splitPeopleState.intValue -= 1
                            } else
                                splitPeopleState.intValue = 1
                            val newSplit = splitPeopleState.intValue
                            totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = newSplit,
                                tipPercentage = tipPercentage
                            )
                        })

                    Text(
                        text = "${splitPeopleState.intValue}",
                        modifier = Modifier
                            .align(
                                alignment = Alignment.CenterVertically
                            )
                            .padding(start = 9.dp, end = 9.dp)
                    )
                    RoundIconButton(
                        modifier = Modifier,
                        imageVector = Icons.Default.Add,
                        onClick = {
                            splitPeopleState.intValue += 1
                            val newSplit = splitPeopleState.intValue
                            totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = newSplit,
                                tipPercentage = tipPercentage
                            )
                        })
                }
            }


            // Tips Section
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 3.dp,
                        vertical = 12.dp
                    ),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Tip",
                    modifier = Modifier
                        .align(
                            alignment = Alignment.CenterVertically
                        )
                )
                Spacer(
                    modifier = Modifier
                        .width(200.dp)
                )
                Text(
                    text = "$ ${totalTipState.doubleValue}",
                    modifier = Modifier
                        .align(
                            alignment = Alignment.CenterVertically
                        )
                )
            }

            //  Tips Percentage

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage %")
                Spacer(
                    modifier = Modifier
                        .height(14.dp)
                )

                // Slider

                Slider(
                    value = sliderPositionState.floatValue,
                    onValueChange = {newVal ->
                        sliderPositionState.floatValue = newVal
                        val newTipPercentage = (newVal * 100).toInt()
                        val newSplit = splitPeopleState.intValue
                        totalTipState.doubleValue = calculateTip(totalBill = totalBillState.value.toDouble() , tipPercentage =  newTipPercentage)
                        totalPerPersonState.doubleValue = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                            splitBy = newSplit ,
                                    tipPercentage = newTipPercentage)
                    },
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp),
                    steps = 5,
                )

            }
            }

        }
    }
}



@Preview
@Composable
fun MyAppPreview() {
    MyApp {
        Column {
            MainContent()
        }
    }
}