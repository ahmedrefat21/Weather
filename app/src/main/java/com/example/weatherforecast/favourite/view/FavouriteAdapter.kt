package com.example.weatherforecast.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.ItemFavouriteBinding
import com.example.weatherforecast.model.entity.FavoriteEntity


class FavouriteAdapter (private var context: Context,
                        private val favoriteClickListener: FavouriteClickListener):
    ListAdapter<FavoriteEntity, FavouriteAdapter.FavouriteViewHolder>(FavouriteDiffUtil()){
    private lateinit var binding : ItemFavouriteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavouriteBinding.inflate(inflater,parent,false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.binding.tvFavouritePlace.text = currentObj.address
        holder.binding.ivDelete.setOnClickListener { favoriteClickListener.deleteFavourite(currentObj.id)}
        holder.binding.cardFavourite.setOnClickListener { favoriteClickListener.showFavouriteDetails(currentObj)}
    }

    inner class FavouriteViewHolder (var binding: ItemFavouriteBinding): RecyclerView.ViewHolder(binding.root)
}

class FavouriteDiffUtil: DiffUtil.ItemCallback<FavoriteEntity>() {
    override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
        return oldItem == newItem
    }
}