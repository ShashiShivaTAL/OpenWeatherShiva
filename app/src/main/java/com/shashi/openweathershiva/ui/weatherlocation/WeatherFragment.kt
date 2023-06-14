package com.shashi.openweathershiva.ui.weatherlocation

import android.app.Service
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.shashi.openweathershiva.data.UserPreferences
import com.shashi.openweathershiva.data.network.Resource
import com.shashi.openweathershiva.databinding.FragmentWeatherBinding
import com.shashi.openweathershiva.ui.base.BaseFragment
import com.shashi.openweathershiva.utils.Constants.Companion.SEARCH_WEATHER_TIME_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment : BaseFragment() {

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var userPreferences: UserPreferences
    private val viewModel by viewModels<WeatherViewModel>()
    private var countryCode: String? = null
    private val TAG = "WeatherFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        viewModel.weatherSearch("hyderabad")
        countryCode = getUserCountry()
        Log.i("shashil", "onCreate: countryCode- $countryCode")
        // Initializing preferences
        userPreferences = UserPreferences(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var job: Job? = null

        // Search only if India and USA
        if (countryCode.equals("IN", ignoreCase = true) ||
            countryCode.equals("USA", ignoreCase = true)
        ) {
            // TextChangeListener for location search
            binding.editTextSearch.addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_WEATHER_TIME_DELAY)
                    editable?.let {
                        if (it.toString().isNotEmpty()) {
                            weatherSearch(it.toString())
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, "You can only search weather for USA and INDIA", Toast.LENGTH_LONG).show()
        }

    }

    private fun weatherSearch(city: String) {
        // API call
        viewModel.weatherSearch(city)
        // Response
        lifecycleScope.launchWhenStarted {
            viewModel.weatherSearchResponse.collect {
                when (it) {

                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Error -> {
                        dismissLoading()
                        if (it.exception == "401") {
                            lifecycleScope.launch {
                                userPreferences.clear()
                            }
                        } else {
                            Snackbar.make(binding.root, it.exception, Snackbar.LENGTH_SHORT).show()
                        }

                    }
                    is Resource.Success -> {
                        dismissLoading()
                        binding.txtWeather.text = "Weather: ${it.data.main.temp}"
                        userPreferences.saveLastWeatherSearchValue(it.data.main.temp.toString())
                    }
                }
            }
        }
    }


    private fun getUserCountry(): String? {
        try {
            val tm = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                return simCountry.lowercase()
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    return networkCountry.lowercase()
                }
            }
        } catch (_: Exception) {
        }
        return null
    }

    override fun showAlertDialogButtonClicked() {
        super.showAlertDialogButtonClicked()

        // Loading the last saved weather value
        /*userPreferences.lastWeatherSearchValue.asLiveData().observe(requireActivity()) {
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