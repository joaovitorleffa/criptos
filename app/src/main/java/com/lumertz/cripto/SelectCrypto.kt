package com.lumertz.cripto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.select_active_activity.*

class SelectCrypto : AppCompatActivity(), ListViewClickListener {

    private val cryptos = listOf<CryptoModel>(
        CryptoModel("Bitcoin", "BTC"),
        CryptoModel("Litecoin", "LTC"),
        CryptoModel("Cardano", "ADA"),
        CryptoModel("Uniswap", "UNI"),
        CryptoModel("USD Coin", "USDC")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_active_activity)

        setupListView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupListView() {
        val models = cryptos.map { CardViewModel(it.name, null, null) }
        selectCryptoListView.adapter = TransactionsAdapter(models, this)
        selectCryptoListView.layoutManager = LinearLayoutManager(this)
    }

    override fun didClickAt(position: Int) {
        val intent = Intent(this, BuyCrypto::class.java)
        intent.putExtra(CRYPTO_MODEL_KEY, cryptos[position])

        startActivity(intent)
    }
}