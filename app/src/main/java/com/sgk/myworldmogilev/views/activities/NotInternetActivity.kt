package com.sgk.myworldmogilev.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.helper.ConnectionLiveData

class NotInternetActivity : AppCompatActivity() {

    private lateinit var cld : ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_internet)
        checkNetworkConnection()
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->
            if (isConnected) {
                finish()
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}