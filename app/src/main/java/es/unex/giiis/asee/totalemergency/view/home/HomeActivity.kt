package es.unex.giiis.asee.totalemergency.view.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import es.unex.giiis.asee.totalemergency.view.home.ContactsViewModel
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel

import es.unex.giiis.asee.totalemergency.R
import es.unex.giiis.asee.totalemergency.data.model.Contact
import es.unex.giiis.asee.totalemergency.databinding.ActivityHomeBinding
import es.unex.giiis.asee.totalemergency.data.model.VideoRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class HomeActivity : AppCompatActivity()
{

    //Factory necesaria para recuperar usuario
    private val viewModel : HomeViewModel by viewModels { HomeViewModel.Factory }

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    companion object {
        const val USER_INFO = "USER_INFO"
        const val USER_COD_INFO = "USER_COD_INFO"

        private var userCod:Long = -1
        fun start(
            context: Context,
            cod: Long,
        ) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra(USER_COD_INFO, cod)
            }

            userCod = cod;

            Log.i("API", "el user cod es ${userCod}")
            context.startActivity(intent)
        }
    }

    //fun getUser(): User {
    //    return my_user
    //}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewModel.userCodInSession = userCod
        viewModel.obtenerUser(userCod)
        viewModel.guardarNavController(navController)


        Log.i("User data", "User is retrieved from database")

        setUpUI()
        //setUpListeners()
    }

    fun setUpUI() {
        binding.bottomNavigation.setupWithNavController(viewModel.navController!!)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.recordRegistryFragment,
                    R.id.homeMenuFragment,
                    R.id.profileFragment,
                    R.id.contactsFragment,
                    R.id.emergencyFragment
                )
            )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(viewModel.navController!!, appBarConfiguration)

        // Hide toolbar and bottom navigation when in detail fragment
        viewModel.navController!!.addOnDestinationChangedListener { _, destination, _ ->
            if ((destination.id == R.id.recordDetail) ||
                (destination.id == R.id.settingsFragment)){
             //   binding.toolbar.visibility = View.GONE
                binding.toolbar.menu.clear()
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }



    }

    override fun onSupportNavigateUp(): Boolean {
        return viewModel.navController!!.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_home, menu)

        //val searchItem = menu?.findItem(R.id.action_search)
        //val searchView = searchItem?.actionView as SearchView

        // Configure the search info and add any event listeners.

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {


        R.id.action_settings -> {
            // User chooses the "Settings" item. Show the app settings UI.
            //val action = DiscoverFragmentDirections.actionHomeToSettingsFragment()
            val action = HomeMenuFragmentDirections.homeToSettings()

            viewModel.navController?.navigate(action)
            true
        }

        else -> {
            // The user's action isn't recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}