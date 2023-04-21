package com.sgk.myworldmogilev.views.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.flexbox.*
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.adapters.DragAdapter
import com.sgk.myworldmogilev.databinding.ActivityTimeLineBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData
import com.sgk.myworldmogilev.helper.DragAndDropListener

import com.sgk.myworldmogilev.helper.DragAndDropContainer

class TimeLineActivity : AppCompatActivity() {

    private lateinit var adapter: DragAdapter
    private lateinit var list: MutableList<String>
    private var testFormModelGeneral = GeneralTestActivity.testForm
    private lateinit var cld: ConnectionLiveData

    companion object {
        var context = TimeLineActivity()
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityTimeLineBinding

        var index: Int = 0
        var size: Int = 0

        private lateinit var value1: String
        private lateinit var value2: String
        private lateinit var value3: String
        private lateinit var value4: String

        private var buttonState = false

        private val blue = Color.parseColor("#3063A4")
        private val white = Color.parseColor("#FFFFFF")
        private val disable = Color.parseColor("#95AFD1")

        private const val duration: Long = 500

        private fun setValuableResult(con1: View?, view: DragAndDropContainer) {
            if (con1 != null) {
                val tag = con1.tag.toString()
                when (view) {
                    binding.container1a -> value1 = tag
                    binding.container2a -> value2 = tag
                    binding.container3a -> value3 = tag
                    binding.container4a -> value4 = tag
                }
            }
        }

        fun checkers() {
            val con12 = binding.container1a.getChildAt(0)
            val con22 = binding.container2a.getChildAt(0)
            val con32 = binding.container3a.getChildAt(0)
            val con42 = binding.container4a.getChildAt(0)

            if (buttonState) {
                binding.choiceSelect.isEnabled = false
                toAnimate(blue, disable, blue)
                buttonState = false
            }

            buttonState = when (null) {
                con12, con22, con32, con42 -> false
                else -> {
                    binding.choiceSelect.isEnabled = true
                    toAnimate(disable, blue, white)
                    true
                }
            }
            setValuableResult(con12, binding.container1a)
            setValuableResult(con22, binding.container2a)
            setValuableResult(con32, binding.container3a)
            setValuableResult(con42, binding.container4a)
        }

        private fun toAnimate(blue: Int, disable: Int, blue1: Int) {
            ObjectAnimator.ofObject(
                binding.choiceSelect,
                "cardBackgroundColor",
                ArgbEvaluator(),
                blue,
                disable
            ).setDuration(duration).start()
            binding.choiceSelect.setCardBackgroundColor(disable)
            binding.textChoice.setTextColor(blue1)
        }
    }


    override fun onBackPressed() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DragAndDropListener.activity = context
        GeneralTestActivity.state = false
        binding.choiceSelect.isEnabled = false

        setData()
        checkNetworkConnection()

        binding.choiceSelect.setOnClickListener {
            binding.choiceSelect.isEnabled = false
            if (resultChoice() == testFormModelGeneral.result) {
                showCorrectResult(true)
            } else
                showCorrectResult(false)
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun setData() {
        binding.textView2.text = "Вопрос ${index + 1}/$size"
        binding.questions.text = testFormModelGeneral.name

        list = mutableListOf(
            testFormModelGeneral.aLink!!,
            testFormModelGeneral.bLink!!,
            testFormModelGeneral.cLink!!,
            testFormModelGeneral.dLink!!
        )
        binding.progressbar.max = size

        binding.progressbar.progress = index + 1

        binding.rvWords.layoutManager =
            FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
                alignItems = AlignItems.CENTER
            }

        adapter = DragAdapter(list)
        binding.rvWords.adapter = adapter
    }

    private fun showCorrectResult(b: Boolean) {
        val result = "Правильный ответ:\n${testFormModelGeneral.result}"
        binding.result.text = result
        binding.result.setTextColor(if (b) getColor(R.color.doneChoice) else getColor(R.color.errorChoice))

        binding.result.animate()
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                Handler(Looper.getMainLooper()).postDelayed({
                    covet(b)
                }, 2500)
            }
            .start()
    }

    private fun resultChoice(): String = "1) [$value1], 2) [$value2], 3) [$value3], 4) [$value4]"

    private fun covet(state: Boolean) {
        if (state) GeneralTestActivity.correctCount++ else GeneralTestActivity.wrongCount++
        index++
        binding.progressbar.progress = index
        if (index <= size - 1) {
            GeneralTestActivity.drag = true
            GeneralTestActivity.state = true
            finish()
            overridePendingTransition(0, 0)
        } else {
            val mainIntent = Intent(this, ResultActivity::class.java)
            mainIntent.putExtra("correct", GeneralTestActivity.correctCount)
            mainIntent.putExtra("wrong", GeneralTestActivity.wrongCount)
            mainIntent.putExtra("url", GeneralTestActivity.path)
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
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }
    }
}