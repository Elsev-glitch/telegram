package com.example.telegram.ui.fragments.mesage_recycle_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_text.view.*

class HolderTextMesage(view: View): RecyclerView.ViewHolder(view) {
    val blocUserMesage: ConstraintLayout = view.bloc_user_message
    val chatUserMesage: TextView = view.chat_user_message
    val chatUserTime: TextView = view.chat_user_time

    val blocReceiveMesage: ConstraintLayout = view.bloc_receive_message
    val chatReceiveMesage: TextView = view.chat_receive_message
    val chatReceiveTime: TextView = view.chat_receive_time
}