package com.sgk.myworldmogilev.views.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.ActivityGeneralTestBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData
import com.sgk.myworldmogilev.models.TestFormModelGeneral
import java.util.*

class GeneralTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralTestBinding
    private lateinit var database: DatabaseReference
    private var _arrayList = mutableListOf<TestFormModelGeneral>()
    private var arrayList = mutableListOf<TestFormModelGeneral>()
    private lateinit var cld: ConnectionLiveData
    private var selectedView: View? = null
    private var index = 0

    companion object {
        lateinit var testForm: TestFormModelGeneral
        lateinit var path: String
        var correctCount = 0
        var wrongCount = 0
        var drag = false
        var state = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNetworkConnection()

        binding.choiceSelect.isClickable = false

        path = intent.getStringExtra("url").toString()

        database = Firebase.database.getReference(path)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val test = data.getValue<TestFormModelGeneral>()
                    if (test != null) {
                        _arrayList.add(test)
                    }
                }
                _arrayList.shuffle()

                if (_arrayList.size <= 20)
                    arrayList = _arrayList
                else for (i in 0 until 20) {
                    arrayList.add(_arrayList[i])
                }

                testForm = arrayList[index]

                setAllData()

                binding.progress.animate()
                    .alpha(0F)
                    .withEndAction {
                        binding.progress.visibility = View.GONE
                    }
                    .setDuration(500)
                    .start()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        wrongCount = 0
        correctCount = 0

        val clickToButton: View.OnClickListener = View.OnClickListener {
            if (selectedView != null && selectedView == it)
                resetDataButton()
            else if (selectedView != null && selectedView != it) {
                selectedView = it
                binding.choiceSelect.isClickable = true
                binding.button1.strokeWidth = 0
                binding.button2.strokeWidth = 0
                binding.button3.strokeWidth = 0
                (it as MaterialCardView).strokeColor = getColor(R.color.blue_color)
                it.strokeWidth = 10
            } else
                selectButton(it)
        }
        binding.button1.setOnClickListener(clickToButton)
        binding.button2.setOnClickListener(clickToButton)
        binding.button3.setOnClickListener(clickToButton)

        binding.back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }

        binding.choiceSelect.setOnClickListener {
            click()
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }

    override fun onResume() {
        super.onResume()
        if (drag) {
            nextQuest()
            drag = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (!state) {
            state = true
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAllData() {
        when (testForm.vid) {
            "test" -> {
                binding.textView2.text = "Вопрос ${index + 1}/${arrayList.size}"
                binding.questions.text = testForm.name
                binding.textButton1.text = "А. ${testForm.aques}"
                binding.textButton2.text = "Б. ${testForm.bques}"
                binding.textButton3.text = "В. ${testForm.cques}"
                binding.textChoice.setText(R.string.choice)
                binding.progressbar.max = arrayList.size
            }
            "soot" -> {
                binding.progressbar.progress = index + 1
                val intent = Intent(this, DragDropActivity::class.java)
                DragDropActivity.size = arrayList.size
                DragDropActivity.index = index
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            "time" -> {
                binding.progressbar.progress = index + 1
                val intent = Intent(this, TimeLineActivity::class.java)
                TimeLineActivity.size = arrayList.size
                TimeLineActivity.index = index
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun enable(bol: Boolean) {
        binding.button1.isClickable = bol
        binding.button2.isClickable = bol
        binding.button3.isClickable = bol
    }

    private fun resetDataButton() {
        selectedView = null
        binding.choiceSelect.isClickable = false
        val colorFrom = getColor(R.color.blue_color)
        val colorTo = getColor(R.color.blue_color_disabled)
        val duration = 500
        ObjectAnimator.ofObject(
            binding.choiceSelect,
            "cardBackgroundColor",
            ArgbEvaluator(),
            colorFrom,
            colorTo
        ).setDuration(duration.toLong()).start()

        binding.choiceSelect.setCardBackgroundColor(getColor(R.color.blue_color_disabled))

        animBackgroundCardView(binding.button1)
        animBackgroundCardView(binding.button2)
        animBackgroundCardView(binding.button3)

        binding.textChoice.setTextColor(getColor(R.color.blue_color))
    }

    private fun animBackgroundCardView(view: MaterialCardView) {
        view.setCardBackgroundColor(getColor(R.color.green_color))
        view.strokeWidth = 0
        view.setRippleColorResource(R.color.green_color)
    }

    private fun selectButton(view: View) {
        selectedView = view
        binding.choiceSelect.isClickable = true
        val colorFrom = getColor(R.color.blue_color_disabled)
        val colorTo = getColor(R.color.blue_color)
        val duration = 500
        ObjectAnimator.ofObject(
            binding.choiceSelect,
            "cardBackgroundColor",
            ArgbEvaluator(),
            colorFrom,
            colorTo
        ).setDuration(duration.toLong()).start()
        binding.choiceSelect.setCardBackgroundColor(getColor(R.color.blue_color))
        binding.textChoice.setTextColor(getColor(R.color.white))

        (view as MaterialCardView).strokeColor = getColor(R.color.blue_color)
        view.strokeWidth = 10

        view.setRippleColorResource(R.color.blue_color)
    }

    private fun correct() {
        correctCount++
        enable(false)
        (selectedView as MaterialCardView).setCardBackgroundColor(getColor(R.color.doneChoice))
        if (index == arrayList.size - 1)
            binding.textChoice.text = getString(R.string.end)
        else
            binding.textChoice.text = getString(R.string.next)
        selectedView = null
    }


    private fun wrong() {
        wrongCount++
        enable(false)
        (selectedView as MaterialCardView).setCardBackgroundColor(getColor(R.color.errorChoice))
        when (testForm.result) {
            "1" -> binding.button1.setCardBackgroundColor(getColor(R.color.doneChoice))
            "2" -> binding.button2.setCardBackgroundColor(getColor(R.color.doneChoice))
            "3" -> binding.button3.setCardBackgroundColor(getColor(R.color.doneChoice))
        }

        if (index == arrayList.size - 1)
            binding.textChoice.text = getString(R.string.end)
        else
            binding.textChoice.text = getString(R.string.next)

        selectedView = null
    }

    private fun click() {
        if (selectedView != null) {
            if (index <= arrayList.size - 1)
                if (selectedView!!.tag == testForm.result)
                    correct()
                else
                    wrong()
        } else {
            nextQuest()
        }
    }

    private fun nextQuest() {
        resetDataButton()
        index++
        binding.progressbar.progress = index
        if (index <= arrayList.size - 1) {
            testForm = arrayList[index]
            setAllData()
        } else {
            val mainIntent = Intent(this, ResultActivity::class.java)
            mainIntent.putExtra("correct", correctCount)
            mainIntent.putExtra("wrong", wrongCount)
            mainIntent.putExtra("url", path)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            finish()
        }
        enable(true)
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