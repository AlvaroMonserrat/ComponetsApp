package com.rrat.componetsapp.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rrat.componetsapp.R
import com.rrat.componetsapp.ui.theme.ComponetsAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBoxGroup(){

    val listText = stringArrayResource(id = R.array.options)
    val listAnswer = remember{
        mutableStateListOf(*List(listText.size){""}.toTypedArray()) }
    val listStates = remember{
        mutableStateListOf(*List(listText.size){false}.toTypedArray()) }

    Column() {
        listText.forEachIndexed { index, text ->
            CheckBoxWithText(
                text = text,
                state = listStates[index],
                onCheckedChange = { listStates[index] = it}
            )
            
            AnimatedVisibility (listStates[index]){
                TextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = listAnswer[index],
                    onValueChange = { newText -> listAnswer[index] = newText}
                )
            }
        }
    }

}

@Composable
private fun CheckBoxWithText(
    text: String,
    state: Boolean,
    onCheckedChange: (Boolean)->Unit
) {

    Row(
        modifier = Modifier.padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = onCheckedChange
        )
        Text(text = text)
    }
}


@Preview(showBackground = true)
@Composable
fun CheckBoxGroupPreview() {
    ComponetsAppTheme {
        CheckBoxGroup()
    }
}