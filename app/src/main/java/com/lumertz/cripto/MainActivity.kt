package com.lumertz.cripto

import android.content.Intent
import android.icu.text.NumberFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), ListViewClickListener {
    var data: List<CryptoTransactionAverage> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
        setupListView()
        setupListeners()
    }

    private fun setupView() {

        val total = CryptoTransactionDao(this).getTotal()
        val formattedValue = formatCurrency(total)

        mainTotalInvested.text = formattedValue
    }

    private fun setupListView() {
        val dao = CryptoTransactionDao(this)
        data = dao.getAverageAll()

        val models = data.map {
            val formattedValue = formatCurrency(it.average)

            CardViewModel(
                "${it.total} ${it.code} - Preço médio: $formattedValue",
                null,
                null
            )
        }

        mainListView.adapter = TransactionsAdapter(models, this)
        mainListView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        mainButton.setOnClickListener {
            val intent = Intent(this, SelectCrypto::class.java)
            startActivity(intent)
        }
    }

    private fun formatCurrency(value: Double): String {
        val ptBr = Locale("pt", "BR")
        val formattedValue = NumberFormat.getCurrencyInstance(ptBr).format(value)
        return formattedValue
    }

    override fun didClickAt(position: Int) {
        val intent = Intent(this, TransactionsDetails::class.java)
        intent.putExtra(TRANSACTION_AVERAGE_MODEL_KEY, data[position])

        startActivity(intent)
    }
}