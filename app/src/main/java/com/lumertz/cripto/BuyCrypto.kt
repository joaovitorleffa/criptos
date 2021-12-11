package com.lumertz.cripto

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.buy_crypto_activity.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BuyCrypto : AppCompatActivity() {
    private val crypto: CryptoModel
        get() = intent.getSerializableExtra(CRYPTO_MODEL_KEY) as CryptoModel

    private var cryptoValue: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buy_crypto_activity)

        setupInitialState()
        setupListeners()
        setupView()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setupInitialState() {
        buyCryptoName.text = "${getString(R.string.crypto_name)} ${crypto.name}"

        val currentDate = Date()
        val formattedDateString = SimpleDateFormat("dd/MM/yyyy").format(currentDate)
        buyCryptoDate.text = "${getString(R.string.current_date)} ${formattedDateString}"
    }

    private fun setupView() {
        loadData {
            try {
                val ticker =  it.getJSONObject("ticker")

                val averagePrice = ticker.getDouble("last")
                cryptoValue = averagePrice

                val ptBr = Locale("pt", "BR")
                val formattedValue = NumberFormat.getCurrencyInstance(ptBr).format(averagePrice)
                buyCryptoAveragePriceField.setText(formattedValue)
            } catch (exception: JSONException) {
                showErrorMessage()
            }
        }
    }

    private fun loadData(handle: (JSONObject) -> Unit) {
        val httpClient = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.mercadobitcoin.net/api/${crypto.code}/ticker/")
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

    private fun setupListeners() {
        buyCryptoCryptoAmountField.addTextChangedListener {
            val cryptoAmount = it.toString().toDoubleOrNull() ?: 0.0
            val amount = cryptoAmount * cryptoValue
            val ptBr = Locale("pt", "BR")
            val formattedValue = NumberFormat.getCurrencyInstance(ptBr).format(amount)
            buyCryptoAmountField.setText(formattedValue)
        }

        buyCryptoCheckoutButton.setOnClickListener {
            val amount = buyCryptoCryptoAmountField.text.toString().toDoubleOrNull()

            if (amount != null) {
                val cryptoTransactionDao = CryptoTransactionDao(this)
                val date = buyCryptoDate.text.toString()
                val total = buyCryptoAmountField.text.toString().toDoubleOrNull() ?: 0.0
                val model = CryptoTransaction(
                    null,
                    crypto.name,
                    crypto.code,
                    amount,
                    cryptoValue,
                    total,
                    date
                )
                val message = cryptoTransactionDao.insert(model)
                showToast(message)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent);
                finish();
            } else {
                showToast(getString(R.string.empty_field_message))
            }
        }
    }
}