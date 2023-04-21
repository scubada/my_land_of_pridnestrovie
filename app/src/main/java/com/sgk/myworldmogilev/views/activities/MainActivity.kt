package com.sgk.myworldmogilev.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sgk.myworldmogilev.views.fragments.MiniMapFragment
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.ActivityMainBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData
import com.sgk.myworldmogilev.helper.MapKitInitializer
import com.sgk.myworldmogilev.views.fragments.InfoFragment
import com.sgk.myworldmogilev.views.fragments.MapFragment
import com.sgk.myworldmogilev.views.fragments.TestFragment
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var cld: ConnectionLiveData
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var binding: ActivityMainBinding

        var stateMiniMap = false
        var stateMap = false
        var stateFragment = false

        fun hideMiniMap() {
            stateMiniMap = true
            stateMap = true
            stateFragment = true
            binding.bottomNavigationView.selectedItemId = R.id.map
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Handler(Looper.getMainLooper()).post {
            MapKitInitializer.initialize(getString(R.string.key), this)
        }

        checkNetworkConnection()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentSave =
            PreferenceManager.getDefaultSharedPreferences(this).getInt("fragment", R.id.map)

        binding.bottomNavigationView.selectedItemId = fragmentSave
        binding.bottomNavigationView.menu.findItem(fragmentSave).isChecked = true
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.frame_layout,
            when (fragmentSave) {
                R.id.test -> TestFragment.newInstance()
                R.id.map -> MiniMapFragment.newInstance()
                R.id.settings -> InfoFragment.newInstance()
                else -> MapFragment.newInstance()
            }
        )
        fragmentTransaction.commit()

        var fragment = Fragment()
        val bottomNav = binding.bottomNavigationView
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.test -> {
                    if (stateFragment)
                        MapFragment.hide()

                    fragment = TestFragment.newInstance()
                    stateMiniMap = false
                    stateMap = false
                    stateFragment = false
                }
                R.id.map -> {
                    if (stateMiniMap) {
                        fragment = MapFragment.newInstance()
                        stateMiniMap = false
                    } else {
                        if (stateFragment)
                            MapFragment.hide()
                        fragment = MiniMapFragment.newInstance()
                    }
                }
                R.id.settings -> {
                    if (stateFragment)
                        MapFragment.hide()

                    fragment = InfoFragment.newInstance()
                    stateMiniMap = false
                    stateMap = false
                    stateFragment = false
                }
            }

            binding.frameLayout.animate()
                .alpha(0F)
                .withEndAction {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .commit()

                    binding.frameLayout.animate()
                        .alpha(1F)
                        .setDuration(250)
                        .start()
                }
                .setDuration(250)
                .start()

            true
        }

        checkUpdate()
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->
            if (!isConnected) {
                startActivity(Intent(this, NotInternetActivity::class.java))
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putInt("recycler_view_position", 0)
            .apply()

        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putInt("fragment", binding.bottomNavigationView.selectedItemId)
            .apply()

        stateMiniMap = false
        stateMap = false

        super.onStop()
    }

    private fun checkUpdate() {
        val defaultsRate: HashMap<String, Any> = HashMap()
        defaultsRate["new_version_code"] = java.lang.String.valueOf(getVersionCode())

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(10)
            .build()

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(defaultsRate)

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                val newVersionCode: String =
                    mFirebaseRemoteConfig.getString("new_version_code")
                if (newVersionCode > getVersionCode()) {
                    val link: String =
                        mFirebaseRemoteConfig.getString("new_version_link")
                    showTheDialog(newVersionCode, link)
                }

            }
        }
    }

    private fun showTheDialog(versionFromRemoteConfig: String, linkLast: String) {
        val dialog: AlertDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Новая версия!")
            .setMessage("Обновите приложение до версии: $versionFromRemoteConfig")
            .setPositiveButton("Обновить", null)
            .setNegativeButton("Отмена", null)
            .show()
        dialog.setCancelable(false)
        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setOnClickListener {
            dialog.dismiss()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkLast))
            startActivity(browserIntent)
        }

        negativeButton.setOnClickListener { dialog.dismiss() }
    }

    private fun getVersionCode(): String = packageManager.getPackageInfo(packageName, 0).versionName

    override fun onBackPressed() {
        if (stateMap) {
            stateMap = false
            binding.bottomNavigationView.selectedItemId = R.id.map
        } else {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
    }
}