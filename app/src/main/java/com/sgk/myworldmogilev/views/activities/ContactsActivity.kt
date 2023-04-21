package com.sgk.myworldmogilev.views.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactsBinding
    private lateinit var clipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.mail.setOnClickListener {
            val clip = ClipData.newPlainText("label", getString(R.string.mail))
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copy), Toast.LENGTH_SHORT).show()
        }

        binding.adress.setOnClickListener {
            val clip = ClipData.newPlainText("label", getString(R.string.address))
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copy), Toast.LENGTH_SHORT).show()
        }

        binding.time.setOnClickListener {
            val clip = ClipData.newPlainText("label", getString(R.string.time))
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copy), Toast.LENGTH_SHORT).show()
        }

        binding.back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
}