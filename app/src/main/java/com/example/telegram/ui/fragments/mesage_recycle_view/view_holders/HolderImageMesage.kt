package com.example.telegram.ui.fragments.mesage_recycle_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_image.view.*

class HolderImageMesage(view: View) : RecyclerView.ViewHolder(view) {

    val blocUserImageMesage: ConstraintLayout = view.bloc_user_image_message
    val chatUserImage: ImageView = view.chat_user_image
    val chatUserImageTime: TextView = view.chat_user_image_time

    val blocReceiveImageMesage: ConstraintLayout = view.bloc_receive_image_message
    val chatReceiveImage: ImageView = view.chat_receive_image
    val chatReceiveImageTime: TextView = view.chat_receive_image_time
}