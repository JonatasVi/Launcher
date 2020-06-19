package com.finnmglas.launcher

import android.app.Activity
import android.view.WindowManager

interface UIObject {
    fun onStart() {
        if (this is Activity){
            window.setFlags(0,0) // clear flags
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setTheme()
        setOnClicks()
        configure()
    }

    // Don't use actual themes, rather create them on the fly for faster theme-switching
    fun setTheme() { }      // colors
    fun setOnClicks() { }   // onClicks
    fun configure() { }     // layoutElements
}