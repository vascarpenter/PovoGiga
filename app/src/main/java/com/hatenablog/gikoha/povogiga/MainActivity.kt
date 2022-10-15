package com.hatenablog.gikoha.povogiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import com.hatenablog.gikoha.povogiga.ui.theme.PovoGigaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}


// Create one line row in Compose LazyColumn
@Composable
fun PovoGigaOneline(data: PovoGiga)
{
    Column {
        Row {
            Text(text = data.date,
                 modifier = Modifier.padding(all = 4.dp))
            Text(text = data.gigaleft + " GB",
                 modifier = Modifier.padding(vertical = 4.dp,
                                             horizontal = 12.dp))
        }
        Text(text = data.memo ?: "",
            fontSize= 10.sp,
             modifier = Modifier.padding(all = 4.dp))
    }
}

@Composable
fun MainScreen()
{
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val viewModel: PovoGigaViewModel = hiltViewModel()

    val buttontitle by viewModel.buttontitle.observeAsState()

    val viewState: PovoGigaViewState by viewModel.state.collectAsState(initial = PovoGigaViewState.EMPTY)
    val items: List<PovoGiga> = viewState.items ?: emptyList()

    if (items.isEmpty())
    {
        viewModel.loadData {
            coroutineScope.launch {
                // Animate scroll to the end of item

                listState.animateScrollToItem(viewState.items?.count() ?: 0)
            }
        }
    }

    PovoGigaMainScreen(items, buttontitle) { temp, memo ->
        focusManager.clearFocus()
        viewModel.postData(temp, memo)
        {
            viewModel.changeTitle("POST OK")
            viewModel.clearData()
            // 削除すると recompose 行われ、loadData が行われ、再度 recomposeされることで recompose保証
            // updateだけでは recomposeすら行われない「ことがある」
        }
    }

}

// Provide sample data for preview
class PGMainScreenParameterProvider : PreviewParameterProvider<List<PovoGiga>>
{
    override val values = sequenceOf(
        listOf(
            PovoGiga("22-10-9", "75.0", "やと　ぎが　かえた　も　とてもかゆい"),
            PovoGiga("22-10-15", "74.0", "かゆい　かゆい　ぎがかつ　きた")
        ),
    )
}

@Preview(name = "MainScreen")
@Composable
fun PovoGigaMainScreen(
    @PreviewParameter(PGMainScreenParameterProvider::class) items: List<PovoGiga>,
    buttontitle: String? = "SUBMIT",
    onClick: (temp: String, memo: String) -> Unit = { _, _ -> },
)
{
    var giga by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    PovoGigaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

            Column {
                TopAppBar(
                    title = { Text("PovoGiga") },
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = giga,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number ),
                        onValueChange = { giga = it },
                        label = { Text("input giga left (eg. 20.34)") },
                        modifier = Modifier.padding(all = 4.dp),
                        maxLines = 1,
                        singleLine = true,
                    )

                    Button(
                        onClick = {
                            onClick(giga, memo)
                        },
                        modifier = Modifier.padding(all = 4.dp)
                    ) {
                        Text(
                            text = buttontitle ?: "",
                            fontSize = 10.sp,
                            maxLines = 1,
                        )
                    }
                }

                TextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("memo") },
                    modifier = Modifier.padding(all = 4.dp).fillMaxWidth(),
                    maxLines = 1,
                    singleLine = true,
                )

                PovoLists(items)
            }


        }
    }
}
@Composable
fun PovoLists(items: List<PovoGiga>)
{

    LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(items) { _, item ->
            PovoGigaOneline(item)
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }

}
