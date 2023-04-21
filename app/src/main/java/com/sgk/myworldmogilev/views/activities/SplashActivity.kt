package com.sgk.myworldmogilev.views.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.helper.MapKitInitializer

class SplashActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sp = getSharedPreferences("sp", MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            if (sp.getString("splash", "").equals("")) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }, 1500)
    }

}