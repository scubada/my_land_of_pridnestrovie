package com.sgk.myworldmogilev.helper

interface ListPositioner {
    val recyclerScrollKey: String

    fun loadListPosition()

    fun saveListPosition()
}