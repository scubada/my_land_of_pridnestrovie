package com.sgk.myworldmogilev.views.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.*
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.adapters.DragAdapter
import com.sgk.myworldmogilev.databinding.ActivityDragDropBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData
import com.sgk.myworldmogilev.helper.DragAndDropListener

class DragDropActivity : AppCompatActivity() {

    private lateinit var adapter: DragAdapter
    private lateinit var list: MutableList<String>
    private var testFormModelGeneral = GeneralTestActivity.testForm
    private lateinit var cld: ConnectionLiveData

    companion object {
        var context = DragDropActivity()
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityDragDropBinding

        var index: Int = 0
        var size: Int = 0

        private lateinit var value1: String
        private lateinit var value2: String
        private lateinit var value3: String
        private lateinit var value4: String

        private var buttonState = false

        private val blue = Color.parseColor("#3063A4")
        private val white = Color.parseColor("#FFFFFF")
        private val gray = Color.parseColor("#333333")
        private val disable = Color.parseColor("#95AFD1")

        private const val duration: Long = 500

        fun checkers() {
            val con1 = binding.container1.getChildAt(0)
            val con2 = binding.container2.getChildAt(0)
            val con3 = binding.container3.getChildAt(0)
            val con4 = binding.container4.getChildAt(0)

            if (buttonState) {
                binding.choiceSelect.isEnabled = false
                toAnimate(blue, disable, blue)
                buttonState = false
            }

            when (null) {
                con1, con2, con3, con4 -> buttonState = false
                else -> {
                    binding.choiceSelect.isEnabled = true
                    toAnimate(disable, blue, white)
                    buttonState = true
                }
            }

            setColorText(con1, binding.textButton1)
            setColorText(con2, binding.textButton2)
            setColorText(con3, binding.textButton3)
            setColorText(con4, binding.textButton4)
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

        private fun setColorText(con1: View?, view: TextView) {
            if (con1 != null) {
                view.setTextColor(blue)
                val tag = con1.tag.toString()
                when (view) {
                    binding.textButton1 -> value1 = tag
                    binding.textButton2 -> value2 = tag
                    binding.textButton3 -> value3 = tag
                    binding.textButton4 -> value4 = tag
                }
            } else
                view.setTextColor(gray)

        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragDropBinding.inflate(layoutInflater)
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

        binding.textButton1.text = testFormModelGeneral.aques
        binding.textButton2.text = testFormModelGeneral.bques
        binding.textButton3.text = testFormModelGeneral.cques
        binding.textButton4.text = testFormModelGeneral.dques

        list = mutableListOf(
            testFormModelGeneral.aLink!!,
            testFormModelGeneral.bLink!!,
            testFormModelGeneral.cLink!!,
            testFormModelGeneral.dLink!!
        )
        binding.progressbar.max = size
        binding.progressbar.progress = index + 1

        binding.rvWords.layoutManager =
            FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.NOWRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
                alignItems = AlignItems.CENTER
            }

        adapter = DragAdapter(list)
        binding.rvWords.adapter = adapter
    }

    private fun showCorrectResult(b: Boolean) {
        val result = "Правильный ответ:\n${testFormModelGeneral.result}"
        binding.result.text = result

        binding.rvWords.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                binding.card.visibility = View.GONE
                binding.result.visibility = View.VISIBLE
                binding.result.setTextColor(if (b) getColor(R.color.doneChoice) else getColor(R.color.errorChoice))
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