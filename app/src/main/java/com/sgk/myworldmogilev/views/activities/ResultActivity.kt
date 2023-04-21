package com.sgk.myworldmogilev.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.ActivityResultBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var cld: ConnectionLiveData
    private var correctCount = 0
    private var wrongCount = 0
    private var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        correctCount = intent.getIntExtra("correct", 0)
        wrongCount = intent.getIntExtra("wrong", 0)
        status = intent.getStringExtra("url").toString()

        getAnalyticsResult()

        checkNetworkConnection()

        binding.back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }

        binding.choiceButton.setOnClickListener {
            val mainIntent = Intent(
                this,
                if (status.contains("test/"))
                    TestActivity::class.java
                else
                    GeneralTestActivity::class.java
            )
            mainIntent.putExtra("url", status)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            finish()
        }
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->
            if (!isConnected) {
                startActivity(Intent(this, NotInternetActivity::class.java))
                finish()
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }

    @SuppressLint("SetTextI18n")
    private fun getAnalyticsResult() {
        val sum: Int = correctCount + wrongCount
        val progress: Int = correctCount * 100 / sum
        binding.circularProgressBar.progress = progress.toFloat()
        binding.prozent.text = "$progress%"
        binding.done.text = "Верных ответов:  $correctCount"
        binding.error.text = "Ошибок:  $wrongCount"
    }
}