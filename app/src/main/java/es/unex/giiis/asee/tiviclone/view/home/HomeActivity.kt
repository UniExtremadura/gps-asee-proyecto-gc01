package es.unex.giiis.asee.tiviclone.view.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavArgument
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavType
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import es.unex.giiis.asee.tiviclone.R
import es.unex.giiis.asee.tiviclone.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.tiviclone.databinding.ActivityHomeBinding
import es.unex.giiis.asee.tiviclone.data.model.User
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), RecordRegistryFragment.OnShowClickListener {
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var db: TotalEmergencyDatabase
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }



    companion object {
        const val USER_INFO = "USER_INFO"
        const val USER_COD_INFO = "USER_COD_INFO"

        private var my_user:User = User()

        private var userCod:Long = -1
        fun start(
            context: Context,
            cod: Long,
        ) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra(USER_COD_INFO, cod)
            }
            //my_user = user;
            userCod = cod;
            Log.i("API", "el user cod es ${userCod}")
            context.startActivity(intent)
        }
    }

    fun getUser(): User {
        return my_user
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //database initialization
        db = TotalEmergencyDatabase.getInstance(applicationContext)!!

        scope.launch {
            my_user = db.userDao().findByCod(userCod)

            setUpUI(my_user)
            setUpListeners()
        }
    }

    fun setUpUI(user: User) {
        binding.bottomNavigation.setupWithNavController(navController)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.homeMenuFragment,
                    R.id.profileFragment,
                    R.id.emergencyFragment
                )
            )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Hide toolbar and bottom navigation when in detail fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if ((destination.id == R.id.recordRegistryFragment) ||
                (destination.id == R.id.recordDetail)){
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
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun setUpListeners() {
        //nothing to do
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_home, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        // Configure the search info and add any event listeners.

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {


        R.id.action_settings -> {
            // User chooses the "Settings" item. Show the app settings UI.
            //val action = DiscoverFragmentDirections.actionHomeToSettingsFragment()
            //navController.navigate(action)
            true
        }

        else -> {
            // The user's action isn't recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onShowClick(video: VideoRecord){
        val action = RecordRegistryFragmentDirections.actionShowRecordDetail2(video)
        navController.navigate(action)
    }
    /*
    override fun onShowClick(show: Show) {
        val action = DiscoverFragmentDirections.actionDiscoverFragmentToShowDetailFragment(show)
        navController.navigate(action)
    }
    override fun onShowClick(show: Show) {
        val action = null
        println("a")
        //val action = DiscoverFragmentDirections.actionDiscoverFragmentToShowDetailFragment(show)
        //navController.navigate(action)
    }
    */
}