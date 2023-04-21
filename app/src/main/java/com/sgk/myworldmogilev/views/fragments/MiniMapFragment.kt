package com.sgk.myworldmogilev.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.sgk.myworldmogilev.databinding.FragmentMiniMapBinding
import com.sgk.myworldmogilev.helper.JsWebInterface

class MiniMapFragment : Fragment() {

    private lateinit var binding: FragmentMiniMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMiniMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
            .edit()
            .putInt("web_view_position", binding.webView.scrollX)
            .apply()
        super.onPause()
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.webView) {
            loadUrl("file:///android_asset/index.html")
            with(settings) {
                javaScriptEnabled = true
                builtInZoomControls = false
                displayZoomControls = false
            }
            isVerticalScrollBarEnabled = false
            setInitialScale(130)

            val scrollPosition =
                PreferenceManager.getDefaultSharedPreferences(requireActivity()).getInt("web_view_position", 0)
            if (scrollPosition != 0) scrollTo(scrollPosition, 0) else scrollTo(500, 0)

            webViewClient = object : WebViewClient() {
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

        binding.webView.setOnTouchListener(object : OnTouchListener {
            var m_downY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.pointerCount > 1) {
                    return true
                }
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        m_downY = event.y
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        event.setLocation(event.x, m_downY)
                    }
                }
                return false
            }
        })

        binding.webView.addJavascriptInterface(JsWebInterface(requireActivity()), "androidApp")
    }

    companion object {
        @JvmStatic
        fun newInstance() = MiniMapFragment()
    }
}