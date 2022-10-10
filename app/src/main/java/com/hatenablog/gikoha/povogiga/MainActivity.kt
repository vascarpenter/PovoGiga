package com.hatenablog.gikoha.povogiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.hatenablog.gikoha.povogiga.ui.theme.PovoGigaTheme


class MainActivity : ComponentActivity()
{
    private lateinit var viewModel: PovoGigaViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        viewModel = PovoGigaViewModel()
        viewModel.loadData()

        setContent {
            DefaultPreview(viewModel)
        }
    }
}

// Provide sample data for preview
class GigaPreviewParameterProvider : PreviewParameterProvider<PovoGiga>
{
    override val values = sequenceOf(
        PovoGiga("2022/10/9", "50.20", "memo"),
    )
}

// Create one line row in Compose LazyColumn
@Preview(name="oneline")
@Composable
fun PovoGigaOneline(
    @PreviewParameter(GigaPreviewParameterProvider::class) data: PovoGiga)
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

// Provide sample viewModel
class GigaVMPreviewParameterProvider : PreviewParameterProvider<PovoGigaViewModel>
{
    override val values = sequenceOf(
        PovoGigaViewModel()
    )
}

@Preview(name = "MainScreen", showBackground = true)
@Composable
fun DefaultPreview(
    @PreviewParameter(GigaVMPreviewParameterProvider::class) viewModel: PovoGigaViewModel
)
{
    val focusManager = LocalFocusManager.current

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
                            focusManager.clearFocus()
                            viewModel.postData(giga, memo)
                            viewModel.loadData()
                        },
                        modifier = Modifier.padding(all = 4.dp)
                    ) {
                        Text("Submit")
                    }
                }

                TextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("memo") },
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = 1,
                    singleLine = true,
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(viewModel.items.count()) {
                        PovoGigaOneline(viewModel.items[it])
                        if (it < viewModel.items.lastIndex)
                            Divider(color = Color.Gray, thickness = 1.dp)
                    }

                }
            }


        }
    }
}