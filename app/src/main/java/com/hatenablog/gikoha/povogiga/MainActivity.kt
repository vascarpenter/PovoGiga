package com.hatenablog.gikoha.povogiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.hatenablog.gikoha.povogiga.ui.theme.PovoGigaTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
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


@Preview(name = "MainScreen", showBackground = true)
@Composable
fun DefaultPreview()
{
    val focusManager = LocalFocusManager.current

    var giga by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    val items = remember {  mutableStateListOf<PovoGiga>() }
    var itemskey = listOf<UUID>()

    loadData(items)
    itemskey = List(items.count()) { UUID.randomUUID() } // List全体を書き換えるためてきとーなkeyを設定

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
                            postData(giga, memo)
                            loadData(items)
                            itemskey = List(items.count()) { UUID.randomUUID() }  // keyをすべて変更しList全体を書き換える

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
                    items(items.count(), key = { ind -> itemskey[ind] })
                    {
                        PovoGigaOneline(items[it])
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }

                }
            }


        }
    }
}

fun loadData(items: SnapshotStateList<PovoGiga>)
{
    val getapi = apiBuilder().create(PovoGigaGet::class.java)

    // repo access is suspended function, so run in viewModelScope
    MainScope().launch {
        val response = getapi.getItems()
        if (response.isSuccessful)
        {
            val data = response.body()!!
            items.clear()
            items.addAll(data)
        }
    }
}

fun postData(data: String, memo: String)
{
    val postapi = apiBuilder().create(PovoGigaPost::class.java)

    val d = PovoGigaPostJson(BuildConfig.povoapikey, getDateString(), data, memo)

    // repo access is suspended function, so run in viewModelScope
    MainScope().launch {
        val response = postapi.postItem(d)
        if (response.isSuccessful)
        {
            // success
            // Log.i("PostData", d.toString())
        }
    }
}

// date utility function

private fun getDateString(): String
{
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)

    return String.format("%04d%02d%02d %02d%02d", year, month, day, hour, minute)
}


// api builder utility function for retrofit

private fun apiBuilder(): Retrofit
{
    // access API
    val client = buildOkHttp()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    return Retrofit.Builder()
        .baseUrl(BuildConfig.povoserverurl)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}

// okhttp build utility function

private fun buildOkHttp(): OkHttpClient
{
    val client = OkHttpClient.Builder()
    client.connectTimeout(20, TimeUnit.SECONDS)
    client.readTimeout(15, TimeUnit.SECONDS)
    client.writeTimeout(15, TimeUnit.SECONDS)
    return client.build()
}
