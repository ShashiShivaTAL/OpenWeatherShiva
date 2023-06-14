package com.shashi.openweathershiva.ui.weatherlatlon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.shashi.openweathershiva.data.UserPreferences
import com.shashi.openweathershiva.data.network.Resource
import com.shashi.openweathershiva.databinding.FragmentWeatherLatLonBinding
import com.shashi.openweathershiva.ui.base.BaseFragment
import com.shashi.openweathershiva.utils.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.shashi.openweathershiva.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherLatLonFragment : BaseFragment() {

    private lateinit var binding: FragmentWeatherLatLonBinding
    private val viewModel by viewModels<WeatherLatLonViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherLatLonBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // Initializing preferences
        userPreferences = UserPreferences(requireContext())

        return binding.root
    }

    private fun updateLatLong(latitude: Double, longitude: Double) {
        // API call
        viewModel.searchWeatherLatLon(latitude, longitude)
        // Response
        lifecycleScope.launchWhenStarted {
            viewModel.weatherLatLonResponse.collect {
                when (it) {

                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Error -> {
                        dismissLoading()
                        if (it.exception == "401") {
                            lifecycleScope.launch {
                                userPreferences.clear()
//                                requireActivity().toast("Session Expired")
                            }
                        } else {
                            Snackbar.make(binding.root, it.exception, Snackbar.LENGTH_SHORT).show()
                        }

                    }
                    is Resource.Success -> {
                        dismissLoading()
                        binding.txtWeather.text = "Weather: ${it.data.main.temp}"
                        userPreferences.saveLastWeatherLocationValue(it.data.main.temp.toString())
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(requireContext()) -> {
                when {
                    PermissionUtils.isLocationEnabled(requireContext()) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(requireContext())
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    requireContext(),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        // for getting the current location update after every 1 minutes with high accuracy
        val locationRequest = LocationRequest().setInterval(10000).setFastestInterval(10000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    var latitude = 0.0
                    var longitude = 0.0
                    for (location in locationResult.locations) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                    Log.i("shashil", "onLocationResult: $latitude --- $longitude")
                    // Update the location of user on server
                    updateLatLong(latitude, longitude)
                }
            },
            Looper.myLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(requireContext()) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(requireContext())
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "PERMISSION NOT GRANTED",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun showAlertDialogButtonClicked() {
        super.showAlertDialogButtonClicked()

        // Loading the last saved weather value
        /*userPreferences.lastWeatherLocationValue.asLiveData().observe(requireActivity()) {
            binding.txtWeather.text = it
        }*/
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun dismissLoading() {
        binding.progressBar.visibility = View.GONE
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

}