package com.example.weatherapp.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.model.FavoriteWeather


class FavoriteAdapter(
    private val context: Context,
    private val onFavoriteItemClick: OnFavoriteItemClick
) :
    ListAdapter<FavoriteWeather, FavoriteViewHolder>(FavoriteDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteWeather: FavoriteWeather = getItem(position)

        holder.favoriteAddress.text = favoriteWeather.timezone
        holder.cvFavorite.setOnClickListener {
            onFavoriteItemClick.showMapDetailsFragment(favoriteWeather.fav_id)
        }

        holder.ivDeletefavorite.setOnClickListener {
            onFavoriteItemClick.deleteFavorite(favoriteWeather)
        }


    }

}

class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val favoriteAddress: TextView = itemView.findViewById(R.id.tv_favorite_address)
    val cvFavorite: CardView = itemView.findViewById(R.id.cv_favorite)
    val ivDeletefavorite: ImageView = itemView.findViewById(R.id.iv_delete_favorite)


}

class FavoriteDiffUtil : DiffUtil.ItemCallback<FavoriteWeather>() {
    override fun areItemsTheSame(oldItem: FavoriteWeather, newItem: FavoriteWeather): Boolean {
        return oldItem.fav_id == newItem.fav_id
    }

    override fun areContentsTheSame(oldItem: FavoriteWeather, newItem: FavoriteWeather): Boolean {
        return oldItem == newItem
    }
}