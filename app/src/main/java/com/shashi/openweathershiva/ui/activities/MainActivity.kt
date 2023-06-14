package com.shashi.openweathershiva.ui.activities

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shashi.openweathershiva.R
import com.shashi.openweathershiva.databinding.ActivityMainBinding
import com.shashi.openweathershiva.utils.Constants.Companion.REQUEST_LOCATION_PERMISSION
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null
    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request location permission for user
        requestLocationPermission();

        val navView: BottomNavigationView = binding.bottomNavigationView
        navController = findNavController(R.id.nav_host_fragment_activity_home)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.weatherLatLonFragment,
                R.id.weatherFragment
            )
        )
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)

        val navGraph = navController!!.navInflater.inflate(R.navigation.weather_nav_graph)

        navGraph.startDestination = R.id.weatherLatLonFragment
        navController?.graph = navGraph

        navController?.setGraph(navController!!.graph)
    }

    private fun navigationItems() {
        navController!!.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.weatherFragment -> {
                    showNavBar()
                }
                R.id.weatherLatLonFragment -> {
                    showNavBar()
                }
                else -> hideNavBar()
            }
        }
    }

    private fun hideNavBar() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    private fun showNavBar() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navigationItems()
    }

    override fun onRestart() {
        super.onRestart()
        navigationItems()
    }

    override fun onPause() {
        super.onPause()
        navigationItems()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        val search = menu?.findItem(R.id.appSearchBar)
        val searchView = search?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $newText")
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    fun requestLocationPermission() {
        if (EasyPermissions.hasPermissions(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Permission granted
        } else {
            EasyPermissions.requestPermissions(
                this@MainActivity,
                "Please grant the location permission",
                REQUEST_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
}