package com.sgk.myworldmogilev.views.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.ActivitySendLetterBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData


class SendLetterActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendLetterBinding
    private lateinit var cld: ConnectionLiveData
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    private var applicationEmail: String? = null
    private var applicationEmailPassword: String? = null
    private var sendToMail: String? = null

    private val duration: Long = 500
    private val blue = Color.parseColor("#3063A4")
    private val white = Color.parseColor("#FFFFFF")
    private val disable = Color.parseColor("#95AFD1")
    private var buttonState = false

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString("userEmail", binding.userEmail.text.toString())
            .apply()

        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString("userMessage", binding.userMessage.text.toString())
            .apply()

        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->
            if (!isConnected) {
                startActivity(Intent(this, NotInternetActivity::class.java))
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            } else {
                getRemoteConfigData()
            }
        }

        val userEmailText: String =
            PreferenceManager.getDefaultSharedPreferences(this).getString("userEmail", "")
                .toString()

        val userMessage: String =
            PreferenceManager.getDefaultSharedPreferences(this).getString("userMessage", "")
                .toString()

        binding.userEmail.setText(userEmailText)
        binding.userMessage.setText(userMessage)

        checkTextChanged()

        binding.sendLetter.setOnClickListener {

            val userEmail = binding.userEmail.text.toString()

            if (userEmail.isEmailValid()) {
                hideKeyboard()

                val dialog: AlertDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle(getString(R.string.dialog_send_letter_title))
                    .setMessage(getString(R.string.dialog_send_letter_text))
                    .setPositiveButton(getString(R.string.yes), null)
                    .setNegativeButton(getString(R.string.no), null)
                    .show()
                dialog.setCancelable(false)
                val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                positiveButton.setOnClickListener {
                    dialog.dismiss()

                    applicationEmail?.let {
                        val subjectEmail = "${getString(R.string.newMessage)} ${
                            userEmail.subSequence(
                                0,
                                userEmail.indexOf("@")
                            )
                        }"

                        val oldMessageToSend = binding.userMessage.text.toString()
                        val messageToSend =
                            "${getString(R.string.messageToSendHand)} $userEmail\n" +
                                    "${getString(R.string.messageToSendFooter)} $oldMessageToSend"

                        BackgroundMail.newBuilder(this)
                            .withUsername(applicationEmail!!)
                            .withPassword(applicationEmailPassword!!)
                            .withMailto(sendToMail!!)
                            .withType(BackgroundMail.TYPE_PLAIN)
                            .withSubject(subjectEmail)
                            .withBody(messageToSend)
                            .withOnSuccessCallback {
                                binding.userMessage.text = null
                                finish()
                            }
                            .send()
                    }
                    negativeButton.setOnClickListener { dialog.dismiss() }
                }

            } else {
                Toast.makeText(this, getString(R.string.write_correct_email), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkTextChanged()
            }
        }

        binding.userEmail.addTextChangedListener(textWatcher)
        binding.userMessage.addTextChangedListener(textWatcher)

        binding.back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        this.currentFocus?.let {
            inputManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun checkTextChanged() {
        val userEmail = binding.userEmail.text.toString().trim()
        val userMessage = binding.userMessage.text.toString().trim()

        if (userEmail.isNotEmpty() && userMessage.isNotEmpty()) {
            if (!buttonState) {
                binding.sendLetter.isEnabled = true
                toAnimate(disable, blue, white)
                buttonState = true
            }
        } else {
            binding.sendLetter.isEnabled = false
            if (buttonState) {
                toAnimate(blue, disable, blue)
                buttonState = false
            }
        }
    }

    private fun toAnimate(blue: Int, disable: Int, blue1: Int) {
        ObjectAnimator.ofObject(
            binding.sendLetter,
            "cardBackgroundColor",
            ArgbEvaluator(),
            blue,
            disable
        ).setDuration(duration).start()
        binding.sendLetter.setCardBackgroundColor(disable)
        binding.textChoice.setTextColor(blue1)
    }

    private fun getRemoteConfigData() {
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
                applicationEmail = mFirebaseRemoteConfig.getString("applicationEmail")
                applicationEmailPassword =
                    mFirebaseRemoteConfig.getString("applicationEmailPassword")
                sendToMail = mFirebaseRemoteConfig.getString("sendToMail")
            }
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun getVersionCode(): String = packageManager.getPackageInfo(packageName, 0).versionName
}