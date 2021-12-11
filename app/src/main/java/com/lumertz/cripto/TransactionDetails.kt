package com.lumertz.cripto

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.transaction_details_activity.*
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class TransactionsDetails : AppCompatActivity() {
    private var cryptoValue: Double = 0.0
    private var transactionsList = mutableListOf<CardViewModel>()

    val cryptoTransaction: CryptoTransactionAverage
        get() = intent.getSerializableExtra(TRANSACTION_AVERAGE_MODEL_KEY) as CryptoTransactionAverage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_details_activity)


        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    private fun setupView() {
        loadVariation {
            try {
                val ticker = it.getJSONObject("ticker")

                val averagePrice = ticker.getDouble("last")

                cryptoValue = averagePrice

                val currentValue = averagePrice * cryptoTransaction.total

                val totalValue = cryptoTransaction.average * cryptoTransaction.total

                val dec = DecimalFormat("#.##")

                if (currentValue > totalValue) {
                    val variation = (currentValue - totalValue) * 100 / currentValue;
                    detailsVariation.text = "Variação: +${dec.format(variation)}%"
                } else {
                    val variation = (totalValue - currentValue) * 100 / totalValue;
                    detailsVariation.text = "Variação: -${dec.format(variation)}%"
                }
            } catch (exception: JSONException) {
                showErrorMessage()
            } finally {
                lifecycleScope.launch {
                    setupListView()
                }
            }
        }
    }

    private fun loadVariation(handle: (JSONObject) -> Unit) {
        val httpClient = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.mercadobitcoin.net/api/${cryptoTransaction.code}/ticker/")
            .build()

        httpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                showErrorMessage()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonObject = JSONObject(response.body?.string() ?: "")
                    handle(jsonObject)
                }
            }
        })
    }

    private fun showErrorMessage() {
        showToast(getString(R.string.failed_load_data))
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setupListView() {
        detailsTotal.text = "${getString(R.string.total_crypto)} ${cryptoTransaction.total} ${cryptoTransaction.code}"

        val dao = CryptoTransactionDao(this)
        val data = dao.getAllByCode(cryptoTransaction.code)

        val transactions = data.map {
            val currentValue = cryptoValue * it.amount
            val totalValue = it.value * it.amount
            val dec = DecimalFormat("#.##")
            var variation: String

            if (currentValue > totalValue) {
                val result = (currentValue - totalValue) * 100 / currentValue
                variation = "+${dec.format(result)}"
            } else {
                val result = (totalValue - currentValue) * 100 / totalValue
                variation = "-${dec.format(result)}"
            }

            CardViewModel(
                "Comprado na ${it.date}",
                "valor: ${formatCurrency(it.value)}, quantidade: ${it.amount}",
                "variação: ${variation}%"
            )
        }
        transactionsList.addAll(transactions)
        detailsListView.adapter?.notifyDataSetChanged()
    }

    private fun formatCurrency(value: Double): String {
        val ptBr = Locale("pt", "BR")
        val formattedValue = NumberFormat.getCurrencyInstance(ptBr).format(value)
        return formattedValue
    }

    private fun initRecyclerView() {
        val adapter = TransactionsAdapter(transactionsList)
        detailsListView.adapter = adapter
        detailsListView.layoutManager = LinearLayoutManager(this)
    }
}