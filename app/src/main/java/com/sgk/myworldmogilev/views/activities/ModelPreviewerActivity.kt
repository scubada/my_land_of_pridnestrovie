package com.sgk.myworldmogilev.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.ActivityModelPreviewerBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData

class ModelPreviewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModelPreviewerBinding
    private lateinit var cld : ConnectionLiveData

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelPreviewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNetworkConnection()

        with(binding){
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(intent.getStringExtra("model").toString())

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    binding.progress.animate()
                        .alpha(0F)
                        .withEndAction {
                            binding.progress.visibility = View.GONE
                        }
                        .setDuration(500)
                        .start()
                }
            }
        }

        binding.back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
}