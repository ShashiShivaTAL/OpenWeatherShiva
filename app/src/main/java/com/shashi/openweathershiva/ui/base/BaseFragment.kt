package com.shashi.openweathershiva.ui.base

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.shashi.openweathershiva.databinding.InternetLayoutBinding
import com.shashi.openweathershiva.utils.NetworkConnectivityChecker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInternetConnectivityObserver()
        showAlertDialogButtonClicked()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectivityChecker.checkForConnection()
    }


    private fun setInternetConnectivityObserver() {
        NetworkConnectivityChecker.observe(this) { isConnected ->
            if (!isConnected) {
                //Can use your own logic later -- Using snackbar as default. Build your own listener to create custom view
                view?.let {

                }

                dialog?.show()
            } else {
                // snackbar?.dismiss()
                dialog?.dismiss()

            }
        }
    }

    open fun showAlertDialogButtonClicked() {

        var binding: InternetLayoutBinding? = null
        // Create an alert builder
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)

        // set the custom layout
        binding = InternetLayoutBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        binding.btnRetry.setOnClickListener {
            binding.progressBar.isVisible = true

            Handler(Looper.getMainLooper()).postDelayed({ binding.progressBar.isVisible = false },
                1000)
        }

        // the alert dialog
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       // dialog!!.show()
    }
}