package com.example.telegram.ui.mesage_recycle_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.mesage_recycle_view.views.MesageView
import com.example.telegram.utils.asTime
import com.example.telegram.utils.downloadAndSetImage
import kotlinx.android.synthetic.main.message_item_image.view.*

class HolderImageMesage(view: View):RecyclerView.ViewHolder(view),MesageHolder {

    private val blocUserImageMesage: ConstraintLayout = view.bloc_user_image_message
    private val chatUserImage: ImageView = view.chat_user_image
    private val chatUserImageTime: TextView = view.chat_user_image_time

    private val blocReceiveImageMesage: ConstraintLayout = view.bloc_receive_image_message
    private val chatReceiveImage: ImageView = view.chat_receive_image
    private val chatReceiveImageTime: TextView = view.chat_receive_image_time

    override fun drawMesage(view: MesageView) {
        if (view.from == CURRENT_UID) {
            blocReceiveImageMesage.visibility = View.GONE
            blocUserImageMesage.visibility = View.VISIBLE
            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageTime.text = view.timeStamp.asTime()
        } else {
            blocReceiveImageMesage.visibility = View.VISIBLE
            blocUserImageMesage.visibility = View.GONE
            chatReceiveImage.downloadAndSetImage(view.fileUrl)
            chatReceiveImageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MesageView) {

    }

    override fun onDetach() {

    }
}