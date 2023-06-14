package com.shashi.openweathershiva.ui.base

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.shashi.openweathershiva.databinding.InternetLayoutBinding
import com.shashi.openweathershiva.utils.NetworkConnectivityChecker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    var baseDialog: AlertDialog? = null

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
                baseContext.let {

                }

                baseDialog?.show()
            } else {
                baseDialog?.dismiss()

            }
        }
    }

     open fun showAlertDialogButtonClicked() {

        var binding: InternetLayoutBinding? = null
        // Create an alert builder
        val builder = AlertDialog.Builder(this)
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
         baseDialog = builder.create()
         baseDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // dialog!!.show()
    }
}