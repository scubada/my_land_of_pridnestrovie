package com.sgk.myworldmogilev.helper

import android.app.Activity
import android.webkit.JavascriptInterface
import com.sgk.myworldmogilev.views.activities.MainActivity
import com.sgk.myworldmogilev.views.fragments.MapFragment

class JsWebInterface(activity: Activity) {

    private val activity: Activity = activity

    @JavascriptInterface
    fun pos(latitude: String?, longitude: String?) {
        MapFragment.laM = latitude!!.toDouble()
        MapFragment.loM = longitude!!.toDouble()
        MainActivity.hideMiniMap()
    }
}