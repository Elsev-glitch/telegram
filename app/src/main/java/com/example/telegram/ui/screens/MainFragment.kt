package com.example.telegram.ui.screens

import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.hideKeyboard

class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}