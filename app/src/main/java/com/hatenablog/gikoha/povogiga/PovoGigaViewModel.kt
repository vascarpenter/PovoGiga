package com.hatenablog.gikoha.povogiga

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class PovoGigaViewModel : ViewModel()
{
    private val _items = mutableStateListOf<PovoGiga>()
    val items: List<PovoGiga> get() = _items

    fun loadData()
    {
        val getapi = apiBuilder().create(PovoGigaGet::class.java)

        // repo access is suspended function, so run in viewModelScope
        viewModelScope.launch {
            val response = getapi.getItems()
            if (response.isSuccessful)
            {
                val data = response.body()!!
                _items.clear()
                _items.addAll(data)
            }
        }
    }

    fun postData(data: String, memo: String)
    {
        val postapi = apiBuilder().create(PovoGigaPost::class.java)

        val d = PovoGigaPostJson(BuildConfig.povoapikey, getDateString(), data, memo)

        // repo access is suspended function, so run in viewModelScope
        viewModelScope.launch {
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

}
