package com.example.telegram.ui.mesage_recycle_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.mesage_recycle_view.views.MesageView
import com.example.telegram.utils.asTime
import kotlinx.android.synthetic.main.message_item_text.view.*

class HolderTextMesage(view: View): RecyclerView.ViewHolder(view), MesageHolder {
    private val blocUserMesage: ConstraintLayout = view.bloc_user_message
    private val chatUserMesage: TextView = view.chat_user_message
    private val chatUserTime: TextView = view.chat_user_time

    private val blocReceiveMesage: ConstraintLayout = view.bloc_receive_message
    private val chatReceiveMesage: TextView = view.chat_receive_message
    private val chatReceiveTime: TextView = view.chat_receive_time

    override fun drawMesage(view: MesageView) {
        if (view.from == CURRENT_UID) {
            blocUserMesage.visibility = View.VISIBLE
            blocReceiveMesage.visibility = View.GONE
            chatUserMesage.text = view.text
            chatUserTime.text = view.timeStamp.asTime()
        } else {
            blocUserMesage.visibility = View.GONE
            blocReceiveMesage.visibility = View.VISIBLE
            chatReceiveMesage.text = view.text
            chatReceiveTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MesageView) {

    }

    override fun onDetach() {

    }

}