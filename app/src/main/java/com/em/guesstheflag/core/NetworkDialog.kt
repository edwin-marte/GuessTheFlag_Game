package com.em.guesstheflag.core

import androidx.appcompat.app.AppCompatActivity
import com.em.guesstheflag.ui.NetworkConnectionFragment

object NetworkDialog {
    fun show(activity: AppCompatActivity) {val showPopup = NetworkConnectionFragment()
        showPopup.isCancelable = false
        showPopup.show(activity.supportFragmentManager, "showPopup")
    }
}