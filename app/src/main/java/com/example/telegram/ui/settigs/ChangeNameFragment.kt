package com.example.telegram.ui.settigs

import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.ui.screens.base.BaseChangeFragment
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_name.*

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()
        fullNameList()
    }

    private fun fullNameList() {
        val fullnamelist = USER.fullname.split(" ")
        if (fullnamelist.size > 1) {
            settings_input_name.setText(fullnamelist[0])
            settings_input_surname.setText(fullnamelist[1])
        } else settings_input_name.setText(fullnamelist[0])
    }

    override fun change() {
        val name = settings_input_name.text.toString()
        val surname = settings_input_surname.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullname = "$name $surname"
            setNameToDatabase(fullname)
        }
    }
}