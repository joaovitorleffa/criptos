package com.lumertz.cripto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_view_activity.view.*

class CardViewModel(
    val title: String,
    val subtitleLeft: String?,
    val subtitleRight: String?
) { }

interface ListViewClickListener {
    fun didClickAt(position: Int)
}

class TransactionsAdapter(
    private val models: List<CardViewModel>,
    private val clickListener: ListViewClickListener? = null
): RecyclerView.Adapter<TransactionsAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.card_view_activity,parent,
            false
        )

        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val model = models[position]
        holder.titleView.text = model.title

        holder.subtitleLeftView.text = model.subtitleLeft
        holder.subtitleLeftView.visibility = when(model.subtitleLeft.isNullOrEmpty()) {
            true -> View.GONE
            false -> View.VISIBLE
        }

        holder.subtitleRightView.text =  model.subtitleRight

        holder.subtitleRightView.visibility = when(model.subtitleRight.isNullOrEmpty()) {
            true -> View.GONE
            false -> View.VISIBLE
        }

        if (!model.subtitleLeft.isNullOrEmpty() || !model.subtitleRight.isNullOrEmpty()) {
            holder.spacer.visibility = View.VISIBLE
        } else {
            holder.spacer.visibility = View.GONE
        }

        holder.cardView.setOnClickListener {
            clickListener?.didClickAt(position)
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    class VH(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleView: TextView = itemView.cardViewTitle
        val subtitleLeftView: TextView = itemView.cardViewSubtitleLeft
        val subtitleRightView: TextView = itemView.cardViewSubtitleRight
        val spacer: Space = itemView.cardViewSpacer
        val cardView: CardView = itemView.cardViewContainerView
    }
}
