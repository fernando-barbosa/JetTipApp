package br.com.fernandobarbosa.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fernandobarbosa.jettipapp.components.InputField
import br.com.fernandobarbosa.jettipapp.ui.theme.JetTipAppTheme
import br.com.fernandobarbosa.jettipapp.util.calculateTotalPerPerson
import br.com.fernandobarbosa.jettipapp.util.calculateTotalTip
import br.com.fernandobarbosa.jettipapp.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }

}

@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total por pessoa",
                style = MaterialTheme.typography.h5,
                color = Color.Black
            )
            Text(
                text = "R$ $total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    //val range = IntRange(start = 1, endInclusive = 100)

    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState,
    ) {}
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1 .. 100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Column {
        TopHeader(totalPerPerson = totalPerPersonState.value)

        Surface(
            modifier = modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Total da comanda",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )
                if (validState) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(modifier = Modifier.width(200.dp))

                        //Split Row
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitByState.value =
                                        if (splitByState.value > 1) splitByState.value - 1
                                        else 1

                                    totalPerPersonState.value =
                                        calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value,
                                            tipPercentage = tipPercentage)
                                }
                            )

                            Text(
                                text = "${splitByState.value}",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp, end = 10.dp)
                            )

                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitByState.value < range.last)
                                        splitByState.value = splitByState.value + 1

                                    totalPerPersonState.value =
                                        calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value,
                                            tipPercentage = tipPercentage)
                                }
                            )
                        }
                    }

                    //Tip Row
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Tip",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(212.dp))
                        Text(
                            text = "R$ ${tipAmountState.value}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "$tipPercentage %")
                        Spacer(modifier = Modifier.height(14.dp))

                        Slider(
                            value = sliderPositionState.value,
                            onValueChange = { newVal ->
                                sliderPositionState.value = newVal
                                tipAmountState.value =
                                    calculateTotalTip(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = tipPercentage)

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage)
                            },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            steps = 5,
                            onValueChangeFinished = {
                                /*TODO*/
                            }
                        )
                    }
                } else {
                    Box() {}
                }
            }
        }
    }
}
