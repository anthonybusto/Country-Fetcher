package com.busto.countryfetcher.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.busto.countryfetcher.R
import com.busto.countryfetcher.databinding.ItemCountryBinding
import com.busto.countryfetcher.ui.renderables.CountryRenderable

class CountryListAdapter :
    ListAdapter<CountryRenderable, CountryListAdapter.CountryViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CountryViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(country: CountryRenderable) {
            with(binding) {
                nameTextView.text = itemView.context.getString(
                    R.string.country_item_title,
                    country.name,
                    country.region
                )
                codeTextView.text = country.code
                capitalTextView.text = country.capital
            }
        }
    }

    class CountryDiffCallback : DiffUtil.ItemCallback<CountryRenderable>() {
        override fun areItemsTheSame(oldItem: CountryRenderable, newItem: CountryRenderable): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CountryRenderable, newItem: CountryRenderable): Boolean {
            return oldItem == newItem
        }
    }
}
