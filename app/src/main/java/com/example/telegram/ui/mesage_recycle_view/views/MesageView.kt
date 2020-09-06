package com.example.telegram.ui.mesage_recycle_view.views

interface MesageView {
    val id: String
    val from: String
    val timeStamp: String
    val fileUrl: String
    val text: String

    companion object{
        val MESAGE_IMAGE: Int
            get() = 0
        val MESAGE_TEXT: Int
            get() = 1
        val MESAGE_VOICE: Int
            get() = 2
        val MESAGE_FILE: Int
            get() = 3

    }

    fun getTypeView(): Int
}