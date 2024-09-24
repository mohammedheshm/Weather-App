package com.example.weatherapp.favorite.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentFavoriteBinding
import com.example.weatherapp.db.WeatherLocalDataSourceImpl
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.repository.WeatherRepoImpl
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import com.example.weatherapp.util.PermissionChecksUtil
import kotlinx.coroutines.launch

const val FAVORITE_FRAGMENT = "favorite_fragment"

class FavoriteFragment : Fragment(), OnFavoriteItemClick {

    private val TAG = "FavoriteFragment"

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.progressBarFavorite.visibility = View.GONE
        binding.fabAddFav.setOnClickListener {
            if (PermissionChecksUtil.checkConnection(requireContext())) {
                val action =
                    FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment2(
                        FAVORITE_FRAGMENT
                    )
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }
        }



        favoriteAdapter = FavoriteAdapter(requireContext(), this)
        binding.rvFavorites.adapter = favoriteAdapter
        binding.rvFavorites.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)

        favoriteViewModel.getFavoriteWeatherFromDataBase()
        lifecycleScope.launch {
            favoriteViewModel.favoriteWeather.collect { favoriteWeatherList ->
                favoriteAdapter.submitList(favoriteWeatherList)



                // Update visibility based on the list size
                if (favoriteWeatherList.isEmpty()) {
                    binding.imgEmptyState.visibility = View.VISIBLE
                    binding.rvFavorites.visibility = View.GONE
                    binding.tvFavoritePlaceholderText1.visibility = View.VISIBLE
                    binding.tvFavoritePlaceholderText2.visibility = View.VISIBLE
                } else {
                    binding.imgEmptyState.visibility = View.GONE
                    binding.rvFavorites.visibility = View.VISIBLE
                    binding.tvFavoritePlaceholderText1.visibility = View.GONE
                    binding.tvFavoritePlaceholderText2.visibility = View.GONE
                }


            }
        }

    }

    override fun showMapDetailsFragment(favoriteId: Int) {
        Log.d(TAG, "id: $favoriteId ")

        val action =
            FavoriteFragmentDirections.actionFavoriteFragmentToDetailsFavoriteFragment(favoriteId)
        findNavController().navigate(action)
    }


    override fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_alert, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val buttonOk: Button = dialogView.findViewById(R.id.button_ok)
        val buttonCancel: Button = dialogView.findViewById(R.id.button_cancel)

        dialogTitle.text = "Are you sure delete this item"

        val dialog = dialogBuilder.create()

        buttonOk.setOnClickListener {
            lifecycleScope.launch {
                favoriteViewModel.deleteFavorite(favoriteWeather)
            }
            Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}

